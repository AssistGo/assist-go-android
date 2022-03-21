package com.example.assistgoandroid.Contact;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
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
import com.example.assistgoandroid.contactActivity;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class editContactCardActivity extends AppCompatActivity {
    String TAG = "EditContact";
    Contact contact, temp;
    String selectedImage;

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
                    if(data != null) {
                        selectedImage = String.valueOf(data.getData());
                        Glide.with(this)
                                .load(data.getData())
                                .centerCrop()
                                .override(400, 400)
                                .fitCenter() // scale to fit entire image within ImageView
                                .transform(new RoundedCornersTransformation(500, 10))
                                .into(contactProfilePicture);
                    }
                }
            });

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

    // Apply changes on contact
    public void onSaveChangesCall(View view) {
        if (contactName.getText().toString().isEmpty() ||
            contactPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermissionForEdit();
            else
                editContact();

        }
    }

    //Source: https://www.dev2qa.com/how-to-update-delete-android-contacts-programmatically/
    public void editContact() {

        if(!contact.getPhoneNumber().equals(contactPhoneNumber.getText().toString().trim())) {
            contact.setPhoneNumber(contactPhoneNumber.getText().toString().trim());
            changePhoneNumber(getContentResolver());
        }

        if(!contact.getName().equals(contactName.getText().toString().trim())) {
            contact.setName(contactName.getText().toString().trim());
            changeName(getContentResolver());
        }

        //todo Null reference error
//        if(!contact.getContactPicture().equals(selectedImage)) {
//            contact.setContactPicture(selectedImage);
//            changeProfilePicture(getContentResolver());
//        }

        Log.i("EditContact", "Contact " + contact.getName() + " has been changed: " + contact);

        // Go back to contact list
        Intent intent = new Intent(this, contactActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Contact " + contact.getName() + " has been updated.", Toast.LENGTH_SHORT).show();
    }

    private void changePhoneNumber(ContentResolver contactHelper) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber());

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append("=");
        whereClauseBuf.append(contact.getContactID());

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");

        Log.i(TAG, ContactsContract.Data.MIMETYPE);
        Log.i(TAG, mimetype);

        // Specify phone type.
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE);
        whereClauseBuf.append(" = ");
        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        // Get update data count.
        contactHelper.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    //todo doesnt work https://stackoverflow.com/questions/6465905/how-to-update-e-mail-address-nickname-etc-of-a-contact-in-android/6530159#6530159
    private void changeName(ContentResolver contactHelper) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName());

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append("=");
        whereClauseBuf.append(contact.getContactID());

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        // Get update data count.
        contactHelper.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    private void changeProfilePicture(ContentResolver contactHelper) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.CommonDataKinds.Phone.PHOTO_URI, contact.getContactPicture());

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append("=");
        whereClauseBuf.append(contact.getContactID());

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        // Get update data count.
        contactHelper.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    // Delete contact
    public void onDeleteContactCall(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissionForDelete();
        else
            deleteContact(getContentResolver());
    }

    //Source: https://www.dev2qa.com/how-to-update-delete-android-contacts-programmatically/
    public void deleteContact(ContentResolver contactHelper) {

        // Data table content process uri.
        Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

        // Create data table where clause.
        StringBuffer dataWhereClauseBuf = new StringBuffer();
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        dataWhereClauseBuf.append(" = ");
        dataWhereClauseBuf.append(contact.getContactID());

        // Delete all this contact related data in data table.
        contactHelper.delete(dataContentUri, dataWhereClauseBuf.toString(), null);

        Log.i("EditContact", "Contact " + contact.getName() + " deleted.");

        // Go back to contact list
        Intent intent = new Intent(this, contactActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Contact " + contact.getName() + " deleted.", Toast.LENGTH_SHORT).show();
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
            deleteContact(getContentResolver());
        }
    }

    private void checkPermissionForEdit(){
        if (ContextCompat.checkSelfPermission(editContactCardActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, EDIT_PERMISSION_CODE);
        }
        else {
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
                deleteContact(getContentResolver());
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
