package com.example.assistgoandroid.call;

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
import android.os.AsyncTask;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.assistgoandroid.BuildConfig;
import com.example.assistgoandroid.call.videoCallHelpers.notify.api.TwilioSDKStarterAPI;
import com.example.assistgoandroid.call.videoCallHelpers.notify.api.model.Invite;
import com.example.assistgoandroid.call.videoCallHelpers.notify.api.model.Notification;
import com.example.assistgoandroid.call.videoCallHelpers.notify.service.RegistrationIntentService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tvi.webrtc.Camera1Enumerator;
import tvi.webrtc.VideoSink;

public class VideoCall extends AppCompatActivity {
    //Resources
    //https://github.com/twilio/video-quickstart-android/blob/master/exampleVideoInvite/src/main/java/com/twilio/video/examples/videoinvite/VideoInviteActivity.java
    private final String TAG = "VideoCall";
    private String CURRENT_TIME;
    private Contact contact;

    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private String accessToken;
    private final String tokenURL = "https://rackley-iguana-5070.twil.io/video-token";
    private Room room;

    /*
     * A LocalParticipant represents the identity and tracks provided by this instance
     */
    private LocalParticipant localParticipant;
    private String remoteParticipantIdentity;

    String roomName;
    private String identity;
    private String frontCameraId = null;
    private String backCameraId = null;
    private final Camera1Enumerator camera1Enumerator = new Camera1Enumerator();
    private CameraCapturer cameraCapturer;
    private VideoSink localVideoView;
    private boolean disconnectedFromOnDestroy;
    private AudioManager audioManager;
    private AlertDialog alertDialog;

    //notifications
    private boolean isReceiverRegistered;
    private LocalBroadcastReceiver localBroadcastReceiver;
    private NotificationManager notificationManager;
    private Intent cachedVideoNotificationIntent;

    private int previousAudioMode;

    public static final String TWILIO_SDK_STARTER_SERVER_URL = BuildConfig.TWILIO_SDK_STARTER_SERVER_URL;
    public static final String ACTION_REGISTRATION = "ACTION_REGISTRATION";
    public static final String ACTION_VIDEO_NOTIFICATION = "ACTION_VIDEO_NOTIFICATION";
    public static final String REGISTRATION_ERROR = "REGISTRATION_ERROR";
    public static final String REGISTRATION_IDENTITY = "REGISTRATION_IDENTITY";
    public static final String REGISTRATION_TOKEN = "REGISTRATION_TOKEN";
    public static final String VIDEO_NOTIFICATION_TITLE = "VIDEO_NOTIFICATION_TITLE";
    public static final String VIDEO_NOTIFICATION_ROOM_NAME = "VIDEO_NOTIFICATION_ROOM_NAME";

    /*
     * The tag used to notify others when this identity is connecting to a Video room.
     */
    public static final List<String> NOTIFY_TAGS =
            new ArrayList<String>() {
                {
                    add("video");
                }
            };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_page);
        requestPermissionForCameraAndMicrophone();
        ImageView switchCameraBtn = findViewById(R.id.switchCamBtn);
        ImageView turnVideoOffBtn = findViewById(R.id.turnVideoOffBtn);
        ImageView muteBtn = findViewById(R.id.muteBtn);
        ImageView hangupBtn = findViewById(R.id.hangupBtn);
        primaryVideoView = findViewById(R.id.primary_video_view);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);

        View.OnClickListener switchCameraClick = v -> switchCamera();

        View.OnClickListener videoChatClick = v -> {
            if (localVideoTrack != null) {
                boolean enable = !localVideoTrack.isEnabled();
                localVideoTrack.enable(enable);
                int camIcon = enable ? R.drawable.videocam_off : R.drawable.videocam_on;
                turnVideoOffBtn.setImageDrawable(
                        ContextCompat.getDrawable(VideoCall.this, camIcon));
            }
        };

        View.OnClickListener muteClick = v -> {
            if (localAudioTrack != null) {
                boolean enable = !localAudioTrack.isEnabled();
                localAudioTrack.enable(enable);
                int muteIcon = enable ? R.drawable.mic_on : R.drawable.mic_off;
                muteBtn.setImageDrawable(
                        ContextCompat.getDrawable(VideoCall.this, muteIcon));
            }
        };

        View.OnClickListener hangupClick = v -> {
            hangup();
            //go back to previous page
            finish();
        };

        switchCameraBtn.setOnClickListener(switchCameraClick);
        turnVideoOffBtn.setOnClickListener(videoChatClick);
        muteBtn.setOnClickListener(muteClick);
        hangupBtn.setOnClickListener(hangupClick);

        requestPermissionForCameraAndMicrophone();

        //passed when call is accepted
        contact = getIntent().getParcelableExtra("CONTACT_CARD");
        roomName = contact.getPhoneNumber();

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
        localBroadcastReceiver = new LocalBroadcastReceiver();
        registerReceiver();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = getIntent();

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else if (intent != null && intent.getAction() == ACTION_REGISTRATION) {
            handleRegistration(intent);
        } else if (intent != null && intent.getAction() == ACTION_VIDEO_NOTIFICATION) {
            /*
             * Cache the video invite notification intent until an access token is obtained through
             * registration
             */
            cachedVideoNotificationIntent = intent;
            register();
        } else {
            register();
        }

        createLocalTracks();

        String url = tokenURL + "?identity=" + contact.getPhoneNumber().replace(" ", "%20");

        HttpGetRequest httpGetRequest = new HttpGetRequest();
        httpGetRequest.execute(url);
        try {
            accessToken = httpGetRequest.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(TAG, accessToken + " " + contact.getFullName());
        connectToRoom(roomName);
        //notify(roomName);
    }

    private void register() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    /*
     * Called when a notification is clicked and this activity is in the background or closed
     */
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() == ACTION_VIDEO_NOTIFICATION) {
            handleVideoNotificationIntent(intent);
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_VIDEO_NOTIFICATION);
            intentFilter.addAction(ACTION_REGISTRATION);
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(localBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
        isReceiverRegistered = false;
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
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
                        setTitle(room.getName());

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
                        if (!disconnectedFromOnDestroy) {
                            moveLocalVideoToPrimaryView();
                        }
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
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {
                Toast.makeText(getApplicationContext(), "onVideoTrackSubscriptionFailed", LENGTH_SHORT).show();
            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                Toast.makeText(getApplicationContext(), "onVideoTrackUnsubscribed", LENGTH_SHORT).show();
                removeParticipantVideo(remoteVideoTrack);
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
                Toast.makeText(getApplicationContext(), String.format(
                        "Failed to subscribe to %s video track",
                        remoteParticipant.getIdentity()), LENGTH_SHORT).show();
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

    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_REGISTRATION)) {
                handleRegistration(intent);
            } else if (action.equals(ACTION_VIDEO_NOTIFICATION)) {
                handleVideoNotificationIntent(intent);
            }
        }
    }

    private void handleVideoNotificationIntent(Intent intent) {
        notificationManager.cancelAll();
        /*
         * Only handle the notification if not already connected to a Video Room
         */
        if (room == null) {
            String title = intent.getStringExtra(VIDEO_NOTIFICATION_TITLE);
            String dialogRoomName = intent.getStringExtra(VIDEO_NOTIFICATION_ROOM_NAME);
            showVideoNotificationConnectDialog(title, dialogRoomName);
        }
    }

    /*
     * Creates a connect UI dialog to handle notifications
     */
    private void showVideoNotificationConnectDialog(String title, String dialogRoomName) {
        EditText roomEditText = new EditText(this);
        roomEditText.setText(roomName);
        // Use the default color instead of the disabled color
        int currentColor = roomEditText.getCurrentTextColor();
        roomEditText.setEnabled(false);
        roomEditText.setTextColor(currentColor);
        alertDialog =
                createConnectDialog(
                        title,
                        roomEditText,
                        videoNotificationConnectClickListener(roomEditText),
                        cancelConnectDialogClickListener(),
                        this);
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        /*
         * If the local video track was released when the app was put in the background, recreate.
         */
        if (localVideoTrack == null
                && checkPermissionForCameraAndMicrophone()
                && cameraCapturer != null) {
            localVideoTrack = LocalVideoTrack.create(this, true, cameraCapturer);
            localVideoTrack.addSink(localVideoView);

            /*
             * If connected to a Room then share the local video track.
             */
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);
            }
        }

        /*
         * Update reconnecting UI
         */
        if (room != null) {
            Toast.makeText(getApplicationContext(), "Connected to " + room.getName(), LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver();
        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         *
         * If this local video track is being shared in a Room, participants will be notified
         * that the track has been unpublished.
         */
        if (localVideoTrack != null) {
            /*
             * If this local video track is being shared in a Room, unpublish from room before
             * releasing the video track. Participants will be notified that the track has been
             * removed.
             */
            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
            }
            localVideoTrack.release();
            localVideoTrack = null;
        }
        super.onPause();
    }

    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
        return (dialog, which) -> alertDialog.dismiss();
    }

    private DialogInterface.OnClickListener videoNotificationConnectClickListener(
            final EditText roomEditText) {
        return (dialog, which) -> {
            /*
             * Connect to room
             */
            connectToRoom(roomEditText.getText().toString());
        };
    }

    void notify(final String roomName) {
        String inviteJsonString;
        Invite invite = new Invite(identity, roomName);

        /*
         * Use Twilio Notify to let others know you are connecting to a Room
         */
        Notification notification =
                new Notification(
                        "Join " + identity + " in room " + roomName,
                        identity + " has invited you to join video room " + roomName,
                        invite.getMap(),
                        NOTIFY_TAGS);
        TwilioSDKStarterAPI.notify(notification)
                .enqueue(
                        new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()) {
                                    String message =
                                            "Sending notification failed: "
                                                    + response.code()
                                                    + " "
                                                    + response.message();
                                    Log.e(TAG, message);
                                    Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                String message = "Sending notification failed: " + t.getMessage();
                                Log.e(TAG, message);
                                Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
                            }
                        });
    }

    public static AlertDialog createConnectDialog(
            String title,
            EditText roomEditText,
            DialogInterface.OnClickListener callParticipantsClickListener,
            DialogInterface.OnClickListener cancelClickListener,
            Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.video_chat_icon);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton("Connect", callParticipantsClickListener);
        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(roomEditText);
        return alertDialogBuilder.create();
    }

    private void handleRegistration(Intent intent) {
        String registrationError = intent.getStringExtra(REGISTRATION_ERROR);
        if (registrationError != null) {
            Toast.makeText(getApplicationContext(), REGISTRATION_ERROR, LENGTH_SHORT).show();
        } else {
            createLocalTracks();
            //identity = intent.getStringExtra(REGISTRATION_IDENTITY);
            identity = "5188059149";
//            HttpGetRequest httpRequest = new HttpGetRequest();
//            httpRequest.execute("http://34.73.16.73:8080/users/info?user_id=USER1");
//            try {
//                identity = httpRequest.get();
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
            accessToken = intent.getStringExtra(REGISTRATION_TOKEN);
            Toast.makeText(getApplicationContext(), "Registered", LENGTH_SHORT).show();
            if (cachedVideoNotificationIntent != null) {
                handleVideoNotificationIntent(cachedVideoNotificationIntent);
                cachedVideoNotificationIntent = null;
            }
        }
    }
}

