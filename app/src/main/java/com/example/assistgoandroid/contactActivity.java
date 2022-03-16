package com.example.assistgoandroid;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.Contact.contactListAdapter;
import com.example.assistgoandroid.Contact.newContactCardActivity;
import java.util.ArrayList;
import java.util.List;

public class contactActivity extends AppCompatActivity {
    contactListAdapter adapter;
    RecyclerView rvContactList;
    SearchView searchView;
    private List<Contact> contactsList = new ArrayList<Contact>();
    static final String TAG = "ContactListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_page);

        searchView = findViewById(R.id.svContactSearch);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        rvContactList = findViewById(R.id.rvContacts);

        checkPermission();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvContactList.addItemDecoration(dividerItemDecoration);
    }

    private void filterList(String text) {
        List<Contact> filteredContactList = new ArrayList<Contact>();
        for(Contact contact : contactsList){
            if (contact.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredContactList.add(contact);
            }
        }
        if (filteredContactList.isEmpty()) {
            Toast.makeText(this, "No results found", Toast.LENGTH_LONG).show();
        }
        else {
            adapter.setFilterList(filteredContactList);
        }
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
                String photo = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);

                if (phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Contact contact = new Contact();
                    contact.setContactID(id);
                    contact.setName(name);
                    contact.setPhoneNumber(number);
                    contact.setContactPicture(photo);
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

    public void onNewContactClick(View view){
        Intent intent = new Intent(contactActivity.this, newContactCardActivity.class);
        startActivity(intent);
    }
}
