package com.example.assistgoandroid;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.assistgoandroid.Call.callAdapter;
import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.Contact.contactListAdapter;
import com.example.assistgoandroid.Helpers.TimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class callActivity extends AppCompatActivity {

    callAdapter adapter;
    RecyclerView rvCalledList;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Contact> calledList = new ArrayList<Contact>();
    static final String TAG = "callActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_page);

        rvCalledList = findViewById(R.id.rvContacts);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvCalledList.addItemDecoration(dividerItemDecoration);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                populateCallList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // import called contacts
    private void populateCallList() {
        // rearrange the rv
        calledList.clear();

        //todo: do the importing stuff
        //Contact contact;
        //calledList.add(contact);

        // set the recycler view
        rvCalledList.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(calledList, Contact.LastCalledComparator);
        adapter = new callAdapter(this, calledList);
        rvCalledList.setAdapter(adapter);
    }

    public static String getFormattedTimestamp(String time) {
        return TimeFormatter.getTimeDifference(time);
    }
}
