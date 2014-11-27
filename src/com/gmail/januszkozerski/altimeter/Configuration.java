package com.gmail.januszkozerski.altimeter;

import android.hardware.SensorManager;

public abstract class Configuration {

    /* ---- build-in configuration ---- */
    /* Default temperature */
    public static final int DEFAULT_TEMPERATURE = 20;

    /* Max possible samples to get average of start pressure */
    public static final int START_AVG_MAX = 50;

    /* Max possible samples to get average of current pressure */
    public static final int CURRENT_AVG_MAX = 20;

    /* Max correction range. E.g. for value 1.0 you can fix the pressure up to the. +/-1hPa */
    public static final double MAX_CORRECTION_RANGE = 1.0; // hPa

    /* Minimal value you can adjust */
    public static final double CORRECTION_STEP = 0.05; // hPa

    /* Counter of correction levels */
    public static final int CORRECTION_LEVELS = (int) (2 * (MAX_CORRECTION_RANGE / CORRECTION_STEP));

    /* How many samples use to calculate average of start pressure */
    public static final int start_avg_max_set_default   = 16; // default value

    /* How many samples use to calculate average of current pressure */
    public static final int current_avg_max_set_default = 8;  // default value


    /* ---- runtime configuration ---- */

    /* Sensor delay */
    //TODO: Implement GUI switch for changing sensor delay
    public static int sensorManagerDelay = SensorManager.SENSOR_DELAY_NORMAL;

    /* How many samples use to calculate average of start pressure */
    public static int start_avg_max_set   = start_avg_max_set_default;

    /* How many samples use to calculate average of current pressure */
    public static int current_avg_max_set = current_avg_max_set_default;
    
    
    /* ---- not real configuration - just internal setup---- */
    public static int is_working = 0;
    public static int correction;
    public static PresureHeight presureHeight = new PresureHeight();

}
