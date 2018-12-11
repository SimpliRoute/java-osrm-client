package com.osrm.client;

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
        return latitude.hashCode() + longitude.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (this.hashCode() == obj.hashCode());
    }
}
