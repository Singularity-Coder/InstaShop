package com.singularitycoder.instashop.dashboard.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.admin.view.AddProductsFragment;
import com.singularitycoder.instashop.auth.view.MainActivity;
import com.singularitycoder.instashop.cart.view.CartListFragment;
import com.singularitycoder.instashop.dashboard.adapter.DashboardAdapter;
import com.singularitycoder.instashop.dashboard.model.DashboardItem;
import com.singularitycoder.instashop.dashboard.viewmodel.DashboardViewModel;
import com.singularitycoder.instashop.helpers.CustomDialogFragment;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.view.ProductListFragment;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.lang.String.valueOf;

public class DashboardFragment extends Fragment implements CustomDialogFragment.SimpleAlertDialogListener {

    @Nullable
    @BindView(R.id.iv_banner)
    ImageView ivBanner;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Nullable
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @NonNull
    private final List<DashboardItem> dashboardList = new ArrayList<>();

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String IMAGE_BANNER = "https://cdn.pixabay.com/photo/2017/11/06/13/50/family-2923690_960_720.jpg";

    @NonNull
    private final String TAG = "DashboardActivity";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private HelperSharedPreference helperSharedPreference;

    @NonNull
    private FirebaseAuth.AuthStateListener authListener = firebaseAuth -> {
        if (null == firebaseAuth.getCurrentUser()) goToMainActivity();
    };

    @Nullable
    private Menu dashMenu;

    @Nullable
    private ProgressDialog progressDialog;

    @Nullable
    private ProductViewModel productViewModel;

    @Nullable
    private DashboardViewModel dashboardViewModel;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initialisations(view);
        setUpToolBar();
        setUpCollapsingToolbar();
        checkIfUserExists();
        dashboardViewModel.getAuthUserDataFromRepository(getContext(), helperSharedPreference.getEmail()).observe(getViewLifecycleOwner(), liveDataObserver());
        return view;
    }

    private void initialisations(View fragView) {
        ButterKnife.bind(this, fragView);
        unbinder = ButterKnife.bind(this, fragView);
        helperSharedPreference = HelperSharedPreference.getInstance(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        helperObject.glideImageWithErrHandle(getContext(), IMAGE_BANNER, ivBanner, null);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle("InstaShop");
        }
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        toolbar.setOverflowIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
    }

    private void setUpCollapsingToolbar() {
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
    }

    private void checkIfUserExists() {
        authListener = firebaseAuth -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) goToMainActivity();
        };
    }

    private void setUpRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        dashboardList.add(new DashboardItem(R.drawable.ic_local_movies_black_24dp, "Movies"));
        dashboardList.add(new DashboardItem(R.drawable.ic_music_note_black_24dp, "Music"));
        dashboardList.add(new DashboardItem(R.drawable.ic_camera_alt_black_24dp, "Cameras"));
        dashboardList.add(new DashboardItem(R.drawable.ic_toys_black_24dp, "Toys"));
        dashboardList.add(new DashboardItem(R.drawable.ic_phone_iphone_black_24dp, "Mobiles"));
        dashboardList.add(new DashboardItem(R.drawable.ic_computer_black_24dp, "Computers"));

        DashboardAdapter dashboardAdapter = new DashboardAdapter(dashboardList, getContext());
        dashboardAdapter.setHasStableIds(true);
        dashboardAdapter.setDashView((position) -> {

            Bundle movieBundle = new Bundle();
            movieBundle.putString("CATEGORY", "Movies");
            if (position == 0)
                showFragment(movieBundle, R.id.con_lay_dashboard, new ProductListFragment());

            Bundle musicBundle = new Bundle();
            musicBundle.putString("CATEGORY", "Music");
            if (position == 1)
                showFragment(musicBundle, R.id.con_lay_dashboard, new ProductListFragment());

            Bundle cameraBundle = new Bundle();
            cameraBundle.putString("CATEGORY", "Cameras");
            if (position == 2)
                showFragment(cameraBundle, R.id.con_lay_dashboard, new ProductListFragment());

            Bundle toyBundle = new Bundle();
            toyBundle.putString("CATEGORY", "Toys");
            if (position == 3)
                showFragment(toyBundle, R.id.con_lay_dashboard, new ProductListFragment());

            Bundle mobileBundle = new Bundle();
            mobileBundle.putString("CATEGORY", "Mobiles");
            if (position == 4)
                showFragment(mobileBundle, R.id.con_lay_dashboard, new ProductListFragment());

            Bundle computerBundle = new Bundle();
            computerBundle.putString("CATEGORY", "Computers");
            if (position == 5)
                showFragment(computerBundle, R.id.con_lay_dashboard, new ProductListFragment());
        });
        recyclerView.setAdapter(dashboardAdapter);
    }

    private void updateEmail(DialogFragment dialog, String newEmail) {
        FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getActivity().runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            signOut();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "updateEmail: trace: " + e.getMessage()));
    }

    private void changePassword(DialogFragment dialog, String password) {
        getActivity().runOnUiThread(() -> progressDialog.show());
        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
                    .updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            getActivity().runOnUiThread(() -> {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                signOut();
                            });
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "changePassword: trace: " + e.getMessage()));
        }
    }

    private void signOut() {
        AsyncTask.execute(() -> {
            FirebaseAuth.getInstance().signOut();
            authListener = firebaseAuth -> {
                helperSharedPreference.setUserDocId("");
                helperSharedPreference.setMemberType("");
                helperSharedPreference.setName("");
                helperSharedPreference.setEmail("");
                productViewModel.deleteAll();
                if (null == firebaseAuth.getCurrentUser()) goToMainActivity();
            };
        });
    }

    private void goToMainActivity() {
        getActivity().startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    private Observer liveDataObserver() {
        Observer<RequestStateMediator> observer = null;
        observer = requestStateMediator -> {
            if (UiState.LOADING == requestStateMediator.getStatus()) {
                getActivity().runOnUiThread(() -> {
                    progressDialog.setMessage(valueOf(requestStateMediator.getMessage()));
                    progressDialog.setCanceledOnTouchOutside(false);
                    if (null != progressDialog && !progressDialog.isShowing())
                        progressDialog.show();
                });
            }

            if (UiState.SUCCESS == requestStateMediator.getStatus()) {

                if (("AUTH_USER_DATA").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                        // Hide add products menu if shopper
                        MenuItem addItem = dashMenu.findItem(R.id.action_add_products);
                        if (("Admin").equals(helperSharedPreference.getMemberType())) {
                            addItem.setVisible(true);
                        } else {
                            addItem.setVisible(false);
                        }

                        dashboardList.clear();
                        setUpRecyclerView();
                    });
                }

                if (("DELETE_ACCOUNT").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        goToMainActivity();
                    });
                }

                if (("XXXX").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                    });
                }

                getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }

            if (UiState.EMPTY == requestStateMediator.getStatus()) {
                getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }

            if (UiState.ERROR == requestStateMediator.getStatus()) {
                getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }
        };

        return observer;
    }

    public void showFragment(Bundle bundle, int parentLayout, Fragment fragment) {
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(parentLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    // todo generify this
    private void btnUpdateEmail() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "updateEmailDialog");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void btnChangePassword() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "changePasswordDialog");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);
        dashMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_cart:
                showFragment(new Bundle(), R.id.con_lay_dashboard, new CartListFragment());
                return true;
            case R.id.action_add_products:
                showFragment(new Bundle(), R.id.con_lay_dashboard, new AddProductsFragment());
                return true;
            case R.id.action_update_email:
                btnUpdateEmail();
                return true;
            case R.id.action_change_password:
                btnChangePassword();
                return true;
            case R.id.action_delete_account:
                dashboardViewModel.deleteAccountFromRepository().observe(this, liveDataObserver());
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) FirebaseAuth.getInstance().removeAuthStateListener(authListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDialogPositiveClick(String dialogType, DialogFragment dialog, Map<Object, Object> map) {
        if (("DIALOG_TYPE_UPDATE_EMAIL").equals(dialogType)) {
            AsyncTask.execute(() -> updateEmail(dialog, (String) map.get("KEY_EMAIL")));
        }

        if (("DIALOG_TYPE_CHANGE_PASSWORD").equals(dialogType)) {
            changePassword(dialog, (String) map.get("KEY_PASSWORD"));
        }
    }

    @Override
    public void onDialogNegativeClick(String dialogType, DialogFragment dialog) {

    }

    @Override
    public void onDialogNeutralClick(String dialogType, DialogFragment dialog) {

    }
}
