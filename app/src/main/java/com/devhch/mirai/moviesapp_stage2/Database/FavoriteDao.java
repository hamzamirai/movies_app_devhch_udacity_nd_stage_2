package com.devhch.mirai.moviesapp_stage2.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/24/2020
 */
@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite_table")
    LiveData<List<FavoriteEntry>> loadAllFavorite();

    @Query("SELECT * FROM favorite_table WHERE title = :title")
    List<FavoriteEntry> loadAll(String title);

    @Insert
    void insertFavorite(FavoriteEntry favoriteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(FavoriteEntry favoriteEntry);

    @Delete
    void deleteFavorite(FavoriteEntry favoriteEntry);

    @Query("DELETE FROM favorite_table WHERE movie_id = :movie_id")
    void deleteFavoriteById(int movie_id);

    @Query("DELETE FROM favorite_table")
    void deleteAllFavorite();

    @Query("SELECT * FROM favorite_table WHERE id = :id")
    LiveData<FavoriteEntry> loadFavoriteById(int id);
}


