package com.example.assistgoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.assistgoandroid.call.callAdapter;
import com.example.assistgoandroid.helpers.LocalDatabaseHelper;
import com.example.assistgoandroid.helpers.TimeFormatter;
import com.example.assistgoandroid.helpers.VoiceAssistantHelper;
import com.example.assistgoandroid.settings.settingsActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class callActivity extends AppCompatActivity {

    LocalDatabaseHelper localDatabaseHelper;

    callAdapter adapter;
    RecyclerView rvCalledList;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Contact> calledList = new ArrayList<Contact>();
    static final String TAG = "callActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_page);

        localDatabaseHelper = LocalDatabaseHelper.getInstance(this);

        rvCalledList = findViewById(R.id.rvContacts);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//       rvCalledList.addItemDecoration(dividerItemDecoration);

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

        calledList.addAll(localDatabaseHelper.getAllContacts());

        // set the recycler view
        rvCalledList.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(calledList, Contact.LastCalledComparator);
        adapter = new callAdapter(this, calledList);
        rvCalledList.setAdapter(adapter);
    }

    public static String getFormattedTimestamp(String time) {
        return TimeFormatter.getTimeDifference(time);
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

        VoiceAssistantHelper.parseInput(this, requestCode, resultCode, data);
    }
}
