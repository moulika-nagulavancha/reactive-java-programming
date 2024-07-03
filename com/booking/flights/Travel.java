package com.booking.flights;

public interface Travel {
    int price();
    String from();
    String to();
    String airline();

    static Travel readTravel(TravelQuery travelQuery1, TravelQuery travelQuery2, TravelQuery travelQuery3) {
        var scope = new TravelScope();

        try (scope) {
            scope.fork(() -> MultiLegFlight.readMultiLegFlight(travelQuery2, travelQuery3));
            scope.fork(() -> Flight.readFlight(travelQuery1));

            scope.join();

            return scope.bestTravel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
