package com.xlog.xloguser.finaldriverapp.Room.Dao;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.xlog.xloguser.finaldriverapp.Room.Entity.TokenEntity;

import java.util.List;
@android.arch.persistence.room.Dao
public interface Dao {

    @Insert
    void addToken(TokenEntity... tokenEntities);

    @Query("SELECT * FROM token")
    List<TokenEntity> getToken();


    @Query("UPDATE Token SET access_token= :token WHERE id = :id")
    void update(String token, int id);

    @Query("SELECT COUNT(*) from Token")
    int countCountries();

}
