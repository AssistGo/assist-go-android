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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class editContactCardActivity extends AppCompatActivity {
    String TAG = "EditContact";
    Contact contact, temp;

    private static final int GALLERY_PERMISSION_CODE = 101;
    private static final int DELETE_PERMISSION_CODE = 102;
    private static final int EDIT_PERMISSION_CODE = 103;


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
            checkPermissionForGallery();
        else
            selectImageFromGallery();
    }

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

    // Apply changes on contact
    public void onSaveChangesCall(View view) {
        //todo https://www.youtube.com/watch?v=sW0xia1E7yw&t=777s honestly just use setters on contact that's it
        if (contactName.getText().toString().isEmpty() ||
            contactPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
        else {
            checkPermissionForEdit();

            contact.setName(contactName.getText().toString().trim());
            contact.setPhoneNumber(contactPhoneNumber.getText().toString().trim());

        }
    }

    public void editContact() {

    }

    // Delete contact
    public void onDeleteContactCall(View view) {
        //todo
        checkPermissionForDelete();
    }

    public void deleteContact() {

    }

    // check permission to access gallery
    private void checkPermissionForGallery() {
        if (ContextCompat.checkSelfPermission(editContactCardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        }
        else {
            selectImageFromGallery();
        }
    }

    private void checkPermissionForDelete(){
        if (ContextCompat.checkSelfPermission(editContactCardActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, DELETE_PERMISSION_CODE);
        }
        else {
            Log.i("EditContact", "Delete permission given");
            deleteContact();
        }
    }

    private void checkPermissionForEdit(){
        if (ContextCompat.checkSelfPermission(editContactCardActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, EDIT_PERMISSION_CODE);
        }
        else {
            Log.i("EditContact", "Edit permission given");
            editContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImageFromGallery();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == DELETE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                deleteContact();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == EDIT_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                editContact();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}
