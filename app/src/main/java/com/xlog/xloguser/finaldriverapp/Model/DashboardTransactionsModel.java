package com.xlog.xloguser.finaldriverapp.Model;

import android.support.v7.widget.RecyclerView;

public class DashboardTransactionsModel {
    private String transactionID;

    public DashboardTransactionsModel(String transactionID) {
        this.transactionID = transactionID;

    }


    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
}
