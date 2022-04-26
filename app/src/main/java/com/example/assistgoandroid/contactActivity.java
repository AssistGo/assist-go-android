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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.assistgoandroid.Helpers.LocalDatabaseHelper;
import com.example.assistgoandroid.models.Contact;
import com.example.assistgoandroid.Contact.contactListAdapter;
import com.example.assistgoandroid.Contact.newContactCardActivity;
import com.example.assistgoandroid.models.User;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class contactActivity extends AppCompatActivity {

    LocalDatabaseHelper localDatabaseHelper;

    contactListAdapter adapter;
    RecyclerView rvContactList;
    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Contact> contactsList = new ArrayList<>();
    static final String TAG = "ContactListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_page);

        localDatabaseHelper = LocalDatabaseHelper.getInstance(this);

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

        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                populateContactList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void filterList(String text) {
        List<Contact> filteredContactList = new ArrayList<Contact>();
        for(Contact contact : contactsList){
            if (contact.getFullName().toLowerCase().contains(text.toLowerCase())) {
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
        contactsList.clear();
        //Initialize uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //sort by asc
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
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

                    if (avoidDuplicates(id)){
                        Contact contact = new Contact();
                        contact.setContactID(id);
                        contact.setFullName(name);
                        contact.setPhoneNumber(number);
                        contact.setProfileImageUrl(photo);
                        localDatabaseHelper.addOrUpdateContact(contact);
                        contactsList.add(contact);
                    }
                    phoneCursor.close();
                }
            }
            cursor.close();
        }

        rvContactList.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(contactsList, Contact.ContactComparator);
        Collections.sort(contactsList, Contact.FavoritesComparator);
        adapter = new contactListAdapter(this, contactsList);
        rvContactList.setAdapter(adapter);

//        MainActivity.user.getUserAsJsonMap();
//        MainActivity.user.setContactList(contactsList);
//        MainActivity.user.syncUser();
    }

    private boolean avoidDuplicates(String id){
        for (Contact c : contactsList) {
            if (c.getContactID().equals(id))
                return false;
        }
        return true;
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
