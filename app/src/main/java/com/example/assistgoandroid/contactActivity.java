package com.example.assistgoandroid;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.Contact.contactListAdapter;
import java.util.ArrayList;

public class contactActivity extends AppCompatActivity {
    contactListAdapter adapter;
    RecyclerView rvContactList;
    private ArrayList<Contact> contactsList = new ArrayList<Contact>();
    static final String TAG = "ContactListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_page);

        rvContactList = findViewById(R.id.rvContacts);

        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(contactActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            //Request Permission
            ActivityCompat.requestPermissions(contactActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        else {
            populateContactList();
        }

    }

    private void populateContactList() {
        //Initialize uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //sort by asc
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";
        //initialize cursor
        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0){

            while(cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);

                if (phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setName(number);
                    contactsList.add(contact);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        rvContactList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new contactListAdapter(this, contactsList);
        rvContactList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            populateContactList();
        }
        else {
            Toast.makeText(contactActivity.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}
