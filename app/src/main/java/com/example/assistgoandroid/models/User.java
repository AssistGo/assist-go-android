package com.example.assistgoandroid.models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.codepath.asynchttpclient.AsyncHttpClient;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Headers;


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


    @SuppressLint("MissingPermission")
    public User() {
        TelephonyManager telMgr = (TelephonyManager) userContext.getSystemService(Context.TELEPHONY_SERVICE);

        this.id = generateRandomId();
        this.country = telMgr.getNetworkCountryIso();
        this.countryCode = telMgr.getLine1Number().substring(0, telMgr.getLine1Number().length() - 10);
        this.phoneNumber = telMgr.getLine1Number().substring(telMgr.getLine1Number().length() - 10);
        this.fullPhoneNumber = telMgr.getLine1Number();
        this.fullName = "New User";
        this.profileImageUrl = "https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg";
        this.callRecordList = new ArrayList<CallRecord>();
        this.contactList = new ArrayList<Contact>();

        int simState = telMgr.getSimState();

        if (simState == TelephonyManager.SIM_STATE_READY) {
            this.hasSimCard = true;
        } else {
            this.hasSimCard = false;
        }

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
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
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

        Log.d("SYNC REQUEST", "WELCOME TO SYNC FUNCTION");

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("user", getUserAsJsonMap());

        client.post("http://localhost:8080/users/sync", String.valueOf(jsonBody), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("SYNC REQUEST", "Sync request completed!");
                // TODO SYNC USER HERE
                // Sync the user data here
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("SYNC REQUEST", "Sync request failed!");
                // Throw Error/Exception
            }
        });


//        ObjectMapper mapper = new ObjectMapper();
//
//        Map map = new HashMap();
//        map.put("id", this.getId()); //pretend user id
//
//        try {
//            OkHttpClient client = new OkHttpClient();
//            //MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
//
//            Request request = new Request.Builder()
//                    .url("http://34.73.16.73:8080/users/contact/all/" + this.getId())
//                    .get()
//                    .build();
//            Response response = client.newCall(request).execute();
//            String jsonDataString = null;
//            try {
//                jsonDataString = response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Map<String, ?> responseJson = mapper.readValue(jsonDataString, Map.class);
//
//            System.out.println(responseJson.get("resStatus"));
//            System.out.println(responseJson.get("user"));
//            System.out.println(responseJson.get("message"));
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}