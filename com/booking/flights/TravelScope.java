package com.booking.flights;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;

public class TravelScope extends StructuredTaskScope<Travel> {
    private volatile Collection<Travel> travels = new ConcurrentLinkedQueue<>();
    private volatile Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();

    @Override
    protected void handleComplete(Subtask<? extends Travel> subtask) {

        switch (subtask.state()) {
            case FAILED -> this.exceptions.add(subtask.exception());
            case UNAVAILABLE -> throw new IllegalStateException("Task is NOT AVAILABLE");
            case SUCCESS -> this.travels.add(subtask.get());
        }
    }

    public FlightException getExceptions() {
        FlightException flightException = new FlightException();
        this.exceptions.forEach(flightException::addSuppressed);

        return flightException;
    }

    public Travel bestTravel() {
        return this.travels.stream()
                .min(Comparator.comparingInt(Travel::price))
                .orElseThrow(this::getExceptions);
    }
}
