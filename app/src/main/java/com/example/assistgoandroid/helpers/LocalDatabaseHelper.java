package com.example.assistgoandroid.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.assistgoandroid.EnteringActivity;
import com.example.assistgoandroid.models.Contact;
import com.example.assistgoandroid.models.User;

import java.util.ArrayList;
import java.util.List;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "LocalAssistGoDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CONTACTS = "contact";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "fullName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "picture";
    private static final String KEY_USER_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_USER_FULL_PHONE_NUMBER = "fullPhoneNumber";
    private static final String KEY_USER_COUNTRY_CODE = "countryCode";
    private static final String KEY_USER_COUNTRY = "country";
    private static final String TAG = "LocalDatabaseHelper";

    // Contact table Columns
    private static final String KEY_CONTACT_ID = "id";
    private static final String KEY_CONTACT_USER_ID_FK = "userId";
    private static final String KEY_CONTACT_NAME = "fullName";
    private static final String KEY_CONTACT_PROFILE_PICTURE_URL = "picture";
    private static final String KEY_CONTACT_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_CONTACT_FAVORITE = "isFavorite";
    private static final String KEY_CONTACT_LAST_CALLED = "lastCalled";


    //singleton pattern helps avoid memory leaks and unnecessary reallocations
    private static LocalDatabaseHelper sInstance;

    public static synchronized LocalDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new LocalDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT," +
                KEY_USER_PHONE_NUMBER + " TEXT," +
                KEY_USER_FULL_PHONE_NUMBER + " TEXT," +
                KEY_USER_COUNTRY_CODE + " TEXT," +
                KEY_USER_COUNTRY + " TEXT" +
                ")";

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS +
                "(" +
                KEY_CONTACT_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_CONTACT_NAME + " TEXT," +
                KEY_CONTACT_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_CONTACT_PROFILE_PICTURE_URL + " TEXT," +
                KEY_CONTACT_PHONE_NUMBER + " TEXT," +
                KEY_CONTACT_FAVORITE + " TEXT," +
                KEY_CONTACT_LAST_CALLED + " TEXT" +
                ")";

        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            onCreate(sqLiteDatabase);
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addOrUpdateUser(User user) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.getFullName());
            values.put(KEY_USER_PROFILE_PICTURE_URL, user.getProfileImageUrl());
            values.put(KEY_USER_PHONE_NUMBER, user.getPhoneNumber());
            values.put(KEY_USER_FULL_PHONE_NUMBER, user.getFullPhoneNumber());
            values.put(KEY_USER_COUNTRY_CODE, user.getCountryCode());
            values.put(KEY_USER_COUNTRY, user.getCountry());

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_USERS, values, KEY_CONTACT_NAME + "= ?", new String[]{user.getId()});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_USER_ID, TABLE_USERS, KEY_USER_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.getFullName())});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public long addOrUpdateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        long contactID = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CONTACT_USER_ID_FK, EnteringActivity.user.getId());
            values.put(KEY_CONTACT_NAME, contact.getFullName());
            values.put(KEY_CONTACT_PROFILE_PICTURE_URL, contact.getProfileImageUrl());
            values.put(KEY_CONTACT_PHONE_NUMBER, contact.getPhoneNumber());
            values.put(KEY_CONTACT_FAVORITE, contact.isFavorite());
            values.put(KEY_CONTACT_LAST_CALLED, contact.getLastCalled());

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_CONTACTS, values, KEY_CONTACT_ID + "= ?", new String[]{contact.getFullName()});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String contactsSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_CONTACT_ID, TABLE_CONTACTS, KEY_CONTACT_NAME);
                Cursor cursor = db.rawQuery(contactsSelectQuery, new String[]{String.valueOf(contact.getFullName())});
                try {
                    if (cursor.moveToFirst()) {
                        contactID = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                contactID = db.insertOrThrow(TABLE_CONTACTS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }

        return contactID;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String CONTACTS_SELECT_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        TABLE_CONTACTS,
                        TABLE_USERS,
                        TABLE_CONTACTS, KEY_CONTACT_USER_ID_FK,
                        TABLE_USERS, KEY_USER_ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Contact newContact = new Contact();
                    newContact.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_NAME)));
                    newContact.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_PHONE_NUMBER)));
                    newContact.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_PROFILE_PICTURE_URL)));
                    newContact.setFavorite(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_FAVORITE))));
                    newContact.setLastCalled(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_LAST_CALLED)));
                    contacts.add(newContact);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contacts;
    }

    public User getCurrentUser() {
        User user = new User();

        String CONTACTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_USERS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAME)));
                    user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PHONE_NUMBER)));
                    user.setFullPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_FULL_PHONE_NUMBER)));
                    user.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PROFILE_PICTURE_URL)));
                    user.setCountryCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_COUNTRY_CODE)));
                    user.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_COUNTRY)));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return user;
    }

    public void deleteAllContactsAndUsers(){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_CONTACTS, null, null);
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }
}
