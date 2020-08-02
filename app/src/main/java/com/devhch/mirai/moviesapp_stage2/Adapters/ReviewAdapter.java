package com.devhch.mirai.moviesapp_stage2.Adapters;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.devhch.mirai.moviesapp_stage2.Models.ReviewResult;
import com.devhch.mirai.moviesapp_stage2.R;
import com.devhch.mirai.moviesapp_stage2.databinding.ReviewCardBinding;

import java.util.List;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/22/2020
 */

/**
 * {@link ReviewAdapter} is a {@link RecyclerView.Adapter} that can provide the layout for
 * each list item based on a data source which is a list of {@link ReviewResult} objects.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    /**
     * Context of the app
     */
    private Context mContext;
    private List<ReviewResult> reviewResults;

    public ReviewAdapter(Context mContext, List<ReviewResult> reviewResults) {
        this.mContext = mContext;
        this.reviewResults = reviewResults;

    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ReviewCardBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.review_card, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.binding.reviewAuthor.setText(reviewResults.get(i).getAuthor());
        viewHolder.binding.reviewLink.setText(reviewResults.get(i).getUrl());
        Linkify.addLinks(viewHolder.binding.reviewLink, Linkify.WEB_URLS);
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * reviewResults has not been updated (means initially, it's null, and we can't return null).
     *
     * @return reviewResults.size()
     */
    @Override
    public int getItemCount() {
        return reviewResults != null ? reviewResults.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ReviewCardBinding binding;

        public MyViewHolder(@NonNull ReviewCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

