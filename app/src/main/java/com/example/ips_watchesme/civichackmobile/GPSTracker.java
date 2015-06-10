
package com.example.ips_watchesme.civichackmobile;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class GPSTracker extends Service implements GoogleMap.OnMyLocationChangeListener {

    private double distance;
    private boolean enabled;
    private Location lastLocation;
    private GoogleMap map;

    public GPSTracker(GoogleMap map) {
        this.map = map;
        setup();
    }

    public void onLocationChanged(Location location) {
        if(enabled) {
            CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder().target(new LatLng(location.getLatitude(),
                            location.getLongitude())).target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(17).build());
            map.animateCamera(myLoc);
            if (lastLocation != null) {
                distance += lastLocation.distanceTo(location);
            }

            if(lastLocation != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                PolylineOptions rectOptions = new PolylineOptions()
                        .add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                        .add(new LatLng(location.getLatitude(), location.getLongitude()));

                // Get back the mutable Polyline
                Polyline polyline = map.addPolyline(rectOptions);

                Circle circle = map.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(2)
                        .fillColor(Color.BLUE));
            }
        }

        lastLocation = location;
    }

    private void setup() {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);

        /* Default to Indiana view. */
        CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(39.7910, -86.1480)).zoom(6).build());
        map.moveCamera(myLoc);
    }

    public double getDistance() {
        return distance;
    }

    public void enable() {
        map.clear();
        distance = 0;
        enabled = true;
        if(lastLocation != null) {
            CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder().target(new LatLng(lastLocation.getLatitude(),
                            lastLocation.getLongitude())).target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).zoom(17).build());
            map.moveCamera(myLoc);
        }
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void onMyLocationChange(Location lastKnownLocation) {

        if(enabled) {
            if(lastKnownLocation.distanceTo(lastLocation) > 20)
                onLocationChanged(lastKnownLocation);
        } else {
            lastLocation = lastKnownLocation;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}