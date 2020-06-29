package com.singularitycoder.instashop.home.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    @Nullable
    @BindView(R.id.con_lay_home_frag_root)
    ConstraintLayout conLayHome;

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String TAG = "HomeFragment";

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
}