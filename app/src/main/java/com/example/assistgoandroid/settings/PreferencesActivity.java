package com.example.assistgoandroid.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.MainActivity;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.callActivity;
import com.example.assistgoandroid.contactActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.helpers.VoiceAssistantHelper;
import com.example.assistgoandroid.homemessageActivity;
import com.example.assistgoandroid.translateActivity;

public class PreferencesActivity extends AppCompatActivity {
    ListView listView;
    Button deleteAccountButton;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_page_layout);
        //Set up the cells and search bar for settings page
        String[] title =  getResources().getStringArray(R.array.preferences_switchers_titles);
        listView = findViewById(R.id.preferencesListView);
        deleteAccountButton = findViewById(R.id.deleteAccount);
        // now create an adapter class
        PreferencesActivity.MyAdapter adapter = new PreferencesActivity.MyAdapter(this, title);
        listView.setAdapter(adapter);

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog();
                deleteAccountDialog.show(getSupportFragmentManager(),"delete account");
            }
        });
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

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String[] title;

        MyAdapter(Context c, String[] title) {
            super(c, R.layout.preferences_page_cell1, R.id.switcher_title, title);
            this.context = c;
            this.title = title;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.preferences_page_cell1, parent, false);
            TextView myTitle = row.findViewById(R.id.switcher_title);

            //title of each cell on the view
            myTitle.setText(title[position]);

            return row;
        }
    }
}