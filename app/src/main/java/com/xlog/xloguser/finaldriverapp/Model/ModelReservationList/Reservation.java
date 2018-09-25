
package com.xlog.xloguser.finaldriverapp.Model.ModelReservationList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("prefixed_id")
    @Expose
    private String prefixedId;
    @SerializedName("value_of_goods")
    @Expose
    private String valueOfGoods;
    @SerializedName("commodity_description")
    @Expose
    private String commodityDescription;
    @SerializedName("other_special_conditions")
    @Expose
    private String otherSpecialConditions;
    @SerializedName("total_volume")
    @Expose
    private String totalVolume;
    @SerializedName("total_gross_weight")
    @Expose
    private String totalGrossWeight;
    @SerializedName("shipper")
    @Expose
    private Shipper shipper;
    @SerializedName("commodity")
    @Expose
    private Commodity commodity;

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

    public String getValueOfGoods() {
        return valueOfGoods;
    }

    public void setValueOfGoods(String valueOfGoods) {
        this.valueOfGoods = valueOfGoods;
    }

    public String getCommodityDescription() {
        return commodityDescription;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public String getOtherSpecialConditions() {
        return otherSpecialConditions;
    }

    public void setOtherSpecialConditions(String otherSpecialConditions) {
        this.otherSpecialConditions = otherSpecialConditions;
    }

    public String getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(String totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getTotalGrossWeight() {
        return totalGrossWeight;
    }

    public void setTotalGrossWeight(String totalGrossWeight) {
        this.totalGrossWeight = totalGrossWeight;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

}
