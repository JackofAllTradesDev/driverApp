
package com.xlog.xloguser.finaldriverapp.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SnapToRoad {

    @SerializedName("snappedPoints")
    @Expose
    private List<SnappedPoint> snappedPoints = null;

    public List<SnappedPoint> getSnappedPoints() {
        return snappedPoints;
    }

    public void setSnappedPoints(List<SnappedPoint> snappedPoints) {
        this.snappedPoints = snappedPoints;
    }

}
