package com.example.assistgoandroid.Contact;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import com.example.assistgoandroid.models.Contact;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class editContactCardActivity extends AppCompatActivity implements deleteContactDialog.deleteContactDialogListener{
    String TAG = "EditContact";
    Contact contact;
    Bitmap mBitmap;

    private static final int GALLERY_PERMISSION_CODE = 101;

    // These two permissions are basically the same permissions
    // but I am using this approach to differentiate the operations
    // for delete/contact since they have different functionalities
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

        contact = getIntent().getParcelableExtra("CONTACT_CARD");

        Log.i(TAG, contact.toString());

        contactName.setText(contact.getFullName());
        contactPhoneNumber.setText(contact.getPhoneNumber());

        Glide.with(this)
                .load(contact.getProfileImageUrl())
                .centerCrop()
                .override(400, 400)
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(500,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(contactProfilePicture);

        contactPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    // Ask for permission for gallery
    public void onChangePictureCall(View view){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissionForGallery();
        else
            selectImageFromGallery();
    }

    // Open Gallery and let the user select an image
    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

    // Process the image selected by the user from gallery
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if(data != null) {
                        Uri selectedImage = data.getData();
                        InputStream imageStream = null;

                        try {
                            // Getting InputStream of the selected image
                            imageStream = getContentResolver().openInputStream(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // Creating bitmap of the selected image from its input stream
                        mBitmap = BitmapFactory.decodeStream(imageStream);
                        // Change image view to picked image for display
                        contactProfilePicture.setImageBitmap(mBitmap);
                    }
                }
            });

    // Apply changes on contact when save button is clicked
    public void onSaveChangesCall(View view) {
        if (contactName.getText().toString().isEmpty() ||
                contactPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
        else if (contact.getPhoneNumber().equals(contactPhoneNumber.getText().toString().trim())
                && contact.getFullName().equals(contactName.getText().toString().trim())
                && mBitmap == null) {

            // No changes so go back with no updates
            Intent intent = new Intent(this, contactActivity.class);
            startActivity(intent);
            Toast.makeText(this, "No changes have been made for " + contact.getFullName(), Toast.LENGTH_SHORT).show();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermissionForEdit();
            else
                editContact();
        }
    }

    public void editContact() {

        // Change phone number
        if(!contact.getPhoneNumber().equals(contactPhoneNumber.getText().toString().trim())) {
            contact.setPhoneNumber(contactPhoneNumber.getText().toString().trim());
            changePhoneNumber(getContentResolver());
        }

        // Change contact name
        if(!contact.getFullName().equals(contactName.getText().toString().trim())) {
            contact.setFullName(contactName.getText().toString().trim());
            changeName(getContentResolver());
        }

        // Change contact picture
        if(mBitmap != null) {
            try {
                changeProfilePicture(getContentResolver());
            } catch (Exception e) {
                Log.e(TAG, e + "");
            }
        }

        // Go back to contact list
        Intent intent = new Intent(this, contactActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Contact " + contact.getFullName() + " has been updated.", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Contact " + contact.getFullName() + " has been changed: " + contact);

        //todo sync changes with the backend
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

        contactHelper.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    private void changeName(ContentResolver contactHelper) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getFullName());

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append("=");
        whereClauseBuf.append(contact.getContactID());

        // Specify the row data mimetype to name mimetype
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");

        // Update name info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        contactHelper.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    private void changeProfilePicture(ContentResolver contactHelper) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG , 100, stream);

        Uri rawContactUri = null;
        Cursor rawContactCursor = managedQuery(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[] {
                        ContactsContract.RawContacts._ID
                },
                ContactsContract.RawContacts.CONTACT_ID + " = " + contact.getContactID(),
                null,
                null);
        if (!rawContactCursor.isAfterLast()) {
            rawContactCursor.moveToFirst();
            rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendPath("" + rawContactCursor.getLong(0)).build();
        }
        rawContactCursor.close();

        ContentValues values = new ContentValues();
        int photoRow = -1;
        String whereFilter = ContactsContract.Data.RAW_CONTACT_ID + " == " +
                ContentUris.parseId(rawContactUri) + " AND " + ContactsContract.Data.MIMETYPE + "=='" +
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
        Cursor cursor = managedQuery(
                ContactsContract.Data.CONTENT_URI,
                null,
                whereFilter,
                null,
                null);
        int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);
        if (cursor.moveToFirst()) {
            photoRow = cursor.getInt(idIdx);
        }

        cursor.close();

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, ContentUris.parseId(rawContactUri));
        values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray());
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withValues(values).build());

        if (photoRow >= 0) {
            contactHelper.update(
                    ContactsContract.Data.CONTENT_URI,
                    values,
                    ContactsContract.Data._ID + " = " + photoRow, null);
        } else {
            contactHelper.insert(
                    ContactsContract.Data.CONTENT_URI,
                    values);
        }
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, e + "");
        }

        // Flush stream
        try {
            stream.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete contact
    public void onDeleteContactCall(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissionForDelete();
        else
            openDialog();
            //deleteContact();
    }

    @Override
    public void deleteContact() {

        // Data table content process uri.
        Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

        // Create data table where clause.
        StringBuffer dataWhereClauseBuf = new StringBuffer();
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        dataWhereClauseBuf.append(" = ");
        dataWhereClauseBuf.append(contact.getContactID());

        // Delete all this contact related data in data table.
        getContentResolver().delete(dataContentUri, dataWhereClauseBuf.toString(), null);

        Log.i("EditContact", "Contact " + contact.getFullName() + " deleted.");

        // Go back to contact list
        Intent intent = new Intent(this, contactActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Contact " + contact.getFullName() + " deleted.", Toast.LENGTH_SHORT).show();
    }

    public void openDialog() {
        deleteContactDialog deleteContactDialog = new deleteContactDialog();
        deleteContactDialog.show(getSupportFragmentManager(), "deleteContactDialog");
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

    // Check permission to delete
    private void checkPermissionForDelete(){
        if (ContextCompat.checkSelfPermission(editContactCardActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, DELETE_PERMISSION_CODE);
        }
        else {
            openDialog();//deleteContact();
        }
    }

    // Check permission to edit
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
                openDialog(); //deleteContact();
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
