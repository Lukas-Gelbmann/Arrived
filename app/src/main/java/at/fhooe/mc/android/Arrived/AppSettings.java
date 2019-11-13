package at.fhooe.mc.android.Arrived;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * this activity is there to change settings
 */
public class AppSettings extends AppCompatActivity {

    private static final String TAG = "xdd";
    TextView delayDisplay;
    SeekBar delayChanger;
    int delay;

    /**
     * gets called when the activity starts, displays the different objects
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings);
        SharedPreferences sharedPreferences = getSharedPreferences("entries", MODE_PRIVATE);
        //code for switch
        Switch s = findViewById(R.id.switch3);
        s.setChecked(sharedPreferences.getBoolean("switch", false));
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "AppSettings::onCreate(): clicked on switch");
                SharedPreferences sharedPreferences = getSharedPreferences("entries", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("switch", isChecked);
                editor.apply();
            }
        });
        //code for delay seekbar and the text
        delayDisplay = findViewById(R.id.options_delayText);
        delayChanger = findViewById(R.id.options_delaySeekBar);
        delayChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "AppSettings::onCreate(): seekbar changed to: " +progress);
                SharedPreferences sharedPreferences = getSharedPreferences("entries", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (progress <= 8) {
                    progress = progress + 1;
                    delayDisplay.setText(getString(R.string.location_query_delay) +" " + progress + " s");
                    delay = progress * 1000;
                } else if (progress > 8 && progress <= 13) {
                    progress = (progress - 8) * 10;
                    delayDisplay.setText(getString(R.string.location_query_delay) +" " + progress + " s");
                    delay = progress * 1000;
                } else if (progress > 13 && progress <= 18) {
                    progress = progress - 13;
                    delayDisplay.setText(getString(R.string.location_query_delay) +" " + progress + " m");
                    delay = progress * 60000;
                } else if (progress == 19) {
                    delayDisplay.setText(getString(R.string.location_query_delay) +" " +10 +" m");
                    delay = 600000;
                } else {
                    delayDisplay.setText(getString(R.string.location_query_delay) +" "+ 30 +" m");
                    delay = 1800000;
                }
                editor.putInt("delay", delay);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }
}
