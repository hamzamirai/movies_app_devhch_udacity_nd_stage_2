package com.devhch.mirai.moviesapp_stage2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devhch.mirai.moviesapp_stage2.DetailActivity;
import com.devhch.mirai.moviesapp_stage2.Models.Movie;
import com.devhch.mirai.moviesapp_stage2.R;
import com.devhch.mirai.moviesapp_stage2.databinding.MovieCardBinding;


import java.util.List;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/5/2020
 */

/**
 * {@link MoviesAdapter} is a {@link RecyclerView.Adapter} that can provide the layout for
 * each list item based on a data source which is a list of {@link Movie} objects.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    /**
     * Context of the app
     */
    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter(Activity mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MoviesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MovieCardBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.movie_card, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MyViewHolder holder, int position) {
        holder.binding.title.setText(movieList.get(position).getOriginalTitle());

        String vote = Double.toString(movieList.get(position).getVoteAverage());
        holder.binding.userRating.setText(String.format("Rating: %s/10", vote));

        Glide.with(mContext)
                .load(movieList.get(position).getPosterPath())
                .placeholder(R.drawable.loading)
                .into(holder.binding.thumbnail);

        holder.itemView.setOnClickListener(view -> {
            if (position != RecyclerView.NO_POSITION) {
                Movie clickedDataItem = movieList.get(position);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra( DetailActivity.KEY_MOVIE_ID, movieList.get(position).getId());
                intent.putExtra( DetailActivity.KEY_ORIGINAL_TITLE, movieList.get(position).getOriginalTitle());
                intent.putExtra( DetailActivity.KEY_PATH_POSTER, movieList.get(position).getPosterPath());
                intent.putExtra( DetailActivity.KEY_OVERVIEW, movieList.get(position).getOverview());
                intent.putExtra( DetailActivity.KEY_VOTE_AVERAGE, Double.toString(movieList.get(position).getVoteAverage()));
                intent.putExtra( DetailActivity.KEY_RELEASE_DATE, movieList.get(position).getReleaseDate());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * movieList has not been updated (means initially, it's null, and we can't return null).
     *
     * @return movieList.size()
     */
    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private MovieCardBinding binding;

        public MyViewHolder(@NonNull MovieCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

