package com.devhch.mirai.moviesapp_stage2.Database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/24/2020
 */
public class FavoriteViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = FavoriteViewModel.class.getSimpleName();

    // Using LiveData and caching what loadAllFavorite() returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<FavoriteEntry>> favorite;

    public FavoriteViewModel(Application application) {
        super(application);
        FavoriteRooDatabase database = FavoriteRooDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        favorite = database.favoriteDao().loadAllFavorite();
    }

    public LiveData<List<FavoriteEntry>> getFavorite() {
        return favorite;
    }
}

