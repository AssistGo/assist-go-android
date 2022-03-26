package com.example.assistgoandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Le Jie Bennett
 * Notes:
 * Need to add the translation feature that actually translates the inputted text
 * Also need to update the lists in Strings.xml depending on the translation API
 */
public class translateActivity extends AppCompatActivity {
    TextInputEditText translatePhraseInput;
    TextView translatedOutput;
    Button inputLanguageBtn, outputLanguageBtn;
    LinearLayout translateButtonsLayout;

    String[] detectedLanguageList;
    String[] translatedLanguageList;

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData().getBooleanExtra("isInputLanguage", true) == true) {
                            inputLanguageBtn.setText(result.getData().getStringExtra("language"));
                        } else {
                            outputLanguageBtn.setText(result.getData().getStringExtra("language"));

                        }
                    }
                }
            });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_page);

        translateButtonsLayout = findViewById(R.id.translateButtonsLayout);
        translatePhraseInput = findViewById(R.id.translatePhraseInput);
        translatedOutput = findViewById(R.id.translatedOutput);
        outputLanguageBtn = findViewById(R.id.outputLanguageBtn);

        translatePhraseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Updates the translation ouput text field
                //Will need to add the actual translation function here
                translatedOutput.setText(editable.toString());

            }
        });


        outputLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(translateActivity.this, chooseLanguageActivity.class);
                intent.putExtra("isInputLanguage", false);
                activityResultLaunch.launch(intent);
            }
        });
        inputLanguageBtn = findViewById(R.id.inputLanguageBtn);
        inputLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(translateActivity.this, chooseLanguageActivity.class);
                intent.putExtra("isInputLanguage", true);
                activityResultLaunch.launch(intent);
            }
        });
    }
}


