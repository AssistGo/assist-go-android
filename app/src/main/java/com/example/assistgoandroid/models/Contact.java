package com.example.assistgoandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Contact implements Parcelable {
    String contactID;
    String country;
    String countryCode;
    String fullName;
    String phoneNumber;
    String fullPhoneNumber;
    String profileImageUrl;
    boolean hasSimCard;
    boolean isFavorite;
    String lastCalled;

    public Contact() {

    }

    protected Contact(Parcel in) {
        contactID = in.readString();
        fullName = in.readString();
        phoneNumber = in.readString();
        profileImageUrl = in.readString();
    }

    public Contact(String contactID, String country, String countryCode, String phoneNumber, String fullPhoneNumber, String fullName, String profileImageUrl, boolean hasSimCard) {
        this.contactID = contactID;
        this.country = country;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.fullPhoneNumber = fullPhoneNumber;
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
        this.hasSimCard = hasSimCard;
        this.isFavorite = false;
        this.lastCalled = null;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getLastCalled() {
        return lastCalled;
    }

    public void setLastCalled(String lastCalled) {
        this.lastCalled = lastCalled;
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

    public String getFullPhoneNumber() {
        return fullPhoneNumber;
    }

    public void setFullPhoneNumber(String fullPhoneNumber) {
        this.fullPhoneNumber = fullPhoneNumber;
    }

    public boolean getHasSimCard() {
        return hasSimCard;
    }

    public void setHasSimCard(boolean hasSimCard) {
        this.hasSimCard = hasSimCard;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactID);
        parcel.writeString(fullName);
        parcel.writeString(phoneNumber);
        parcel.writeString(profileImageUrl);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactID='" + contactID + '\'' +
                ", name='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", contactPicture='" + profileImageUrl + '\'' +
                '}';
    }

    public JSONObject getContactAsJsonMap() throws JSONException {
        JSONObject jsonContact = new JSONObject();

        jsonContact.put("id", getContactID());
        jsonContact.put("country", getCountry());
        jsonContact.put("countryCode", getCountryCode());
        jsonContact.put("phoneNumber", getPhoneNumber());
        jsonContact.put("fullPhoneNumber", getFullPhoneNumber());
        jsonContact.put("fullName", getFullName());
        jsonContact.put("profileImageUrl", getProfileImageUrl());
        jsonContact.put("hasSimCard", getHasSimCard());

        return jsonContact;
    }

    public static Comparator<Contact> ContactComparator
            = (contact1, contact2) -> {

                String contactName1 = contact1.getFullName();
                String contactName2 = contact2.getFullName();

                //ascending order
                return contactName1.compareTo(contactName2);
            };

    public static Comparator<Contact> LastCalledComparator
            = (contact1, contact2) -> {

        String contactCalled1 = contact1.getLastCalled();
        String contactCalled2 = contact2.getLastCalled();

        //ascending order
        return contactCalled1.compareTo(contactCalled2);
    };

    public static Comparator<Contact> FavoritesComparator
            = (contact1, contact2) -> {

        boolean favContact1 = contact1.isFavorite();
        boolean favContact2 = contact2.isFavorite();

        //ascending order
        return Boolean.compare(favContact2, favContact1);
    };


}
