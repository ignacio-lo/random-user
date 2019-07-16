package com.example.ignacio.randuserapp.users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ignacio.randuserapp.R;
import com.example.ignacio.randuserapp.data.User;
import com.example.ignacio.randuserapp.data.UserMapper;
import com.example.ignacio.randuserapp.userdetail.UserDetailActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.util.Preconditions.checkNotNull;
import static com.example.ignacio.randuserapp.util.ActivityUtils.getActivity;

public class UsersView extends FrameLayout implements UsersContract.View {

    private UsersContract.Presenter mUsersPresenter;

    public UsersRecyclerViewAdapter mUsersAdapter;

    public EndlessRecyclerViewScrollListener mScrollListener;

    private FavUsersAdapter mFavUsersAdapter;

    private RecyclerView rvUsers;

    private RecyclerView rvFavUsers;

    private RelativeLayout mRvLayoutErrorRefreshUsers;

    public UsersView(Context context) {
        super(context);
        init();
    }

    public UsersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.user_list_frame, this);

        rvUsers = findViewById(R.id.rvUsers);
        rvFavUsers = findViewById(R.id.rvFavUsers);

        rvUsers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManagerFav = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvUsers.setLayoutManager(linearLayoutManager);
        rvFavUsers.setLayoutManager(linearLayoutManagerFav);

        mUsersAdapter = new UsersRecyclerViewAdapter(new ArrayList<User>(0));

        mFavUsersAdapter = new FavUsersAdapter(new ArrayList<User>(0));

        rvUsers.setAdapter(mUsersAdapter);
        rvFavUsers.setAdapter(mFavUsersAdapter);

        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    mUsersPresenter.loadMore(page);
            }
        };

        rvUsers.addOnScrollListener(mScrollListener);

        //Refresh por si da error el server o no tengo internet
        mRvLayoutErrorRefreshUsers = findViewById(R.id.rvlayout_error_users);
        mRvLayoutErrorRefreshUsers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadUsers();
            }
        });
    }

    @Override
    public void showUsers(List<User> users) {
        //Refresco la lista del adapter con los primeros usuarios
        mUsersAdapter.replaceData(users);

        //Marco los usuarios favoritos en la lista de la api
        markFavUsersList();
    }

    @Override
    public void showNoUsersError() {
        //Error al mostrar usuarios
        mRvLayoutErrorRefreshUsers.setVisibility(View.VISIBLE);

        rvUsers.setVisibility(View.GONE);
    }

    @Override
    public void showMoreUsers(List<User> users) {
        //Refresco la lista del adapter con los nuevos usuarios
        mUsersAdapter.addMoreData(users);

        //Marco los usuarios favoritos en la lista de la api
        markFavUsersList();
    }

    @Override
    public void showNoMoreUsersError(int lastPageFetched) {
        Toast.makeText((this).getContext(), R.string.error_getting_users, Toast.LENGTH_SHORT).show();

        mScrollListener.resetState(lastPageFetched);
    }

    @Override
    public void showFavUsers(List<User> favUsers) {
        mFavUsersAdapter.replaceDataFav(favUsers);

        rvFavUsers.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFavUsers() {
        //Si no tengo favoritos
        mFavUsersAdapter.clearDataFav();

        rvFavUsers.setVisibility(View.GONE);
    }

    @Override
    public void markFavUser(User favUser) {
        mUsersAdapter.markUserAsFav(favUser);
    }

    @Override
    public void markFavUsersList() {
        mUsersAdapter.markUsersListAsFav();
    }

    @Override
    public void reloadUsers() {
        mRvLayoutErrorRefreshUsers.setVisibility(View.GONE);

        mUsersPresenter.loadUsers();

        rvUsers.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUserDetail(View view) {
        checkNotNull(view);

        User mUser = (User) view.getTag();

        Activity activity = getActivity(view);

        Intent intent = new Intent(getContext(), UserDetailActivity.class);

        intent.putExtra(UserDetailActivity.USER_OBJ, mUser);

        activity.startActivityForResult(intent, UserDetailActivity.REQUEST_USER_DETAIL);
    }

    @Override
    public void setPresenter(UsersContract.Presenter presenter) {
        mUsersPresenter = presenter;
    }

    //Adapter Endless Scrolling
    public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

        private List<User> mUsers;
        private List<User> mUsersForFilter;

        private static final int VIEW_TYPE_USER = 0;
        private static final int VIEW_TYPE_LOADING = 1;

        private UsersRecyclerViewAdapter(List<User> mUsers) {
            this.mUsers = mUsers;
        }

        //Evento al hacer click en una fila
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(view);
            }
        };

        private void replaceData(List<User> users) {
            mUsers = users;

            this.mUsersForFilter = new ArrayList<>(mUsers);

            notifyDataSetChanged();
        }

        private void addMoreData(List<User> users) {
            mUsers.addAll(users);

            this.mUsersForFilter = new ArrayList<>(mUsers);

            notifyDataSetChanged();
        }

        //Marco o desmarco usuarios favoritos, comparando la lista de favoritos con la lista de la api
        //Se usa al iniciar la app o al cargar una nueva pagina de usuarios de la paginacion
        private void markUsersListAsFav() {
            if (!mFavUsersAdapter.mFavUsers.isEmpty()) {
                for (User favUser : mFavUsersAdapter.mFavUsers) {
                    for (User user : mUsersAdapter.mUsers) {
                        //Uso el login id que proporciona la api para matchear los usuarios y setearle el id autogenerado por room + favorito
                        if (user.getLogin().getUuid().equals(favUser.getLogin().getUuid())) {
                            user.setIdUser(favUser.getIdUser());
                            user.setFavorite(favUser.isFavorite());
                        }
                    }
                }

                notifyDataSetChanged();
            }
        }

        //Marco o desmarco un usuario favorito en la lista
        private void markUserAsFav(User favUser) {
            for (User user : mUsersAdapter.mUsers) {
                //Uso el login id que proporciona la api para matchear el usuario y setearle el id autogenerado por room + favorito
                if (user.getLogin().getUuid().equals(favUser.getLogin().getUuid())) {
                    user.setIdUser(favUser.getIdUser());
                    user.setFavorite(favUser.isFavorite());
                }
            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            //Muestro el layout del usuario o del progress bar si es el ultimo de la lista
            switch (viewType) {
                case VIEW_TYPE_USER:

                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_content, parent, false);

                    return new UserViewHolder(view);

                case VIEW_TYPE_LOADING:

                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_progress, parent, false);

                    return new LoadingViewHolder(view);

                default:
                    throw new IllegalArgumentException("unexpected viewType: " + viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_USER:
                    final UserViewHolder mUserViewHolder = (UserViewHolder) holder;

                    mUserViewHolder.mTvFullName.setText(mUsers.get(position).getFullName());

                    //Imagen del perfil
                    Glide
                            .with(((UsersActivity) getContext()))
                            .load(mUsers.get(position).getPicture().getMedium())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    mUserViewHolder.mProgressBar.setVisibility(View.GONE);
                                    mUserViewHolder.mCivImgProfileUser.setVisibility(View.VISIBLE);
                                    mUserViewHolder.mCivImgProfileUser.setImageResource(R.drawable.ic_error_black_24dp);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    mUserViewHolder.mProgressBar.setVisibility(View.GONE);
                                    mUserViewHolder.mCivImgProfileUser.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(mUserViewHolder.mCivImgProfileUser);

                    mUserViewHolder.itemView.setTag(mUsers.get(position));
                    mUserViewHolder.itemView.setOnClickListener(mOnClickListener);

                    break;

                case VIEW_TYPE_LOADING:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        @Override
        public int getItemViewType(int position) {
            //Muestro el layout del usuario o del progress bar si es el ultimo de la lista y no esta el SearchView filtrando
            return (position == mUsers.size() - 1 && !mScrollListener.searchViewFiltering) ? VIEW_TYPE_LOADING : VIEW_TYPE_USER;
        }

        @Override
        public Filter getFilter() {
            return mUserFilter;
        }

        private Filter mUserFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<User> usersFiltered = new ArrayList<>();
                    if (constraint == null | constraint.length() == 0) {
                        usersFiltered.addAll(mUsersForFilter);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (User user : mUsersForFilter) {
                            if (user.getFullName().toLowerCase().contains(filterPattern)) {
                                usersFiltered.add(user);
                            }
                        }
                    }

                    FilterResults results = new FilterResults();
                    results.values = usersFiltered;

                    return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mUsers.clear();
                mUsers.addAll((List<User>) results.values);
                notifyDataSetChanged();
            }
        };

        class UserViewHolder extends RecyclerView.ViewHolder {
            final TextView mTvFullName;
            final ProgressBar mProgressBar;
            final CircleImageView mCivImgProfileUser;

            UserViewHolder(View view) {
                super(view);
                mProgressBar = (ProgressBar) view.findViewById(R.id.pb_userListContent);
                mTvFullName = (TextView) view.findViewById(R.id.tv_fullName);
                mCivImgProfileUser = (CircleImageView) view.findViewById(R.id.civ_imgProfileUser);
            }
        }

        class LoadingViewHolder extends RecyclerView.ViewHolder {
            final ProgressBar mLoadingProgressBar;

            LoadingViewHolder(View view) {
                super(view);
                mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.pb_loadMoreUsers);
            }
        }
    }

    public class FavUsersAdapter extends RecyclerView.Adapter<FavUsersAdapter.FavViewHolder> {

        private List<User> mFavUsers;

        private FavUsersAdapter(List<User> mFavUsers) {
            this.mFavUsers = mFavUsers;
        }

        @NonNull
        @Override
        public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_user_list_content, parent, false);

            return new FavViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FavViewHolder favViewHolder, int position) {
            favViewHolder.mFavUserName.setText(UserMapper.capitalizeName(mFavUsers.get(position).getName().getFirst()));

            //Imagen del perfil
            Glide
                    .with(((UsersActivity)getContext()))
                    .load(mFavUsers.get(position).getPicture().getLarge())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            favViewHolder.mFavProgressBar.setVisibility(View.GONE);
                            favViewHolder.mFavUserIcon.setVisibility(View.VISIBLE);
                            favViewHolder.mFavUserIcon.setImageResource(R.drawable.ic_error_black_24dp);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            favViewHolder.mFavProgressBar.setVisibility(View.GONE);
                            favViewHolder.mFavUserIcon.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(favViewHolder.mFavUserIcon);

            favViewHolder.itemView.setTag(mFavUsers.get(position));
            favViewHolder.itemView.setOnClickListener(mFavOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mFavUsers.size();
        }

        private void replaceDataFav(List<User> users) {
            mFavUsers.clear();

            mFavUsers.addAll(users);

            notifyDataSetChanged();
        }

        private void clearDataFav() {
            mFavUsers.clear();

            notifyDataSetChanged();
        }

        //Evento al hacer click en una fila
        private final View.OnClickListener mFavOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(view);
            }
        };

        class FavViewHolder extends RecyclerView.ViewHolder {
            final TextView mFavUserName;
            final CircleImageView mFavUserIcon;
            final ProgressBar mFavProgressBar;

            FavViewHolder(View view) {
                super(view);
                mFavUserName = (TextView) view.findViewById(R.id.tv_favUserName);
                mFavUserIcon = (CircleImageView) view.findViewById(R.id.civ_iconUser);
                mFavProgressBar = (ProgressBar) view.findViewById(R.id.pb_favUserListContent);
            }
        }
    }
}