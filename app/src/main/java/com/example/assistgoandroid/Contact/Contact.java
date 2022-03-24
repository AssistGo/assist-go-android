package com.example.assistgoandroid.Contact;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Comparator;

import java.util.Comparator;

public class Contact implements Parcelable {
    String contactID;
    String name;
    String phoneNumber;
    String contactPicture;
    String lookupKey;
    boolean isFavorite;

    protected Contact(Parcel in) {
        contactID = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        contactPicture = in.readString();
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

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setContactPicture(String contactPicture) {
        this.contactPicture = contactPicture;
    }

    public String getContactPicture() {
        return contactPicture;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactID);
        parcel.writeString(name);
        parcel.writeString(phoneNumber);
        parcel.writeString(contactPicture);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactID='" + contactID + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", contactPicture='" + contactPicture + '\'' +
                '}';
    }

    //todo sort based on fav: bring favs on top
    public static Comparator<Contact> ContactComparator
            = (contact1, contact2) -> {

                String contactName1 = contact1.getName();
                String contactName2 = contact2.getName();

                //ascending order
                return contactName1.compareTo(contactName2);
            };
}
