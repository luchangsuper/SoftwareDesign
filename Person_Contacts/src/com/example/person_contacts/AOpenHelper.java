
package com.example.person_contacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class AOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "personal_contacts";
    public static final String TABLE_NAME = "contacts";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";

    public AOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + " (" + ID + " integer primary key," + NAME + " varchar,"
                + PHONE + " varchar," + MOBILE + " varchar," + EMAIL + " varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
