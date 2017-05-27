package com.example.android.habittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.example.android.habittracker.Contract.HabitEntry.COLUMN_ITEM_DESCRIPTION;
import static com.example.android.habittracker.Contract.HabitEntry.COLUMN_ITEM_HABIT;
import static com.example.android.habittracker.Contract.HabitEntry.COLUMN_ITEM_OCCURENCE;
import static com.example.android.habittracker.Contract.HabitEntry.COLUMN_ITEM_TIME;
import static com.example.android.habittracker.Contract.HabitEntry.CONTENT_URI;
import static com.example.android.habittracker.Contract.HabitEntry._ID;

/**
 * Created by Marcin on 2017-05-21.
 */

public class MainActivity extends AppCompatActivity {


    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public Cursor getAllHabits() {
        return db.query(String.valueOf(CONTENT_URI), new String[]{
                _ID,
                COLUMN_ITEM_HABIT,
                COLUMN_ITEM_DESCRIPTION,
                COLUMN_ITEM_OCCURENCE,
                COLUMN_ITEM_TIME
        }, null, null, null, null, null);

    }

    public Cursor getHabit(long rowID) throws SQLException {

        Cursor cursor = db.query(String.valueOf(CONTENT_URI), new String[]{
                _ID,
                COLUMN_ITEM_HABIT,
                COLUMN_ITEM_DESCRIPTION,
                COLUMN_ITEM_OCCURENCE,
                COLUMN_ITEM_TIME
        }, _ID + "=" + rowID, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    private void insertHabit() {

        // Create a ContentValues object, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_HABIT, "Dogs");
        values.put(COLUMN_ITEM_DESCRIPTION, "Runnin");
        values.put(COLUMN_ITEM_OCCURENCE, Contract.HabitEntry.DAILY);
        values.put(COLUMN_ITEM_TIME, 7);

        // Insert the new row, returning the primary key value of the new row
        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
    }

}