package com.example.assistgoandroid.Call;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.models.Contact;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import okhttp3.Headers;
import tvi.webrtc.Camera1Enumerator;
import tvi.webrtc.VideoSink;
import com.codepath.asynchttpclient.AsyncHttpClient;

import org.json.JSONException;

public class VideoCall extends AppCompatActivity {
    //Resources
    //https://www.twilio.com/blog/add-muting-unmuting-video-chat-app-30-seconds
    //https://www.twilio.com/docs/video/android-getting-started#connect-to-a-room
    String TAG = "VideoCall";
    String CURRENT_TIME;
    Contact contact;

    VideoView primaryVideoView;
    VideoView thumbnailVideoView;
    LocalAudioTrack localAudioTrack;
    LocalVideoTrack localVideoTrack;
    String accessToken;
    String tokenURL = "https://rackley-iguana-5070.twil.io/video-token";
    Room room;

    /*
     * A LocalParticipant represents the identity and tracks provided by this instance
     */
    private LocalParticipant localParticipant;
    private String remoteParticipantIdentity;

    String roomName;
    boolean muted = false;
    boolean videoOn = false;
    private String frontCameraId = null;
    private String backCameraId = null;
    private final Camera1Enumerator camera1Enumerator = new Camera1Enumerator();
    private CameraCapturer cameraCapturer;
    private VideoSink localVideoView;
    private boolean disconnectedFromOnDestroy;
    private AudioManager audioManager;

    //notifications
    private boolean isReceiverRegistered;
//    private LocalBroadcastReceiver localBroadcastReceiver;
    private NotificationManager notificationManager;
    private Intent cachedVideoNotificationIntent;

    private int previousAudioMode;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_page);

        requestPermissionForCameraAndMicrophone();
        ImageView switchCameraBtn = findViewById(R.id.switchCamBtn);
        ImageView videochatBtn = findViewById(R.id.videochatBtn);
        ImageView muteBtn = findViewById(R.id.muteBtn);
        ImageView hangupBtn = findViewById(R.id.hangupBtn);
        primaryVideoView = findViewById(R.id.primary_video_view);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);

        //passed when call is accepted
        contact = getIntent().getParcelableExtra("CONTACT_CARD");
        roomName = getIntent().getStringExtra("ROOM");

        Log.i(TAG, "Contact is " + contact);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        /*
         * Setup the broadcast receiver to be notified of video notification messages
         */
//        localBroadcastReceiver = new LocalBroadcastReceiver();
//        registerReceiver();

        View.OnClickListener switchCameraClick = v -> {
            switchCamera();
        };

        View.OnClickListener videoChatClick = v -> {
            //todo turn off camera or turn on camera
            if(!videoOn)
                turnVideOff();
            else
                turnVideoOn();
        };

        View.OnClickListener muteClick = v -> {
            if (localAudioTrack != null) {
                boolean enable = !localAudioTrack.isEnabled();
                localAudioTrack.enable(enable);
                int icon = enable ? R.drawable.mic_on : R.drawable.mic_off;
                muteBtn.setImageDrawable(
                        ContextCompat.getDrawable(VideoCall.this, icon));
            }
        };

        View.OnClickListener hangupClick = v -> {
            hangup();
            //go back to previous page
            finish();
        };

        switchCameraBtn.setOnClickListener(switchCameraClick);
        videochatBtn.setOnClickListener(videoChatClick);
        muteBtn.setOnClickListener(muteClick);
        hangupBtn.setOnClickListener(hangupClick);

        if(!checkPermissionForCameraAndMicrophone())
            requestPermissionForCameraAndMicrophone();

        createLocalTracks(); //same as commented below

//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            OkHttpClient client = new OkHttpClient();
//            //MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
//
//            Request request = new Request.Builder()
//                    .url(tokenURL + "?identity=" + contact.getPhoneNumber())
//                    .get()
//                    .build();
//            Response response = client.newCall(request).execute();
//            String jsonDataString = null;
//            try {
//                jsonDataString = response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Map<String, ?> responseJson = mapper.readValue(jsonDataString, Map.class);
//
//            accessToken = String.valueOf(responseJson.get("token"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //easier way to get json object
        AsyncHttpClient client = new AsyncHttpClient();
        String url = tokenURL + "?identity=" + contact.getPhoneNumber().replace(" ", "%20");
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON json) {
                // Access a JSON object response with `json.jsonObject`
                Log.d("DEBUG OBJECT", json.jsonObject.toString());
                try {
                    accessToken = json.jsonObject.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON error");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "JSON error");
            }
        });

        accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzVhNGRkZWVmZDBhZjM2NzIwMTJlNGY4MGJiZmY4Y2U4LTE2NTE5MjgzNDUiLCJncmFudHMiOnsiaWRlbnRpdHkiOiIoMjIyKSAyODktMjIyMiIsInZpZGVvIjp7InJvb20iOiJEYWlseVN0YW5kdXAifX0sImlhdCI6MTY1MTkyODM0NSwiZXhwIjoxNjUxOTMxOTQ1LCJpc3MiOiJTSzVhNGRkZWVmZDBhZjM2NzIwMTJlNGY4MGJiZmY4Y2U4Iiwic3ViIjoiQUNhNTI4OTc0MmRkNjA4MGRhMmU4ZDJlODQyMTMwZGIxMCJ9.gonpxAWmVh0pn00PYmxbDEiXLidEmEnLrqIbwIBcXP0";
        //todo accesstoken is null
        Log.i(TAG, accessToken + " " + contact.getFullName());
        connectToRoom("roomName");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void switchCamera() {
        if (cameraCapturer != null) {
            String cameraId =
                    cameraCapturer.getCameraId().equals(getFrontCameraId())
                            ? getBackCameraId()
                            : getFrontCameraId();
            cameraCapturer.switchCamera(cameraId);
            if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                thumbnailVideoView.setMirror(cameraId.equals(getBackCameraId()));
            } else {
                primaryVideoView.setMirror(cameraId.equals(getBackCameraId()));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void turnVideOff() {

        room.getLocalParticipant().getLocalVideoTracks().forEach(localVideoTrackPublication -> localVideoTrackPublication.getLocalVideoTrack().enable(false));
        videoOn= false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void turnVideoOn() {

        room.getLocalParticipant().getLocalVideoTracks().forEach(localVideoTrackPublication -> localVideoTrackPublication.getLocalVideoTrack().enable(true));
        videoOn= true;
    }

    public void hangup() {
        Date currentTime = Calendar.getInstance().getTime();
        CURRENT_TIME = currentTime.toString();
        contact.setLastCalled(CURRENT_TIME);

        //Disconnect from a Room
        room.disconnect();

        finish();
    }

    //https://www.twilio.com/docs/video/android-getting-started#connect-to-a-room
    //The name of the Room specifies which Room you wish to join.
    // If a Room by that name does not already exist, it will be created upon connection.
    // If a Room by that name is already active, you'll be connected to the Room and receive notifications from any other Participants
    // also connected to the same Room. Room names must be unique within an account.
    public void connectToRoom(String roomName) {

        enableAudioFocus(true);
        enableVolumeControl(true);

        ConnectOptions.Builder connectOptionsBuilder =
                new ConnectOptions.Builder(accessToken).roomName(roomName);

        /*
         * Add local audio track to connect options to share with participants.
         */
        if (localAudioTrack != null) {
            connectOptionsBuilder.audioTracks(Collections.singletonList(localAudioTrack));
        }

        /*
         * Add local video track to connect options to share with participants.
         */
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }

        room = Video.connect(getApplicationContext(), connectOptionsBuilder.build(), new Room.Listener() {
                    @Override
                    public void onConnected(@NonNull Room room) {
                        Log.d(TAG,"Connected to " + room.getName());

                        localParticipant = room.getLocalParticipant();

                        for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                            addRemoteParticipant(remoteParticipant);
                            break;
                        }
                    }

                    @Override
                    public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {
                        Toast.makeText(getApplicationContext(), "Connection Failed", LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                        Toast.makeText(getApplicationContext(), "Reconnecting...", LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReconnected(@NonNull Room room) {
                        Toast.makeText(getApplicationContext(), "Reconnected", LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDisconnected(@NonNull Room room, @Nullable TwilioException twilioException) {
                        Toast.makeText(getApplicationContext(), "Disconnected", LENGTH_SHORT).show();
                        enableAudioFocus(false);
                        enableVolumeControl(false);
                        VideoCall.this.room = null;
                        localParticipant = null;
                        localVideoTrack.release();
                    }

                    @Override
                    public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                        addRemoteParticipant(remoteParticipant);
                    }

                    @Override
                    public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                        Log.i("Room.Listener", remoteParticipantIdentity + " has left the room.");
                        Toast.makeText(getApplicationContext(), remoteParticipant.getIdentity() + " has left the room.", LENGTH_SHORT).show();
                        removeParticipant(remoteParticipant);
                    }

                    @Override
                    public void onRecordingStarted(@NonNull Room room) {
                    }

                    @Override
                    public void onRecordingStopped(@NonNull Room room) {
                    }
                }
        );

        // ... Assume we have received the connected callback
        // After receiving the connected callback the LocalParticipant becomes available
        //Log.i("LocalParticipant ", room.getLocalParticipant().getIdentity());
        //LocalParticipant localParticipant = room.getLocalParticipant();

        // Get the first participant from the room
        //RemoteParticipant participant = room.getRemoteParticipants().get(0);
        //Log.i("HandleParticipants", participant.getIdentity() + " is in the room.");
    }

    /* In the Participant listener, we can respond when the Participant adds a Video
    Track by rendering it on screen: */
    //https://www.twilio.com/docs/video/android-getting-started#connect-to-a-room
    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {

            @Override
            public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Toast.makeText(getApplicationContext(), "onAudioTrackPublished", LENGTH_SHORT).show();
            }

            @Override
            public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Toast.makeText(getApplicationContext(), "onAudioTrackUnpublished", LENGTH_SHORT).show();
            }

            @Override
            public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {
                Toast.makeText(getApplicationContext(), "onAudioTrackSubscribed", LENGTH_SHORT).show();
            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull TwilioException twilioException) {
                Toast.makeText(getApplicationContext(), "onAudioTrackSubscriptionFailed", LENGTH_SHORT).show();
            }

            @Override
            public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {
                Toast.makeText(getApplicationContext(), "onAudioTrackUnsubscribed", LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Toast.makeText(getApplicationContext(), "onVideoTrackPublished", LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Toast.makeText(getApplicationContext(), "onVideoTrackUnpublished", LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                Toast.makeText(getApplicationContext(), "onVideoTrackSubscribed", LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {
                Toast.makeText(getApplicationContext(), "onVideoTrackSubscriptionFailed", LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                Toast.makeText(getApplicationContext(), "onVideoTrackUnsubscribed", LENGTH_SHORT).show();
            }

            @Override
            public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {
                Toast.makeText(getApplicationContext(), "onDataTrackPublished", LENGTH_SHORT).show();
            }

            @Override
            public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {
                Toast.makeText(getApplicationContext(), "onDataTrackUnpublished", LENGTH_SHORT).show();
            }

            @Override
            public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {
                Toast.makeText(getApplicationContext(), "onDataTrackSubscribed", LENGTH_SHORT).show();
            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull TwilioException twilioException) {
                Toast.makeText(getApplicationContext(), "onDataTrackSubscriptionFailed", LENGTH_SHORT).show();
            }

            @Override
            public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {
                Toast.makeText(getApplicationContext(), "onDataTrackUnsubscribed", LENGTH_SHORT).show();
            }

            @Override
            public void onAudioTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
            }

            @Override
            public void onAudioTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
            }

            @Override
            public void onVideoTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
            }

            @Override
            public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
            }
        };
    }

    // participant Joining a video chat
    /*
     * Called when remote participant joins the room
     */
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        remoteParticipantIdentity = remoteParticipant.getIdentity();
        Toast.makeText(getApplicationContext(), "remoteParticipantIdentity " + " joined.", LENGTH_SHORT).show();

        /*
         * Add remote participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Only render video tracks that are subscribed to
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }

        /*
         * Start listening for participant media events
         */
        remoteParticipant.setListener(remoteParticipantListener());
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        primaryVideoView.setMirror(false);
        videoTrack.addSink(primaryVideoView);
    }

    private void moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            if (localVideoTrack != null) {
                localVideoTrack.removeSink(primaryVideoView);
                localVideoTrack.addSink(thumbnailVideoView);
            }
            localVideoView = thumbnailVideoView;
            thumbnailVideoView.setMirror(cameraCapturer.getCameraId().equals(getFrontCameraId()));
        }
    }

    /*
     * Called when participant leaves the room
     */
    private void removeParticipant(RemoteParticipant remoteParticipant) {
        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {
            return;
        }

        /*
         * Remove participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Remove video only if subscribed to participant track.
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }
        moveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeSink(primaryVideoView);
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            localVideoTrack.removeSink(thumbnailVideoView);
            thumbnailVideoView.setVisibility(View.GONE);
            localVideoTrack.removeSink(primaryVideoView);
            localVideoView = primaryVideoView;
            primaryVideoView.setMirror(cameraCapturer.getCameraId().equals(getFrontCameraId()));
        }
    }

    // audio config
    private void enableAudioFocus(boolean focus) {
        if (focus) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch.
            requestAudioFocus();
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
        }
    }

    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
            AudioFocusRequest focusRequest =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(i -> {})
                            .build();
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(
                    null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void enableVolumeControl(boolean volumeControl) {
        if (volumeControl) {
            /*
             * Enable changing the volume using the up/down keys during a conversation
             */
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        } else {
            setVolumeControlStream(getVolumeControlStream());
        }
    }


    //ending call
    @Override
    protected void onDestroy() {
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }

        super.onDestroy();
    }

    //permissions

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {

        // request permission in fragment
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
        }
    }

    private String getFrontCameraId() {
        if (frontCameraId == null) {
            for (String deviceName : camera1Enumerator.getDeviceNames()) {
                if (camera1Enumerator.isFrontFacing(deviceName)) {
                    frontCameraId = deviceName;
                }
            }
        }

        return frontCameraId;
    }

    private String getBackCameraId() {
        if (backCameraId == null) {
            for (String deviceName : camera1Enumerator.getDeviceNames()) {
                if (camera1Enumerator.isBackFacing(deviceName)) {
                    backCameraId = deviceName;
                }
            }
        }

        return backCameraId;
    }

    private void createLocalTracks() {
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(this, true);

        // Share your camera
        cameraCapturer = new CameraCapturer(this, getFrontCameraId());
        localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer);
        primaryVideoView.setMirror(true);
        localVideoTrack.addSink(primaryVideoView);
        localVideoView = primaryVideoView;
    }

    //notifications

//    public static AlertDialog createConnectDialog(
//            String title,
//            EditText roomEditText,
//            DialogInterface.OnClickListener callParticipantsClickListener,
//            DialogInterface.OnClickListener cancelClickListener,
//            Context context) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setIcon(R.drawable.ic_video_call_black_24dp);
//        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder.setPositiveButton("Connect", callParticipantsClickListener);
//        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener);
//        alertDialogBuilder.setCancelable(false);
//        alertDialogBuilder.setView(roomEditText);
//        return alertDialogBuilder.create();
//    }
//    private DialogInterface.OnClickListener connectClickListener(final EditText roomEditText) {
//        return (dialog, which) -> {
//            final String roomName = roomEditText.getText().toString();
//            /*
//             * Connect to room
//             */
//            connectToRoom(roomName);
//            /*
//             * Notify other participants to join the room
//             */
//            VideoInviteActivity.this.notify(roomName);
//        };
//    }
//
//    private DialogInterface.OnClickListener videoNotificationConnectClickListener(
//            final EditText roomEditText) {
//        return (dialog, which) -> {
//            /*
//             * Connect to room
//             */
//            connectToRoom(roomEditText.getText().toString());
//        };
//    }
//
//    private View.OnClickListener disconnectClickListener() {
//        return v -> {
//            /*
//             * Disconnect from room
//             */
//            if (room != null) {
//                room.disconnect();
//            }
//            intializeUI();
//        };
//    }
//
//    private View.OnClickListener connectActionClickListener() {
//        return v -> showConnectDialog();
//    }
//
//    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
//        return (dialog, which) -> {
//            intializeUI();
//            alertDialog.dismiss();
//        };
//    }
    /*
     * Creates a connect UI dialog
     */
//    private void showConnectDialog() {
//        EditText roomEditText = new EditText(this);
//        String title = "Connect to a video room";
//        roomEditText.setHint("room name");
//        alertDialog =
//                createConnectDialog(
//                        title,
//                        roomEditText,
//                        connectClickListener(roomEditText),
//                        cancelConnectDialogClickListener(),
//                        this);
//        alertDialog.show();
//    }
//    /*
//     * Register to obtain a token and register a binding with Twilio Notify
//     */
//    private void register() {
//        Intent intent = new Intent(this, RegistrationIntentService.class);
//        startService(intent);
//    }
//
//    /*
//     * Called when a notification is clicked and this activity is in the background or closed
//     */
//    @Override
//    protected void onNewIntent(final Intent intent) {
//        super.onNewIntent(intent);
//        if (intent.getAction() == ACTION_VIDEO_NOTIFICATION) {
//            handleVideoNotificationIntent(intent);
//        }
//    }
//
//    private void handleRegistration(Intent intent) {
//        String registrationError = intent.getStringExtra(REGISTRATION_ERROR);
//        if (registrationError != null) {
//            statusTextView.setText(registrationError);
//        } else {
//            createLocalTracks();
//            identity = intent.getStringExtra(REGISTRATION_IDENTITY);
//            token = intent.getStringExtra(REGISTRATION_TOKEN);
//            identityTextView.setText(identity);
//            statusTextView.setText(R.string.registered);
//            intializeUI();
//            if (cachedVideoNotificationIntent != null) {
//                handleVideoNotificationIntent(cachedVideoNotificationIntent);
//                cachedVideoNotificationIntent = null;
//            }
//        }
//    }
//
//    private void handleVideoNotificationIntent(Intent intent) {
//        notificationManager.cancelAll();
//        /*
//         * Only handle the notification if not already connected to a Video Room
//         */
//        if (room == null) {
//            String title = intent.getStringExtra(VIDEO_NOTIFICATION_TITLE);
//            String dialogRoomName = intent.getStringExtra(VIDEO_NOTIFICATION_ROOM_NAME);
//            showVideoNotificationConnectDialog(title, dialogRoomName);
//        }
//    }
//
//    private void registerReceiver() {
//        if (!isReceiverRegistered) {
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(ACTION_VIDEO_NOTIFICATION);
//            intentFilter.addAction(ACTION_REGISTRATION);
//            LocalBroadcastManager.getInstance(this)
//                    .registerReceiver(localBroadcastReceiver, intentFilter);
//            isReceiverRegistered = true;
//        }
//    }
//
//    private void unregisterReceiver() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
//        isReceiverRegistered = false;
//    }
//
//    private class LocalBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(ACTION_REGISTRATION)) {
//                handleRegistration(intent);
//            } else if (action.equals(ACTION_VIDEO_NOTIFICATION)) {
//                handleVideoNotificationIntent(intent);
//            }
//        }
//    }
//
//    /*
//     * Creates a connect UI dialog to handle notifications
//     */
//    private void showVideoNotificationConnectDialog(String title, String roomName) {
//        EditText roomEditText = new EditText(this);
//        roomEditText.setText(roomName);
//        // Use the default color instead of the disabled color
//        int currentColor = roomEditText.getCurrentTextColor();
//        roomEditText.setEnabled(false);
//        roomEditText.setTextColor(currentColor);
//        alertDialog =
//                createConnectDialog(
//                        title,
//                        roomEditText,
//                        videoNotificationConnectClickListener(roomEditText),
//                        cancelConnectDialogClickListener(),
//                        this);
//        alertDialog.show();
//    }

}

