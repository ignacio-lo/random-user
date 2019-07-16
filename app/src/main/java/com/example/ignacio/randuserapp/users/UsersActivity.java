package com.example.ignacio.randuserapp.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ignacio.randuserapp.R;
import com.example.ignacio.randuserapp.util.Injection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import static androidx.core.util.Preconditions.checkNotNull;

public class UsersActivity extends AppCompatActivity {

    private UsersView mUserView;

    private UsersPresenter mUsersPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mUserView = findViewById(R.id.users_view);

        checkNotNull(mUserView);

        mUsersPresenter = new UsersPresenter(mUserView, Injection.provideUserRepository(getApplicationContext()));

        mUsersPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mUsersPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_user_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        //Filtrar la lista con search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mUserView.mUsersAdapter.getFilter().filter(s);

                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            //Abro la search view, deshabilito filtrar la lista
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mUserView.mScrollListener.searchViewFiltering = true;
                return true;
            }

            //Cierro la search view, habilito volver a filtrar
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mUserView.mScrollListener.searchViewFiltering = false;
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
