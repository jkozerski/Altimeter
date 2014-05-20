package com.gmail.januszkozerski.altimeter;

import java.text.NumberFormat;

import com.gmail.januszkozerski.altimeter.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private class MyListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
            // Probably not needed
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (counter < max_values) {
                Log.d("WYS", "counter : " + counter);
                last_values[counter] = event.values[0];
                ++counter;
            } else if (counter == max_values) {
                Log.d("WYS", "counter : " + counter);
                double sum = 0;
                for (int i = 0; i < max_values; ++i) {
                    sum += last_values[i];
                }
                presureHeight.set_presure_start(sum / max_values);
                Log.d("WYS", "start presure value: " + sum / max_values);
                textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");
                ++counter;
            } else if (counter > max_values) {
                Log.d("WYS", "presure value: " + event.values[0]);
                add_avg_value(event.values[0]);
                presureHeight.set_presure_final(count_agv());
                // presureHeight.set_presure_final(event.values[0]);
                textView4.setText(nf2.format(event.values[0]) + "hPa");
                textView1.setText(nf1.format(presureHeight.get_altitude(checkBox1.isChecked())) + "m");
            }
        }
    }

    // private SensorManager sensorManager = (SensorManager)
    // getSystemService(SENSOR_SERVICE);
    // private Sensor sensor =
    // sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    private SensorManager sensorManager;
    private Sensor sensor;
    private MyListener myListenerInstance = new MyListener();

    private static PresureHeight presureHeight = new PresureHeight();
    private static final int max_values = 16; // How many values to measure to get
                                              // average pressure (start point)
    private static double last_values[] = new double[max_values];
    private static int avg_max = 4;
    private static double presure_avg[] = new double[avg_max];
    private static int avg_cnt = 0;
    private static int counter = 0;
    private static int is_working = 0;

    // ----
    private TextView textView1;
    private TextView textView3;
    private TextView textView4;
    private EditText editText1;
    private CheckBox checkBox1;
    // ----

    NumberFormat nf2 = NumberFormat.getNumberInstance();
    NumberFormat nf1 = NumberFormat.getNumberInstance();

    private static double count_agv()
    {
        double sum = 0;
        for (int i = 0; i < avg_max; ++i) {
            sum += presure_avg[i];
        }
        return sum / avg_max;
    }

    private static void add_avg_value(double value)
    {
        presure_avg[avg_cnt] = value;
        avg_cnt = (avg_cnt + 1) % avg_max;
    }

    public static void reset_counter()
    {
        counter = 0;
    }

    private void startMeasurement()
    {
        presureHeight.set_temperature_celcius(20.0);
        // sensorManager.registerListener(myListenerInstance, sensor,
        // SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(myListenerInstance, sensor, SensorManager.SENSOR_DELAY_FASTEST);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onRestart()
    {
        if (is_working == 1) {
            textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");
            sensorManager.registerListener(myListenerInstance, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else
            sensorManager.unregisterListener(myListenerInstance);
        super.onRestart();
    }

    protected void onResume()
    {
        if (is_working == 1)
            textView3.setText(nf2.format(presureHeight.get_presure_start()) + "hPa");
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

}
