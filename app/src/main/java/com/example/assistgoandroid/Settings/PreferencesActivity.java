package com.example.assistgoandroid.Settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;

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