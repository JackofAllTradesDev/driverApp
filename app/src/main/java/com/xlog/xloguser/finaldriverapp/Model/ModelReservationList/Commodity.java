
package com.xlog.xloguser.finaldriverapp.Model.ModelReservationList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Commodity {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("translation")
    @Expose
    private Translation translation;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }

}
