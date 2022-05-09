package com.example.assistgoandroid.models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;

public class CallRecord {
    private Context userContext;

    private String historyId;
    private String id;
    private String country;
    private String countryCode;
    private String phoneNumber;
    private String fullPhoneNumber;
    private String fullName;
    private String profileImageUrl;
    private boolean hasSimCard;
    private Date timeOfContact;


    public CallRecord(String id, String country, String countryCode, String phoneNumber, String fullPhoneNumber, String fullName, String profileImageUrl, boolean hasSimCard) {
        this.historyId = generateRandomId();
        this.id = id;
        this.country = country;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.fullPhoneNumber = fullPhoneNumber;
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
        this.hasSimCard = hasSimCard;
        this.timeOfContact = new Date();
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
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

    public boolean getHasSimCard() {
        return hasSimCard;
    }

    public void setHasSimCard(boolean hasSimCard) {
        this.hasSimCard = hasSimCard;
    }

    public Date getTimeOfContact() {
        return timeOfContact;
    }

    public void setTimeOfContact(Date timeOfContact) {
        this.timeOfContact = timeOfContact;
    }

    public String generateRandomId() {
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    public JSONObject getCallRecordAsJsonMap() throws JSONException {
        JSONObject jsonCallRecord = new JSONObject();

        jsonCallRecord.put("historyId", getHistoryId());
        jsonCallRecord.put("id", getId());
        jsonCallRecord.put("country", getCountry());
        jsonCallRecord.put("countryCode", getCountryCode());
        jsonCallRecord.put("phoneNumber", getPhoneNumber());
        jsonCallRecord.put("fullPhoneNumber", getFullPhoneNumber());
        jsonCallRecord.put("fullName", getFullName());
        jsonCallRecord.put("profileImageUrl", getProfileImageUrl());
        jsonCallRecord.put("hasSimCard", getHasSimCard());
        jsonCallRecord.put("timeOfContact", getTimeOfContact());

        return jsonCallRecord;
    }

}
