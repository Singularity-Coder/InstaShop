package com.singularitycoder.instashop.home.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperGeneral;

public final class BaseActivity extends AppCompatActivity {

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperObject.setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_base);
        showBaseFragment();
    }

    private void showBaseFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.con_lay_base_activity_root, new BaseFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(); //Pops one of the added fragments
        }
    }
}
