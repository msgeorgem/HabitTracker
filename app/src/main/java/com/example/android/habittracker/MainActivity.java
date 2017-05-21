package com.example.android.habittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcin on 2017-05-21.
 */

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void insertHabit() {

        // Create a ContentValues object, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Contract.HabitEntry.COLUMN_ITEM_HABIT, "Dogs");
        values.put(Contract.HabitEntry.COLUMN_ITEM_DESCRIPTION, "Runnin");
        values.put(Contract.HabitEntry.COLUMN_ITEM_OCCURENCE, Contract.HabitEntry.DAILY);
        values.put(Contract.HabitEntry.COLUMN_ITEM_TIME, 7);

        // Insert the new row, returning the primary key value of the new row
        Uri newUri = getContentResolver().insert(Contract.HabitEntry.CONTENT_URI, values);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                Contract.HabitEntry._ID,
                Contract.HabitEntry.COLUMN_ITEM_HABIT,
                Contract.HabitEntry.COLUMN_ITEM_DESCRIPTION,

        };

        // Perform a query using CursorLoader
        return new CursorLoader(this,    // Parent activity context
                Contract.HabitEntry.CONTENT_URI, // Provider content URI to query
                projection,            // The columns to include in the resulting Cursor
                null,                  // The values for the WHERE clause
                null,                  // No selection arguments
                null);                 // Default sort order
    }

}