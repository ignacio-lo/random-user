package com.example.ignacio.randuserapp.userdetail;

import com.example.ignacio.randuserapp.BasePresenter;
import com.example.ignacio.randuserapp.BaseView;
import com.example.ignacio.randuserapp.data.User;


public interface UserDetailContract {

    interface View extends BaseView<Presenter> {
        void showAddContactSuccess();
        void showAddContactFailure();
        void setIdUser(Integer userId);
        void showUserFavError();
    }

    interface Presenter extends BasePresenter {
        void addContact(User mUser);

        void saveFavUser(User mFavUser);

        void deleteFavUser(Integer mFavUserId);
    }
}
