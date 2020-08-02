package com.devhch.mirai.moviesapp_stage2.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/24/2020
 */

/**
 * A basic class representing an entity that is a row in a one-column database table.
 *
 * @ Entity - You must annotate the class as an entity and supply a table name if not class name.
 * @ PrimaryKey - You must identify the primary key.
 * @ ColumnInfo - You must supply the column name if it is different from the variable name.
 */
@Entity(tableName = "favorite_table")
public class FavoriteEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "movie_id")
    private int movieId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "user_rating")
    private Double userRating;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "overview")
    private String overview;

    @Ignore
    public FavoriteEntry(int movieId, String title, Double userRating, String posterPath, String overview) {
        this.movieId = movieId;
        this.title = title;
        this.userRating = userRating;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    public FavoriteEntry(int id, int movieId, String title, Double userRating, String posterPath, String overview) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.userRating = userRating;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setOverview(String image) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

}

