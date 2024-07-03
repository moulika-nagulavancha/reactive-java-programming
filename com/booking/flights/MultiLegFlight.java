package com.booking.flights;

import java.util.concurrent.StructuredTaskScope;

public record MultiLegFlight(String from, String via, String to, int price, String airline) implements Travel {

    public static MultiLegFlight of(Flight flight1, Flight flight2) {
        return new MultiLegFlight(flight1.from(), flight1.to(), flight2.to(), flight1.price() + flight2.price(), flight1.airline());
    }

    public static MultiLegFlight readMultiLegFlight(FlightQuery flightQuery1, FlightQuery flightQuery2) {
        var scope = new StructuredTaskScope.ShutdownOnFailure();

        try (scope) {
            var flight1 = scope.fork(flightQuery1::readFromAlphaAirlines);
            var flight2 = scope.fork(flightQuery2::readFromBetaAirlines);

            scope.join();

            MultiLegFlight travel = of(flight1.get(), flight2.get());
            return travel;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Travel readMultiLegFlight(TravelQuery travelQuery1, TravelQuery travelQuery2) {
        var scope = new MultiLegFlightScope();

        try (scope) {
            scope.fork(() -> readFromAlphaAirlines(travelQuery1, travelQuery2));
            scope.fork(() -> readFromBetaAirlines(travelQuery1, travelQuery2));
            scope.fork(() -> readFromGammaAirlines(travelQuery1, travelQuery2));

            scope.join();

            return scope.bestTravel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Travel readFromAlphaAirlines(TravelQuery travelQuery1, TravelQuery travelQuery2) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var t1 = scope.fork(travelQuery1::readFromAlphaAirlines);
            var t2 = scope.fork(travelQuery2::readFromAlphaAirlines);

            scope.join();

            return of(t1.get(), t2.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Travel readFromBetaAirlines(TravelQuery travelQuery1, TravelQuery travelQuery2) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var t1 = scope.fork(travelQuery1::readFromBetaAirlines);
            var t2 = scope.fork(travelQuery2::readFromBetaAirlines);

            scope.join();

            return of(t1.get(), t2.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Travel readFromGammaAirlines(TravelQuery travelQuery1, TravelQuery travelQuery2) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var t1 = scope.fork(travelQuery1::readFromGammaAirlines);
            var t2 = scope.fork(travelQuery2::readFromGammaAirlines);

            scope.join();

            return of(t1.get(), t2.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Travel of(Travel travel1, Travel travel2) {
        return new MultiLegFlight(travel1.from(), travel1.to(), travel2.to(), travel1.price() + travel2.price(), travel1.airline());
    }
}
