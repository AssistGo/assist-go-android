package com.example.assistgoandroid.Contact;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.contactActivity;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class editContactCardActivity extends AppCompatActivity {
    String TAG = "EditContact";
    Contact contact, temp;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    ImageView contactProfilePicture;
    EditText contactName, contactPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact_activity);

        contactProfilePicture = findViewById(R.id.ivContactProfilePicture);
        contactName = findViewById(R.id.editContactName);
        contactPhoneNumber = findViewById(R.id.editContactPhoneNumber);

        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        temp = contact;

        Log.i(TAG, contact.toString());

        contactName.setText(contact.getName());
        contactPhoneNumber.setText(contact.getPhoneNumber());

        Glide.with(this)
                .load(contact.getContactPicture())
                .centerCrop()
                .override(400, 400)
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(500,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(contactProfilePicture);
    }

    // Ask for permission for gallery
    public void onChangePictureCall(View view){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission();
        else
            selectImageFromGallery();
    }

    // OnActivityResult method is deprecated fix: https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
//    private void selectImageFromGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, IMAGE_PICK_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
//            if(data != null)
//                contactProfilePicture.setImageURI(data.getData());
//        }
//    }

    // new way
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if(data != null)
                        //contactProfilePicture.setImageURI(data.getData());
                        Glide.with(this)
                                .load(data.getData())
                                .centerCrop()
                                .override(400, 400)
                                .fitCenter() // scale to fit entire image within ImageView
                                .transform(new RoundedCornersTransformation(500,10))
                                .into(contactProfilePicture);
                }
            });

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

    // check permission to access gallery
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(editContactCardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
        else {
            selectImageFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImageFromGallery();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Apply changes on contact
    public void onSaveChangesCall(View view) {
        //todo https://www.youtube.com/watch?v=sW0xia1E7yw&t=777s honestly just use setters on contact that's it
    }

    // Delete contact
    public void onDeleteContactCall(View view) {
        //todo https://www.youtube.com/watch?v=uS7cVb9WBNA
    }
}
