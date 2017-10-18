package com.refrii.client;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UnitService {
    @GET("/units")
    Call<List<Unit>> getUnits();

    @PUT("/units/{id}")
    Call<Unit> updateUnit(
            @Path("id") int id,
            @Body RequestBody body);
}
