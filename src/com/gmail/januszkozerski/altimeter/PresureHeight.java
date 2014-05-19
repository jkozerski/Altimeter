package com.gmail.januszkozerski.altimeter;

public class PresureHeight {

    // Air molar mass             = 0.0289644
    // Gravitational acceleration = 9.80665
    // Gas constant               = 8.31446217576

    private static final double ug_R = (-1 * 0.0289644 * 9.80665) / 8.31446217576;
    private static final double normal_presure = 1013.25; // hPa;

    private double presure_start; // Pa
    private double presure_final; // Pa
    private double altitude;      // m
    private double temperature;   // K

    private void calculate_altitude(boolean use_normal_presure)
    {
        if (this.temperature == 0)
            return;
        if (use_normal_presure)
            this.altitude = java.lang.Math.log(this.presure_final / normal_presure) / (ug_R / this.temperature);
        else
            this.altitude = java.lang.Math.log(this.presure_final / this.presure_start) / (ug_R / this.temperature);
    }

    public void set_presure_start(double value)
    {
        this.presure_start = value;
    }

    public double get_presure_start()
    {
        return this.presure_start;
    }

    public void set_presure_final(double value)
    {
        this.presure_final = value;
    }

    public void set_temperature_kelvin(double value)
    {
        this.temperature = value;
        calculate_altitude(false);
    }

    public void set_temperature_celcius(double value)
    {
        this.temperature = value + 273.15;
        calculate_altitude(false);
    }

    public double get_altitude(boolean use_normal_presure)
    {
        calculate_altitude(use_normal_presure);
        return this.altitude;
    }
}
