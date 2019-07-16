package com.example.ignacio.randuserapp.data.source.local;

import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.data.source.UserDataSource;
import com.example.ignacio.randuserapp.util.AppExecutors;

import java.util.List;

import androidx.annotation.NonNull;

import static androidx.core.util.Preconditions.checkNotNull;

public class UsersLocalDataSource implements UserDataSource.Local {

    private static volatile UsersLocalDataSource INSTANCE;

    private UsersDao mUsersDao;

    private AppExecutors mAppExecutors;

    private UsersLocalDataSource(@NonNull UsersDao mUsersDao, @NonNull AppExecutors appExecutors) {
        this.mUsersDao = mUsersDao;
        this.mAppExecutors = appExecutors;
    }

    public static UsersLocalDataSource getInstance(@NonNull UsersDao mUsersDao, @NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (UsersLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UsersLocalDataSource(mUsersDao, appExecutors);
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void getFavUsers(@NonNull final OnFavUsersCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List <User> favUsers = mUsersDao.getFavUsers();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (favUsers.isEmpty()) {
                            callback.onFavFailure();
                        } else {
                            callback.onFavSuccess(favUsers);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void insertUser(@NonNull final User user, @NonNull final OnFavInsertCallback callback) {
        checkNotNull(user);
        Runnable insRunnable = new Runnable() {
            @Override
            public void run() {
                Integer idUser = (int)(long)mUsersDao.insertUser(user);

                if (idUser != 0) {
                    callback.onFavInsertSuccess(idUser);
                } else {
                    callback.onFavInsertFailure();
                }
            }
        };

        mAppExecutors.diskIO().execute(insRunnable);
    }

    @Override
    public void deleteUserById(@NonNull final Integer userId) {
        Runnable delRunnable = new Runnable() {
            @Override
            public void run() {
                mUsersDao.deleteUserById(userId);
            }
        };

        mAppExecutors.diskIO().execute(delRunnable);
    }

    @Override
    public void deleteUsers() {
        Runnable delRunnable = new Runnable() {
            @Override
            public void run() {
                mUsersDao.deleteUsers();
            }
        };

        mAppExecutors.diskIO().execute(delRunnable);
    }
}
