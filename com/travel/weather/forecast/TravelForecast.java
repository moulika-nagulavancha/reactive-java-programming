package com.travel.weather.forecast;

import com.booking.flights.Travel;
import com.booking.flights.TravelQuery;
import com.weather.forecast.Weather;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;

public class TravelForecast {
    public static final ScopedValue<String> licenseKey = ScopedValue.newInstance();

    public static void main(String[] args) {
        String from = "New York";
        String via = "Atlanta";
        String to = "Kansas";

        TravelQuery travelQuery1 = new TravelQuery(from, to);
        TravelQuery travelQuery2 = new TravelQuery(from, via);
        TravelQuery travelQuery3 = new TravelQuery(via, to);

        var begin = Instant.now();

        ScopedValue.where(licenseKey, "KEY_1")
                .run(
                        () -> {
                            Page page = readPage(travelQuery1, travelQuery2, travelQuery3);
                            System.out.println(page);
                        }
                );

        var end = Instant.now();
        System.out.println("Done in " + Duration.between(begin, end).toMillis() + "ms.");
    }

    private static Page readPage(TravelQuery travelQuery1, TravelQuery travelQuery2, TravelQuery travelQuery3) {
        try ( var scope = new StructuredTaskScope<>()) {
            var task1 = scope.fork(() -> Weather.readWeather());
            var task2 = scope.fork(() -> Travel.readTravel(travelQuery1, travelQuery2, travelQuery3));

            scope.join();
            var weather = task1.get();
            var travel = task2.get();

            Page page = new Page(weather, travel);

            return page;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
