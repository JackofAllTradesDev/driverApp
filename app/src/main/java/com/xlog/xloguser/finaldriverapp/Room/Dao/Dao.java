package com.xlog.xloguser.finaldriverapp.Room.Dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.xlog.xloguser.finaldriverapp.Room.Entity.Coordinates;
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

    @Insert
    void AddCoordinates(Coordinates... coordinates);

    @Query("SELECT * FROM Coordinates")
    List<Coordinates> getAll();

    @Query("DELETE FROM Coordinates")
    void deleteAll();

}
