package com.example.assistgoandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;

public class Contact implements Parcelable {
    String contactID;
    String fullName;
    String phoneNumber;
    String profileImageUrl;
    boolean isFavorite;
    String lastCalled;

    protected Contact(Parcel in) {
        contactID = in.readString();
        fullName = in.readString();
        phoneNumber = in.readString();
        profileImageUrl = in.readString();
    }

    public Contact(){
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

    //todo sort based on fav: bring favs on top
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
