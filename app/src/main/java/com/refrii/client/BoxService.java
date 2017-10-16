package com.refrii.client;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BoxService {
    @GET("/boxes")
    Call<List<Box>> getBoxes();

    @GET("/boxes/{id}")
    Call<Box> getBox(@Path("id") int id);

    @PUT("/boxes/{id}")
    Call<Box> updateBox(
            @Path("id") int id,
            @Body RequestBody body);
}
