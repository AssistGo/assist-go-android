package com.example.assistgoandroid.emergency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.assistgoandroid.R;

import java.util.ArrayList;
import java.util.Locale;

public class emergencyActivity extends AppCompatActivity {
    ListView listView_em;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_page_layout);

        //Set up the cells and search bar for settings page
        String[] title =  getResources().getStringArray(R.array.emergency_titles);
        int[] images = {R.drawable.image_am, R.drawable.image_police, R.drawable.image_fire};

        listView_em = findViewById(R.id.listView_em);

        // now create an adapter class
        MyAdapter adapterem = new MyAdapter(this, title, images);
        listView_em.setAdapter(adapterem);
        //onclick listener for each cell
        listView_em.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    //Intent intent = new Intent(emergencyActivity.this, ambulanceActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, EmergencyDialerActivity.class);
                    intent.putExtra("EMERGENCY_SERVICE", "AMBULANCE");
                    startActivity(intent);
                }
                if (position ==  1) {
                    //Intent intent = new Intent(emergencyActivity.this, policeActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, EmergencyDialerActivity.class);
                    intent.putExtra("EMERGENCY_SERVICE", "POLICE");

                    startActivity(intent);
                }
                if (position ==  2) {
                    //Intent intent = new Intent(emergencyActivity.this, fiirefighterActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, EmergencyDialerActivity.class);
                    intent.putExtra("EMERGENCY_SERVICE", "FIREFIGHTER");
                    startActivity(intent);
                }
                if (position ==  3) {
                    //Intent intent = new Intent(emergencyActivity.this, callActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, EmergencyDialerActivity.class);
                    startActivity(intent);
                }

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

        if(requestCode == 100 && resultCode == RESULT_OK){
            String resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            // TODO PARSE STRING HERE!!!!

        }
    }
}
class MyAdapter extends ArrayAdapter<String> {

    Context context;
    String[] title;
    int[] imgs;

    MyAdapter (Context c, String[] title, int[] imgs) {
        super(c, R.layout.emergy_page_cell, R.id.textView_em1, title);
        this.context = c;
        this.title = title;
        this.imgs = imgs;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.emergy_page_cell, parent, false);
        ImageView images = row.findViewById(R.id.image);
        TextView myTitle = row.findViewById(R.id.textView_em1);

        // now set our images and title of each cell on the view
        images.setImageResource(imgs[position]);
        myTitle.setText(title[position]);

        return row;
    }
}


