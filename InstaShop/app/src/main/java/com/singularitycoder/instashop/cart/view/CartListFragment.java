package com.singularitycoder.instashop.cart.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.cart.adapter.CartListAdapter;
import com.singularitycoder.instashop.cart.model.CartItem;
import com.singularitycoder.instashop.cart.viewmodel.CartViewModel;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.lang.String.valueOf;

public final class CartListFragment extends Fragment {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    @Nullable
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @Nullable
    @BindView(R.id.tv_total_qty)
    TextView tvTotalQty;
    @Nullable
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String TAG = "CartListFragment";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private HelperSharedPreference helperSharedPreference;

    @Nullable
    private ProgressDialog progressDialog;

    @NonNull
    private final ArrayList<CartItem> productCartList = new ArrayList<>();

    @Nullable
    private CartListAdapter cartListAdapter;

    @Nullable
    private CartViewModel cartViewModel;

    private int totalPrice, totalQty = 0;

    // todo merge ProductCartItem with ProductItem

    public CartListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_list, container, false);
        getBundleData();
        initialisations(view);
        setUpToolBar();
        setUpRecyclerView();
        getCartList();
        setSwipeRefreshLayout();
        setCallBacks();
        setClickListeners();
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
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle("Cart");
//            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setUpRecyclerView() {
        cartListAdapter = new CartListAdapter(getContext(), productCartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cartListAdapter);
    }

    private void getCartList() {
        if (helperObject.hasInternet(getContext())) {
            cartViewModel.getCartProductsFromFirestoreFromRepository(getContext()).observe(getViewLifecycleOwner(), liveDataObserver());
        } else {
            // todo no internet image
            cartViewModel.getAllFromRoomDbFromRepository().observe(getViewLifecycleOwner(), liveDataObserverForRoomDb());
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(this::getCartList);
    }

    private Observer<List<CartItem>> liveDataObserverForRoomDb() {
        Observer<List<CartItem>> observer = null;
        observer = productCartItems -> {
            productCartList.clear();
            productCartList.addAll(productCartItems);
            cartListAdapter.notifyDataSetChanged();

            calculateTotals();

            if (productCartList.size() == 0) tvNothing.setVisibility(View.VISIBLE);
            else tvNothing.setVisibility(View.GONE);
        };
        return observer;
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
                if (("STATE_GET_CART_FIRESTORE").equals(requestStateMediator.getKey())) {
                    getActivity().runOnUiThread(() -> {

                        productCartList.clear();

                        QuerySnapshot queryDocumentSnapshots = (QuerySnapshot) requestStateMediator.getData();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot docSnap : docList) {
                                CartItem cartItem = docSnap.toObject(CartItem.class);
                                if (null != cartItem) {

                                    if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_IMAGE)))) {
                                        cartItem.setProductImageUrl(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_IMAGE)));
                                    } else {
                                        cartItem.setProductImageUrl("Empty");
                                    }

                                    if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_PRICE)))) {
                                        cartItem.setProductPrice(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_PRICE)));
                                    } else {
                                        cartItem.setProductPrice("Empty");
                                    }

                                    if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_NAME)))) {
                                        cartItem.setProductName(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_NAME)));
                                    } else {
                                        cartItem.setProductName("Empty");
                                    }

                                    if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CATEGORY)))) {
                                        cartItem.setProductCategory(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CATEGORY)));
                                    } else {
                                        cartItem.setProductCategory("Empty");
                                    }

                                    if (!("").equals(valueOf(docSnap.getString("productQty")))) {
                                        cartItem.setProductQty(valueOf(docSnap.getString("productQty")));
                                    } else {
                                        cartItem.setProductQty("Empty");
                                    }
                                }

                                Log.d(TAG, "firedoc id: " + docSnap.getId());
                                cartItem.setProductDocId(docSnap.getId());
                                productCartList.add(cartItem);
                            }

                            cartListAdapter.notifyDataSetChanged();

                            if (null != getActivity()) {
                                getActivity().runOnUiThread(() -> {
                                    // todo calculate total from firestore
                                    // todo remove from firestore if online else remove from room db if offline
                                    // todo on network reconnection fire worker to sync cart n firestore

//                                    calculateTotals();

                                    if (productCartList.size() == 0)
                                        tvNothing.setVisibility(View.VISIBLE);
                                    else tvNothing.setVisibility(View.GONE);

                                    if (null != progressDialog && progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (swipeRefreshLayout != null)
                                        swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                                });
                            }

                        } else {
                            if (null != getActivity()) {
                                getActivity().runOnUiThread(() -> {
                                    if (null != progressDialog && progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (swipeRefreshLayout != null)
                                        swipeRefreshLayout.setRefreshing(false);
                                    tvNothing.setVisibility(View.VISIBLE);
                                });
                            }
                        }

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

    private void setCallBacks() {
        cartListAdapter.setProductViewListener(new CartListAdapter.ProductViewListener() {
            @Override
            public void onProductClickedListener(int position) {

            }

            @Override
            public void onRemoveProduct(int position) {
                CartItem cartItem = new CartItem();
                cartItem.setRoomId(productCartList.get(position).getRoomId());
                cartViewModel.deleteFromRoomDbFromRepository(cartItem);
            }

            @Override
            public void onQuantityIncreased(int position) {
//                calculateTotals();
            }

            @Override
            public void onQuantityDecreased(int position) {
//                calculateTotals();
            }
        });
    }

    private void setClickListeners() {
    }

    private void calculateTotals() {
        totalQty = 0;
        totalPrice = 0;

        for (int i = 0; i < productCartList.size(); i++) {
            totalQty += Integer.parseInt(productCartList.get(i).getProductQty());
            int singlePrice = Integer.parseInt(productCartList.get(i).getProductPrice()) * Integer.parseInt(productCartList.get(i).getProductQty());
            totalPrice += singlePrice;
        }

        tvTotalPrice.setText("Total Price: $" + valueOf(totalPrice));
        tvTotalQty.setText("Total Qty: " + valueOf(totalQty));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cart, menu);
        MenuItem searchItem = menu.findItem(R.id.action_product_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search Cart");
        searchView.setIconifiedByDefault(true);
//        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.string_search_hint) + "</font>"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_product_search:
                return true;
            case R.id.action_help:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void searchUsers(String text) {
        List<CartItem> filteredUsers = new ArrayList<>();
        for (CartItem cartItem : productCartList) {
            if (cartItem.getProductName().toLowerCase().trim().contains(text.toLowerCase())) {
                filteredUsers.add(cartItem);
            }
        }
        cartListAdapter.filterList(filteredUsers);
        cartListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
