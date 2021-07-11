package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.example.android.pets.data.PetContract.PetsEntry;

public class Pet_DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="shelter.db";
    private static final int DATABASE_VERSION= 1;

    public Pet_DbHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE="CREATE TABLE "+ PetsEntry.TABLE_NAME+"("
                +PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +PetsEntry.COLUMN_PET_NAME +" TEXT NOT NULL, "
                +PetsEntry.COLUMN_PET_BREED+" TEXT, "
                +PetsEntry.COLUMN_PET_GENDER+" INTEGER NOT NULL, "
                +PetsEntry.COLUMN_PET_WEIGHT+" INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
