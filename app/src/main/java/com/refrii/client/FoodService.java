package com.refrii.client;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface FoodService {

    @GET("/foods/{id}")
    Call<Food> getFood(
            @Header("Authorization") String token,
            @Path("id") int id);

    @POST("/foods")
    Call<Food> addFood(
            @Header("Authorization") String token,
            @Body RequestBody body);

    @PUT("/foods/{id}")
    Call<Food> updateFood(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body RequestBody body);
}
