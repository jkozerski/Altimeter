package com.gmail.januszkozerski.wysokosciomierz;

public class PresureHeight {
	
	// Air molar mass               = 0.0289644
	// Gravitational acceleration = 9.80665
	// Gas constant               = 8.31446217576
	
	private static final double ug_R = (-1 * 0.0289644 * 9.80665) / 8.31446217576;
	
	private double presure_start; // Pa
	private double presure_final; // Pa
	private double altitude;      // m
	private double temperature;   // K
	
	private void calculate_altitude ()
	{
		if(temperature == 0)
			return;
		this.altitude = java.lang.Math.log(presure_final/presure_start) / (ug_R / temperature);
	}
	
	public void set_presure_start (double value)
	{
		this.presure_start = value;
	}
	
	public double get_presure_start ()
	{
		return this.presure_start;
	}
	
	public void set_presure_final (double value)
	{
		this.presure_final = value;
	}
	
	public void set_temperature_kelvin (double value)
	{
		this.temperature = value;
	}
	
	public void set_temperature_celcius (double value)
	{
		this.temperature = value + 273.15;
	}
	
	public double get_altitude ()
	{
		calculate_altitude();
		return this.altitude;
	}
	
}
