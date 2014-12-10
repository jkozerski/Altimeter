package com.gmail.januszkozerski.altimeter;

import java.text.NumberFormat;

import com.gmail.januszkozerski.altimeter.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private SensorManager sensorManager;
    private Sensor sensor;
    private PresureListener myListenerInstance = new PresureListener();

    //private static PresureHeight presureHeight = new PresureHeight();
    
    public static double start_values[] = new double[Configuration.START_AVG_MAX];
    public static double presure_avg[] = new double[Configuration.CURRENT_AVG_MAX];

    private static int avg_cnt = 0;
    private static int counter = 0;

    // ----
    private TextView altitudeTextView;
    private TextView startPresureTextView;
    private TextView currentPresureTextView;
    private EditText temperatureUpdateEditText;
    private CheckBox useNormalPresureCheckBox;
    private TextView correctionValueTextView;
    private SeekBar  correctionSeekBar;

    private Button startButton;
    private Button stopButton;
    private Button resetPressureButton;
    private Button temperatureUpdateButton;
    private Button resetCorrectionButton;
    // ----

    NumberFormat nf2 = NumberFormat.getNumberInstance();
    NumberFormat nf1 = NumberFormat.getNumberInstance();

    private class PresureListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
            // Probably not needed
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (counter < Configuration.START_AVG_MAX) {
                Log.d("WYS", "counter : " + counter);
                start_values[counter] = event.values[0];
            }

            if (counter == Configuration.start_avg_max_set) {
                Log.d("WYS", "counter : " + counter);
                double sum = 0;
                for (int i = 0; i < Configuration.start_avg_max_set; ++i) {
                    sum += start_values[i];
                }
                Configuration.presureHeight.set_presure_start(sum / Configuration.start_avg_max_set);
                Log.d("WYS", "start presure value: " + sum / Configuration.start_avg_max_set);
                startPresureTextView.setText(nf2.format(Configuration.presureHeight.get_presure_start()) + "hPa");
            } else if (counter > Configuration.start_avg_max_set) {
                Log.d("WYS", "presure value: " + event.values[0]);
                add_avg_value(event.values[0]);
                Configuration.presureHeight.set_presure_final(count_agv());
                // presureHeight.set_presure_final(event.values[0]);
                currentPresureTextView.setText(nf2.format(event.values[0]) + "hPa");
                altitudeTextView.setText(nf1.format(Configuration.presureHeight.get_altitude(useNormalPresureCheckBox.isChecked())) + "m");
            }

            if (counter <= Configuration.START_AVG_MAX)
                ++counter;
        }
    }

    private static double count_agv()
    {
        double sum = 0;
        for (int i = 0; i < Configuration.current_avg_max_set; ++i) {
            sum += presure_avg[i];
        }
        return sum / Configuration.current_avg_max_set;
    }

    private static void add_avg_value(double value)
    {
        presure_avg[avg_cnt] = value;
        avg_cnt = (avg_cnt + 1) % Configuration.current_avg_max_set;
    }

    public void resetReferencePressure()
    {
        counter = 0;

        if (Configuration.is_working == 1) {
            startPresureTextView.setText(R.string.measuring_reference_pressure);
            altitudeTextView.setText(R.string.zero_m);
        }
        else
            startPresureTextView.setText(R.string.N_A);
    }

    private void startMeasurement()
    {
        Configuration.presureHeight.set_temperature_celcius(20.0);
        sensorManager.registerListener(myListenerInstance, sensor, Configuration.sensorManagerDelay);
        startPresureTextView.setText(nf2.format(Configuration.presureHeight.get_presure_start()) + "hPa");
        Configuration.is_working = 1;

        if (counter < Configuration.start_avg_max_set)
            startPresureTextView.setText(R.string.measuring_reference_pressure);

        // Disable start-button if measuring is started
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopMeasurement()
    {
        Log.d("WYS", "stopMeasurement()");
        sensorManager.unregisterListener(myListenerInstance);
        Configuration.is_working = 0;

        // Disable stop-button if measuring is stopped
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // Change displayed value of reference pressure for N/A if too few samples has been measured.
        if (counter < Configuration.start_avg_max_set) {
            startPresureTextView.setText(R.string.N_A);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        altitudeTextView = (TextView) findViewById(R.id.altitude_textView);
        startPresureTextView = (TextView) findViewById(R.id.startPresure_textView);
        currentPresureTextView = (TextView) findViewById(R.id.currentPresure_textView4);
        temperatureUpdateEditText = (EditText) findViewById(R.id.temperatureUpdate_editText);
        useNormalPresureCheckBox = (CheckBox) findViewById(R.id.useNormalPresure_checkBox);

        correctionValueTextView = (TextView) findViewById(R.id.correction_value_textView);
        correctionSeekBar = (SeekBar) findViewById(R.id.correction_seekBar);

        nf2.setMaximumFractionDigits(2);
        nf2.setMinimumFractionDigits(2);

        nf1.setMaximumFractionDigits(1);
        nf1.setMinimumFractionDigits(1);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                startMeasurement();
            }
        });

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                stopMeasurement();
            }
        });
        stopButton.setEnabled(false);

        resetPressureButton = (Button) findViewById(R.id.resetStartPresure_button);
        resetPressureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                resetReferencePressure();
            }
        });

        temperatureUpdateEditText.setText(Integer.toString(Configuration.DEFAULT_TEMPERATURE));
        temperatureUpdateButton = (Button) findViewById(R.id.temperatureUpdate_button);
        temperatureUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Configuration.presureHeight.set_temperature_celcius(Double.valueOf(temperatureUpdateEditText.getText().toString()));
                altitudeTextView.setText(nf1.format(Configuration.presureHeight.get_altitude(useNormalPresureCheckBox.isChecked())) + "m");
            }
        });

        /* Seek bar for setting up correction */
        correctionSeekBar.setMax(Configuration.CORRECTION_LEVELS); // Set max value
        correctionSeekBar.setProgress(Configuration.CORRECTION_LEVELS / 2); // Set default value;
        correctionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double correction;
                Configuration.presureHeight.set_corrections((progress - Configuration.CORRECTION_LEVELS / 2) *
                        Configuration.CORRECTION_STEP);

                // Update high even if in measurement is stopped after change correction
                altitudeTextView.setText(nf1.format(Configuration.presureHeight.get_altitude(useNormalPresureCheckBox.isChecked())) + "m");

                //FIXME: Correction bar change its length when correction have negative value. Fix it.
                if ((correction = Configuration.presureHeight.get_corrections()) < 0)
                    correctionValueTextView.setText(nf2.format(correction));
                else
                    correctionValueTextView.setText(" " + nf2.format(correction));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }
        });

        /* Button for reset correction value */
        resetCorrectionButton = (Button) findViewById(R.id.reset_correction_button);
        resetCorrectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                correctionSeekBar.setProgress(Configuration.CORRECTION_LEVELS / 2); // Set default value;
            }
        });
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onRestart()
    {
        Log.d("WYS", "onRestart()");
        if (Configuration.is_working == 1)
            sensorManager.registerListener(myListenerInstance, sensor, Configuration.sensorManagerDelay);
        else
            sensorManager.unregisterListener(myListenerInstance);
        super.onRestart();
    }

    protected void onResume()
    {
        if (Configuration.is_working == 1) {
            startPresureTextView.setText(nf2.format(Configuration.presureHeight.get_presure_start()) + "hPa");
            
            // This is the only place when we need to re-create listener if delay has changed.
            // It's because from Configuration Activity we can only go here.
            sensorManager.unregisterListener(myListenerInstance);
            sensorManager.registerListener(myListenerInstance, sensor, Configuration.sensorManagerDelay);
        }

        // Display start pressure if it was measured
        if ((Configuration.presureHeight.get_presure_start()) > 0)
            startPresureTextView.setText(nf2.format(Configuration.presureHeight.get_presure_start()) + "hPa");

        super.onResume();
    }

    protected void onPause()
    {
        Log.d("WYS", "onPause()");
        sensorManager.unregisterListener(myListenerInstance);
        super.onPause();
    }

    protected void onStop()
    {
        Log.d("WYS", "onStop()");
        sensorManager.unregisterListener(myListenerInstance);
        super.onStop();
    }

    protected void onDestroy()
    {
        sensorManager.unregisterListener(myListenerInstance);
        Configuration.is_working = 0;
        super.onDestroy();
    }

    /* Menu button support */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Intent intent = new Intent(this, ConfigurationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
