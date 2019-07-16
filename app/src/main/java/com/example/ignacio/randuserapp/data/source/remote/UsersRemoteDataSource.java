package com.example.ignacio.randuserapp.data.source.remote;

import android.util.Log;

import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.data.UserResponse;
import com.example.ignacio.randuserapp.data.source.UserDataSource;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersRemoteDataSource implements UserDataSource.Remote {

    private static volatile UsersRemoteDataSource INSTANCE;

    private UsersApi usersApi;

    private static final String BASE_URL = "https://randomuser.me/";
    private static final int RESULTS = 50;
    private static final String SEED = "randuser";
    private static final int PAGE = 1;

    //Ultima pagina cargada correctamente, se usa por si hay errores
    private static int LAST_PAGE_FETCHED = 0;


    public static UsersRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (UsersRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UsersRemoteDataSource();
                }
            }
        }

        return INSTANCE;
    }


    //Primer pagina de usuarios
    @Override
    public void getUsers(@NonNull final OnUsersCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usersApi = retrofit.create(UsersApi.class);

        usersApi.getUsers(PAGE, RESULTS, SEED).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    callback.onFailure();

                    return;
                }

                LAST_PAGE_FETCHED = PAGE;

                UserResponse mUserResponse = response.body();

                List<User> users = mUserResponse.getResults();

                if (users != null) {
                    callback.onSuccess(users);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onFailure();
                Log.e("UsersRemoteDataSource", "onFailure: " + t.getMessage());
            }
        });
    }

    //Recargar mas usuarios
    @Override
    public void getMoreUsers(@NonNull final OnMoreUsersCallback callback, final int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usersApi = retrofit.create(UsersApi.class);

        usersApi.getUsers(page, RESULTS, SEED).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    callback.onMoreFailure(page);

                    return;
                }

                LAST_PAGE_FETCHED = page;

                UserResponse mUserResponse = response.body();

                List<User> users = mUserResponse.getResults();

                if (users != null) {
                    callback.onMoreSuccess(users);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onMoreFailure(LAST_PAGE_FETCHED);
                Log.e("UsersRemoteDataSource", "onFailure: " + t.getMessage());
            }
        });
    }
}
