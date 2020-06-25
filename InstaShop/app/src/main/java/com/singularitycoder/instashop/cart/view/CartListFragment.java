package com.singularitycoder.instashop.cart.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

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

import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.cart.adapter.CartListAdapter;
import com.singularitycoder.instashop.cart.model.ProductCartItem;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.lang.String.valueOf;

public class CartListFragment extends Fragment {

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
    private final ArrayList<ProductCartItem> productCartList = new ArrayList<>();

    @Nullable
    private CartListAdapter cartListAdapter;

    @Nullable
    private ProductViewModel productViewModel;

    private int totalPrice, totalQty = 0;

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
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle("Cart");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getAll().observe(getViewLifecycleOwner(), liveDataObserver());
    }

    private Observer<List<ProductCartItem>> liveDataObserver() {
        Observer<List<ProductCartItem>> observer = null;
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

    private void setClickListeners() {
        cartListAdapter.setProductViewListener(new CartListAdapter.ProductViewListener() {
            @Override
            public void onProductClickedListener(int position) {

            }

            @Override
            public void onRemoveProduct(int position) {
                ProductCartItem productCartItem = new ProductCartItem();
                productCartItem.setId(productCartList.get(position).getId());
                productViewModel.delete(productCartItem);
            }
        });
    }

    private void calculateTotals() {
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

        menu.findItem(R.id.action_add_products).setVisible(false);
        menu.findItem(R.id.action_change_password).setVisible(false);
        menu.findItem(R.id.action_delete_account).setVisible(false);
        menu.findItem(R.id.action_show_cart).setVisible(false);
        menu.findItem(R.id.action_update_email).setVisible(false);
        menu.findItem(R.id.action_sign_out).setVisible(false);

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
        }
        return super.onOptionsItemSelected(item);
    }


    private void searchUsers(String text) {
        List<ProductCartItem> filteredUsers = new ArrayList<>();
        for (ProductCartItem productCartItem : productCartList) {
            if (productCartItem.getProductName().toLowerCase().trim().contains(text.toLowerCase())) {
                filteredUsers.add(productCartItem);
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
