package com.refrii.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface BoxService {
    @GET("/boxes")
    Call<List<Box>> getBoxes(@Header("Authorization") String token);

    @GET("/boxes/{id}")
    Call<Box> getBox(
            @Header("Authorization") String token,
            @Path("id") int id);
}
