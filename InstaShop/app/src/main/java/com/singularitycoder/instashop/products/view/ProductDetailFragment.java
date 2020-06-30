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
import com.singularitycoder.instashop.cart.model.CartItem;
import com.singularitycoder.instashop.cart.viewmodel.CartViewModel;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;
import com.singularitycoder.instashop.wishlist.model.WishlistItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

import static java.lang.String.valueOf;

public final class ProductDetailFragment extends Fragment {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.tv_no_internet)
    TextView tvNoInternet;
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
    @BindView(R.id.btn_add_to_wishlist)
    Button btnAddToWishlist;
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
    private final CartItem cartItem = new CartItem();

    @NonNull
    private ProductItem productItem = new ProductItem();

    @NonNull
    private final WishlistItem wishlistItem = new WishlistItem();

    @Nullable
    private ProductViewModel productViewModel;

    @Nullable
    private CartViewModel cartViewModel;

    private int qty = 1;

    public ProductDetailFragment() {
    }

    // todo active network listener
    // todo on add to cart pressed - send item to server cart - if offline, store in local db else store in sub coll of user n if offline show local db list else show remote list
    // todo work manager service to sync remote n local db cart list when user added items to cart in offline mode to local db - so active listen to internet n on network sync local to remote
    // todo change dashboard to another fragment on the dashboard activity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        getBundleData();
        initialisations(rootView);
        setUpToolBar();
        getProductDetails(rootView);
        setClickListeners(rootView);
        return rootView;
    }

    private void getBundleData() {
        if (null != getArguments()) {
            if (!("").equals(getArguments().getString("DOCID"))) {
                docId = getArguments().getString("DOCID");
                title = getArguments().getString("TITLE");
            }
        }
    }

    private void initialisations(View rootView) {
        ButterKnife.bind(this, rootView);
        unbinder = ButterKnife.bind(this, rootView);
        helperSharedPreference = HelperSharedPreference.getInstance(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog = new ProgressDialog(getContext());
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
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

    private void getProductDetails(View rootView) {
        if (helperObject.hasInternet(getContext())) {
            productViewModel.getProductFromRepository(docId).observe(getViewLifecycleOwner(), liveDataObserver(rootView));
        } else {
            // todo no internet image
            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }
    }

    private Observer<RequestStateMediator> liveDataObserver(View rootView) {
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

                if (("STATE_PRODUCT_DETAIL").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        productItem = ((ProductItem) requestStateMediator.getData());
                        String productImage = productItem.getProductImageUrl();
                        String productCategory = productItem.getProductCategory();
                        String productName = productItem.getProductName();
                        String productPrice = productItem.getProductPrice();
                        String productDateCreated = productItem.getProductCreationDate();
                        String productDesc = productItem.getProductDescription();
                        String productDocId = productItem.getProductDocId();

                        helperObject.glideImage(getContext(), productImage, ivProductImage);
                        tvProductName.setText(productName);
                        tvProductPrice.setText("$" + productPrice);
                        tvProductPriceDesc.setText("Price: " + productPrice);
                        tvProductCategoryDesc.setText("Category: " + productCategory);
                        tvProductDateDesc.setText("Date: " + productDateCreated);
                        tvProductDescription.setText("Description: " + productDesc);

                        // Prepare Cart Item
                        cartItem.setProductCategory(productCategory);
                        cartItem.setProductName(productName);
                        cartItem.setProductImageUrl(productImage);
                        cartItem.setProductPrice(productPrice);
                        cartItem.setProductDocId(productDocId);
                        cartItem.setAddedOnDate(valueOf(helperObject.getCurrentEpochTime()));

                        // Prepare Wishlist Item
                        wishlistItem.setProductCategory(productCategory);
                        wishlistItem.setProductName(productName);
                        wishlistItem.setProductImageUrl(productImage);
                        wishlistItem.setProductPrice(productPrice);
                        wishlistItem.setProductDocId(productDocId);
                        wishlistItem.setAddedOnDate(valueOf(helperObject.getCurrentEpochTime()));
                    });
                }

                if (("STATE_ADD_CART_FIRESTORE").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        helperObject.showSnack(rootView, "Added To Cart", getResources().getColor(android.R.color.white), "OK", null);
                    });
                }

                if (("STATE_ADD_WISHLIST_FIRESTORE").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {
                        helperObject.showSnack(rootView, "Added To Wishlist", getResources().getColor(android.R.color.white), "OK", null);
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

    private void setClickListeners(View rootView) {
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
                                button -> addToCart(rootView),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );

        compositeDisposable.add(
                RxView.clicks(btnAddToWishlist)
                        .map(o -> btnAddToWishlist)
                        .subscribe(
                                button -> addToWishlist(rootView),
                                throwable -> Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                        )
        );
    }

    private void addToCart(View rootView) {
        if (helperObject.hasInternet(getContext())) {
            // add to sub coll of user
            tvNoInternet.setVisibility(View.GONE);
            cartItem.setProductQty(valueOf(tvQuantity.getText()));
            cartViewModel.addCartProductToFirestoreFromRepository(getContext(), cartItem).observe(getViewLifecycleOwner(), liveDataObserver(rootView));
        } else {
            // add to local db when no network
            tvNoInternet.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            cartItem.setProductQty(valueOf(tvQuantity.getText()));
            cartViewModel.insertIntoRoomDbFromRepository(cartItem);
            helperObject.showSnack(rootView, "Added To Cart", getResources().getColor(android.R.color.white), "OK", null);
        }
    }

    private void addToWishlist(View rootView) {
        if (helperObject.hasInternet(getContext())) {
            // add to sub coll of user
            tvNoInternet.setVisibility(View.GONE);
            productViewModel.addWishlistProductToFirestoreFromRepository(getContext(), wishlistItem).observe(getViewLifecycleOwner(), liveDataObserver(rootView));
        } else {
            // add to local db when no network
            tvNoInternet.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
        }
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
