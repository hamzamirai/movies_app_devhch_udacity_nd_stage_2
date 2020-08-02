package com.devhch.mirai.moviesapp_stage2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.google.android.material.snackbar.Snackbar;
import com.devhch.mirai.moviesapp_stage2.Adapters.ReviewAdapter;
import com.devhch.mirai.moviesapp_stage2.Adapters.TrailerAdapter;
import com.devhch.mirai.moviesapp_stage2.Api.Client;
import com.devhch.mirai.moviesapp_stage2.Api.Service;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteEntry;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteExecutors;
import com.devhch.mirai.moviesapp_stage2.Database.FavoriteRooDatabase;
import com.devhch.mirai.moviesapp_stage2.Models.Movie;
import com.devhch.mirai.moviesapp_stage2.Models.Review;
import com.devhch.mirai.moviesapp_stage2.Models.ReviewResult;
import com.devhch.mirai.moviesapp_stage2.Models.Trailer;
import com.devhch.mirai.moviesapp_stage2.Models.TrailerResponse;
import com.devhch.mirai.moviesapp_stage2.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG_ERROR = "Error";
    public static final String KEY_PATH_POSTER = "path_poster";
    public static final String KEY_ORIGINAL_TITLE = "original_title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_MOVIE_ID = "movie_id";

    // We Create a data binding instance called mBinding of type ActivityDetailBinding
    private ActivityDetailBinding mBinding;

    private FavoriteRooDatabase mFavoriteRooDatabase;

    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private Movie favorite;

    private List<FavoriteEntry> entries = new ArrayList<>();
    private String thumbnail, movieName, synopsis, rating, dateOfRelease;
    private int movie_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*
         * DataBindUtil.setContentView replaces our normal call of setContent view.
         * DataBindingUtil also created our ActivityMainBinding that we will eventually use to
         * display all of our data.
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mFavoriteRooDatabase = FavoriteRooDatabase.getInstance(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getExtras() != null) {
            Bundle bundle = intentThatStartedThisActivity.getExtras();

            thumbnail = bundle.getString(KEY_PATH_POSTER);
            movieName = bundle.getString(KEY_ORIGINAL_TITLE);
            synopsis = bundle.getString(KEY_OVERVIEW);
            rating = bundle.getString(KEY_VOTE_AVERAGE);
            dateOfRelease = bundle.getString(KEY_RELEASE_DATE);
            movie_id = bundle.getInt(KEY_MOVIE_ID);

            Glide.with(this)
                    .load(thumbnail)
                    .placeholder(R.drawable.loading)
                    .into(mBinding.thumbnailImageHeader);

            RandomTransitionGenerator generator = new RandomTransitionGenerator(1000, new DecelerateInterpolator());
            mBinding.thumbnailImageHeader.setTransitionGenerator(generator);

            mBinding.movieTitle.append(" " + movieName);
            mBinding.plotSynopsis.append(" " + synopsis);
            mBinding.userRating.append(" " + rating + "/10");
            mBinding.releaseDate.append(" " + dateOfRelease);

            new CheckFavoriteStatusAsyncTask().execute();
            initViews();
        } else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        LinearLayoutManager mLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerViewTrailer.setLayoutManager(mLayoutManager);

        mBinding.recyclerViewTrailer.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadTrailers();
        loadReviews();
    }

    private void loadTrailers() {
        try {
            if (MainActivity.KEY_API.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.please_get_your_api_key), Toast.LENGTH_SHORT).show();
                return;
            } else {
                Client mClient = new Client();
                Service apiService = mClient.getClient().create(Service.class);
                Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, MainActivity.KEY_API);
                call.enqueue(new Callback<TrailerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                List<Trailer> trailer = response.body().getResults();
                                TrailerAdapter trailerAdapter = new TrailerAdapter(getApplicationContext(), trailer);
                                LinearLayoutManager firstManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                                mBinding.recyclerViewTrailer.setLayoutManager(firstManager);
                                mBinding.recyclerViewTrailer.setAdapter(trailerAdapter);
                                mBinding.emptyViewTrailer.setVisibility(trailerAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(DetailActivity.this, R.anim.layout_slide_right);
                                mBinding.recyclerViewTrailer.setLayoutAnimation(controller);
                                mBinding.recyclerViewTrailer.scheduleLayoutAnimation();

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                        Log.d(TAG_ERROR, Objects.requireNonNull(t.getMessage()));
                        Toast.makeText(DetailActivity.this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            Log.d(TAG_ERROR, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReviews() {
        try {
            if (MainActivity.KEY_API.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.please_get_your_api_key), Toast.LENGTH_SHORT).show();
                return;
            } else {
                Client mClient = new Client();
                Service apiService = mClient.getClient().create(Service.class);
                Call<Review> call = apiService.getReview(movie_id, MainActivity.KEY_API);

                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(@NonNull Call<Review> call, @NonNull Response<Review> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                List<ReviewResult> reviewResults = response.body().getResults();
                                ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(), reviewResults);
                                LinearLayoutManager firstManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                                mBinding.recyclerViewReview.setLayoutManager(firstManager);
                                mBinding.recyclerViewReview.setAdapter(reviewAdapter);

                                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(DetailActivity.this, R.anim.layout_slide_right);
                                mBinding.recyclerViewReview.setLayoutAnimation(controller);
                                mBinding.recyclerViewReview.scheduleLayoutAnimation();

                                mBinding.emptyViewReview.setVisibility(reviewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Review> call, @NonNull Throwable t) {

                    }
                });
            }

        } catch (Exception e) {
            Log.d(TAG_ERROR, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();
        }
    }


    public void saveFavorite() {
        String mThumbnail = thumbnail.replace(
                "https://image.tmdb.org/t/p/w500",
                ""
        ).trim();
        final FavoriteEntry favoriteEntry = new FavoriteEntry(
                movie_id,
                movieName,
                Double.parseDouble(rating),
                mThumbnail, synopsis);
        FavoriteExecutors.getInstance().diskIO().execute(() ->
                mFavoriteRooDatabase.favoriteDao().insertFavorite(favoriteEntry));
    }

    private void deleteFavorite(final int movie_id) {
        FavoriteExecutors.getInstance().diskIO().execute(() ->
                mFavoriteRooDatabase.favoriteDao().deleteFavoriteById(movie_id));
    }


    private class CheckFavoriteStatusAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            entries.clear();
            entries = mFavoriteRooDatabase.favoriteDao().loadAll(movieName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (entries.size() > 0) {
                mBinding.favoriteButton.setFavorite(true);
                mBinding.favoriteButton.setOnFavoriteChangeListener(
                        (buttonView, favorite) -> {
                            if (favorite) {
                                saveFavorite();
                                Snackbar.make(buttonView, getString(R.string.added_to_favorite),
                                        Snackbar.LENGTH_SHORT).show();
                            } else {
                                deleteFavorite(movie_id);
                                Snackbar.make(buttonView, getString(R.string.removed_from_favorite),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            } else {
                mBinding.favoriteButton.setOnFavoriteChangeListener(
                        (buttonView, favorite) -> {
                            if (favorite) {
                                saveFavorite();
                                Snackbar.make(buttonView, getString(R.string.added_to_favorite),
                                        Snackbar.LENGTH_SHORT).show();
                            } else {
                                int movie_id = Objects.requireNonNull(getIntent().getExtras()).getInt("id");
                                deleteFavorite(movie_id);
                                Snackbar.make(buttonView, getString(R.string.removed_from_favorite),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
