package com.singularitycoder.instashop.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.home.view.HomeFragment;

public final class BaseActivity extends AppCompatActivity {

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
                .replace(R.id.con_lay_dashboard, new BaseFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(); //Pops one of the added fragments
        }
    }
}
