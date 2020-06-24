package com.singularitycoder.instashop.auth.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.auth.model.AuthUserItem;
import com.singularitycoder.instashop.auth.viewmodel.AuthViewModel;
import com.singularitycoder.instashop.dashboard.view.DashboardActivity;
import com.singularitycoder.instashop.helpers.CustomDialogFragment;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements CustomDialogFragment.ListDialogListener, CustomDialogFragment.ResetPasswordListener {

    @Nullable
    @BindView(R.id.iv_background)
    ImageView ivBackground;
    @Nullable
    @BindView(R.id.tv_member_type)
    TextView tvMemberType;
    @Nullable
    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @Nullable
    @BindView(R.id.et_name)
    EditText etName;
    @Nullable
    @BindView(R.id.et_email)
    EditText etEmail;
    @Nullable
    @BindView(R.id.et_password)
    EditText etPassword;
    @Nullable
    @BindView(R.id.btn_authenticate)
    Button btnAuthenticate;
    @Nullable
    @BindView(R.id.btn_already_registered)
    Button btnAlreadyRegistered;
    @Nullable
    @BindView(R.id.btn_reset_password)
    Button btnResetPassword;

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final String IMAGE_BACKGROUND = "https://cdn.pixabay.com/photo/2017/11/06/13/50/family-2923690_960_720.jpg";

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private Unbinder unbinder;

    @Nullable
    private ProgressDialog progressDialog;

    @Nullable
    private AuthViewModel authViewModel;

    // todo null checks for progress dialog
    // todo active network listener
    // todo use snackbar instead of toast
    // todo work manager service to sync remote n local db cart list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperObject.setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initialisations();
        checkIfUserExists();
        setClickListeners();
    }

    private void initialisations() {
        ButterKnife.bind(this);
        unbinder = ButterKnife.bind(this);
        helperObject.glideImage(this, IMAGE_BACKGROUND, ivBackground);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        btnAuthenticate.setText("SignUp");
        btnAlreadyRegistered.setText("Already Registered? LogIn");
        btnResetPassword.setVisibility(View.GONE);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void checkIfUserExists() {
        if (null != FirebaseAuth.getInstance().getCurrentUser()) goToDashboardActivity();
    }

    private void setClickListeners() {
        compositeDisposable.add(
                RxView.clicks(btnAuthenticate)
                        .map(o -> btnAuthenticate)
                        .subscribe(
                                button -> btnAuthenticate(),
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnAlreadyRegistered)
                        .map(o -> btnAlreadyRegistered)
                        .subscribe(
                                button -> btnAlreadyRegistered(),
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnResetPassword)
                        .map(o -> btnResetPassword)
                        .subscribe(
                                button -> btnResetPassword(),
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(tvMemberType)
                        .map(o -> tvMemberType)
                        .subscribe(
                                button -> btnShowMemberTypeDialog(),
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );
    }

    private void btnShowMemberTypeDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_TITLE", "Who are you?");
        bundle.putStringArray("KEY_LIST", new String[]{"Admin", "Shopper"});

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void btnAuthenticate() {
        if (("signup").equals(valueOf(btnAuthenticate.getText()).toLowerCase().trim())) {
            if (hasInternet(MainActivity.this)) {
                if (hasValidInput("signup", tvMemberType, etEmail, etPassword)) {
                    authViewModel.signUpFromRepository(
                            MainActivity.this,
                            valueOf(tvMemberType.getText()),
                            valueOf(etName.getText()),
                            valueOf(etEmail.getText()),
                            valueOf(etPassword.getText()),
                            valueOf(helperObject.getCurrentEpochTime()),
                            helperObject.currentDateTime()
                    ).observe(MainActivity.this, liveDataObserver());
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (hasInternet(MainActivity.this)) {
                if (hasValidInput("login", null, etEmail, etPassword)) {
                    authViewModel.signInFromRepository(
                            MainActivity.this,
                            valueOf(etEmail.getText()),
                            valueOf(etPassword.getText())
                    ).observe(MainActivity.this, liveDataObserver());
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void btnAlreadyRegistered() {
        if (("signup").equals(valueOf(btnAuthenticate.getText()).toLowerCase().trim())) {
            btnAuthenticate.setText("SignIn");
            btnResetPassword.setVisibility(View.VISIBLE);
            tvMemberType.setVisibility(View.GONE);
            etName.setVisibility(View.GONE);
            tilName.setVisibility(View.GONE);
            btnAlreadyRegistered.setText("Not a member? Create New Account");
        } else {
            btnAuthenticate.setText("SignUp");
            btnResetPassword.setVisibility(View.GONE);
            tvMemberType.setVisibility(View.VISIBLE);
            etName.setVisibility(View.VISIBLE);
            tilName.setVisibility(View.VISIBLE);
            btnAlreadyRegistered.setText("Already Registered? LogIn");
        }
    }

    private void btnResetPassword() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "resetPasswordDialog");

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean hasValidInput(String key, TextView tvMemberType, EditText etEmail, EditText etPassword) {
        String email = valueOf(etEmail.getText()).trim();
        String password = valueOf(etPassword.getText()).trim();

        if (("signup").equals(key)) {
            String memberType = valueOf(tvMemberType.getText()).trim();
            if (("").equals(memberType)) {
                tvMemberType.setError("MemberType is Required!");
                tvMemberType.requestFocus();
                return false;
            }
        }

        if (("").equals(email)) {
            etEmail.setError("Email is Required!");
            etEmail.requestFocus();
            return false;
        }

        if (!helperObject.hasValidEmail(email)) {
            etEmail.setError("Enter valid Email!");
            etEmail.requestFocus();
            return false;
        }

        if (("").equals(password)) {
            etPassword.setError("Password is Required!");
            etPassword.requestFocus();
            return false;
        }

        if (!helperObject.hasValidPassword(password)) {
            etPassword.setError("Password must be > 8 characters, must contain numbers, at least 1 upper case character and at least 1 lower case character!");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void createUserFirestore(String memberType, String name, String email, String password, String epochTime, String date) {

        runOnUiThread(() -> progressDialog.show());

        // AuthUserItem obj
        AuthUserItem authUserItem = new AuthUserItem();
        authUserItem.setMemberType(memberType);
        authUserItem.setName(name);
        authUserItem.setEmail(email);
        authUserItem.setPassword(password);
        authUserItem.setEpochTime(epochTime);
        authUserItem.setDate(date);

        // Shared Pref
        HelperSharedPreference helperSharedPreference = HelperSharedPreference.getInstance(this);
        helperSharedPreference.setMemberType(memberType);
        helperSharedPreference.setName(name);
        helperSharedPreference.setEmail(email);

        // Save AuthUserItem obj to Firestore - Add a new document with a generated ID
        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_AUTH_USERS)
                .add(authUserItem)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(MainActivity.this, "AuthUserItem Created", Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> progressDialog.dismiss());
                    authUserItem.setDocId(documentReference.getId());
                    helperSharedPreference.setUserDocId(documentReference.getId());
                    btnAuthenticate.setEnabled(false);
                    goToDashboardActivity();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(MainActivity.this, "Failed to create AuthUserItem", Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> progressDialog.dismiss());
                });
    }

    private void resetPassword(DialogFragment dialog, String email) {
        runOnUiThread(() -> progressDialog.show());
        FirebaseAuth
                .getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "We have sent instructions to your email to reset your password. Please check!", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed to send 'reset password' email. Try again or restart Internet connection!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "resetPassword: trace: " + e.getMessage()));
    }

    private void goToDashboardActivity() {
        MainActivity.this.startActivity(new Intent(MainActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        MainActivity.this.finish();
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

                if (("SIGNUP").equals(requestStateMediator.getKey())) {
                    runOnUiThread(() -> {
                        Map<String, String> map = (HashMap<String, String>) requestStateMediator.getData();
                        createUserFirestore(map.get("MEMBER_TYPE"),
                                map.get("NAME"),
                                map.get("EMAIL"),
                                map.get("PASSWORD"),
                                map.get("EPOCH_TIME"),
                                map.get("DATE"));
                    });
                }

                if (("SIGNIN").equals(requestStateMediator.getKey())) {
                    runOnUiThread(() -> {
                        String email = (String) requestStateMediator.getData();
                        // Shared Pref
                        HelperSharedPreference helperSharedPreference = HelperSharedPreference.getInstance(this);
                        helperSharedPreference.setEmail(email);
                        goToDashboardActivity();
                    });
                }

                if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(MainActivity.this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();

            }

            if (UiState.EMPTY == requestStateMediator.getStatus()) {
                runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }

            if (UiState.ERROR == requestStateMediator.getStatus()) {
                runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                });
            }
        };

        return observer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        compositeDisposable.dispose();
    }

    @Override
    public void onListDialogItemClicked(String listItemText) {
        tvMemberType.setText(listItemText);
    }

    @Override
    public void onResetClicked(String dialogType, DialogFragment dialog, String email) {
        if (("RESET PASSWORD").equals(dialogType)) {
            AsyncTask.execute(() -> resetPassword(dialog, email));
        }
    }

    @Override
    public void onCancelClicked(String dialogType, DialogFragment dialog) {
        if (("RESET PASSWORD").equals(dialogType)) {
            dialog.dismiss();
        }
    }
}