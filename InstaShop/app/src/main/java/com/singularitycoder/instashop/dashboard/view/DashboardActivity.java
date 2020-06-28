package com.singularitycoder.instashop.dashboard.view;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperGeneral;

public class DashboardActivity extends AppCompatActivity {

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperObject.setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_dashboard);
        showDashboardFragment();
    }

    private void showDashboardFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.con_lay_dashboard, new DashboardFragment())
                .commit();
    }
}
