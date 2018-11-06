package com.xlog.xloguser.finaldriverapp.Room.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
@Entity(tableName = "Transactions")
public class Transactions {
    public Transactions(Integer id, String transactionID, Integer status) {
        this.id = id;
        this.transactionID = transactionID;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @PrimaryKey(autoGenerate = true)
    public Integer id = 0;
    public String transactionID;
    public Integer status;



}
