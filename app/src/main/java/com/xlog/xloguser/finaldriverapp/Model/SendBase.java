
package com.xlog.xloguser.finaldriverapp.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendBase {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("driverID")
    @Expose
    private int driverID;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("encodeFile")
    @Expose
    private List<EncodeFile> encodeFile = null;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EncodeFile> getEncodeFile() {
        return encodeFile;
    }

    public void setEncodeFile(List<EncodeFile> encodeFile) {
        this.encodeFile = encodeFile;
    }

}
