
package com.xlog.xloguser.finaldriverapp.Model.ModelReservationList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Consignee {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
