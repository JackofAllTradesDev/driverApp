
package com.xlog.xloguser.finaldriverapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncodeFile {

    @SerializedName("encodedfile")
    @Expose
    private String encodedfile;
    @SerializedName("ext")
    @Expose
    private String ext;

    public String getEncodedfile() {
        return encodedfile;
    }

    public void setEncodedfile(String encodedfile) {
        this.encodedfile = encodedfile;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

}
