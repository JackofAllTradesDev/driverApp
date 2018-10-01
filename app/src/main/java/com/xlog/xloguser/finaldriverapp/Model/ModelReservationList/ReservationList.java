
package com.xlog.xloguser.finaldriverapp.Model.ModelReservationList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReservationList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("prefixedId")
    @Expose
    private String prefixedId;
    @SerializedName("trucker_id")
    @Expose
    private Integer truckerId;
    @SerializedName("reservation_id")
    @Expose
    private Integer reservationId;
    @SerializedName("trucking_reservation_event_status_id")
    @Expose
    private Integer truckingReservationEventStatusId;
    @SerializedName("reservation_type")
    @Expose
    private String reservationType;
    @SerializedName("supporting_documents")
    @Expose
    private List<Object> supportingDocuments = null;
    @SerializedName("special_instructions")
    @Expose
    private Object specialInstructions;
    @SerializedName("terms_of_payment")
    @Expose
    private Integer termsOfPayment;
    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;
    @SerializedName("reservation")
    @Expose
    private Reservation reservation;
    @SerializedName("trucker")
    @Expose
    private Integer trucker;
    @SerializedName("waypoints")
    @Expose
    private List<String> waypoints = null;
    @SerializedName("delivery_dates")
    @Expose
    private List<DeliveryDate> deliveryDates = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrefixedId() {
        return prefixedId;
    }

    public void setPrefixedId(String prefixedId) {
        this.prefixedId = prefixedId;
    }

    public Integer getTruckerId() {
        return truckerId;
    }

    public void setTruckerId(Integer truckerId) {
        this.truckerId = truckerId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getTruckingReservationEventStatusId() {
        return truckingReservationEventStatusId;
    }

    public void setTruckingReservationEventStatusId(Integer truckingReservationEventStatusId) {
        this.truckingReservationEventStatusId = truckingReservationEventStatusId;
    }

    public String getReservationType() {
        return reservationType;
    }

    public void setReservationType(String reservationType) {
        this.reservationType = reservationType;
    }

    public List<Object> getSupportingDocuments() {
        return supportingDocuments;
    }

    public void setSupportingDocuments(List<Object> supportingDocuments) {
        this.supportingDocuments = supportingDocuments;
    }

    public Object getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(Object specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Integer getTermsOfPayment() {
        return termsOfPayment;
    }

    public void setTermsOfPayment(Integer termsOfPayment) {
        this.termsOfPayment = termsOfPayment;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Integer getTrucker() {
        return trucker;
    }

    public void setTrucker(Integer trucker) {
        this.trucker = trucker;
    }

    public List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String> waypoints) {
        this.waypoints = waypoints;
    }

    public List<DeliveryDate> getDeliveryDates() {
        return deliveryDates;
    }

    public void setDeliveryDates(List<DeliveryDate> deliveryDates) {
        this.deliveryDates = deliveryDates;
    }

}
