package com.example.ignacio.randuserapp.userdetail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ignacio.randuserapp.R;
import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.util.Injection;

public class UserDetailActivity extends AppCompatActivity {

    private static int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 100;

    private UserDetailFragment mUserDetailFragmentView;
    private UserDetailPresenter mUserDetailPresenter;

    private User mUser;

    public boolean favModified = false;

    public static final int REQUEST_USER_DETAIL = 1;
    public static final String FAV_USER = "favUser";
    public static final String USER_OBJ = "userObj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        //Pido el permiso para guardar contactos
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Up button action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUserDetailFragmentView = (UserDetailFragment) getSupportFragmentManager().findFragmentById(R.id.user_detail_container);

        if (savedInstanceState == null && mUserDetailFragmentView == null) {
            if (getIntent().hasExtra(USER_OBJ)) {
                mUserDetailFragmentView = new UserDetailFragment();

                Bundle arguments = new Bundle();

                mUser = (User) getIntent().getSerializableExtra(USER_OBJ);

                arguments.putSerializable(USER_OBJ, mUser);

                mUserDetailFragmentView.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.user_detail_container, mUserDetailFragmentView)
                        .commit();
            }
        }

        mUserDetailPresenter = new UserDetailPresenter(mUserDetailFragmentView,
                Injection.provideUserRepository(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);

        if (mUser.isFavorite()) {
            menu.findItem(R.id.action_addFav).setIcon(R.drawable.ic_favorite_24dp);
        } else {
            menu.findItem(R.id.action_addFav).setIcon(R.drawable.ic_favorite_border_24dp);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            setActivityResult();

            return true;
        }

        //Si hice click en el corazon de favoritos
        if (id == R.id.action_addFav) {
            favModified = true;

            //Si ya es favorito lo elimino si no lo inserto
            if (mUser.isFavorite()) {
                mUser.setFavorite(false);

                mUserDetailPresenter.deleteFavUser(mUser.getIdUser());

                item.setIcon(R.drawable.ic_favorite_border_24dp);
            } else {
                mUser.setFavorite(true);

                mUserDetailPresenter.saveFavUser(mUser);

                item.setIcon(R.drawable.ic_favorite_24dp);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //Si el usuario fue modificado, RESULT_OK y data. Si no fue, RESULT_CANCELED.
    void setActivityResult() {
        if (favModified) {
            Intent data = new Intent();

            //Mando el fav user para marcarlo/desmarcarlo en la lista de users de la api
            data.putExtra(FAV_USER, mUser);

            this.setResult(Activity.RESULT_OK, data);
        } else {
            this.setResult(Activity.RESULT_CANCELED);
        }

        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setActivityResult();

        super.onBackPressed();
    }

    // Callback con la respuesta a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            //Si no fue concedido muestro leyenda
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
