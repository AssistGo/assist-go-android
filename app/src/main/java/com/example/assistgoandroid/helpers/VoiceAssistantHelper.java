package com.example.assistgoandroid.helpers;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;

import com.example.assistgoandroid.MainActivity;
import com.example.assistgoandroid.callActivity;
import com.example.assistgoandroid.contactActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.homemessageActivity;
import com.example.assistgoandroid.settings.settingsActivity;
import com.example.assistgoandroid.translateActivity;

public class VoiceAssistantHelper {

    public static void parseInput(Context context, int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == 100 && resultCode == RESULT_OK){
            String resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            // TODO PARSE STRING HERE!!!!
            if (resultString.equalsIgnoreCase("go to the main screen")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the main page")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Page")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Screen")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Page")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Screen")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main screen")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main page")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main screen")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main page")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to main")) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency screen")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency page")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("i have an emergency")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Page")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Screen")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("ambulance")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("police")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("fire")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("firefighter")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency screen")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency page")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency screen")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency page")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to emergency")) {
                Intent intent = new Intent(context, emergencyActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to send a message")) {
                Intent intent = new Intent(context, homemessageActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Message")) {
                Intent intent = new Intent(context, homemessageActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(context, homemessageActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Send a message")) {
                Intent intent = new Intent(context, homemessageActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(context, homemessageActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translate")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translation")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me translation page")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation screen")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate screen")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate screen")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate page")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation screen")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation page")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate something")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate")) {
                Intent intent = new Intent(context, translateActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("call")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call someone")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call page")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call screen")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call page")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call screen")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call page")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call screen")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call page")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call screen")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I need to call")) {
                Intent intent = new Intent(context, callActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go settings")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings page")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings screen")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings screen")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings page")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting screen")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting page")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings screen")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings page")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting screen")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting page")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("setting")) {
                Intent intent = new Intent(context, settingsActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts page")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts screen")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact page")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact screen")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts page")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts screen")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact page")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact screen")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts page")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts screen")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact page")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact screen")) {
                Intent intent = new Intent(context, contactActivity.class);
                context.startActivity(intent);
            }
        }
    }
}
