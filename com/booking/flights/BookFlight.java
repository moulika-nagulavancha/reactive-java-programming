package com.booking.flights;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Stream;

public class BookFlight {
    public static void main(String[] args) {
        String from = "New York";
        String via = "Atlanta";
        String to = "Kansas";

        traditionalBestFlightRetrieval(from, to);
        structuredConcurrentBestFlightRetrieval(from, to);
        traditionalMultiLegFlightBooking(from, via, to);
        structuredConcurrentMultiLegFlightRetrieval(from, via, to);
    }

    private static void structuredConcurrentMultiLegFlightRetrieval(String from, String via, String to) {
        var start = Instant.now();

        FlightQuery flightQuery1 = new FlightQuery(from, via);
        FlightQuery flightQuery2 = new FlightQuery(via, to);

        MultiLegFlight travel = MultiLegFlight.readMultiLegFlight(flightQuery1, flightQuery2);

        var end = Instant.now();

        System.out.println("\nMultiLegFlight using Structured Concurrency: " + travel + " in " + Duration.between(start, end).toMillis() + "ms.");
    }

    private static void traditionalMultiLegFlightBooking(String from, String via, String to) {
        var start = Instant.now();

        FlightQuery flightQuery1 = new FlightQuery(from, via);
        FlightQuery flightQuery2 = new FlightQuery(via, to);

        Flight flight1 = flightQuery1.readFromAlphaAirlines();
        Flight flight2 = flightQuery2.readFromAlphaAirlines();

        MultiLegFlight travel = MultiLegFlight.of(flight1, flight2);

        var end = Instant.now();

        System.out.println("\nMultiLegFlight using traditional retrieval: " + travel + " in " + Duration.between(start, end).toMillis() + "ms.");

    }

    private static void structuredConcurrentBestFlightRetrieval(String from, String to) {
        var start = Instant.now();
        var bestFlight = Flight.readFlight(from, to);
        var end = Instant.now();
        System.out.println("\nBest Flight Using Structured Concurrency: " + bestFlight + " in " + Duration.between(start, end).toMillis() + "ms.");
    }

    private static void traditionalBestFlightRetrieval(String from, String to) {
        var start = Instant.now();
        var flight1 = Flight.readFromAlphaAirlines(from, to);
        var flight2 = Flight.readFromBetaAirlines(from, to);
        var flight3 = Flight.readFromGammaAirlines(from, to);
        var end = Instant.now();

        System.out.println("Flight 1: " + flight1);
        System.out.println("Flight 2: " + flight2);
        System.out.println("Flight 3: " + flight3);

        Flight bestFlight = Stream.of(flight1, flight2, flight3)
                .min(Comparator.comparing(Flight::price))
                .get();

        System.out.println("\nBest Flight using Traditional retrieval: " + bestFlight + " in " + Duration.between(start, end).toMillis() + "ms.");
    }
}
