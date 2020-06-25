package com.singularitycoder.instashop.dashboard.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.view.ProductListFragment;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.lang.String.valueOf;

public class DashboardActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperObject.setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_dashboard);
        initialisations();
        setUpToolBar();
        setUpCollapsingToolbar();
        checkIfUserExists();
        dashboardViewModel.getAuthUserDataFromRepository(this, helperSharedPreference.getEmail()).observe(this, liveDataObserver());
    }

    private void initialisations() {
        ButterKnife.bind(this);
        unbinder = ButterKnife.bind(this);
        helperSharedPreference = HelperSharedPreference.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        helperObject.glideImageWithErrHandle(this, IMAGE_BANNER, ivBanner, null);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("InstaShop");
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
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

        DashboardAdapter dashboardAdapter = new DashboardAdapter(dashboardList, this);
        dashboardAdapter.setHasStableIds(true);
        dashboardAdapter.setDashView((position) -> {
            if (position == 0) showFragment("CATEGORY", "Movies", new ProductListFragment());

            if (position == 1) showFragment("CATEGORY", "Music", new ProductListFragment());

            if (position == 2) showFragment("CATEGORY", "Cameras", new ProductListFragment());

            if (position == 3) showFragment("CATEGORY", "Toys", new ProductListFragment());

            if (position == 4) showFragment("CATEGORY", "Mobiles", new ProductListFragment());

            if (position == 5) showFragment("CATEGORY", "Computers", new ProductListFragment());
        });
        recyclerView.setAdapter(dashboardAdapter);
    }

    private void showFragment(String key, String value, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.con_lay_dashboard, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void btnUpdateEmail() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etUpdateEmail = new EditText(DashboardActivity.this);
        etUpdateEmail.setHint("Type New Email");
        etUpdateEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams etUpdateEmailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etUpdateEmailParams.setMargins(48, 16, 48, 0);
        etUpdateEmail.setLayoutParams(etUpdateEmailParams);

        linearLayout.addView(etUpdateEmail);

        AlertDialog dialog = new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Update Email")
                .setMessage("Enter new Email ID!")
                .setView(linearLayout)
                .setPositiveButton("UPDATE", (dialog1, which) -> {
                    if (helperObject.hasInternet(this)) {
                        if (!TextUtils.isEmpty(valueOf(etUpdateEmail.getText()))) {
                            AsyncTask.execute(() -> updateEmail(dialog1, valueOf(etUpdateEmail.getText())));
                        } else {
                            Toast.makeText(this, "Email is Required!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private void updateEmail(DialogInterface dialog, String newEmail) {
        FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            signOut();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(DashboardActivity.this, "Failed to update Email!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "updateEmail: trace: " + e.getMessage()));
    }

    private void btnChangePassword() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etNewPassword = new EditText(DashboardActivity.this);
        etNewPassword.setHint("Type New Password");
        etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        LinearLayout.LayoutParams etNewPasswordParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etNewPasswordParams.setMargins(48, 16, 48, 0);
        etNewPassword.setLayoutParams(etNewPasswordParams);

        linearLayout.addView(etNewPassword);

        AlertDialog dialog = new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Change Password")
                .setMessage("Type new password. ")
                .setView(linearLayout)
                .setPositiveButton("CHANGE", (dialog1, which) -> {
                    if (helperObject.hasInternet(DashboardActivity.this)) {
                        if (!TextUtils.isEmpty(valueOf(etNewPassword.getText()))) {
                            AsyncTask.execute(() -> changePassword(dialog1, valueOf(etNewPassword.getText())));
                        } else {
                            Toast.makeText(this, "Password is Required!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private void changePassword(DialogInterface dialog, String password) {
        runOnUiThread(() -> progressDialog.show());
        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
                    .updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                signOut();
                            });
                        } else {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(DashboardActivity.this, "Failed to update Password!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "changePassword: trace: " + e.getMessage()));
        }
    }

    private void deleteAccount() {
        AsyncTask.execute(() -> {
            runOnUiThread(() -> progressDialog.show());
            if (null != FirebaseAuth.getInstance().getCurrentUser()) {
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                runOnUiThread(() -> {
                                    goToMainActivity();
                                    progressDialog.dismiss();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(DashboardActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                });
                            }
                        })
                        .addOnFailureListener(e -> Log.d(TAG, "removeUser: trace: " + e.getMessage()));
            }
        });
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
        DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        DashboardActivity.this.finish();
    }

    private void addProducts() {
        Fragment fragment = new AddProductsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.con_lay_dashboard, fragment)
                .addToBackStack(null)
                .commit();
    }

    private Observer liveDataObserver() {
        Observer<RequestStateMediator> observer = null;
        observer = requestStateMediator -> {
            if (UiState.LOADING == requestStateMediator.getStatus()) {
                runOnUiThread(() -> {
                    progressDialog.setMessage(valueOf(requestStateMediator.getMessage()));
                    progressDialog.setCanceledOnTouchOutside(false);
                    if (null != progressDialog && !progressDialog.isShowing())
                        progressDialog.show();
                });
            }

            if (UiState.SUCCESS == requestStateMediator.getStatus()) {

                if (("AUTH_USER_DATA").equals(requestStateMediator.getKey())) {
                    runOnUiThread(() -> {

                        // Hide add products menu if shopper
                        MenuItem addItem = dashMenu.findItem(R.id.action_add_products);
                        if (("Admin").equals(helperSharedPreference.getMemberType())) {
                            addItem.setVisible(true);
                        } else {
                            addItem.setVisible(false);
                        }

                        setUpRecyclerView();
                    });
                }

                if (("XXXX").equals(requestStateMediator.getKey())) {
                    runOnUiThread(() -> {

                    });
                }

                if (("XXXX").equals(requestStateMediator.getKey())) {
                    runOnUiThread(() -> {

                    });
                }

                runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(DashboardActivity.this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }

            if (UiState.EMPTY == requestStateMediator.getStatus()) {
                runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(DashboardActivity.this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }

            if (UiState.ERROR == requestStateMediator.getStatus()) {
                runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(DashboardActivity.this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }
        };

        return observer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        dashMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_cart:
                showFragment("", "", new CartListFragment());
                return true;
            case R.id.action_add_products:
                addProducts();
                return true;
            case R.id.action_update_email:
                btnUpdateEmail();
                return true;
            case R.id.action_change_password:
                btnChangePassword();
                return true;
            case R.id.action_delete_account:
                deleteAccount();
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
