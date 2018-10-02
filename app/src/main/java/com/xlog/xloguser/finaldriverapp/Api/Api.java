package com.xlog.xloguser.finaldriverapp.Api;

import com.xlog.xloguser.finaldriverapp.Model.Login;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.SnapToRoad;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    String URL = "http://ec2-35-174-156-110.compute-1.amazonaws.com/codeigniter/";
    String userDetails = "http://ec2-35-174-156-110.compute-1.amazonaws.com/codeigniter/";
    String snapToRoadUrl = "http://ec2-35-174-156-110.compute-1.amazonaws.com/codeigniter/";
    String truckerList = "http://ec2-35-174-156-110.compute-1.amazonaws.com/codeigniter/";
    String transactionNumber = "http://ec2-35-174-156-110.compute-1.amazonaws.com/codeigniter/";

    @GET("mobile_api/loginauth.php")
    Call<Login> getToken(@Query("client_secret") String client_secret, @Query("client_id") String client_id, @Query("username") String username, @Query("password") String password, @Query("grant_type") String grant_type, @Query("scope") String scope );
    @GET("mobile_api/currentUserDetails.php")
    Call<UserDetails> getUserDetails(@Query("access_token") String access_token);
    @POST("mobile_api/snaptoroad.php")
    Call<SnapToRoad> getCoordinates(@Query("coordinates") String coordinates, @Query("driverId") int driver, @Query("prefixId")String prefixId);
    @GET("mobile_api/truckerList.php")
    Call<List<ReservationList>> getReservationList(@Query("access_token") String token);
    @GET("mobile_api/truckerList.php")
    Call<List<ReservationList>> getInfo(@Query("access_token") String token, @Query("prefixId")String trans);
//    @POST("mobile_api/truckerList.php")
//    @FormUrlEncoded
//    Call<List<ReservationList>> savePost(@Query("access_token") String token, @Query("prefixId")String trans, @Field("trucker") int track);
}
