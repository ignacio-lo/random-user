package com.example.ignacio.randuserapp.data;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "iduser")
    private Integer idUser;

    @Ignore
    private String gender;

    @Embedded
    private userName name;

    @ColumnInfo(name = "nation")
    private String nat;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "cellphone")
    private String cell;

    @Embedded
    private userPicture picture;

    @Embedded
    private userLogin login;

    @ColumnInfo(name = "favorite")
    private boolean favorite = false;

    @ColumnInfo(name = "fullname")
    private String fullName;

    //Getters & Setters
    @NonNull
    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(@NonNull Integer idUser) {
        this.idUser = idUser;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public userName getName() {
        return name;
    }

    public void setName(userName name) {
        this.name = name;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public userPicture getPicture() {
        return picture;
    }

    public void setPicture(userPicture picture) {
        this.picture = picture;
    }

    public userLogin getLogin() {
        return login;
    }

    public void setLogin(userLogin login) {
        this.login = login;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    //Subclases
    public static class userName implements Serializable {
        @Ignore
        private String title;

        @ColumnInfo(name = "firstname")
        private String first;

        @ColumnInfo(name = "lastname")
        private String last;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }
    }

    public static class userPicture implements Serializable {
        @ColumnInfo(name = "imglarge")
        private String large;

        @ColumnInfo(name = "imgmedium")
        private String medium;

        @ColumnInfo(name = "imgthumbnail")
        private String thumbnail;

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }

    public static class userLogin implements Serializable {
        @ColumnInfo(name = "idloginapi")
        private String uuid;

        @ColumnInfo(name = "username")
        private String username;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
