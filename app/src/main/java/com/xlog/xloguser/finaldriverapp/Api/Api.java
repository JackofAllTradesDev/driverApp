package com.xlog.xloguser.finaldriverapp.Api;

import com.google.gson.JsonObject;
import com.xlog.xloguser.finaldriverapp.Model.Login;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.RetrieveLocation.RetrieveLocation;
import com.xlog.xloguser.finaldriverapp.Model.SendBase;
import com.xlog.xloguser.finaldriverapp.Model.SnapToRoad;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    /**DEV SIDE**/
    String URL = "http://ec2-35-174-156-110.compute-1.amazonaws.com/mobileapi/devapi/";

    /**QA SIDE**/
    String URLQA = "http://ec2-35-174-156-110.compute-1.amazonaws.com/mobileapi/qaapi/";
    String load = "http://qa-gps-api.xlog.asia/api/";

    /**PROD SIDE**/
    String URLPROD = "http://ec2-35-174-156-110.compute-1.amazonaws.com/mobileapi/prodapi/";
    String loadProd = "http://xlog-driver-api-prod-1.us-east-1.elasticbeanstalk.com/api/";

    /**UAT SIDE**/
    String URLUAT = "http://ec2-35-174-156-110.compute-1.amazonaws.com/mobileapi/uatapi/";
    String loadUAT = "http://xlog-driver-api-uat.us-east-1.elasticbeanstalk.com/api/";

    @GET("loginauth.php")
    Call<Login> getToken(@Query("client_secret") String client_secret, @Query("client_id") String client_id, @Query("username") String username, @Query("password") Integer password, @Query("grant_type") String grant_type, @Query("scope") String scope );
    @GET("currentUserDetails.php")
    Call<UserDetails> getUserDetails(@Query("access_token") String access_token);
    @POST("snaptoroad.php")
    Call<SnapToRoad> getCoordinates(@Query("coordinates") String coordinates, @Query("driverId") int driver, @Query("prefixId")String prefixId);
    @POST("truckerList.php")
    Call<List<ReservationList>> getReservationList(@Query("access_token") String token);
    @POST("truckerList.php")
    Call<List<ReservationList>> getInfo(@Query("access_token") String token, @Query("prefixId")String trans);
    @POST("saveattachment.php")
    Call<List<SendBase>> sendBase64(@Body ArrayList<SendBase> post);
    @Headers("Authorization: Basic Z3BzYXBpZGV2OlFYbDFjMmc2U1c1a2FXRQ")
    @GET("driver-locations")
    Call<RetrieveLocation> getLocation(@Query("prefixedId") String prefix, @Query("driverId") String driverID, @Query("start") String start, @Query("end") String endTime, @Query("type") String type);
    @GET("setroutestatus.php")
    Call<JsonObject> setRoutStatus(@Query("status") int status, @Query("trucking_reservation_id") int trID, @Query("trucker_truck_id") int ttID, @Query("prefixId") String prID,@Query("access_token") String access_token );
}
