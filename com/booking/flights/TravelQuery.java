package com.booking.flights;

public record TravelQuery(String from, String to) {
    public Travel readFromAlphaAirlines() {
        return Flight.readFromAlphaAirlines(from, to);
    }

    public Travel readFromBetaAirlines() {
        return Flight.readFromBetaAirlines(from, to);
    }

    public Travel readFromGammaAirlines() {
        return Flight.readFromGammaAirlines(from, to);
    }
}
