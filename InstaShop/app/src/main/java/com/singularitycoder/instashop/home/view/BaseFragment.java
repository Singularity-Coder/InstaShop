package com.singularitycoder.instashop.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.cart.view.CartListFragment;
import com.singularitycoder.instashop.categories.view.CategoriesFragment;
import com.singularitycoder.instashop.more.MoreFragment;
import com.singularitycoder.instashop.payments.PaymentsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public final class BaseFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Nullable
    @BindView(R.id.con_lay_base_frag_root)
    ConstraintLayout conLayBase;
    @Nullable
    @BindView(R.id.bottom_navigation_base)
    BottomNavigationView bottomNavigationView;

    @NonNull
    private final String TAG = "BaseFragment";

    @NonNull
    private Unbinder unbinder;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        getBundleData();
        initialisations(view);
        setUpToolBar();
        setUpBottomNavigation();
        loadFragment(new HomeFragment());   // Loading the default fragment
        return view;
    }

    private void getBundleData() {
        if (null != getArguments()) {

        }
    }

    private void initialisations(View view) {
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
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

    private void setUpBottomNavigation() {
        // Getting bottom navigation view and attaching the listener
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
//        bottomNavigationView.setItemIconTintList(null);
    }

    private boolean loadFragment(Fragment fragment) {
        // Home or Base fragments should not contain addToBackStack. But if u want to navigate to home frag then add HomeFrag
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                conLayBase.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_categories:
                fragment = new CategoriesFragment();
                conLayBase.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_cart:
                fragment = new CartListFragment();
                conLayBase.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_payments:
                fragment = new PaymentsFragment();
                conLayBase.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_more:
                fragment = new MoreFragment();
                conLayBase.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;
        }

        return loadFragment(fragment);
    }
}