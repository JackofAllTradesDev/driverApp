
package com.xlog.xloguser.finaldriverapp.Model.ModelReservationList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shipper {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
