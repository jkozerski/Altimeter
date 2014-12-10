package com.gmail.januszkozerski.altimeter;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.annotation.TargetApi;
import android.os.Build;

public class ConfigurationActivity extends Activity {
    
    private SeekBar     startAvgMax_seekBar;
    private SeekBar     currentAvgMax_seekBar;
    private TextView    startSampleCountValue_textView;
    private TextView    currentSampleCountValue_textView;
    private Button      resetDefault;
    private RadioButton sensorDelay_RadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_activity);
        // hide button in the action bar.
        setupActionBar();

        startAvgMax_seekBar = (SeekBar) findViewById(R.id.startAvgMax_seekBar);
        startSampleCountValue_textView = (TextView)
                findViewById(R.id.startSampleCountValue_textView);

        currentAvgMax_seekBar = (SeekBar) findViewById(R.id.currentAvgMax_seekBar);
        currentSampleCountValue_textView = (TextView)
                findViewById(R.id.currentSampleCountValue_textView);

        resetDefault = (Button) findViewById(R.id.resetDefault_button);
        resetDefault.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                startAvgMax_seekBar.setProgress(Configuration.start_avg_max_set_default-1);
                startSampleCountValue_textView.setText(String.valueOf(Configuration.start_avg_max_set_default));
                
                currentAvgMax_seekBar.setProgress(Configuration.current_avg_max_set_default-1);
                currentSampleCountValue_textView.setText(String.valueOf(Configuration.current_avg_max_set_default));
            }
        });

        switch (Configuration.sensorManagerDelay) {
        case SensorManager.SENSOR_DELAY_NORMAL:
            sensorDelay_RadioButton = (RadioButton) findViewById(R.id.sensorNormal_radio);
            break;
        case SensorManager.SENSOR_DELAY_UI:
            sensorDelay_RadioButton = (RadioButton) findViewById(R.id.sensorFast_radio);
            break;
        case SensorManager.SENSOR_DELAY_FASTEST:
            sensorDelay_RadioButton = (RadioButton) findViewById(R.id.sensorFastest_radio);
            break;
        default:
            sensorDelay_RadioButton = (RadioButton) findViewById(R.id.sensorNormal_radio);
        }
        sensorDelay_RadioButton.setChecked(true);

        /* Seek bar for setting up a number of samples to calculate average value of
         * pressure at current level */
        currentAvgMax_seekBar.setMax(Configuration.CURRENT_AVG_MAX-1); // Set max value
        currentAvgMax_seekBar.setProgress(Configuration.current_avg_max_set-1);
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
        
        /* Seek bar for setting up a number of samples to calculate average value of
         * pressure at start level */
        startAvgMax_seekBar.setMax(Configuration.START_AVG_MAX-1); // Set max value
        startAvgMax_seekBar.setProgress(Configuration.start_avg_max_set-1);
        startSampleCountValue_textView.setText(String.valueOf(Configuration.start_avg_max_set));
        startAvgMax_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                startSampleCountValue_textView.setText(String.valueOf(progress+1));
                Configuration.start_avg_max_set = progress+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            { /* Nothing to do here */ }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Do not add the menu 
        //getMenuInflater().inflate(R.menu.configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    /* Change pressure sensor delay */
    public void onRadioButtonClicked(View view)
    {
        Log.d("WYS", "onRadioButtonClicked");
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.sensorNormal_radio:
                if (checked)
                    Configuration.sensorManagerDelay = SensorManager.SENSOR_DELAY_NORMAL;
                break;
            case R.id.sensorFast_radio:
                if (checked)
                    Configuration.sensorManagerDelay = SensorManager.SENSOR_DELAY_UI;
                break;
            case R.id.sensorFastest_radio:
                if (checked)
                    Configuration.sensorManagerDelay = SensorManager.SENSOR_DELAY_FASTEST;
                break;
        }
    }

}
