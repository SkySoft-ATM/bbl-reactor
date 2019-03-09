package com.skysoftatm.bblreactor.ch00.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Speed {
    private final double speed;

    @JsonCreator
    public Speed(@JsonProperty("speed") double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Speed{" +
                "speed=" + speed +
                '}';
    }
}
