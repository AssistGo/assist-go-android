package com.example.assistgoandroid.Contact;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dynamsoft.dce.CameraEnhancer;
import com.dynamsoft.dce.CameraEnhancerException;
import com.dynamsoft.dce.DCECameraView;
import com.dynamsoft.dce.DCEFrameListener;
import com.example.assistgoandroid.R;

public class newContactQRCodeActivity extends AppCompatActivity {
    CameraEnhancer cameraEnhancer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact_qrcode_activity);
        DCECameraView previewView = findViewById(R.id.dce_viewFinder);

        cameraEnhancer = new CameraEnhancer(this);
        cameraEnhancer.setCameraView(previewView);
        cameraEnhancer.addListener((DCEFrameListener) this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            cameraEnhancer.open();
        } catch (CameraEnhancerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            cameraEnhancer.close();
        } catch (CameraEnhancerException e) {
            e.printStackTrace();
        }
    }
}