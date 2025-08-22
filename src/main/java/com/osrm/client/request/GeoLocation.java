package com.osrm.client.request;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeoLocation {
    private final Double latitude;
    private final Double longitude;

    public String getLatLongString() {
        return this.getLatitude() + "," + this.getLongitude();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getLatitude(), this.getLongitude());
    }

    @Override
    public boolean equals(Object obj) {
        return (this.hashCode() == obj.hashCode());
    }
}
