package com.example.ignacio.randuserapp.users;

import android.app.Activity;
import android.content.Intent;

import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.data.UserMapper;
import com.example.ignacio.randuserapp.data.source.UserDataSource;
import com.example.ignacio.randuserapp.data.source.UserRepository;
import com.example.ignacio.randuserapp.userdetail.UserDetailActivity;

import java.util.List;

import androidx.annotation.NonNull;

public class UsersPresenter implements UsersContract.Presenter, UserDataSource.Remote.OnUsersCallback, UserDataSource.Remote.OnMoreUsersCallback, UserDataSource.Local.OnFavUsersCallback {

    private UsersContract.View mUsersView;

    private final UserRepository mUsersRepository;

    private boolean firstLoad = true;

    public UsersPresenter(@NonNull UsersContract.View usersView, @NonNull UserRepository mUserRepository) {
        this.mUsersView = usersView;
        this.mUsersRepository = mUserRepository;

        mUsersView.setPresenter(this);
    }

    @Override
    public void start() {
        //La primer pagina de usuarios solo se carga al iniciar
        if (firstLoad) {
            //Cargo los favoritos
            loadFavUsers();

            //Cargo la primer pagina de la api
            loadUsers();

            firstLoad = false;
        }
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        //Si un usuario fue marcado/desmarcado como favorito, refresco la lista de favoritos
        if (UserDetailActivity.REQUEST_USER_DETAIL == requestCode && Activity.RESULT_OK == resultCode && data.hasExtra(UserDetailActivity.FAV_USER)) {
            User favUser = (User) data.getSerializableExtra(UserDetailActivity.FAV_USER);

            loadFavUsers();

            //Busco el usuario en la lista de la api para marcarlo/desmarcarlo
            mUsersView.markFavUser(favUser);
        }
    }

    @Override
    public void loadUsers() {
        mUsersRepository.getUsers(this);
    }

    @Override
    public void loadMore(int page) {
        mUsersRepository.getMoreUsers(this, page);
    }

    @Override
    public void loadFavUsers() {
        mUsersRepository.getFavUsers(this);
    }

    //Metodos implementados por callbacks
    @Override
    public void onSuccess(List<User> users) {
        //Mapeo la lista de usuarios para unir nombre y apellido con mayusculas
        users = UserMapper.mapList(users);

        mUsersView.showUsers(users);
    }

    @Override
    public void onFailure() {
        mUsersView.showNoUsersError();
    }

    @Override
    public void onMoreSuccess(List<User> users) {
        users = UserMapper.mapList(users);

        mUsersView.showMoreUsers(users);
    }

    @Override
    public void onMoreFailure(int lastPageFetched) {
        mUsersView.showNoMoreUsersError(lastPageFetched);
    }

    @Override
    public void onFavSuccess(List<User> favUsers) {
        mUsersView.showFavUsers(favUsers);
    }

    @Override
    public void onFavFailure() {
        mUsersView.hideFavUsers();
    }
}
