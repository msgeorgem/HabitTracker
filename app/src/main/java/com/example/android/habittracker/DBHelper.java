package com.example.android.habittracker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import static android.R.attr.id;

/**
 * Created by Marcin on 2017-05-21.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DBHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "habits.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;
    /** URI matcher code for the content URI for the habits table */
    private static final int ITEMS = 100;
    /** URI matcher code for the content URI for a single pet in the habits table */
    private static final int ITEM_ID = 101;
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
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ITEMS +"/#", ITEM_ID);
    }

    /**
     * Constructs a new instance of {@link DBHelper}.
     *
     * @param context of the app
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the items table
        String SQL_CREATE_ITEMS_TABLE =  "CREATE TABLE " + Contract.HabitEntry.TABLE_NAME + " ("
                + Contract.HabitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.HabitEntry.COLUMN_ITEM_HABIT + " TEXT NOT NULL, "
                + Contract.HabitEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, "
                + Contract.HabitEntry.COLUMN_ITEM_OCCURENCE + " INTEGER NOT NULL, "
                + Contract.HabitEntry.COLUMN_ITEM_TIME + " INTEGER NOT NULL DEFAULT 0,";

        Log.v(LOG_TAG,SQL_CREATE_ITEMS_TABLE);
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }
    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // For the ITEMS code, query the habits table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the habits table.
                cursor = database.query(Contract.HabitEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.habittracker/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = Contract.HabitEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the Habits table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(Contract.HabitEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

       return cursor;
    }
    /**
     * Insert new data into the provider with the given ContentValues.
     */

    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertHabit(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertHabit(Uri uri, ContentValues values) {

        // Check that the habit is not null
        String habit = values.getAsString(Contract.HabitEntry.COLUMN_ITEM_HABIT);
        if (habit == null) {
            throw new IllegalArgumentException("Habit requires a habit");
        }
        // Check that the description is not null
        String description = values.getAsString(Contract.HabitEntry.COLUMN_ITEM_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Habit requires a description");
        }
        // Check that the occurence is not null
        Integer occurence = values.getAsInteger(Contract.HabitEntry.COLUMN_ITEM_OCCURENCE);
        if (occurence == null || !Contract.HabitEntry.isValidType(occurence)) {
            throw new IllegalArgumentException("Habit requires valid occurence");
        }
        // If the time is provided, check that it's greater than or equal to 0 min
        Integer time = values.getAsInteger(Contract.HabitEntry.COLUMN_ITEM_TIME);
        if (time != null && time < 0) {
            throw new IllegalArgumentException("Habit requires valid time");
        }

        // / Gets the database in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Insert a new row for habit in the database, returning the ID of that new row.
        long newRowId = db.insert(Contract.HabitEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }
}
