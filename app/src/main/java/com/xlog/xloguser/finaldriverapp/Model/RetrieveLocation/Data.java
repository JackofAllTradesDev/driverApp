
package com.xlog.xloguser.finaldriverapp.Model.RetrieveLocation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("road")
    @Expose
    private List<List<Road>> road = null;
    @SerializedName("complete_path")
    @Expose
    private List<CompletePath> completePath = null;

    public List<List<Road>> getRoad() {
        return road;
    }

    public void setRoad(List<List<Road>> road) {
        this.road = road;
    }

    public List<CompletePath> getCompletePath() {
        return completePath;
    }

    public void setCompletePath(List<CompletePath> completePath) {
        this.completePath = completePath;
    }

}
