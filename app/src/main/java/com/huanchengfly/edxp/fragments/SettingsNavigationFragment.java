package com.huanchengfly.edxp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import org.meowcat.edxposed.manager.R;

import de.robv.android.xposed.installer.WelcomeActivity;

public class SettingsNavigationFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("StaticFieldLeak")
    private static Activity sActivity;

    public SettingsNavigationFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_navigation, container, false);

        NavigationView navigationView = v.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        return v;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(false);
        if (sActivity instanceof WelcomeActivity) {
            ((WelcomeActivity) sActivity).navigate(item.getItemId());
            return true;
        }
        return false;
    }
}
