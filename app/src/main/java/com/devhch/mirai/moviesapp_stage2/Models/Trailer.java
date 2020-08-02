package com.devhch.mirai.moviesapp_stage2.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/22/2020
 */

/**
 * {@link Trailer}
 */
public class Trailer {

    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;

    public Trailer() {
    }

    /**
     * Create a new Trailer object.
     *
     * @param key
     * @param name
     */
    public Trailer(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

