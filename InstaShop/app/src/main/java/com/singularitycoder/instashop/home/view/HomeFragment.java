package com.singularitycoder.instashop.home.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.cart.view.CartListFragment;
import com.singularitycoder.instashop.categories.CategoriesFragment;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.more.MoreFragment;
import com.singularitycoder.instashop.notifications.NotificationsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Nullable
    @BindView(R.id.con_lay_home_frag_root)
    ConstraintLayout conLayHome;

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String TAG = "CartListFragment";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private HelperSharedPreference helperSharedPreference;

    @Nullable
    private ProgressDialog progressDialog;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getBundleData();
        initialisations(view);
        setUpToolBar();
        return view;
    }

    private void getBundleData() {
        if (null != getArguments()) {

        }
    }

    private void initialisations(View view) {
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        helperSharedPreference = HelperSharedPreference.getInstance(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog = new ProgressDialog(getContext());
    }

    private void setUpToolBar() {
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        if (null != activity) {
//            activity.setSupportActionBar(toolbar);
//            activity.setTitle("Cart");
//            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private boolean loadFragment(Fragment fragment) {
        // Home or Base fragments should not contain addToBackStack. But if u want to navigate to home frag then add HomeFrag
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.con_lay_home_frag_root, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                conLayHome.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;

            case R.id.nav_categories:
                fragment = new CategoriesFragment();
                conLayHome.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;

            case R.id.nav_cart:
                fragment = new CartListFragment();
                conLayHome.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;

            case R.id.nav_notifications:
                fragment = new NotificationsFragment();
                conLayHome.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;

            case R.id.nav_more:
                fragment = new MoreFragment();
                conLayHome.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
        }

        return loadFragment(fragment);
    }
}