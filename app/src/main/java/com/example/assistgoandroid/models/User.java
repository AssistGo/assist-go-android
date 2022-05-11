package com.example.assistgoandroid.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class User {

    private Context userContext;

    private String id;
    private String country;
    private String countryCode;
    private String phoneNumber;
    private String fullPhoneNumber;
    private String fullName;
    private String profileImageUrl;
    private List<Contact> contactList;
    private List<CallRecord> callRecordList;
    private boolean hasSimCard;
    private String TAG = "User";


    @SuppressLint("MissingPermission")
    public User() {
//        TelephonyManager telMgr = (TelephonyManager) userContext.getSystemService(Context.TELEPHONY_SERVICE);

        this.id = generateRandomId();
//        this.country = telMgr.getNetworkCountryIso();
        this.country = "us";
//        this.countryCode = telMgr.getLine1Number().substring(0, telMgr.getLine1Number().length() - 10);
        this.countryCode = "+1";
//        this.phoneNumber = telMgr.getLine1Number().substring(telMgr.getLine1Number().length() - 10);
        this.phoneNumber = "5188059149";
//        this.fullPhoneNumber = telMgr.getLine1Number();
        this.fullPhoneNumber = "+15188059149";
        this.fullName = "New User";
        this.profileImageUrl = "https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg";
        this.callRecordList = new ArrayList<CallRecord>();
        this.contactList = new ArrayList<Contact>();

//        int simState = telMgr.getSimState();

        this.hasSimCard = true;
//        if (simState == TelephonyManager.SIM_STATE_READY) {
//            this.hasSimCard = true;
//        } else {
//            this.hasSimCard = false;
//        }

    }

    @SuppressLint("MissingPermission")
    public User(String country, String countryCode, String phoneNumber, String fullPhoneNumber) {

        TelephonyManager telMgr = (TelephonyManager) userContext.getSystemService(Context.TELEPHONY_SERVICE);

        this.id = generateRandomId();
        this.country = country;
        this.countryCode = telMgr.getLine1Number().substring(0, telMgr.getLine1Number().length() - 10);
        this.phoneNumber = telMgr.getLine1Number().substring(telMgr.getLine1Number().length() - 10);

        this.fullPhoneNumber = telMgr.getLine1Number();
        this.fullName = "New User";
        this.profileImageUrl = "https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg";
        int simState = telMgr.getSimState();

        if (simState == TelephonyManager.SIM_STATE_READY) {
            this.hasSimCard = true;
        } else {
            this.hasSimCard = false;
        }
        this.callRecordList = new ArrayList<CallRecord>();
        this.contactList = new ArrayList<Contact>();
    }

    public User(String id, String country, String countryCode, String phoneNumber, String fullPhoneNumber, String fullName, String profileImageUrl, List<Contact> contactList, List<CallRecord> callRecordList, boolean hasSimCard) {
        this.id = id;
        this.country = country;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.fullPhoneNumber = fullPhoneNumber;
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
        this.contactList = contactList;
        this.callRecordList = callRecordList;
        this.hasSimCard = hasSimCard;
    }

    public String generateRandomId() {
//        byte[] array = new byte[16];
//        new Random().nextBytes(array);
//        return new String(array, Charset.forName("UTF-8"));
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String getId() {
       return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getFullPhoneNumber() {
        return fullPhoneNumber;
    }
    public void setFullPhoneNumber(String fullPhoneNumber) {
        this.fullPhoneNumber = fullPhoneNumber;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    public List<Contact> getContactList() {
        return contactList;
    }
    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public boolean getHasSimCard() {
        return hasSimCard;
    }

    public void setHasSimCard(boolean hasSimCard) {
        this.hasSimCard = hasSimCard;
    }
    public List<CallRecord> getCallRecordList() {
        return callRecordList;
    }

    public void setCallRecordList(List<CallRecord> callRecordList) {
        this.callRecordList = callRecordList;
    }

    public JSONObject getUserAsJsonMap() throws JSONException {
        JSONObject jsonUser = new JSONObject();

        jsonUser.put("id", getId());
        jsonUser.put("country", getCountry());
        jsonUser.put("countryCode", getCountryCode());
        jsonUser.put("phoneNumber", getPhoneNumber());
        jsonUser.put("fullPhoneNumber", getFullPhoneNumber());
        jsonUser.put("fullName", getFullName());
        jsonUser.put("profileImageUrl", getProfileImageUrl());

        JSONArray contactArray = new JSONArray();
        for (int i = 0; i < getContactList().size(); i++) {
            contactArray.put(getContactList().get(i).getContactAsJsonMap());
        }

        jsonUser.put("contactList", contactArray);
        jsonUser.put("hasSimCard", getHasSimCard());

        JSONArray callHistoryArray = new JSONArray();
        for (int i = 0; i < getCallRecordList().size(); i++) {
            callHistoryArray.put(getCallRecordList().get(i).getCallRecordAsJsonMap());
        }
        jsonUser.put("callHistory", callHistoryArray);

        return jsonUser;
    }

    public void syncUser() throws JSONException {
        //todo throws networking on main thread exception
        ObjectMapper mapper = new ObjectMapper();
        Handler handler = new Handler();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user", getUserAsJsonMap());
                    OkHttpClient client = new OkHttpClient();
                    MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

                    Request request = new Request.Builder()
                            .url("http://34.73.16.73:8080/users/sync/")
                            .post(RequestBody.create(JSON_TYPE, jsonObject.toString()))
                            .addHeader("content-type","application/json")
                            .build();
                    Response response = client.newCall(request).execute();
                    String jsonDataString = null;
                    try {
                        jsonDataString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Map<String, ?> responseJson = mapper.readValue(jsonDataString, Map.class);
                    Log.i(TAG, String.valueOf(responseJson.get("user")));

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });


//        JSONObject postData = new JSONObject();
//        postData.put("user", getUserAsJsonMap());
//        HttpPostAsyncTask task = new HttpPostAsyncTask(postData);
//        task.execute("http://192.168.1.148:8080/users/sync");
    }

//    public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {
//        // This is the JSON body of the post
//        JSONObject postData;
//        // This is a constructor that allows you to pass in the JSON body
//        public HttpPostAsyncTask(JSONObject postData) {
//            if (postData != null) {
//                this.postData = postData;
//            }
//        }
//
//        // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
//        @Override
//        protected Void doInBackground(String... params) {
//
//            try {
//                // This is getting the url from the string we passed in
//                URL url = new URL(params[0]);
//
//                // Create the urlConnection
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//
//                urlConnection.setDoInput(true);
//                urlConnection.setDoOutput(true);
//
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//
//                urlConnection.setRequestMethod("POST");
//
//                MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
//
//                // Send the post body
//                if (this.postData != null) {
//                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
//                    writer.write(postData.toString());
//                    writer.flush();
//                }
//
//                int statusCode = urlConnection.getResponseCode();
//
//                if (statusCode ==  200) {
//
//                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
//
//                    String response = convertInputStreamToString(inputStream);
//                    Log.i(TAG, "Successssssssssssssss " + response);
//
//                    // From here you can convert the string to JSON with whatever JSON parser you like to use
//                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
//                } else {
//                    // Status code is not 200
//                    // Do something to handle the error
//
//
//                    Log.e(TAG, Integer.toString(statusCode));
//                    Log.e(TAG, "Failure");
//                }
//
//            } catch (Exception e) {
//                Log.d(TAG, e.getLocalizedMessage());
//            }
//            return null;
//        }
//    }
//
//    private String convertInputStreamToString(InputStream inputStream) {
//        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        try {
//            while((line = bufferedReader.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return sb.toString();
//    }

}