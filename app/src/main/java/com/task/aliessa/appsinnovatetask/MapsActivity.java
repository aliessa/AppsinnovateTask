package com.task.aliessa.appsinnovatetask;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;


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
        LatLng Appsinnovate = new LatLng(30.100847, 31.379992);
        mMap.addMarker(new MarkerOptions().position(Appsinnovate).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Appsinnovate,18));
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Projection projection = mMap.getProjection();
                LatLng markerPosition = marker.getPosition();
                Point markerPoint = projection.toScreenLocation(markerPosition);
                Point targetPoint = new Point(markerPoint.x, markerPoint.y);
                LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 1000, null);
                marker.showInfoWindow();
                return true;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 1000, null);
            }
        });
        // Add a marker in Sydney and move the camera
    }



    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
        }

        @Override
        public View getInfoContents(Marker innerMarker) {

            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker windowMarker) {
            marker = windowMarker;


            final TextView cityTitle;
            ImageView closePopUp =  view.findViewById(R.id.close_popup);
            cityTitle =  view.findViewById(R.id.cityTitle);
                  cityTitle.setText("Appsinnovate Company ");

            closePopUp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    marker.hideInfoWindow();
                    return true;
                }
            });
            return view;
        }
    }
}
