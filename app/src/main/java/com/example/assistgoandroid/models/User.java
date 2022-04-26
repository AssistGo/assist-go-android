package com.example.assistgoandroid.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class User {

    private String id;
    private String country;
    private String countryCode;
    private String phoneNumber;
    private String fullPhoneNumber;
    private String fullName;
    private String profileImageUrl;
    private List<Contact> contactList;

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

    public Map getUserAsJsonMap() {
        Map jsonUser = new HashMap();

        jsonUser.put("id", getId());
        jsonUser.put("country", getCountry());
        jsonUser.put("countryCode", getCountryCode());
        jsonUser.put("phoneNumber", getPhoneNumber());
        jsonUser.put("fullPhoneNumber", getFullPhoneNumber());
        jsonUser.put("fullName", getFullName());
        jsonUser.put("profileImageUrl", getProfileImageUrl());
        jsonUser.put("contactList", getContactList());

        return jsonUser;
    }
    public void syncUser() {
        ObjectMapper mapper = new ObjectMapper();

        Map map = new HashMap();
        map.put("id", this.getId()); //pretend user id

        try {
            OkHttpClient client = new OkHttpClient();
            //MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

            Request request = new Request.Builder()
                    .url("http://34.73.16.73:8080/users/contact/all/" + this.getId())
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String jsonDataString = null;
            try {
                jsonDataString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, ?> responseJson = mapper.readValue(jsonDataString, Map.class);

            System.out.println(responseJson.get("resStatus"));
            System.out.println(responseJson.get("user"));
            System.out.println(responseJson.get("message"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}