package com.xlog.xloguser.finaldriverapp.Model;

public class PendingTransactionModel {
    private String transactionID;
    private String contentTxt;

    public PendingTransactionModel(String transactionID, String contentTxt) {
        this.transactionID = transactionID;
        this.contentTxt = contentTxt;

    }


    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getContentTxt() {
        return contentTxt;
    }

    public void setContentTxt(String contentTxt) {
        this.contentTxt = contentTxt;
    }
}
