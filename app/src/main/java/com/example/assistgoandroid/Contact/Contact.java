package com.example.assistgoandroid.Contact;

import java.util.ArrayList;

public class Contact {
    int contactID;
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

    public int getContactID() {
        return contactID;
    }
//    private static int lastContactId = 0;
//    public static ArrayList<Contact> createContactsList(int numContacts) {
//        ArrayList<Contact> contacts = new ArrayList<Contact>();
//        for (int i = 1; i <= numContacts; i++) {
//            contacts.add(new Contact(++lastContactId, "", "", ""));
//        }
//
//        return contacts;
//    }
}
