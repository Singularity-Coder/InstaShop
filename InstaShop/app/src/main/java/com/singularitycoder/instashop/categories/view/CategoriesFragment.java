package com.singularitycoder.instashop.categories.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.singularitycoder.instashop.cart.viewmodel.ProductCartViewModel;
import com.singularitycoder.instashop.categories.adapter.CategoriesAdapter;
import com.singularitycoder.instashop.categories.model.CategoriesItem;
import com.singularitycoder.instashop.categories.viewmodel.CategoriesViewModel;
import com.singularitycoder.instashop.helpers.CustomDialogFragment;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.view.ProductListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.lang.String.valueOf;

public final class CategoriesFragment extends Fragment implements CustomDialogFragment.SimpleAlertDialogListener {

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
    private final List<CategoriesItem> dashboardList = new ArrayList<>();

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String IMAGE_BANNER = "https://cdn.pixabay.com/photo/2017/11/06/13/50/family-2923690_960_720.jpg";

    @NonNull
    private final String TAG = "CategoriesFragment";

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
    private ProductCartViewModel productCartViewModel;

    @Nullable
    private CategoriesViewModel categoriesViewModel;

    public CategoriesFragment() {
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
        categoriesViewModel.getAuthUserDataFromRepository(getContext(), helperSharedPreference.getEmail()).observe(getViewLifecycleOwner(), liveDataObserver());
        return view;
    }

    private void initialisations(View fragView) {
        ButterKnife.bind(this, fragView);
        unbinder = ButterKnife.bind(this, fragView);
        helperSharedPreference = HelperSharedPreference.getInstance(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        helperObject.glideImageWithErrHandle(getContext(), IMAGE_BANNER, ivBanner, null);
        productCartViewModel = new ViewModelProvider(this).get(ProductCartViewModel.class);
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
//            activity.setTitle("Categories");
        }
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        toolbar.setOverflowIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
    }

    private void setUpCollapsingToolbar() {
        collapsingToolbarLayout.setTitle("Categories");
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
    }

    private void checkIfUserExists() {
        authListener = firebaseAuth -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) goToMainActivity();
        };
    }

    private void setUpRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        dashboardList.add(new CategoriesItem(R.drawable.ic_local_movies_black_24dp, "Movies"));
        dashboardList.add(new CategoriesItem(R.drawable.ic_music_note_black_24dp, "Music"));
        dashboardList.add(new CategoriesItem(R.drawable.ic_camera_alt_black_24dp, "Cameras"));
        dashboardList.add(new CategoriesItem(R.drawable.ic_toys_black_24dp, "Toys"));
        dashboardList.add(new CategoriesItem(R.drawable.ic_phone_iphone_black_24dp, "Mobiles"));
        dashboardList.add(new CategoriesItem(R.drawable.ic_computer_black_24dp, "Computers"));

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(dashboardList, getContext());
        categoriesAdapter.setHasStableIds(true);
        categoriesAdapter.setDashView((position) -> {

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
        recyclerView.setAdapter(categoriesAdapter);
    }

    private void goToMainActivity() {
        getActivity().startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    @NonNull
    private Observer<RequestStateMediator> liveDataObserver() {
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

                if (("STATE_AUTH_USER_DATA").equals(requestStateMediator.getKey())) {
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

                if (("STATE_DELETE_ACCOUNT").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        goToMainActivity();
                    });
                }

                if (("STATE_UPDATE_EMAIL").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                    });
                }

                if (("STATE_CHANGE_PASSWORD").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                    });
                }

                if (("STATE_SIGN_OUT").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        authListener = firebaseAuth -> {
                            helperSharedPreference.setUserDocId("");
                            helperSharedPreference.setMemberType("");
                            helperSharedPreference.setName("");
                            helperSharedPreference.setEmail("");
                            productCartViewModel.deleteAllFromRoomDbFromRepository();
                            if (null == firebaseAuth.getCurrentUser()) goToMainActivity();
                        };
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

    private void showFragment(Bundle bundle, int parentLayout, Fragment fragment) {
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

    // FIXME: 30/06/20
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
        inflater.inflate(R.menu.menu_categories, menu);
        dashMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
                if (helperObject.hasInternet(getContext())) {
                    categoriesViewModel.deleteAccountFromRepository().observe(this, liveDataObserver());
                } else {
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_sign_out:
                if (helperObject.hasInternet(getContext())) {
                    categoriesViewModel.signOurFromRepository().observe(getViewLifecycleOwner(), liveDataObserver());
                } else {
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
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
            if (helperObject.hasInternet(getContext())) {
                categoriesViewModel.updateEmailFromRepository(dialog, (String) map.get("KEY_EMAIL")).observe(getViewLifecycleOwner(), liveDataObserver());
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        }

        if (("DIALOG_TYPE_CHANGE_PASSWORD").equals(dialogType)) {
            if (helperObject.hasInternet(getContext())) {
                categoriesViewModel.changePasswordFromRepository(dialog, (String) map.get("KEY_PASSWORD")).observe(getViewLifecycleOwner(), liveDataObserver());
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDialogNegativeClick(String dialogType, DialogFragment dialog) {

    }

    @Override
    public void onDialogNeutralClick(String dialogType, DialogFragment dialog) {

    }
}
