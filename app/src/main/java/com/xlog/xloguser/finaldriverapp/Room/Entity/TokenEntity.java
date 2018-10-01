package com.xlog.xloguser.finaldriverapp.Room.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Token")
public class TokenEntity {
    @PrimaryKey(autoGenerate = true)
    public Integer id = 1;
    public String access_token;

    public TokenEntity(Integer id, String access_token) {
        this.id = id;
        this.access_token = access_token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}

