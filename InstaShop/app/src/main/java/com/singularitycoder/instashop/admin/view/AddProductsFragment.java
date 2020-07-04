package com.singularitycoder.instashop.admin.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.admin.viewmodel.AdminViewModel;
import com.singularitycoder.instashop.categories.view.CategoriesFragment;
import com.singularitycoder.instashop.helpers.CustomDialogFragment;
import com.singularitycoder.instashop.helpers.CustomDialogFragmentConstants;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.model.ProductItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.String.valueOf;

public final class AddProductsFragment extends Fragment implements CustomDialogFragment.ListDialogListener {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.iv_product)
    ImageView ivProduct;
    @Nullable
    @BindView(R.id.tv_category)
    TextView tvCategory;
    @Nullable
    @BindView(R.id.et_product_name)
    EditText etProductName;
    @Nullable
    @BindView(R.id.et_product_price)
    EditText etProductPrice;
    @Nullable
    @BindView(R.id.et_product_description)
    EditText etProductDescription;
    @Nullable
    @BindView(R.id.btn_add_product)
    Button btnAddProduct;

    @NonNull
    private final String TAG = "AddProductsFragment";

    @NonNull
    private static final int REQUEST_CODE_SELECT_IMAGE = 111;

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private Unbinder unbinder;

    @Nullable
    private ProgressDialog progressDialog;

    @Nullable
    private Uri imageUri;

    @Nullable
    private AdminViewModel adminViewModel;

    public AddProductsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_products, container, false);
        initialisations(view);
        setUpToolBar();
        setClickListeners(view);
        return view;
    }

    private void initialisations(View view) {
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle("Add Product");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setClickListeners(View fragView) {
        compositeDisposable.add(
                RxView.clicks(ivProduct)
                        .map(o -> ivProduct)
                        .subscribe(
                                button -> helperObject.checkPermissions(getActivity(), () -> selectImage(), Manifest.permission.READ_EXTERNAL_STORAGE),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(tvCategory)
                        .map(o -> tvCategory)
                        .subscribe(
                                button -> btnShowCategoriesDialog(),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnAddProduct)
                        .map(o -> btnAddProduct)
                        .subscribe(
                                button -> btnUploadProduct(fragView),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );
    }

    private boolean hasValidInput(
            Uri imageUri,
            TextView tvCategory,
            EditText etProductName,
            EditText etProductPrice,
            EditText etProductDescription) {

        String category = valueOf(tvCategory.getText()).trim();
        String productName = valueOf(etProductName.getText()).trim();
        String productPrice = valueOf(etProductPrice.getText()).trim();
        String productDescription = valueOf(etProductDescription.getText()).trim();

        if (null == imageUri) {
            Toast.makeText(getContext(), "Add an Image!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (("").equals(category)) {
            tvCategory.setError("Category is Required!");
            tvCategory.requestFocus();
            return false;
        }

        if (("").equals(productName)) {
            etProductName.setError("Product Name is Required!");
            etProductName.requestFocus();
            return false;
        }

        if (("").equals(productPrice)) {
            etProductPrice.setError("Product Price is Required!");
            etProductPrice.requestFocus();
            return false;
        }

        if (("").equals(productDescription)) {
            etProductDescription.setError("Product Description is Required!");
            etProductDescription.requestFocus();
            return false;
        }

        return true;
    }

    private void btnUploadProduct(View view) {
        helperObject.hideFragmentKeyboard(getContext(), view);
        view.clearFocus();

        if (helperObject.hasInternet(getContext())) {
            if (hasValidInput(imageUri, tvCategory, etProductName, etProductPrice, etProductDescription)) {
                ProductItem productItem = new ProductItem();
                productItem.setProductCategory(valueOf(tvCategory.getText()));
                productItem.setProductImageUri(imageUri);
                productItem.setProductImageName("Image_" + helperObject.getCurrentEpochTime() + "." + helperObject.getFileExtension(imageUri, getContext()));
                productItem.setProductCreationDate(helperObject.currentDateTime());
                productItem.setProductCreationEpochTime(valueOf(helperObject.getCurrentEpochTime()));
                productItem.setProductDescription(valueOf(etProductDescription.getText()));
                productItem.setProductName(valueOf(etProductName.getText()));
                productItem.setProductPrice(valueOf(etProductPrice.getText()));

                adminViewModel.uploadProductFromRepository(productItem).observe(getViewLifecycleOwner(), liveDataObserver());
            }
        } else {
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private Void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_SELECT_IMAGE);
        return null;
    }

    @UiThread
    private void btnShowCategoriesDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_TITLE", "Categories");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "AddProductsFragment");
        bundle.putStringArray("KEY_LIST", new String[]{"Movies", "Music", "Cameras", "Toys", "Mobiles", "Computers"});

        DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setTargetFragment(AddProductsFragment.this, CustomDialogFragmentConstants.REQUEST_CODE_DIALOG_FRAGMENT_CATEGORIES_LIST);
        dialogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void dumpFields() {
        ivProduct.setImageURI(null);
        tvCategory.setText("");
        etProductName.setText("");
        etProductDescription.setText("");
        etProductPrice.setText("");
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

                if (("UPLOAD_IMAGE").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                    });
                }

                if (("GET_STORAGE_URI").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                    });
                }

                if (("ADD_TO_FIRESTORE").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        dumpFields();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        compositeDisposable.dispose();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // BACK PRESSED
        if (requestCode == RESULT_CANCELED) {
            return;
        }

        // IMAGE
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            ivProduct.setImageURI(null);
            ivProduct.setImageURI(imageUri);
        }
    }

    @Override
    public void onListDialogItemClick(String listItemText) {
        tvCategory.setText(listItemText);
    }
}
