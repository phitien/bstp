package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.model.User;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by sgp0458 on 4/12/15.
 */
public interface IRestServiceAPI {
    @POST("/im-js-support-webapp/1/rest/authentication")
    Call<Response> login(@Body User user);
}
