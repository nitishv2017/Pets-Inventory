package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PetProvider extends ContentProvider {

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#", PET_ID);

    }

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    Pet_DbHelper mDbHelper;
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper=new Pet_DbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor c;
        int match=sUriMatcher.match(uri);

        switch (match)
        {
            case PETS:
                c= db.query(PetContract.PetsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PET_ID:
                selection= PetContract.PetsEntry._ID + "=?";
                selectionArgs= new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                c= db.query(PetContract.PetsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        return c;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match= sUriMatcher.match(uri);

        switch(match)
        {
            case PETS:
                return insertPets(uri, contentValues);
            default: throw new IllegalArgumentException("Cannot insert by unknown URI " + uri);
        }


    }

    Uri insertPets(Uri uri , ContentValues values)
    {
        String name = values.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
        if (name==null || name.length()==0) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);
        if (gender == null && !PetContract.PetsEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);

        if(weight!=null && weight<0)
        {
            throw new IllegalArgumentException("Pet's weight should be non-negetive weight");
        }


        SQLiteDatabase db= mDbHelper.getWritableDatabase();

        long _id= db.insert(PetContract.PetsEntry.TABLE_NAME,null,values);
        if (_id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri,_id);

    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {



        Cursor c;
        int match=sUriMatcher.match(uri);

        switch (match)
        {   case PETS:
                return updatePets(uri,contentValues,selection,selectionArgs);

            case PET_ID:
                selection= PetContract.PetsEntry._ID + "=?";
                selectionArgs= new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                return updatePets(uri,contentValues,selection,selectionArgs);

            default: throw new IllegalArgumentException("Cannot update unknown URI " + uri);
        }

    }

    int updatePets(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if (values.containsKey(PetContract.PetsEntry.COLUMN_PET_NAME))
        {
            String name = values.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null || name.length()==0) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(PetContract.PetsEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);
            if (gender == null && !PetContract.PetsEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(PetContract.PetsEntry.COLUMN_PET_WEIGHT)) {
            // If the weight is provided, check that it's greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);

            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet's weight should be non-negetive weight");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int response= db.update(PetContract.PetsEntry.TABLE_NAME,values,selection,selectionArgs);

        return response;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(PetContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}
