package com.example.covidheatmap.ui.heatmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.covidheatmap.R;
import com.example.covidheatmap.SettingsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private final int REQUEST_LOCATION = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            // Checks if user has given permission privileges for LocationServices
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            //Allows the map to find the users location
            mMap.setMyLocationEnabled(true);

            //Enables UI Settings
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Add Heatmap to Map
            List<WeightedLatLng> latLngs = null;

            //Reads JSON
            try {
                latLngs = readItems(R.raw.confirmed_covid);
            }catch (JSONException e) {
                Toast.makeText(getActivity(), "Problem reading list of locations.", Toast.LENGTH_LONG).show();
            }

            // Create a heat map tile provider, passing it the latlngs of the confirmed_covid locations.
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .weightedData(latLngs)
                    .radius(15)
                    .build();

            //Add a tile overlay to the map, using the heat map tile provider.
            TileOverlay overlay = googleMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(provider));
            overlay.setVisible(true);

            // For dropping a marker at a point on the Map
            LatLng DemoLocation = new LatLng(30.580529, -95.943441);

            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(DemoLocation).zoom(9).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        return rootView;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        Intent intent = new Intent(getActivity(), SettingsActivity.class);
//        startActivity(intent);
//        return true;
//    }

    private ArrayList<WeightedLatLng> readItems(int resource) throws JSONException {
        ArrayList<WeightedLatLng> list = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            double weight = object.getDouble("confirmed_cases");
            LatLng combinedLatLng = new LatLng(lat, lng);
            list.add(new WeightedLatLng(combinedLatLng, weight));
        }
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}