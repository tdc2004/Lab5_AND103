package com.chinhdev.lab5_and103.APIServer;

import com.chinhdev.lab5_and103.model.DistributorModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIServer {
    @GET("/list")
    Call<ArrayList<DistributorModel>> getDistributor();

    @POST("/list/add")
    Call<Void> postDistributor(@Body DistributorModel model);
    @PUT("/list/{id}")
    Call<Void> putDistributor(@Path("id") String id, @Body DistributorModel model);
    @DELETE("/list/{id}")
    Call<Void> deleteDistributor(@Path("id") String id);
    @GET("search_distributor")
    Call<ArrayList<DistributorModel>> searchDistributor(@Query("key") String key);

}
