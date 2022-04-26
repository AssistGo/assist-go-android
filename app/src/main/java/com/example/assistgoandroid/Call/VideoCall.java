package com.example.assistgoandroid.Call;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;
import com.example.assistgoandroid.models.Contact;
import com.twilio.video.Camera2Capturer;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tvi.webrtc.Camera2Enumerator;
import tvi.webrtc.VideoSink;



public class VideoCall extends AppCompatActivity implements Call {
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
    Room room;
    boolean muted = false;
    boolean videoOn = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_page);

        ImageView switchCameraBtn = findViewById(R.id.switchCamBtn);
        ImageView videochatBtn = findViewById(R.id.videochatBtn);
        ImageView muteBtn = findViewById(R.id.muteBtn);
        ImageView hangupBtn = findViewById(R.id.hangupBtn);
        videoView = findViewById(R.id.primary_video_view);


        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        Log.i(TAG, "Contact is " + contact);

        View.OnClickListener switchCameraClick = v -> {
            switchCamera();
        };

        View.OnClickListener videoChatClick = v -> {
            //todo turn off camera or turn on camera
            if(videoOn==false)
                turnVideOff();
            else
                turnVideoOn();
        };


        View.OnClickListener muteClick = v -> {
            //if clicked for the first time
            if(muted == false)
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
        LocalAudioTrack localAudioTrack = LocalAudioTrack.create(getApplicationContext(), enable);

        // A video track requires an implementation of a VideoCapturer. Here's how to use the front camera with a Camera2Capturer.
        Camera2Enumerator camera2Enumerator = new Camera2Enumerator(getApplicationContext());
        String frontCameraId = null;
        for (String cameraId : camera2Enumerator.getDeviceNames()) {
            if (camera2Enumerator.isFrontFacing(cameraId)) {
                frontCameraId = cameraId;
                break;
            }
        }
        if(frontCameraId != null) {
            // Create the CameraCapturer with the front camera
            CameraCapturer cameraCapturer = new CameraCapturer(getApplicationContext(), frontCameraId);

            // Create a video track
            LocalVideoTrack localVideoTrack = LocalVideoTrack.create(getApplicationContext(), enable, cameraCapturer);

            // Rendering a local video track requires an implementation of VideoSink
            // Let's assume we have added a VideoView in our view hierarchy
            VideoView videoView = (VideoView) findViewById(R.id.primary_video_view);

            // Render a local video track to preview your camera
            localVideoTrack.addSink(videoView);

            // Release the audio track to free native memory resources
            localAudioTrack.release();

            // Release the video track to free native memory resources
            localVideoTrack.release();
        }

        connectToRoom(accessToken);

    }

    private void switchCamera() {
        //TODO implement method
    }

    @Override
    public void turnOnSpeaker() {

    }

    @Override
    public void turnOffSpeaker() {

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
    @Override
    public void mute() {

        room.getLocalParticipant().getLocalAudioTracks().forEach(localAudioTrackPublication -> localAudioTrackPublication.getLocalAudioTrack().enable(false));
        muted = true;
    }

    //https://www.twilio.com/blog/add-muting-unmuting-video-chat-app-30-seconds
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void unmute() {
        room.getLocalParticipant().getLocalAudioTracks().forEach(localAudioTrackPublication -> localAudioTrackPublication.getLocalAudioTrack().enable(true));
        muted = false;
    }

    @Override
    public void hangup() {
        Date currentTime = Calendar.getInstance().getTime();
        CURRENT_TIME = currentTime.toString();
        contact.setLastCalled(CURRENT_TIME);

        ///Disconnect from a Room
        room.disconnect();

        finish();
    }

    //https://www.twilio.com/docs/video/android-getting-started#connect-to-a-room
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
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

            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {

            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {

            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {

            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {

            }
        };
    }

    //https://www.twilio.com/docs/video/android-getting-started#connect-to-a-room
    //The name of the Room specifies which Room you wish to join.
    // If a Room by that name does not already exist, it will be created upon connection.
    // If a Room by that name is already active, you'll be connected to the Room and receive notifications from any other Participants
    // also connected to the same Room. Room names must be unique within an account.
    public void connectToRoom(String roomName) {
        ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
                .roomName(roomName)
                .audioTracks((List<LocalAudioTrack>) localAudioTrack)
                .videoTracks((List<LocalVideoTrack>) localVideoTrack)
                .build();
        room = Video.connect(getApplicationContext(), connectOptions, new Room.Listener() {
            @Override
            public void onConnected(@NonNull Room room) {

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
        LocalParticipant localParticipant = room.getLocalParticipant();
        Log.i("LocalParticipant ", localParticipant.getIdentity());

        // Get the first participant from the room
        RemoteParticipant participant = room.getRemoteParticipants().get(0);
        Log.i("HandleParticipants", participant.getIdentity() + " is in the room.");
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


}


