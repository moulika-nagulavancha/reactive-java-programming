package com.weather.forecast;

import com.travel.weather.forecast.TravelForecast;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public record Weather(String weather, String server) {
    public static Random random = new Random();

    public Weather {
        if (!TravelForecast.licenseKey.isBound() || !"KEY_1".equals(TravelForecast.licenseKey.get())) {
            throw new IllegalStateException("License Key is Invalid!");
        }
    }

    private enum WeatherForecast {
        SUNNY, RAINY, CLOUDY
    }

    public static Weather readWeather() throws ExecutionException {
        var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather>();

        try (scope) {
            scope.fork(Weather::readFromInternationalWF);
            scope.fork(Weather::readFromGlobalWF);
            scope.fork(Weather::readFromInternationalWF);

            scope.join();

            return scope.result();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
