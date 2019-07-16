package com.example.ignacio.randuserapp.util;

import android.content.Context;

import com.example.ignacio.randuserapp.data.source.UserRepository;
import com.example.ignacio.randuserapp.data.source.local.UserDatabase;
import com.example.ignacio.randuserapp.data.source.local.UsersLocalDataSource;
import com.example.ignacio.randuserapp.data.source.remote.UsersRemoteDataSource;

import androidx.annotation.NonNull;

import static androidx.core.util.Preconditions.checkNotNull;


public class Injection {

    public static UserRepository provideUserRepository(@NonNull Context context) {
        checkNotNull(context);
        UserDatabase database = UserDatabase.getInstance(context);
        return UserRepository.getInstance(UsersLocalDataSource.getInstance(database.userDao(), new AppExecutors()), UsersRemoteDataSource.getInstance());
    }
}
