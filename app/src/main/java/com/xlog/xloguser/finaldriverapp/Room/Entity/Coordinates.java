package com.xlog.xloguser.finaldriverapp.Room.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Coordinates")
public class Coordinates {
    @PrimaryKey(autoGenerate = true)
    public Integer id = 0;
    public String latLang;

    public Integer getId() {
        return id;
    }

    public String getLatLang() {
        return latLang;
    }

    public Coordinates(Integer id, String latLang) {
        this.id = id;
        this.latLang = latLang;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLatLang(String latLang) {
        this.latLang = latLang;
    }
}
