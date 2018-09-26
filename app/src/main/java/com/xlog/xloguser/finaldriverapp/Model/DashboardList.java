package com.xlog.xloguser.finaldriverapp.Model;

public class DashboardList {
    String transactionID;

    public DashboardList(){

    }

    public DashboardList(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
}
