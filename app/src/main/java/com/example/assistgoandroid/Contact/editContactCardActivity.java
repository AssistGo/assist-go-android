package com.example.assistgoandroid.Contact;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class editContactCardActivity extends AppCompatActivity {
    String TAG = "EditContact";
    Contact contact, temp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact_activity);

        ImageView contactProfilePicture = findViewById(R.id.ivContactProfilePicture);
        EditText contactName = findViewById(R.id.editContactName);
        EditText contactPhoneNumber = findViewById(R.id.editContactPhoneNumber);

        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        temp = contact;

        Log.i(TAG, contact.toString());

        contactName.setText(contact.getName());
        contactPhoneNumber.setText(contact.getPhoneNumber());

        Glide.with(this)
                .load(contact.getContactPicture())
                .centerCrop()
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(500,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(contactProfilePicture);
    }

    // Ask for permission for gallery
    public void onChangePictureCall(View view){
        //todo https://www.youtube.com/watch?v=O6dWwoULFI8
    }

    // check permission to access gallery
    private void checkPermission() {
        //todo
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //todo
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
