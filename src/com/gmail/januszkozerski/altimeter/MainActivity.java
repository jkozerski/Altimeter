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

    private static PresureHeight presureHeight = new PresureHeight();
    
    public static double start_values[] = new double[Configuration.START_AVG_MAX];
    public static double presure_avg[] = new double[Configuration.CURRENT_AVG_MAX];

    private static int avg_cnt = 0;
    private static int counter = 0;
    private static int is_working = 0;

    // ----
    private TextView textView1;
    private TextView textView3;
    private TextView textView4;
    private EditText editText1;
    private CheckBox checkBox1;
    private SeekBar  startAvgMax_seekBar;
    private SeekBar  currentAvgMax_seekBar;
    private TextView startSampleCountValue_textView;
    private TextView currentSampleCountValue_textView;
    private SeekBar  correction_seekBar;
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
            if (counter < Configuration.start_avg_max_set) {
                textView3.setText("Mierzenie ciśnienia startowego...");
            } else if (counter == Configuration.start_avg_max_set) {
                Log.d("WYS", "counter : " + counter);
                double sum = 0;
                for (int i = 0; i < Configuration.start_avg_max_set; ++i) {
                    sum += start_values[i];
                }
                presureHeight.set_presure_start(sum / Configuration.start_avg_max_set);
                Log.d("WYS", "start presure value: " + sum / Configuration.start_avg_max_set);
                textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");
            } else if (counter > Configuration.start_avg_max_set) {
                Log.d("WYS", "presure value: " + event.values[0]);
                add_avg_value(event.values[0]);
                presureHeight.set_presure_final(count_agv());
                // presureHeight.set_presure_final(event.values[0]);
                textView4.setText(nf2.format(event.values[0]) + "hPa");
                textView1.setText(nf1.format(presureHeight.get_altitude(checkBox1.isChecked())) + "m");
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

    public static void reset_counter()
    {
        counter = 0;
    }

    private void startMeasurement()
    {
        presureHeight.set_temperature_celcius(20.0);
        sensorManager.registerListener(myListenerInstance, sensor, Configuration.sensorManagerDelay);
        textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");
        is_working = 1;
    }

    private void stopMeasurement()
    {
        sensorManager.unregisterListener(myListenerInstance);
        is_working = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView1 = (TextView) findViewById(R.id.altitude_textView);
        textView3 = (TextView) findViewById(R.id.startPresure_textView);
        textView4 = (TextView) findViewById(R.id.currentPresure_textView4);
        editText1 = (EditText) findViewById(R.id.temperatureUpdate_editText);
        checkBox1 = (CheckBox) findViewById(R.id.useNormalPresure_checkBox);
        startAvgMax_seekBar = (SeekBar) findViewById(R.id.startAvgMax_seekBar);
        correction_seekBar = (SeekBar) findViewById(R.id.correction_seekBar);
        startSampleCountValue_textView = (TextView)
                findViewById(R.id.startSampleCountValue_textView);
        currentAvgMax_seekBar = (SeekBar) findViewById(R.id.currentAvgMax_seekBar);
        currentSampleCountValue_textView = (TextView)
                findViewById(R.id.currentSampleCountValue_textView);

        nf2.setMaximumFractionDigits(2);
        nf2.setMinimumFractionDigits(2);

        nf1.setMaximumFractionDigits(1);
        nf1.setMinimumFractionDigits(1);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        final Button button1 = (Button) findViewById(R.id.start_button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                startMeasurement();
            }
        });

        final Button button2 = (Button) findViewById(R.id.stop_button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                stopMeasurement();
            }
        });

        final Button button3 = (Button) findViewById(R.id.resetStartPresure_button);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                reset_counter();
            }
        });

        final Button button4 = (Button) findViewById(R.id.temperatureUpdate_button);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                presureHeight.set_temperature_celcius(Double.valueOf(editText1.getText().toString()));
                textView1.setText(nf1.format(presureHeight.get_altitude(checkBox1.isChecked())) + "m");
            }
        });
        
        final Button resetDefault = (Button) findViewById(R.id.resetDefault_button);
        resetDefault.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                startAvgMax_seekBar.setProgress(Configuration.start_avg_max_set_default-1);
                startSampleCountValue_textView.setText(String.valueOf(Configuration.start_avg_max_set_default));
                
                currentAvgMax_seekBar.setProgress(Configuration.current_avg_max_set_default-1);
                currentSampleCountValue_textView.setText(String.valueOf(Configuration.current_avg_max_set_default));
            }
        });

        currentAvgMax_seekBar.setMax(Configuration.CURRENT_AVG_MAX-1); // Set max value
        currentAvgMax_seekBar.setProgress(Configuration.current_avg_max_set-1); // Set default value;
        currentSampleCountValue_textView.setText(String.valueOf(Configuration.current_avg_max_set));
        currentAvgMax_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                    currentSampleCountValue_textView.setText(String.valueOf(progress+1));
                    Configuration.current_avg_max_set = progress+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }
        });

        startAvgMax_seekBar.setMax(Configuration.START_AVG_MAX-1); // Set max value
        startAvgMax_seekBar.setProgress(Configuration.start_avg_max_set-1); // Set default value;
        startSampleCountValue_textView.setText(String.valueOf(Configuration.start_avg_max_set));
        startAvgMax_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                startSampleCountValue_textView.setText(String.valueOf(progress+1));
                Configuration.start_avg_max_set = progress+1;
                
                // Recalculate average start pressure (only if is working)
                if (is_working == 1) {
                    double sum = 0;
                    for (int i = 0; i < Configuration.start_avg_max_set; ++i) {
                        sum += start_values[i];
                    }
                    presureHeight.set_presure_start(sum / Configuration.start_avg_max_set);
                    Log.d("WYS", "start presure value: " + sum / Configuration.start_avg_max_set);
                    textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }
        });

        correction_seekBar.setMax(Configuration.CORRECTION_LEVELS); // Set max value
        correction_seekBar.setProgress(Configuration.CORRECTION_LEVELS / 2); // Set default value;
        correction_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                presureHeight.set_corrections((progress - Configuration.CORRECTION_LEVELS / 2) *
                        Configuration.CORRECTION_STEP);
                textView3.setText(nf2.format(presureHeight.get_presure_start() +
                        presureHeight.get_corrections()) + "hPa");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }
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
        if (is_working == 1)
            sensorManager.registerListener(myListenerInstance, sensor, Configuration.sensorManagerDelay);
        else
            sensorManager.unregisterListener(myListenerInstance);
        super.onRestart();
    }

    protected void onResume()
    {
        if (is_working == 1)
            textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");

        // Display start pressure if it was measured
        if ((presureHeight.get_presure_start()) > 0)
            textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");

        startAvgMax_seekBar.setProgress(Configuration.start_avg_max_set-1);
        startSampleCountValue_textView.setText(String.valueOf(Configuration.start_avg_max_set));

        currentAvgMax_seekBar.setProgress(Configuration.current_avg_max_set-1);
        currentSampleCountValue_textView.setText(String.valueOf(Configuration.current_avg_max_set));

        super.onResume();
    }

    protected void onPause()
    {
        sensorManager.unregisterListener(myListenerInstance);
        super.onPause();
    }

    protected void onStop()
    {
        sensorManager.unregisterListener(myListenerInstance);
        super.onStop();
    }

    protected void onDestroy()
    {
        sensorManager.unregisterListener(myListenerInstance);
        is_working = 0;
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
