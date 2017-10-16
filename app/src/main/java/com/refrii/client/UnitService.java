package com.refrii.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UnitService {
    @GET("/units")
    Call<List<Unit>> getUnits();
}
