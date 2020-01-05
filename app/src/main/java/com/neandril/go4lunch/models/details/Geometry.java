package com.neandril.go4lunch.models.details;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.neandril.go4lunch.models.places.Location;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
