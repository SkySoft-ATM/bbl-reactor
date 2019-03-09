package com.skysoftatm.bblreactor.ch00.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Altitude {

    private final double altitude;

    @JsonCreator
    public Altitude(@JsonProperty("altitude")  double altitude) {
        this.altitude = altitude;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return "Altitude{" +
                "altitude=" + altitude +
                '}';
    }
}
