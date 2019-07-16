package com.example.ignacio.randuserapp.data.source.remote;

import com.example.ignacio.randuserapp.data.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsersApi {

    @GET("api")
    Call<UserResponse> getUsers(@Query("page") int page, @Query("results") int results, @Query("seed") String seed);
}
