package com.booking.flights;

import java.time.Duration;
import java.time.Instant;

public class BookTravel {
    public static void main(String[] args) {
        String from = "New York";
        String via = "Atlanta";
        String to = "Kansas";

        var begin = Instant.now();
        TravelQuery travelQuery1 = new TravelQuery(from, to);
        TravelQuery travelQuery2 = new TravelQuery(from, via);
        TravelQuery travelQuery3 = new TravelQuery(via, to);
        var bestTravel = Travel.readTravel(travelQuery1, travelQuery2, travelQuery3);
        var end = Instant.now();

        System.out.println("Best Travel " + bestTravel + " in " + Duration.between(begin, end).toMillis() + "ms.");

    }
}
