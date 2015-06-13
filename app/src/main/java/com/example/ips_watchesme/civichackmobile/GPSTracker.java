
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
import android.widget.Toast;

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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GPSTracker extends Service implements GoogleMap.OnMyLocationChangeListener {

    private double distance;
    private boolean enabled;
    private Location lastLocation;
    private GoogleMap map;
    PolylineOptions tripOptions = new PolylineOptions();

    public GPSTracker(){}

    public GPSTracker(GoogleMap map) {
        this.map = map;
        setup();
        tripOptions.geodesic(true);
    }

    public void onLocationChanged(Location location) {
        map.clear();
        tripOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
        if(enabled) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(location.getLatitude(), location.getLongitude())), 15.f));
            if (lastLocation != null) {
                distance += lastLocation.distanceTo(location);
            }

            if(lastLocation != null) {
                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Polyline polyline = map.addPolyline(tripOptions);
            }
        }

        lastLocation = location;
    }

    private void setup() {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);

        /* Default to Indiana view. */
        CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(39.7910, -86.1480)).zoom(7).build());
        map.moveCamera(myLoc);
    }

    public double getDistance() {
        return distance;
    }

    public void enable() {
        map.clear();
        tripOptions = new PolylineOptions();
        distance = 0;
        enabled = true;
        if(lastLocation != null) {
            CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder().target(new LatLng(lastLocation.getLatitude(),
                            lastLocation.getLongitude())).target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).zoom(15).build());
            map.moveCamera(myLoc);
        }
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void onMyLocationChange(Location lastKnownLocation) {

        if(enabled && lastLocation != null && lastKnownLocation != null) {
            if(lastKnownLocation.distanceTo(lastLocation) > 10)
                onLocationChanged(lastKnownLocation);
        } else {
            lastLocation = lastKnownLocation;
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}