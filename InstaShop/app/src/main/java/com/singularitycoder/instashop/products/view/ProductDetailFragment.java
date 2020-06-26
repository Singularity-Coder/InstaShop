package com.singularitycoder.instashop.products.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.cart.model.ProductCartItem;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static java.lang.String.valueOf;

public class ProductDetailFragment extends Fragment {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.iv_product_header)
    ImageView ivProductImage;
    @Nullable
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @Nullable
    @BindView(R.id.tv_product_price)
    TextView tvProductPrice;
    @Nullable
    @BindView(R.id.btn_subtract_qty)
    Button btnSubtractQty;
    @Nullable
    @BindView(R.id.tv_qty)
    TextView tvQuantity;
    @Nullable
    @BindView(R.id.btn_add_qty)
    Button btnAddQty;
    @Nullable
    @BindView(R.id.btn_add_to_cart)
    Button btnAddToCart;
    @Nullable
    @BindView(R.id.tv_product_price_desc)
    TextView tvProductPriceDesc;
    @Nullable
    @BindView(R.id.tv_product_category)
    TextView tvProductCategoryDesc;
    @Nullable
    @BindView(R.id.tv_product_date_time)
    TextView tvProductDateDesc;
    @Nullable
    @BindView(R.id.tv_product_description)
    TextView tvProductDescription;

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String TAG = "ProductDetailFragment";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private HelperSharedPreference helperSharedPreference;

    @Nullable
    private ProgressDialog progressDialog;

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    private String docId, title;

    @NonNull
    private final ProductCartItem productCartItem = new ProductCartItem();

    @Nullable
    private ProductViewModel productViewModel;

    private int qty = 1;

    public ProductDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        getBundleData();
        initialisations(view);
        setUpToolBar();
        getProductInfo();
        setClickListeners(view);
        return view;
    }

    private void getBundleData() {
        if (null != getArguments()) {
            if (!("").equals(getArguments().getString("DOCID"))) {
                docId = getArguments().getString("DOCID");
                title = getArguments().getString("TITLE");
            }
        }
    }

    private void initialisations(View view) {
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        helperSharedPreference = HelperSharedPreference.getInstance(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog = new ProgressDialog(getContext());
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle(title);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void getProductInfo() {
        if (helperObject.hasInternet(getContext())) {
            productViewModel.getProductFromRepository(docId).observe(getViewLifecycleOwner(), liveDataObserver());
        } else {
            // todo no internet image
            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }
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
                getActivity().runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();

                    String productImage = ((ProductItem) requestStateMediator.getData()).getProductImageUrl();
                    String productCategory = ((ProductItem) requestStateMediator.getData()).getProductCategory();
                    String productName = ((ProductItem) requestStateMediator.getData()).getProductName();
                    String productPrice = ((ProductItem) requestStateMediator.getData()).getProductPrice();
                    String productDateCreated = ((ProductItem) requestStateMediator.getData()).getProductCreationDate();
                    String productDesc = ((ProductItem) requestStateMediator.getData()).getProductDescription();

                    helperObject.glideImage(getContext(), productImage, ivProductImage);
                    tvProductName.setText(productName);
                    tvProductPrice.setText("$" + productPrice);
                    tvProductPriceDesc.setText("Price: " + productPrice);
                    tvProductCategoryDesc.setText("Category: " + productCategory);
                    tvProductDateDesc.setText("Date: " + productDateCreated);
                    tvProductDescription.setText("Description: " + productDesc);

                    // Prepare Cart Item
                    productCartItem.setProductCategory(productCategory);
                    productCartItem.setProductName(productName);
                    productCartItem.setProductImageUrl(productImage);
                    productCartItem.setProductPrice(productPrice);
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

    private void setClickListeners(View fragView) {
        compositeDisposable.add(
                RxView.clicks(btnAddQty)
                        .map(o -> btnAddQty)
                        .subscribe(
                                button -> {
                                    if (qty < 10) {
                                        qty += 1;
                                        tvQuantity.setText(valueOf(qty));
                                    } else {
                                        Toast.makeText(getContext(), "You have reached the maximum!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnSubtractQty)
                        .map(o -> btnSubtractQty)
                        .subscribe(
                                button -> {
                                    if (qty > 1) {
                                        qty -= 1;
                                        tvQuantity.setText(valueOf(qty));
                                    } else {
                                        Toast.makeText(getContext(), "You have reached the minimum!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnAddToCart)
                        .map(o -> btnAddToCart)
                        .subscribe(
                                button -> addToCart(fragView),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );
    }

    private void addToCart(View fragView) {
        productCartItem.setProductQty(valueOf(tvQuantity.getText()));
        productViewModel.insert(productCartItem);
        helperObject.showSnack(fragView, "Added To Cart", getResources().getColor(android.R.color.white), "OK", null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        compositeDisposable.dispose();
    }
}
