package com.example.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private final static String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final static String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final static int LOCATION_PERMISSION_CODE = 500;
    private FusedLocationProviderClient locationProviderClient ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Weather App");
        ButterKnife.bind(this,v);
        if (getActivity().checkSelfPermission(FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                getActivity().checkSelfPermission(COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            getUserPermission();
        else
            getMyLocation();

        return v;
    }

    private void getMyLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            Task location = locationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: location Found");
                    }else {
                        Log.d(TAG, "onComplete: current location is null");
                    }
                }
            });
        }catch (SecurityException e){
            Log.d(TAG, "getMyLocation: "+e.getMessage());
        }
    }


    private void getUserPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (getActivity().shouldShowRequestPermissionRationale(FINE_LOCATION)) {
                Toast.makeText(getActivity(),
                        "Your location is needed to show you\nthe right temp for you're location"
                        , Toast.LENGTH_LONG)
                        .show();
            }
            String[] permission = {FINE_LOCATION, COARSE_LOCATION};
            getActivity().requestPermissions(permission, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 500:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions.length > 0) {
                    getMyLocation();
                } else {
                    Toast.makeText(getActivity(), "Permission was not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }
}