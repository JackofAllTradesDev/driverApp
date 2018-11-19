
package com.xlog.xloguser.finaldriverapp.Model.ModelReservationList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Truck {

    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("driver_actual_start_date")
    @Expose
    private String driverActualStartDate;
    @SerializedName("driver_actual_end_date")
    @Expose
    private String driverActualEndDate;
    @SerializedName("trucking_reservation_id")
    @Expose
    private Integer truckingReservationId;
    @SerializedName("trucker_truck_id")
    @Expose
    private Integer truckerTruckId;
    @SerializedName("route_status")
    @Expose
    private Integer routeStatus;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDriverActualStartDate() {
        return driverActualStartDate;
    }

    public void setDriverActualStartDate(String driverActualStartDate) {
        this.driverActualStartDate = driverActualStartDate;
    }

    public String getDriverActualEndDate() {
        return driverActualEndDate;
    }

    public void setDriverActualEndDate(String driverActualEndDate) {
        this.driverActualEndDate = driverActualEndDate;
    }

    public Integer getTruckingReservationId() {
        return truckingReservationId;
    }

    public void setTruckingReservationId(Integer truckingReservationId) {
        this.truckingReservationId = truckingReservationId;
    }

    public Integer getTruckerTruckId() {
        return truckerTruckId;
    }

    public void setTruckerTruckId(Integer truckerTruckId) {
        this.truckerTruckId = truckerTruckId;
    }

    public Integer getRouteStatus() {
        return routeStatus;
    }

    public void setRouteStatus(Integer routeStatus) {
        this.routeStatus = routeStatus;
    }

}
