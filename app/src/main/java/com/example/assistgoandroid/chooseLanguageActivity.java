package com.example.assistgoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Le Jie Bennett
 */
public class chooseLanguageActivity extends AppCompatActivity {
    SearchView searchLanguage;
    RecyclerView recyclerView;
    detectedSearchGoalAdapter detectAdapter;
    translatedSearchGoalAdapter translateAdapter;
    ImageButton closeChooseLanguage;
    String[] detectedLanguageList;
    String[] translatedLanguageList;
    boolean isInputLanguage = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooselanguage);

        Bundle bundle = getIntent().getExtras();
        isInputLanguage= bundle.getBoolean("isInputLanguage");

        //Gets the lists from string resource to populate the recyclerviews
        detectedLanguageList =  getResources().getStringArray(R.array.detected_language);
        translatedLanguageList =  getResources().getStringArray(R.array.translated_language);

        searchLanguage = findViewById(R.id.searchLanguage);

        recyclerView = findViewById(R.id.languageRV);

        setUpRecyclers();
        closeChooseLanguage = findViewById(R.id.closeChooseLanguage);
        closeChooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchLanguage.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filterT(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterT(s);
                return false;
            }
        });

    }

    /**
     * Filters the translated language list, used for the search view
      * @param text Substring to filter
     */
    private void filterT(String text) {
        ArrayList<String> filteredList = new ArrayList<>();
        if(isInputLanguage==true){
            for (String item : detectedLanguageList) {
                if (item.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            detectAdapter = new detectedSearchGoalAdapter();
            recyclerView.setAdapter(detectAdapter);
            detectAdapter.loadRV(filteredList);
        }
        else{
            for (String item : translatedLanguageList) {
                if (item.contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            translateAdapter = new translatedSearchGoalAdapter();
            recyclerView.setAdapter(translateAdapter);
            translateAdapter.loadRV(filteredList);
        }

    }

    private void setUpRecyclers() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        if(isInputLanguage==true){
            detectAdapter = new detectedSearchGoalAdapter();
            detectAdapter.loadRV(Arrays.asList(detectedLanguageList));
            recyclerView.setAdapter(detectAdapter);
            recyclerView.setHasFixedSize(true);
        }
        else{
            translateAdapter = new translatedSearchGoalAdapter();
            translateAdapter.loadRV(Arrays.asList(translatedLanguageList));
            recyclerView.setAdapter(translateAdapter);
            recyclerView.setHasFixedSize(true);
        }

    }

class detectedSearchGoalAdapter extends RecyclerView.Adapter{
    List<String> items;
    public detectedSearchGoalAdapter() {
        items = new ArrayList<>();
    }

    public void loadRV(List<String> list){
        items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new detectedSearchLanguageHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        detectedSearchLanguageHolder viewHolder = (detectedSearchLanguageHolder) holder;
        viewHolder.language.setText(items.get(position));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}

    class translatedSearchGoalAdapter extends RecyclerView.Adapter{
        List<String> items;
        public translatedSearchGoalAdapter() {
            items = new ArrayList<>();
        }

        public void loadRV(List<String> list){
            items.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new translateSearchLanguageHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
           translateSearchLanguageHolder viewHolder = (translateSearchLanguageHolder) holder;
            viewHolder.language.setText(items.get(position));
        }


        @Override
        public int getItemCount() {
            return items.size();
        }
    }


class detectedSearchLanguageHolder extends RecyclerView.ViewHolder {

    TextView language;

    public detectedSearchLanguageHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language_translate, parent, false));
        language = (TextView) itemView.findViewById(R.id.languageName);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("selected", "onClick: " + language.getText().toString());
                Intent i = new Intent(chooseLanguageActivity.this, translateActivity.class);
                i.putExtra("isInputLanguage",true);
                i.putExtra("language",language.getText().toString());

                setResult(RESULT_OK,i);
                finish();

            }
        });
    }
}

    class translateSearchLanguageHolder extends RecyclerView.ViewHolder {

        TextView language;

        public translateSearchLanguageHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language_translate, parent, false));
            language = (TextView) itemView.findViewById(R.id.languageName);
            language.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("selected", "onClick: " + language.getText().toString());

                    Intent i = new Intent(chooseLanguageActivity.this, translateActivity.class);
                    i.putExtra("isInputLanguage",false);
                    i.putExtra("language",language.getText().toString());

                    setResult(RESULT_OK,i);
                    finish();

                }
            });
        }
    }
}


