package com.example.assistgoandroid.Call;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.models.Contact;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.twilio.video.VideoView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    VideoView videoView;
    LocalAudioTrack localAudioTrack;
    LocalVideoTrack localVideoTrack;
    String accessToken;
    String tokenURL = "https://rackley-iguana-5070.twil.io/video-token";
    Room room;
    String roomName;
    boolean muted = false;
    boolean videoOn = false;
    private String frontCameraId = null;
    private String backCameraId = null;
    private final Camera1Enumerator camera1Enumerator = new Camera1Enumerator();
    private CameraCapturer cameraCapturer;
    private VideoSink localVideoView;
    private boolean disconnectedFromOnDestroy;


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
        videoView = findViewById(R.id.primary_video_view);


        contact = getIntent().getParcelableExtra("CONTACT_CARD");
        roomName = getIntent().getStringExtra("ROOM");
        Log.i(TAG, "Contact is " + contact);

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
            //if clicked for the first time
            if(!muted)
                mute();
            else
                unmute();
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

        // Create an audio track https://www.twilio.com/docs/video/android-getting-started#connect-to-a-room
        boolean enable = true;

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

    private void switchCamera() {
        //TODO implement method
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

    //https://www.twilio.com/blog/add-muting-unmuting-video-chat-app-30-seconds
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mute() {

        room.getLocalParticipant().getLocalAudioTracks().forEach(localAudioTrackPublication -> localAudioTrackPublication.getLocalAudioTrack().enable(false));
        muted = true;
    }

    //https://www.twilio.com/blog/add-muting-unmuting-video-chat-app-30-seconds
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unmute() {
        room.getLocalParticipant().getLocalAudioTracks().forEach(localAudioTrackPublication -> localAudioTrackPublication.getLocalAudioTrack().enable(true));
        muted = false;
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
                    }

                    @Override
                    public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {

                    }

                    @Override
                    public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {

                    }

                    @Override
                    public void onReconnected(@NonNull Room room) {

                    }

                    @Override
                    public void onDisconnected(@NonNull Room room, @Nullable TwilioException twilioException) {
                        localVideoTrack.release();
                    }

                    @Override
                    public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                        Log.i("Room.Listener", remoteParticipant.getIdentity() + " has joined the room.");
                        remoteParticipant.setListener(remoteParticipantListener());
                    }

                    @Override
                    public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                        Log.i("Room.Listener", remoteParticipant.getIdentity() + " has left the room.");

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

            }

            @Override
            public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
                videoView.setMirror(false);
                remoteVideoTrack.addSink(videoView);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {

            }

            @Override
            public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

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
        videoView.setMirror(true);
        localVideoTrack.addSink(videoView);
        localVideoView = videoView;
    }


}

