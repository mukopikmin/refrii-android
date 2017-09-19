package com.refrii.client;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface AuthService {
    @GET("/auth/google/token")
    Call<Credential> getToken(@QueryMap Map<String, String> params);
}
