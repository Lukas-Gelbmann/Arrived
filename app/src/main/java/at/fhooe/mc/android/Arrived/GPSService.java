package at.fhooe.mc.android.Arrived;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * this class is the geofencing service, it always runs in the background
 */
public class GPSService extends Service {

    private static final String TAG = "xdd";
    int[] radius;
    String[] phoneNumber;
    String[] message;
    float[] lon1;
    float[] lat1;
    int entries;
    private LocationManager locationManager;
    private LocationListener locationListener;

    /**
     * this method gets called whenever the service gets started
     * @param intent intent to give information
     * @param flags flags
     * @param startId startId
     * @return START_STICKY, to restart the activity after it got stoped internally
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * this method gets called whenever the service gets created,
     * it fetches the all the data out of the shared preferences and listens for location changes
     */
    @Override
    public void onCreate() {
        Log.i(TAG, "GPSService::onCreate(): service started");
        //setting size of search entries
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
        entries = sharedPreferences.getInt("entries", 0);
        phoneNumber = new String[entries];
        message = new String[entries];
        lon1 = new float[entries];
        lat1 = new float[entries];
        radius = new int[entries];
        //filling the lists
        for (int i = 0; i < entries; i++) {
            phoneNumber[i] = sharedPreferences.getString("phoneNumber_" + i, "");
            message[i] = sharedPreferences.getString("message_" + i, "");
            lon1[i] = sharedPreferences.getFloat("lon_" + i, 0);
            lat1[i] = sharedPreferences.getFloat("lat_" + i, 0);
            radius[i] = sharedPreferences.getInt("radius_" + i, 0);
            if (radius[i] != 0)
                Log.i(TAG, "GPSService::onCreate():searching for" + lon1[i] + " " + lat1[i] + "in" + radius[i]);
        }
        Log.i(TAG, "GPSService::onCreate():loaded entries");
        //creating locationmanager and locationlistener
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            /**
             * this method gets called whenever the location changes,
             * if the location is near enough (defined by radius), it sends the sms,
             * creates a notification and deletes the entry from the sharedpreferences
             * @param location the actual location
             */
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "GPSService::onLocationChanged(): " + location);
                SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
                for (int i = 0; i < entries; i++) {
                    if (!phoneNumber[i].equals("")) {
                        if (getDistance(lon1[i], lat1[i], location.getLongitude(), location.getLatitude()) < radius[i]) {
                            //deleting entries instant so that it stops searching
                            String number = phoneNumber[i];
                            phoneNumber[i] = "";
                            lon1[i] = 0;
                            lat1[i] = 0;
                            Log.i(TAG, "GPSService::onLocationChanged(): sending sms to: "+ number);
                            //sending sms
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, message[i] + "\n-sent by Arrived", null, null);
                            //creating notification
                            NotificationChannel notificationChannel = new NotificationChannel("sms", "sms", NotificationManager.IMPORTANCE_DEFAULT);
                            notificationChannel.enableLights(true);
                            notificationChannel.enableVibration(true);
                            notificationChannel.setLightColor(Color.GREEN);
                            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500});
                            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                            notificationManager.createNotificationChannel(notificationChannel);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "sms")
                                    .setSmallIcon(R.drawable.arrivedicon)
                                    .setContentTitle("Arrived")
                                    .setContentText(getString(R.string.sms_sent)+" " + sharedPreferences1.getString("name_" +i,"somebody"));
                            notificationManager.notify(0, builder.build());
                            //deleting entry in shared preferences
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.remove("name_" + (i));
                            editor.remove("message_" + (i));
                            editor.remove("place_" + (i));
                            editor.remove("phoneNumber_" + (i));
                            editor.remove("lon_" + (i));
                            editor.remove("lat_" + (i));
                            editor.remove("radius_" + (i));
                            editor.commit();
                        }
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG, "GPSService::onProviderDisabled(): " + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG, "GPSService::onProviderEnabled(): " + provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG, "GPSService::onStatusChanged(): " + provider + " " + status);
            }
        };
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "GPSService::onCreate(): no permission granted");
            return;
        }
        //check if provider enabled
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            Log.e(TAG, "GPSService::onCreate(): network provider not enabled");
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Log.e(TAG, "GPSService::onCreate(): gps provider not enabled");
        //requesting location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, sharedPreferences.getInt("delay",20000), 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, sharedPreferences.getInt("delay",20000), 0, locationListener);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * formula to calculate the distance (in meter) between to places (in longitude and latitude)
     * @param lon1 the destination longitude
     * @param lat1 the destination latitude
     * @param lon2 the actual longitude
     * @param lat2 the actual latitude
     * @return distance in meter
     */
    private double getDistance(double lon1, double lat1, double lon2, double lat2) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        // convert to radians
        double lati1 = Math.toRadians(lat1);
        double lati2 = Math.toRadians(lat2);
        // apply formula
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lati1) *
                        Math.cos(lati2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        Log.i(TAG, "GPSService::getDistance(): " + rad * c + "km");
        return rad * c * 1000;
    }

    /**
     * this method gets called when the service gets stoped
     */
    @Override
    public void onDestroy() {
        Log.i(TAG, "GPSService::onDestroy(): service destroyed");
        super.onDestroy();
    }
}