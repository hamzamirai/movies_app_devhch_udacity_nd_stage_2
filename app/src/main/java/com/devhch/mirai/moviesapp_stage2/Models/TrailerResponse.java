package com.devhch.mirai.moviesapp_stage2.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/22/2020
 */

/**
 * {@link TrailerResponse}
 */
public class TrailerResponse {

    @SerializedName("id")
    private int id_trailer;
    @SerializedName("results")
    private List<Trailer> results;

    public TrailerResponse() {
    }

    /**
     * Create a new TrailerResponse object.
     *
     * @param id_trailer
     * @param results
     */
    public TrailerResponse(int id_trailer, List<Trailer> results) {
        this.id_trailer = id_trailer;
        this.results = results;
    }

    public int getIdTrailer() {
        return id_trailer;
    }

    public void seIdTrailer(int id_trailer) {
        this.id_trailer = id_trailer;
    }

    public List<Trailer> getResults() {
        return results;
    }


    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}

