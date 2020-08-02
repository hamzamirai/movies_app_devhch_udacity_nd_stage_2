package com.devhch.mirai.moviesapp_stage2.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/24/2020
 */

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.  In a real
 * app, consider exporting the schema to help you with migrations.
 */
@Database(entities = {FavoriteEntry.class}, version = 1, exportSchema = false)
public abstract class FavoriteRooDatabase extends RoomDatabase {

    private static final String LOG_TAG = FavoriteRooDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favorite";

    // marking the instance as volatile to ensure atomic access to the variable
    private static FavoriteRooDatabase sInstance;

    public static FavoriteRooDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        FavoriteRooDatabase.class, FavoriteRooDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract FavoriteDao favoriteDao();

}

