package com.osrm.client;

import com.google.common.base.Objects;

public class GeoLocation {
    private final Double latitude;
    private final Double longitude;

    public GeoLocation (Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public String getLatLongString() {
        return this.getLatitude() + "," + this.getLongitude();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getLatitude(), this.getLongitude())^ 0x12b7eff8;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.hashCode() == obj.hashCode());
    }
}
