package com.singularitycoder.instashop.admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.CustomDialogFragment;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.products.model.ProductItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.lang.String.valueOf;

public class AddProductsFragment extends Fragment implements CustomDialogFragment.ListDialogListener {

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
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(tvCategory)
                        .map(o -> tvCategory)
                        .subscribe(
                                button -> btnShowCategoriesDialog(),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnAddProduct)
                        .map(o -> btnAddProduct)
                        .subscribe(
                                button -> btnUploadProduct(fragView),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
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

            uploadImage(productItem);
        }
    }

    private Void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_SELECT_IMAGE);
        return null;
    }

    @UiThread
    private void btnShowCategoriesDialog() {
//        Bundle bundle = new Bundle();
//        bundle.putString("DIALOG_TYPE", "list");
//        bundle.putString("KEY_TITLE", "Categories");
//        bundle.putStringArray("KEY_LIST", new String[]{"Movies", "Music", "Cameras", "Toys", "Mobiles", "Computers"});
//
//        DialogFragment dialogFragment = new CustomDialogFragment();
//        dialogFragment.setArguments(bundle);
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
//        if (previousFragment != null) {
//            fragmentTransaction.remove(previousFragment);
//        }
//        fragmentTransaction.addToBackStack(null);
//        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");

        listDialog();
    }

    @UiThread
    public void listDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Categories");
        alertBuilder.setCancelable(false);
        String[] selectArray = {"Movies", "Music", "Cameras", "Toys", "Mobiles", "Computers"};
        alertBuilder.setItems(selectArray, (dialog, which) -> {
            for (int i = 0; i < selectArray.length; i++) {
                if (which == i) {
                    tvCategory.setText(selectArray[i]);
                }
            }
        });

        alertBuilder.show();
    }

    // UPLOAD TO FIREBASE STORAGE
    private void uploadImage(ProductItem productItem) {
        getActivity().runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
        });
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(HelperConstants.DIR_PRODUCT_IMAGES_PATH)
                .child(productItem.getProductImageName())
                .putFile(productItem.getProductImageUri())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult().getUploadSessionUri();
                        Log.d(TAG, "uploadImages: uri: " + uri);
                        getUriFromFirebaseStorage(HelperConstants.DIR_PRODUCT_IMAGES_PATH, productItem);
                    } else {
                        getActivity().runOnUiThread(() -> {
                            if (null != progressDialog && progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(getContext(), "Couldn't upload Image", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    // GET URI FROM FIREBASE STORAGE
    private void getUriFromFirebaseStorage(String fileDirectory, ProductItem productItem) {
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(fileDirectory)
                .child(productItem.getProductImageName())
                .getDownloadUrl()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        String storageImageUrl = valueOf(task1.getResult());
                        Log.d(TAG, "getUriFromFirebaseStorage: url: " + storageImageUrl);
                        Log.d(TAG, "getUriFromFirebaseStorage: uri: " + task1.getResult().toString());
                        addFileItemToFirestore(storageImageUrl, productItem);
                    } else {
                        deleteFromFirebaseStorage(fileDirectory, productItem);
                        getActivity().runOnUiThread(() -> {
                            if (null != progressDialog && progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(getContext(), "Couldn't get Uri ", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    // DELETE FROM FIREBASE STORAGE
    private void deleteFromFirebaseStorage(String fileDirectory, ProductItem productItem) {
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(fileDirectory)
                .child(productItem.getProductImageName())
                .delete();
    }

    // CREATE FROM FIRESTORE
    private void addFileItemToFirestore(String storageImageUrl, ProductItem productItem) {
        getActivity().runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
        });

        productItem.setProductImageUrl(storageImageUrl);

        FirebaseFirestore
                .getInstance()
                .collection(HelperConstants.COLL_PRODUCTS)
                .add(productItem)
                .addOnSuccessListener(documentReference -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show());
                    dumpFields();
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void dumpFields() {
        ivProduct.setImageURI(null);
        tvCategory.setText("");
        etProductName.setText("");
        etProductDescription.setText("");
        etProductPrice.setText("");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_add_products).setVisible(false);
        menu.findItem(R.id.action_change_password).setVisible(false);
        menu.findItem(R.id.action_delete_account).setVisible(false);
        menu.findItem(R.id.action_show_cart).setVisible(false);
        menu.findItem(R.id.action_update_email).setVisible(false);
        menu.findItem(R.id.action_sign_out).setVisible(false);
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
    public void onListDialogItemClicked(String listItemText) {
        tvCategory.setText(listItemText);
    }
}
