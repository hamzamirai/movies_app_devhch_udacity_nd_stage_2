package com.devhch.mirai.moviesapp_stage2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.devhch.mirai.moviesapp_stage2.Models.Trailer;
import com.devhch.mirai.moviesapp_stage2.R;
import com.devhch.mirai.moviesapp_stage2.databinding.TrailerCardBinding;

import java.util.List;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/22/2020
 */

/**
 * {@link TrailerAdapter} is a {@link RecyclerView.Adapter} that can provide the layout for
 * each list item based on a data source which is a list of {@link Trailer} objects.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    /**
     * Context of the app
     */
    private Context mContext;
    private List<Trailer> trailerList;

    /**
     * Create a new {@link TrailerAdapter} object.
     *
     * @param mContext    is the current context (i.e. Activity) that the adapter is being created in.
     * @param trailerList is the list of {@link Trailer}s to be displayed.
     */
    public TrailerAdapter(Context mContext, List<Trailer> trailerList) {
        this.mContext = mContext;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TrailerCardBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.trailer_card, parent, false);
        return new TrailerAdapter.MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(final TrailerAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.binding.title.setText(trailerList.get(i).getName());
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * trailerList has not been updated (means initially, it's null, and we can't return null).
     *
     * @return trailerList.size()
     */
    @Override
    public int getItemCount() {
        return trailerList != null ? trailerList.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TrailerCardBinding binding;

        public MyViewHolder(TrailerCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.trailerCardView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String videoId = trailerList.get(pos).getKey();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("VIDEO_ID", videoId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

