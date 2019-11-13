package at.fhooe.mc.android.Arrived;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this activity starts after the fab in MainActivity has been pressed
 */
public class CreateEntry extends AppCompatActivity {

    private static final String TAG = "xdd";
    EditText name;
    EditText phoneNumber;
    EditText message;
    String place;
    float lon;
    float lat;
    int radius = 2000;
    TextView radiusDisplay;
    SeekBar radiusChanger;
    boolean foundLocation = false;
    private AutoCompleteTextView mSearchText;

    /**
     * this method gets called whenever the activity starts
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "CreateEntry::onCreate(): activity created");
        radius = 2000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entry);
        //get ids
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.nummer);
        message = findViewById(R.id.nachricht);
        mSearchText = findViewById(R.id.creatEntry_Search);
        init();
        radiusDisplay = findViewById(R.id.textView1);
        radiusChanger = findViewById(R.id.seekBar);
        radiusChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 8) {
                    progress = progress * 100 + 100;
                    radiusDisplay.setText(getString(R.string.create_entry_change_radius)+" " + progress + " m");
                    radius = progress;
                } else if (progress == 19) {
                    radiusDisplay.setText(getString(R.string.create_entry_change_radius)+" 20 km");
                    radius = 20000;
                } else if (progress == 20) {
                    radiusDisplay.setText(getString(R.string.create_entry_change_radius)+" 50 km");
                    radius = 50000;
                } else {
                    progress = progress - 8;
                    radiusDisplay.setText(getString(R.string.create_entry_change_radius)+" " + progress + " km");
                    radius = progress * 1000;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Button create = findViewById(R.id.createbutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (everythingFilledOut()) {
                    //if every field is filled out and the geolocation api has found something
                    Log.i(TAG, "CreateEntry::onClick(): returning new entry");
                    Intent i = new Intent();
                    i.putExtra("name", name.getText().toString());
                    i.putExtra("phoneNumber", phoneNumber.getText().toString());
                    i.putExtra("message", message.getText().toString());
                    i.putExtra("address", place);
                    i.putExtra("lon", lon);
                    i.putExtra("lat", lat);
                    i.putExtra("radius", radius);
                    setResult(RESULT_OK, i);
                    finish();
                    Toast.makeText(getApplicationContext(), name.getText().toString() + " added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill out everything!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "CreateEntry::onClick(): something is not filled out");
                }
            }
        });
    }

    /**
     * this method checks if all the edittexts are filled out
     * @return true if everything filled out, else false
     */
    private boolean everythingFilledOut() {
        return (!name.getText().toString().equals("") && !phoneNumber.getText().toString().equals("") && !message.getText().toString().equals("") && foundLocation);
    }

    /**
     * set actionlistener on the edittext which is responsible for geolocating and check things
     */
    private void init() {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });
    }

    /**
     * using the google api to geolocate, sets the edittext to the entry which has been found
     */
    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(CreateEntry.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "CreateEntry::geoLocate(): IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            foundLocation = true;
            Address address = list.get(0);
            mSearchText.setText(address.getAddressLine(0));
            place = address.getAddressLine(0);
            lon = (float) address.getLongitude();
            lat = (float) address.getLatitude();
            Log.i(TAG, "CreateEntry::geoLocate(): found a location: " + address.toString());
        } else {
            foundLocation = false;
            Log.e(TAG, "CreateEntry::geoLocate(): found no location");
        }
    }
}
