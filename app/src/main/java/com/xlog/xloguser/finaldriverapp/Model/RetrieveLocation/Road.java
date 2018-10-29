
package com.xlog.xloguser.finaldriverapp.Model.RetrieveLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Road {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("dateTimeActual")
    @Expose
    private String dateTimeActual;
    @SerializedName("dateTimeUTC")
    @Expose
    private String dateTimeUTC;
    @SerializedName("timezone")
    @Expose
    private String timezone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDateTimeActual() {
        return dateTimeActual;
    }

    public void setDateTimeActual(String dateTimeActual) {
        this.dateTimeActual = dateTimeActual;
    }

    public String getDateTimeUTC() {
        return dateTimeUTC;
    }

    public void setDateTimeUTC(String dateTimeUTC) {
        this.dateTimeUTC = dateTimeUTC;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
