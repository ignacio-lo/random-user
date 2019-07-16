package com.example.ignacio.randuserapp.data.source.local;

import android.content.Context;

import com.example.ignacio.randuserapp.data.User;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase INSTANCE;

    public abstract UsersDao userDao();

    private static final Object sLock = new Object();

    public static UserDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, "Users.db").build();
            }

            return INSTANCE;
        }
    }
}
