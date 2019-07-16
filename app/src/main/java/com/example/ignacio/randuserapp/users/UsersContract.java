package com.example.ignacio.randuserapp.users;

import android.content.Intent;

import com.example.ignacio.randuserapp.BasePresenter;
import com.example.ignacio.randuserapp.BaseView;
import com.example.ignacio.randuserapp.data.User;

import java.util.List;

public interface UsersContract {

    interface View extends BaseView<Presenter> {
        void showUsers(List<User> users);
        void showNoUsersError();
        void showMoreUsers(List<User> users);
        void showNoMoreUsersError(int currentPage);
        void showFavUsers(List<User> favUsers);
        void hideFavUsers();
        void markFavUser(User favUser);
        void markFavUsersList();
        void reloadUsers();
        void showUserDetail(android.view.View view);
    }

    interface Presenter extends BasePresenter {
        void result(int requestCode, int resultCode, Intent data);
        void loadUsers();
        void loadMore(int page);
        void loadFavUsers();
    }
}
