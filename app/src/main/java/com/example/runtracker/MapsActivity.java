package com.example.runtracker;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.runtracker.databinding.ActivityMapsBinding;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView distText;
    private TextView distText2;

private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMapsBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        distText = findViewById(R.id.distText);
        distText2 = findViewById(R.id.distText2);
        mMap = googleMap;

//        placeMarkers();
        placeMarkers2();



    }

    private void placeMarkers(){
        Intent intent = getIntent();
        ArrayList<Location> markers = (ArrayList<Location>) intent.getSerializableExtra("Markers");
        for(Location i : markers){
            LatLng loco = new LatLng(i.getLatitude(), i.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loco));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(loco));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loco,15));
        }

        float distance = intent.getFloatExtra("Distance", 0);
        String strDistance = String.format("%.2f", distance);
        distText.setText(strDistance + " metres");
    }
    private void placeMarkers2(){
        Intent intent = getIntent();
        ArrayList<Location> markers = (ArrayList<Location>) intent.getSerializableExtra("Markers");
        for(Location i : markers){
            LatLng loco = new LatLng(i.getLatitude(), i.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loco));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(loco));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loco,15));
        }

        float d1 = intent.getFloatExtra("Distance", 0);
        float d2 = intent.getFloatExtra("Test Distance", 0);
        String strDistance = String.format("%.2f", d1);
        distText.setText(strDistance + " metres");

        String strDistance2 = String.format("%.2f", d2);
        distText2.setText(strDistance2 + " metres");
    }
}