package com.example.assistgoandroid.Contact;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    String contactID;
    String name;
    String phoneNumber;
    String contactPicture;

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
}
