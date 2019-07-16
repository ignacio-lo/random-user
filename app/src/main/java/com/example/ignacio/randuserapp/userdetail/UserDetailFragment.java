package com.example.ignacio.randuserapp.userdetail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ignacio.randuserapp.R;
import com.example.ignacio.randuserapp.data.User;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserDetailFragment extends Fragment implements UserDetailContract.View {

    private User mUser;

    private UserDetailContract.Presenter mUserDetailPresenter;

    public UserDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(UserDetailActivity.USER_OBJ)) {
            CircleImageView civ_iconUserDetail;

            mUser = new User();

            mUser = (User) getArguments().getSerializable(UserDetailActivity.USER_OBJ);

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mUser.getFullName());
            }

            //Agregar como contacto
            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(R.string.fab_addcontact).setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUserDetailPresenter.addContact(mUser);

                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            civ_iconUserDetail = (CircleImageView) getActivity().findViewById(R.id.civ_iconUserDetail);

            Glide
                    .with(getActivity())
                    .load(mUser.getPicture().getLarge())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(civ_iconUserDetail);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);

        if (mUser != null) {
            TextView tvUsername;
            TextView tvMail;
            TextView tvPhone;
            TextView tvNation;

            tvUsername = rootView.findViewById(R.id.tv_udUsername);
            tvUsername.setText(mUser.getLogin().getUsername());

            tvMail = rootView.findViewById(R.id.tv_udMail);
            tvMail.setText(mUser.getEmail());

            tvPhone = rootView.findViewById(R.id.tv_udPhone);
            tvPhone.setText(mUser.getCell());

            tvNation = rootView.findViewById(R.id.tv_udNation);
            tvNation.setText(mUser.getNat());
        }

        return rootView;
    }

    @Override
    public void setPresenter(UserDetailContract.Presenter presenter) {
        this.mUserDetailPresenter = presenter;
    }

    @Override
    public void showAddContactSuccess() {
        Toast.makeText((this).getContext(), R.string.contact_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAddContactFailure() {
        Toast.makeText((this).getContext(), R.string.contact_not_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setIdUser(Integer userId) {
        mUser.setIdUser(userId);
    }

    @Override
    public void showUserFavError() {
        Toast.makeText((this).getContext(), R.string.fav_not_saved, Toast.LENGTH_SHORT).show();
    }
}
