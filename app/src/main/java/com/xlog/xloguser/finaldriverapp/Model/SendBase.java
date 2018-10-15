
package com.xlog.xloguser.finaldriverapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendBase {

    @SerializedName("ext")
    @Expose
    private String ext;
    @SerializedName("encodedfile")
    @Expose
    private String encodedfile;

    @SerializedName("driverID")
    @Expose
    private int driverID;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("name")
    @Expose
    private String name;

    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getEncodedfile() {
        return encodedfile;
    }

    public void setEncodedfile(String encodedfile) {
        this.encodedfile = encodedfile;
    }

}
