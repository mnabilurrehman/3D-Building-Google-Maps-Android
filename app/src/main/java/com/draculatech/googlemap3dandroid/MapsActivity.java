package com.draculatech.googlemap3dandroid;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        drawPolygon(googleMap);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void drawPolygon(GoogleMap mMap) {
        LatLng mapCenter = new LatLng(32.794488, -96.780372);
        //Zoom to camera
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mapCenter, 16, 0, 0)));

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        PolygonOptions polygonOptions1 = new PolygonOptions();
        polygonOptions1.add(new LatLng[]{new LatLng(32.7948702128665, -96.78071821155265),
                new LatLng(32.79478789915277, -96.780713852963),
                new LatLng(32.794745606448785, -96.7807638091059),
                new LatLng(32.79465594788115, -96.78065450908855),
                new LatLng(32.794983444026556, -96.78026383449742),
                new LatLng(32.79507153522898, -96.78037564908573),
                new LatLng(32.79502490803293, -96.78043113728472),
                new LatLng(32.79502635107838, -96.78054211368271)})
                .strokeColor(Color.BLACK).fillColor(Color.BLACK).strokeWidth(1).zIndex(1000);

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.add(new LatLng[]{new LatLng(32.7948702128665 + 0.0003, -96.78071821155265),
                new LatLng(32.79478789915277 + 0.0003, -96.780713852963),
                new LatLng(32.794745606448785 + 0.0003, -96.7807638091059),
                new LatLng(32.79465594788115 + 0.0003, -96.78065450908855),
                new LatLng(32.794983444026556 + 0.0003, -96.78026383449742),
                new LatLng(32.79507153522898 + 0.0003, -96.78037564908573),
                new LatLng(32.79502490803293 + 0.0003, -96.78043113728472),
                new LatLng(32.79502635107838 + 0.0003, -96.78054211368271)})
                .strokeColor(Color.BLACK).fillColor(Color.BLACK).strokeWidth(1).zIndex(2000);

        mMap.addPolygon(polygonOptions);

        drawExcrudedShape(mMap, polygonOptions1, 0.0003);
    }

    private void drawExcrudedShape(GoogleMap map, PolygonOptions coordinates, double height) {

        ArrayList<PairCord> pairs = new ArrayList<>();

        // build line pairs for each wall
        for (int i = 0; i < coordinates.getPoints().size(); i++) {

            LatLng point = coordinates.getPoints().get(i);
            int otherIndex = (i == coordinates.getPoints().size() - 1) ? 0 : i + 1;
            LatLng otherPoint = coordinates.getPoints().get(otherIndex);

            pairs.add(new PairCord(point, otherPoint));
        }

        // draw excrusions
        for (int i = 0; i < pairs.size(); i++) {

            LatLng first = pairs.get(i).latLng1;
            LatLng second = pairs.get(i).latLng2;

            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.add(new LatLng[]{first, new LatLng(first.latitude + height, first.longitude),
                    new LatLng(second.latitude + height, second.longitude),
                    second}).strokeColor(Color.GRAY).fillColor(Color.GRAY).zIndex(3 + i);

            map.addPolygon(polygonOptions);
        }
    }

    //Create drawable from view
    private void getDrawableFromView() {
        View view = null;//TextView.getRootView();

        if (view != null) {
            System.out.println("view is not null.....");
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bm = view.getDrawingCache();

            try {
                if (bm != null) {
                    String dir = Environment.getExternalStorageDirectory().toString();
                    System.out.println("bm is not null.....");
                    OutputStream fos = null;
                    File file = new File(dir, "sample.JPEG");
                    fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    bos.flush();
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println("Error=" + e);
                e.printStackTrace();
            }
        }
    }

    class PairCord {
        LatLng latLng1;
        LatLng latLng2;

        public PairCord(LatLng latLng1, LatLng latLng2) {
            this.latLng1 = latLng1;
            this.latLng2 = latLng2;
        }
    }

}
