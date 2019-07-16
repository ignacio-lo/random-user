package com.example.ignacio.randuserapp.data.source;

import com.example.ignacio.randuserapp.data.User;

import androidx.annotation.NonNull;

public class UserRepository implements UserDataSource.Local, UserDataSource.Remote {

    private static UserRepository INSTANCE = null;

    private UserDataSource.Local mUserLocalDataSource;

    private UserDataSource.Remote mUserRemoteDataSource;

    private UserRepository(@NonNull UserDataSource.Local mUserLocalDataSource, @NonNull UserDataSource.Remote mUserRemoteDataSource) {
        this.mUserLocalDataSource = mUserLocalDataSource;
        this.mUserRemoteDataSource = mUserRemoteDataSource;
    }

    public static UserRepository getInstance(UserDataSource.Local mUserLocalDataSource, UserDataSource.Remote mUserRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository(mUserLocalDataSource, mUserRemoteDataSource);
        }

        return INSTANCE;
    }

    @Override
    public void getFavUsers(@NonNull final OnFavUsersCallback callback) {
        mUserLocalDataSource.getFavUsers(callback);
    }

    @Override
    public void insertUser(@NonNull User user, @NonNull OnFavInsertCallback callback) {
        mUserLocalDataSource.insertUser(user, callback);
    }

    @Override
    public void deleteUserById(Integer userId) {
        mUserLocalDataSource.deleteUserById(userId);
    }

    @Override
    public void deleteUsers() {
        mUserLocalDataSource.deleteUsers();
    }

    @Override
    public void getUsers(@NonNull OnUsersCallback onUsersCallback) {
        mUserRemoteDataSource.getUsers(onUsersCallback);
    }

    @Override
    public void getMoreUsers(@NonNull OnMoreUsersCallback onMoreUsersCallback, int page) {
        mUserRemoteDataSource.getMoreUsers(onMoreUsersCallback, page);
    }
}
