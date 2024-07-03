package com.booking.flights;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;

public class FlightScope extends StructuredTaskScope<Travel> {
    /**
     * These Collections can create a race condition as these will be read in another thread apart from virtual thread
     * Hence, should make these two collections thread safe by adding 'final' (immutable) or 'volatile' (mutable)
     */
    private volatile Collection<Flight> flights = new ConcurrentLinkedQueue<>();
    private volatile Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();

    @Override
    protected void handleComplete(Subtask<? extends Travel> subtask) {

        switch (subtask.state()) {
            case FAILED -> this.exceptions.add(subtask.exception());
            case UNAVAILABLE -> throw new IllegalStateException("Task is NOT AVAILABLE");
            case SUCCESS -> this.flights.add((Flight) subtask.get());
        }
    }

    public FlightException getExceptions() {
        FlightException flightException = new FlightException();
        this.exceptions.forEach(flightException::addSuppressed);

        return flightException;
    }

    public Flight bestFlight() {
        return this.flights.stream()
                .min(Comparator.comparingInt(Flight::price))
                .orElseThrow(this::getExceptions);
    }
}
