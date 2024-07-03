package com.booking.flights;

public record FlightQuery(String from, String to) {
    public Flight readFromAlphaAirlines() {
        return Flight.readFromAlphaAirlines(from, to);
    }

    public Flight readFromBetaAirlines() {
        return Flight.readFromBetaAirlines(from, to);
    }

    public Flight readFromGammaAirlines() {
        return Flight.readFromGammaAirlines(from, to);
    }
}
