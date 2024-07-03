package com.travel.weather.forecast;

import com.booking.flights.Travel;
import com.weather.forecast.Weather;

public record Page(Weather weather, Travel travel) {
}
