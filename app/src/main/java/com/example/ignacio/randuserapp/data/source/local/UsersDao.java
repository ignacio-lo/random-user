package com.example.ignacio.randuserapp.data.source.local;

import com.example.ignacio.randuserapp.data.User;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UsersDao {

    @Query("SELECT * FROM Users")
    List<User> getFavUsers();

    @Insert
    long insertUser(User user);

    @Query("DELETE FROM Users WHERE iduser = :userId")
    int deleteUserById(Integer userId);

    @Query("DELETE FROM Users")
    void deleteUsers();
}
