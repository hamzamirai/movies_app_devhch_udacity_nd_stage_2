package com.devhch.mirai.moviesapp_stage2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devhch.mirai.moviesapp_stage2.Adapters.MoviesAdapter;
import com.devhch.mirai.moviesapp_stage2.Api.Client;
import com.devhch.mirai.moviesapp_stage2.Api.Service;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteEntry;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteExecutors;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteRooDatabase;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteViewModel;
import com.devhch.mirai.moviesapp_stage2.Models.Movie;
import com.devhch.mirai.moviesapp_stage2.Models.MoviesResponse;
import com.devhch.mirai.moviesapp_stage2.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // Get YOur API KEY from themoviedb.org
    public static final String KEY_API = "";
    public static final String FONT_NAME = "red_rose.ttf";
    public static final String TAG_ERROR = "Error";

    // We Create a data binding instance called mBinding of type ActivityMainBinding
    private ActivityMainBinding mBinding;

    private MoviesAdapter adapter;
    private ProgressDialog progressDialog;

    private static String LIST_STATE = "list_state";
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private ArrayList<Movie> moviesInstance = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * DataBindUtil.setContentView replaces our normal call of setContent view.
         * DataBindingUtil also created our ActivityMainBinding that we will eventually use to
         * display all of our data.
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Animation animationFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);

        mBinding.tvWelcome.setAnimation(animationFromLeft);
        mBinding.tvSeeToday.setAnimation(animationFromLeft);

        mBinding.noInternetConnection.append(getString(R.string.make_sure_that_you_are_connected));

        mBinding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);

        if (isNetworkAvailable()) {
            mBinding.noInternetConnection.setVisibility(View.GONE);
            if (savedInstanceState != null) {
                moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
                savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
                displayData();
            } else {
                initViews();
            }
        } else if (!checkSortOrder().equals(getString(R.string.favorite))) {
            mBinding.relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mBinding.noInternetConnection.setVisibility(View.VISIBLE);
            mBinding.swipeRefreshLayout.setVisibility(View.GONE);
            mBinding.noInternetConnection.setOnClickListener(v -> {
                if (isNetworkAvailable()) {
                    mBinding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                    mBinding.relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    initViews();
                    mBinding.noInternetConnection.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (checkSortOrder().equals(this.getString(R.string.favorite))) {
            getAllFavorite();
        }

        mBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkAvailable()) {
                initViews();
                Toast.makeText(MainActivity.this, R.string.movies_refreshed, Toast.LENGTH_SHORT).show();
            } else if (!checkSortOrder().equals(getString(R.string.favorite))) {
                mBinding.noInternetConnection.setVisibility(View.VISIBLE);
                mBinding.relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mBinding.swipeRefreshLayout.setVisibility(View.GONE);
                mBinding.noInternetConnection.setOnClickListener(v -> {
                    if (isNetworkAvailable()) {
                        mBinding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                        mBinding.relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        initViews();
                        mBinding.noInternetConnection.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (checkSortOrder().equals(this.getString(R.string.favorite))) {
                getAllFavorite();
            } else {
                if (mBinding.swipeRefreshLayout.isRefreshing()) {
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        // The Method makes app title font change to red_rose.
        changeAppTitleFont();
    }

    private void changeAppTitleFont() {
        ActionBar actionBar = getSupportActionBar();
        TextView tv = new TextView(getApplicationContext());
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_NAME);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        tv.setText(getString(R.string.app_name)); // ActionBar title text
        tv.setTextSize(18);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTypeface(typeface, Typeface.BOLD);
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);
    }

    private void displayData() {
        adapter = new MoviesAdapter(this, moviesInstance);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mBinding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 6));
        }
        mBinding.recyclerViewMovies.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerViewMovies.setAdapter(adapter);
        restoreLayoutManagerPosition();
        adapter.notifyDataSetChanged();
    }

    private void restoreLayoutManagerPosition() {
        if (savedRecyclerLayoutState != null) {
            RecyclerView.LayoutManager layoutManager = mBinding.recyclerViewMovies.getLayoutManager();
            assert layoutManager != null;
            layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.fetching_movies));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mBinding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 6));
        }

        mBinding.recyclerViewMovies.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerViewMovies.setAdapter(adapter);

        // create some animation to recycler view item loading
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.this,
                R.anim.layout_slide_bottom);
        mBinding.recyclerViewMovies.setLayoutAnimation(controller);
        mBinding.recyclerViewMovies.scheduleLayoutAnimation();

        loadJSON();
    }

    private void loadJSON() {
        String sortOrder = checkSortOrder();

        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            try {
                if (KEY_API.isEmpty()) {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.please_obtain_api_key_from),
                            Toast.LENGTH_SHORT
                    ).show();
                    if (mBinding.swipeRefreshLayout.isRefreshing()) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                    progressDialog.dismiss();
                    return;
                }

                Client mClient = new Client();
                Service apiService =
                        mClient.getClient().create(Service.class);
                Call<MoviesResponse> call = apiService.getPopularMovies(KEY_API);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                List<Movie> movies = response.body().getResults();
                                moviesInstance.clear();
                                moviesInstance.addAll(movies);
                                mBinding.recyclerViewMovies.setAdapter(new MoviesAdapter(MainActivity.this, movies));

                                // create some animation to recycler view item loading
                                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.this,
                                        R.anim.layout_slide_bottom);
                                mBinding.recyclerViewMovies.setLayoutAnimation(controller);
                                mBinding.recyclerViewMovies.scheduleLayoutAnimation();

                                mBinding.recyclerViewMovies.smoothScrollToPosition(0);
                                if (mBinding.swipeRefreshLayout.isRefreshing()) {
                                    mBinding.swipeRefreshLayout.setRefreshing(false);
                                }
                                progressDialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                        Log.d(TAG_ERROR, Objects.requireNonNull(t.getMessage()));
                        Toast.makeText(MainActivity.this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                Log.d(TAG_ERROR, Objects.requireNonNull(e.getMessage()));
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (sortOrder.equals(this.getString(R.string.favorite))) {
            getAllFavorite();
            progressDialog.dismiss();
        } else {
            try {
                if (KEY_API.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_obtain_api_key_from), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    if (mBinding.swipeRefreshLayout.isRefreshing()) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                    return;
                }

                Client mClient = new Client();
                Service apiService =
                        mClient.getClient().create(Service.class);
                Call<MoviesResponse> call = apiService.getTopRatedMovies(KEY_API);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                        assert response.body() != null;
                        List<Movie> movies = response.body().getResults();
                        moviesInstance.clear();
                        moviesInstance.addAll(movies);
                        mBinding.recyclerViewMovies.setAdapter(new MoviesAdapter(MainActivity.this, movies));

                        // create some animation to recycler view item loading
                        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.this,
                                R.anim.layout_slide_left);
                        mBinding.recyclerViewMovies.setLayoutAnimation(controller);
                        mBinding.recyclerViewMovies.scheduleLayoutAnimation();

                        mBinding.recyclerViewMovies.smoothScrollToPosition(0);
                        if (mBinding.swipeRefreshLayout.isRefreshing()) {
                            mBinding.swipeRefreshLayout.setRefreshing(false);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                        Log.d(TAG_ERROR, Objects.requireNonNull(t.getMessage()));
                        Toast.makeText(MainActivity.this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                Log.d(TAG_ERROR, Objects.requireNonNull(e.getMessage()));
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String checkSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (isNetworkAvailable()) {
            savedInstanceState.putParcelableArrayList(LIST_STATE, moviesInstance);
            savedInstanceState.putParcelable(
                    BUNDLE_RECYCLER_LAYOUT,
                    Objects.requireNonNull(mBinding.recyclerViewMovies.getLayoutManager())
                            .onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (isNetworkAvailable()) {
            moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (checkSortOrder().equals(getString(R.string.favorite))) {
            menu.findItem(R.id.delete_all_fav)
                    .setVisible(true);
        } else {
            menu.findItem(R.id.delete_all_fav)
                    .setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.delete_all_fav:

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle( getString(R.string.dialog_delete_title))
                        .setMessage( getString(R.string.dialog_delete_message))
                        .setPositiveButton( getString(R.string.dialog_delete_positive_button), (dialog, which) -> {
                            FavoriteRooDatabase mFavoriteRooDatabase = FavoriteRooDatabase.getInstance(getApplicationContext());
                            FavoriteExecutors.getInstance().diskIO().execute(() ->
                                    mFavoriteRooDatabase.favoriteDao().deleteAllFavorite());
                        }).setNegativeButton( getString(R.string.dialog_delete_negative_button), (dialog, which) -> {

                });
                builder.show();
                break;
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBinding.emptyViewFavorite.setVisibility(View.GONE);
        if (isNetworkAvailable()) {
            initViews();
        } else if (!checkSortOrder().equals(getString(R.string.favorite))) {
            mBinding.noInternetConnection.setVisibility(View.VISIBLE);
            mBinding.relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mBinding.swipeRefreshLayout.setVisibility(View.GONE);
            mBinding.noInternetConnection.setOnClickListener(v -> {
                if (isNetworkAvailable()) {
                    mBinding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                    mBinding.relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    initViews();
                    mBinding.noInternetConnection.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (checkSortOrder().equals(this.getString(R.string.favorite))) {
            mBinding.noInternetConnection.setVisibility(View.GONE);
            getAllFavorite();
        } else {
            if (mBinding.swipeRefreshLayout.isRefreshing()) {
                mBinding.swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void getAllFavorite() {

        // Get a new or existing ViewModel from the ViewModelProvider.
        FavoriteViewModel favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        // Add an observer on the LiveData returned by loadAllFavorite().
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        favoriteViewModel.getFavorite().observe(this, favoriteEntries -> {
            moviesInstance.clear();
            assert favoriteEntries != null;
            for (FavoriteEntry entry : favoriteEntries) {
                Movie movie = new Movie();
                movie.setId(entry.getMovieId());
                movie.setOverview(entry.getOverview());
                movie.setOriginalTitle(entry.getTitle());
                movie.setPosterPath(entry.getPosterPath());
                movie.setVoteAverage(entry.getUserRating());
                moviesInstance.add(movie);
            }
            MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, moviesInstance);
            mBinding.recyclerViewMovies.setAdapter(moviesAdapter);

            // create some animation to recycler view item loading
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.this,
                    R.anim.layout_slide_right);
            mBinding.recyclerViewMovies.setLayoutAnimation(controller);
            mBinding.recyclerViewMovies.scheduleLayoutAnimation();

            if (moviesAdapter.getItemCount() == 0) {
                mBinding.emptyViewFavorite.setVisibility(View.VISIBLE);
                //recyclerView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mBinding.emptyViewFavorite.setVisibility(View.GONE);
                //recyclerView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            if (mBinding.swipeRefreshLayout.isRefreshing()) {
                mBinding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}