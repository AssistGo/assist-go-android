package com.example.assistgoandroid.Contact;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
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
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.contactActivity;
import com.example.assistgoandroid.qrScanPageActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;


public class newContactCardActivity extends AppCompatActivity {
    Bitmap mBitmap;
    EditText contactName, contactPhoneNumber;
    ImageView contactProfilePicture;

    private static final int GALLERY_PERMISSION_CODE = 101;
    private static final int ADD_PERMISSION_CODE = 103;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact_activity);

        contactName = findViewById(R.id.editContactName);
        contactPhoneNumber = findViewById(R.id.editContactPhoneNumber);
        contactProfilePicture = findViewById(R.id.ivContactProfilePicture);

        contactPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    public void onNewContactClick(View view) {
        if (contactName.getText().toString().isEmpty() ||
                contactPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermissionForNewContact();
            else
                addNewContact();
            //todo sync contact list with backend
        }
    }

    public void onCancelNewContactClick(View view) {
        Intent intent = new Intent(this, contactActivity.class);
        startActivity(intent);
    }

    public void addNewContact() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName.getText().toString())
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhoneNumber.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(mBitmap!=null){    // If an image is selected successfully
            mBitmap.compress(Bitmap.CompressFormat.PNG , 100, stream);

            // Adding insert operation to operations list
            // to insert Photo in the table ContactsContract.Data
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,stream.toByteArray())
                    .build());

            try {
                stream.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // Executing all the insert operations as a single database transaction
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Intent intent = new Intent(this, contactActivity.class);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
        }
        catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
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

    private void checkPermissionForNewContact(){
        if (ContextCompat.checkSelfPermission(newContactCardActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, ADD_PERMISSION_CODE);
        }
        else {
            addNewContact();
        }
    }

    // check permission to access gallery
    private void checkPermissionForGallery() {
        if (ContextCompat.checkSelfPermission(newContactCardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        }
        else {
            selectImageFromGallery();
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
        else if (requestCode == ADD_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                addNewContact();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void scanQRCodeListener(View view) {
        Intent intent = new Intent(this, qrScanPageActivity.class);
        startActivity(intent);
    }


    public void QRCodeListener(View view) {
        Intent intent = new Intent(this, qrScanPageActivity.class);
        startActivity(intent);
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            String resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            // TODO PARSE STRING HERE!!!!

        }
    }
}