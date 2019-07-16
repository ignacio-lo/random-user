package com.example.ignacio.randuserapp.data.source;

import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.data.UserResponse;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserDataSource {

    interface Local {

        interface OnFavUsersCallback {
            void onFavSuccess(List<User> favUsers);
            void onFavFailure();
        }

        interface OnFavInsertCallback {
            void onFavInsertSuccess(Integer userId);
            void onFavInsertFailure();
        }

        void getFavUsers(@NonNull OnFavUsersCallback callback);

        void insertUser(@NonNull User user, @NonNull OnFavInsertCallback callback);

        void deleteUserById(Integer userId);

        void deleteUsers();
    }

    interface Remote {

        interface OnUsersCallback {
            void onSuccess(List<User> users);
            void onFailure();
        }

        interface OnMoreUsersCallback {
            void onMoreSuccess(List<User> users);
            void onMoreFailure(int lastPageFetched);
        }

        void getUsers(@NonNull OnUsersCallback onUsersCallback);

        void getMoreUsers(@NonNull OnMoreUsersCallback onMoreUsersCallback, int page);
    }
}
