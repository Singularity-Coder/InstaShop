package com.singularitycoder.instashop.products.view;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.rpc.Help;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperConstants;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.helpers.HelperSharedPreference;
import com.singularitycoder.instashop.helpers.RequestStateMediator;
import com.singularitycoder.instashop.helpers.UiState;
import com.singularitycoder.instashop.products.adapter.ProductListAdapter;
import com.singularitycoder.instashop.products.model.ProductItem;
import com.singularitycoder.instashop.products.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.lang.String.valueOf;

public class ProductListFragment extends Fragment {

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
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @NonNull
    private final ArrayList<ProductItem> productsList = new ArrayList<>();

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private final String TAG = "ProductListFragment";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private HelperSharedPreference helperSharedPreference;

    @Nullable
    private ProductListAdapter productListAdapter;

    @Nullable
    private ProgressDialog progressDialog;

    @Nullable
    private String category;

    public ProductListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        getBundleData();
        initialisations(view);
        setUpToolBar();
        setUpRecyclerView();
        getProducts();
        setSwipeRefreshLayout();
        setClickListeners();
        return view;
    }

    private void getBundleData() {
        if (null != getArguments()) {
            if (!("").equals(getArguments().getString("CATEGORY"))) {
                category = getArguments().getString("CATEGORY");
            } else {
                category = getString(R.string.app_name);
            }
        }
    }

    private void initialisations(View view) {
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        helperSharedPreference = HelperSharedPreference.getInstance(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle(category);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setUpRecyclerView() {
        productListAdapter = new ProductListAdapter(getContext(), productsList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(productListAdapter);
    }

    private void getProducts() {
        ProductViewModel productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        if (helperObject.hasInternet(getContext())) {
            productViewModel.getProductListRepository(category).observe(getViewLifecycleOwner(), liveDataObserver());
        } else {
            // todo no internet image
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
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

                productsList.clear();

                QuerySnapshot queryDocumentSnapshots = (QuerySnapshot) requestStateMediator.getData();

                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                    Log.d(TAG, "docList: " + docList);

                    for (DocumentSnapshot docSnap : docList) {
                        ProductItem productItem = docSnap.toObject(ProductItem.class);
                        if (null != productItem) {
                            Log.d(TAG, "ProductItem: " + productItem);

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_IMAGE)))) {
                                productItem.setProductImageUrl(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_IMAGE)));
                            } else {
                                productItem.setProductImageUrl("Empty");
                            }

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_PRICE)))) {
                                productItem.setProductPrice(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_PRICE)));
                            } else {
                                productItem.setProductPrice("Empty");
                            }

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_NAME)))) {
                                productItem.setProductName(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_NAME)));
                            } else {
                                productItem.setProductName("Empty");
                            }

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_DESCRIPTION)))) {
                                productItem.setProductDescription(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_DESCRIPTION)));
                            } else {
                                productItem.setProductDescription("Empty");
                            }

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CREATED_EPOCH_TIME)))) {
                                productItem.setProductCreationEpochTime(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CREATED_EPOCH_TIME)));
                            } else {
                                productItem.setProductCreationEpochTime("Empty");
                            }

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CREATED_DATE)))) {
                                productItem.setProductCreationDate(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CREATED_DATE)));
                            } else {
                                productItem.setProductCreationDate("Empty");
                            }

                            if (!("").equals(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CATEGORY)))) {
                                productItem.setProductCategory(valueOf(docSnap.getString(HelperConstants.KEY_PRODUCT_CATEGORY)));
                            } else {
                                productItem.setProductCategory("Empty");
                            }
                        }

                        Log.d(TAG, "firedoc id: " + docSnap.getId());
                        productItem.setProductDocId(docSnap.getId());
                        productsList.add(productItem);
                    }

                    productListAdapter.notifyDataSetChanged();

                    if (null != getActivity()) {
                        getActivity().runOnUiThread(() -> {
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                        });
                    }

                } else {
                    if (null != getActivity()) {
                        getActivity().runOnUiThread(() -> {
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                            tvNothing.setVisibility(View.VISIBLE);
                        });
                    }
                }
            }

            if (UiState.EMPTY == requestStateMediator.getStatus()) {
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> {
                        if (null != progressDialog && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            if (UiState.ERROR == requestStateMediator.getStatus()) {
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> {
                        productsList.clear();
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        if (null != progressDialog && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(getContext(), valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        };

        return observer;
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(this::getProducts);
    }

    private void setClickListeners() {
        productListAdapter.setProductViewListener(position -> {
            Bundle bundle = new Bundle();
            bundle.putString("DOCID", productsList.get(position).getProductDocId());
            bundle.putString("TITLE", productsList.get(position).getProductName());

            Fragment fragment = new ProductDetailFragment();
            fragment.setArguments(bundle);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.con_lay_dashboard, fragment)
                    .addToBackStack(null)
                    .commit();
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}