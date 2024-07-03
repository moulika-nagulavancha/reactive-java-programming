package com.structuredconcurrency;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

// --enable-preview
public class StructuredConcurrencyInAction {
    public static void main(String[] args) throws ExecutionException {
        var weather = Weather.readWeather();
        System.out.println("\n\nWeather: " + weather);
    }
}

record Weather(String weather, String server) {
    public static Random random = new Random();

    private enum WeatherForecast {
        SUNNY, RAINY, CLOUDY
    }

    public static Weather readWeather() throws ExecutionException {
//        var scope = new StructuredTaskScope<Weather>();
        /**
         * ShutdownOnSuccess - returns the first success event
         */
        var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather>();

        // try with resources statement
        try (scope) {
            var f1 = scope.fork(Weather::readFromInternationalWF);
            var f2 = scope.fork(Weather::readFromGlobalWF);
            var f3 = scope.fork(Weather::readFromInternationalWF);

            scope.join();

            System.out.println("F1: " + f1.state());
            if (f1.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                System.out.println(" " + f1.get());
            }
            System.out.println("F2: " + f2.state());
            if (f2.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                System.out.println(" " + f2.get());
            }
            System.out.println("F3: " + f3.state());
            if (f3.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                System.out.println(" " + f3.get());
            }

            return scope.result();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        return readFromInternationalWF();
    }

    private static Weather readFromInternationalWF() {
        sleepFor(random.nextInt(70, 120), ChronoUnit.MILLIS);
        WeatherForecast.values()[random.nextInt(0, 2)].name();

        return new Weather(
                WeatherForecast.values()[random.nextInt(0, 3)].name(),
                "International Weather Forecast"
        );
    }

    private static Weather readFromGlobalWF() {
        sleepFor(random.nextInt(80, 100), ChronoUnit.MILLIS);

        return new Weather(
                WeatherForecast.values()[random.nextInt(0, 3)].name(),
                "Global Weather Forecast"
        );
    }

    private static Weather readFromPlanetEarthWF() {
        sleepFor(random.nextInt(80, 110), ChronoUnit.MILLIS);

        return new Weather(
                WeatherForecast.values()[random.nextInt(0, 3)].name(),
                "Planet Earth Weather Forecast"
        );
    }

    private static void sleepFor(int amount, ChronoUnit unit) {
        try {
            Thread.sleep(Duration.of(amount, unit));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
