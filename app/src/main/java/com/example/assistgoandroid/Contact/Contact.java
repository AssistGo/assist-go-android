package com.example.assistgoandroid.Contact;

public class Contact {
    String contactID;
    String name;
    String phoneNumber;
    String contactPicture;

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
}
