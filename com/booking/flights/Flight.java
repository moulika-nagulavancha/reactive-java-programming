package com.booking.flights;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public record Flight(String from, String to, int price, String airline) implements Travel {
    private static Random random = new Random();

    /**
     * Using Structured Concurrency to read the Flights
     */
    public static Flight readFlight(String from, String to) {
        var scope = new FlightScope();

        FlightQuery flightQuery = new FlightQuery(from, to);
        try (scope) {
            scope.fork(flightQuery::readFromAlphaAirlines);
            scope.fork(flightQuery::readFromBetaAirlines);
            scope.fork(flightQuery::readFromGammaAirlines);

            scope.join();

            return scope.bestFlight();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Flight readFromAlphaAirlines(String from, String to) {
        sleepFor(random.nextInt(80, 100), ChronoUnit.MILLIS);

        if ("Atlanta".equals(from) || "Atlanta".equals(to)) {
            return new Flight(from, to, random.nextInt(30, 50), "Alpha airlines");
        } else {
            return new Flight(from, to, random.nextInt(70, 120), "Alpha airlines");
        }
    }

    public static Flight readFromBetaAirlines(String from, String to) {
        sleepFor(random.nextInt(90, 110), ChronoUnit.MILLIS);

        return new Flight(from, to, random.nextInt(60, 90), "Beta airlines");

    }

    public static Flight readFromGammaAirlines(String from, String to) {
        sleepFor(random.nextInt(70, 120), ChronoUnit.MILLIS);

        return new Flight(from, to, random.nextInt(70, 90), "Gamma airlines");

    }

    private static void sleepFor(int amount, ChronoUnit unit) {
        try {
            Thread.sleep(Duration.of(amount, unit));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Flight readFlight(TravelQuery travelQuery) {
        try (var scope = new FlightScope()) {
            scope.fork(travelQuery::readFromAlphaAirlines);
            scope.fork(travelQuery::readFromBetaAirlines);
            scope.fork(travelQuery::readFromGammaAirlines);

            scope.join();

            return scope.bestFlight();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
