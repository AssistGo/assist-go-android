package com.example.assistgoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.settings.settingsActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Le Jie Bennett
 * Notes:
 * Need to add the translation feature that actually translates the inputted text
 * Also need to update the lists in Strings.xml depending on the translation API
 * https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes for language codes
 * https://www.tutorialspoint.com/how-to-set-delay-in-android
 */
public class translateActivity extends AppCompatActivity {
    TextInputEditText translatePhraseInput;
    TextView translatedOutput;
    Button inputLanguageBtn, outputLanguageBtn;
    LinearLayout translateButtonsLayout;
    Button translateBtn;

    String[] codedTranslatedLanguageList;
    String[] translatedLanguageList;
    HashMap<String,String> languageToCoded = new HashMap<>();
    String inputLanguageCode = "DetectedLanguage";
    String outputLanguageCode = "English";

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData().getBooleanExtra("isInputLanguage", true) == true) {
                            inputLanguageBtn.setText(result.getData().getStringExtra("language"));

                            if(result.getData().getStringExtra("language")=="Detected Language")
                                ///Use API to figure out language code
                                Log.d("translateActivity", "onActivityResult: detected language");

                            else{
                                inputLanguageCode = languageToCoded.get(result.getData().getStringExtra("language"));
                                Log.d("translateActivity", inputLanguageCode);
                            }
                        } else {
                            outputLanguageBtn.setText(result.getData().getStringExtra("language"));
                            outputLanguageCode = languageToCoded.get(result.getData().getStringExtra("language"));
                            Log.d("translateActivity", outputLanguageCode);

                        }
                    }
                }
            });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_page);

        translateButtonsLayout = findViewById(R.id.translateButtonsLayout);
        translatePhraseInput = findViewById(R.id.translatePhraseInput);
        translatedOutput = findViewById(R.id.translatedOutput);
        outputLanguageBtn = findViewById(R.id.outputLanguageBtn);
        translateBtn = findViewById(R.id.translateBtn);

        // Used to convert to language code
        createLanguageHashmap();

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

        translateBtn.setOnClickListener(view -> {
            String url = "/translation/translate/" + inputLanguageCode + "/" + outputLanguageCode;
            ObjectMapper mapper = new ObjectMapper();

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", translatePhraseInput.toString());

                OkHttpClient client = new OkHttpClient();
                MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

                Request request = new Request.Builder()
                        .url("http://192.168.1.148:8080/translation/translate/" + inputLanguageCode + "/" + outputLanguageCode)
                        .post(RequestBody.create(JSON_TYPE, jsonObject.toString()))
                        .build();
                Response response = client.newCall(request).execute();
                String jsonDataString = null;
                try {
                    jsonDataString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Map<String, ?> responseJson = mapper.readValue(jsonDataString, Map.class);
                translatedOutput.setText((CharSequence) responseJson.get("translation"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void createLanguageHashmap(){
        translatedLanguageList =  getResources().getStringArray(R.array.translated_language);
        codedTranslatedLanguageList = getResources().getStringArray(R.array.coded_translated_language);
        for (int i = 0; i < translatedLanguageList.length; i++) {
            languageToCoded.put(translatedLanguageList[i], codedTranslatedLanguageList[i]);
        }

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
            if (resultString.equalsIgnoreCase("go to the main screen")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the main page")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Page")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Screen")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Page")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Screen")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main screen")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main page")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main screen")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main page")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to main")) {
                Intent intent = new Intent(translateActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency screen")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency page")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("i have an emergency")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Page")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Screen")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("ambulance")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("police")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("fire")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("firefighter")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency screen")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency page")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency screen")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency page")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to emergency")) {
                Intent intent = new Intent(translateActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to send a message")) {
                Intent intent = new Intent(translateActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Message")) {
                Intent intent = new Intent(translateActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(translateActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Send a message")) {
                Intent intent = new Intent(translateActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(translateActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translate")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translation")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me translation page")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation screen")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate screen")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate screen")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate page")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation screen")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation page")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate something")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate")) {
                Intent intent = new Intent(translateActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("call")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call someone")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call page")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call screen")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call page")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call screen")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call page")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call screen")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call page")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call screen")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I need to call")) {
                Intent intent = new Intent(translateActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go settings")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings page")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings screen")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings screen")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings page")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting screen")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting page")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings screen")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings page")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting screen")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting page")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("setting")) {
                Intent intent = new Intent(translateActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts page")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts screen")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact page")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact screen")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts page")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts screen")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact page")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact screen")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts page")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts screen")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact page")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact screen")) {
                Intent intent = new Intent(translateActivity.this, contactActivity.class);
                startActivity(intent);
            }
        }
    }
}


