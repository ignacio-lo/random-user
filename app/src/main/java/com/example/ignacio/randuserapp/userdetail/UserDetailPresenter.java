package com.example.ignacio.randuserapp.userdetail;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.ignacio.randuserapp.R;
import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.data.source.UserDataSource;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class UserDetailPresenter implements UserDetailContract.Presenter, UserDataSource.Local.OnFavInsertCallback {

    private final UserDetailContract.View userDetailView;

    @NonNull
    private final UserDataSource.Local mUsersRepository;

    public UserDetailPresenter(@NonNull UserDetailContract.View userDetailView, @NonNull UserDataSource.Local mUsersRepository) {
        this.userDetailView = userDetailView;
        this.mUsersRepository = mUsersRepository;

        userDetailView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void addContact(User mUser) {
        ArrayList<ContentProviderOperation> ops = new ArrayList <>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (mUser.getFullName() != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            mUser.getFullName()).build());
        }

        //------------------------------------------------------ Mobile Number
        if (mUser.getCell() != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mUser.getCell())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (mUser.getPhone() != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mUser.getPhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Email
        if (mUser.getEmail() != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, mUser.getEmail())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            ((UserDetailFragment)userDetailView).getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            userDetailView.showAddContactSuccess();
        } catch (Exception e) {
            e.printStackTrace();

            userDetailView.showAddContactFailure();
        }
    }

    @Override
    public void saveFavUser(User mFavUser) {
        //Llamar al repo local y guardar fav user
        mUsersRepository.insertUser(mFavUser, this);
    }

    @Override
    public void deleteFavUser(Integer mFavUserId) {
        mUsersRepository.deleteUserById(mFavUserId);
    }

    @Override
    public void onFavInsertSuccess(Integer userId) {
        userDetailView.setIdUser(userId);
    }

    @Override
    public void onFavInsertFailure() {
        userDetailView.showUserFavError();
    }
}
