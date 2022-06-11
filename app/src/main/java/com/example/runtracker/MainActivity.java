package com.example.runtracker;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private TextView titleText;
    private Button locoButton;
    private TextView longText;
    private TextView latText;
    private Button finishButton;
    private TextView tempText;

    private ArrayList<Location> markers = new ArrayList<Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setGUI(); // intitialize the gui components
        finishButton.setEnabled(false);

        if (checkAndRequestPermissions()) {
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // initialize fused location

        createLocationRequest(); // create a location request object

        locoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start getting the locations
                updateLocation();
                locoButton.setEnabled(false); // disable this button
                finishButton.setEnabled(true);
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the buttons and stop receiving locations
                locoButton.setEnabled(true);
                finishButton.setEnabled(false);
                stopLocationUpdates();

                print("Locations Recorded --> " + Integer.toString(markers.size()));

//                float distance = calculateDistance();
//                switchActivity(distance); REVERT BACK TO WORKING SINGLE DISTANCE CALCULATION

                float[] temp = calculateDistance2();
                float d1 = temp[0];
                float d2 = temp[1];
                switchActivity2(d1, d2);

            }
        });

    }

    private float calculateDistance(){
        Location first = markers.get(0); // first recorded location
        float counter = 0; // this is the final distance

        ArrayList<Float> tester = new ArrayList<Float>();

        for (int i = 1; i < markers.size(); i++) { // loop through the arraylist of locations and calculate the distance between each consecutive one
            Location curr = markers.get(i);
            float distance = first.distanceTo(curr);

            tester.add(distance);

            tempText.append("\n" + distance);

            counter += distance;
            first = curr;
        }
        String temp = String.format("%.6f", counter);
        System.out.println("Counter: " + temp);
        latText.setText(temp);

        return counter;
    }

    private float[] calculateDistance2(){
        Location first = markers.get(0); // first recorded location
        float counter = 0; // this is the final distance

        ArrayList<Float> distances = new ArrayList<Float>();

        for (int i = 1; i < markers.size(); i++) { // loop through the arraylist of locations and calculate the distance between each consecutive one
            Location curr = markers.get(i);
            float distance = first.distanceTo(curr);

            distances.add(distance);

            tempText.append("\n" + distance);

            counter += distance;
            first = curr;
        }
        String output = String.format("%.6f", counter);
        System.out.println("Counter: " + output);
        latText.setText(output);

        float avgDist = 0;
        int count = 0;
        for (float i : distances) {
            avgDist += i;
            count += 1;
        }
        avgDist = avgDist / count;

        float testDistance = 0;
        for (float i : distances) {
            if (i > (3 * avgDist)){
                testDistance += avgDist;
            }
            else{
                testDistance += i;
            }
        }

        float[] temp = new float[2];
        temp[0] = counter;
        temp[1] = testDistance;
        return temp;
    }


    private void createLocationRequest() { // sets up the location request object and sets its setting
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(2);
        locationRequest.setWaitForAccurateLocation(true);
    }

    public void updateLocation() { // checks if we have permission, initializes the locationcallback, then uses fusedlocation to start getting the updates
        //check perms
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions();
            return;
        }

        // create locationcallback and loop through each one adding to arraylist
        locationCallback = new LocationCallback() { // initialize a location callback object
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()){ //get it to add the locations it receives to an arraylist of locations
                    markers.add(location);
                    longText.setText(location.toString());
                    //tempText.append("\n" + location.getAccuracy());
                }
            }
        };

        // this actually starts or "calls" the locationcallback
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private boolean checkAndRequestPermissions() { // checks the perms
        int coursePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int finePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (finePerm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coursePerm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }




    private void startLocationUpdates() { // dont yet know if I need this and on resume and onpause
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions();
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    private void switchActivity(float distance){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Distance", distance);
        intent.putExtra("Markers", markers);
        startActivity(intent);
    }
    private void switchActivity2(float distance, float testDistance){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Distance", distance);
        intent.putExtra("Markers", markers);
        intent.putExtra("Test Distance", testDistance);
        startActivity(intent);
    }

    private void cleanMarkers(){

    }


    private void setGUI(){
        titleText = (TextView) findViewById(R.id.titleText);
        locoButton = (Button) findViewById(R.id.locationbutton);
        longText = (TextView) findViewById(R.id.longText);
        latText = (TextView) findViewById(R.id.latText);
        finishButton = (Button) findViewById(R.id.finishButton);
        tempText = (TextView) findViewById(R.id.tempText);
    }
    // Testing Tools
    public void print(String word){
        System.out.println(word);
    }


}