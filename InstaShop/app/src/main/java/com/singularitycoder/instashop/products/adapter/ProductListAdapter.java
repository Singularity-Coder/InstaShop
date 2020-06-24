package com.singularitycoder.instashop.products.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.helpers.HelperGeneral;
import com.singularitycoder.instashop.products.model.ProductItem;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private List<ProductItem> productList = Collections.EMPTY_LIST;

    @Nullable
    private Context context;

    @Nullable
    private ProductViewListener productViewListener;

    @NonNull
    HelperGeneral helperGeneral = new HelperGeneral();

    public ProductListAdapter(Context context, List<ProductItem> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false);
        return new ProductListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductItem productItem = productList.get(position);
        if (holder instanceof ProductListViewHolder && null != holder) {
            ProductListViewHolder productListViewHolder = (ProductListViewHolder) holder;
            productListViewHolder.tvProductName.setText(productItem.getProductName());
            productListViewHolder.tvProductPrice.setText("$" + productItem.getProductPrice());
            helperGeneral.glideImage(context, productItem.getProductImageUrl(), productListViewHolder.ivProductImage);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ProductViewListener {
        void onProductClickedListener(int position);
    }

    public void setProductViewListener(ProductViewListener productViewListener) {
        this.productViewListener = productViewListener;
    }

    class ProductListViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.iv_product_image)
        ImageView ivProductImage;
        @Nullable
        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @Nullable
        @BindView(R.id.tv_product_price)
        TextView tvProductPrice;

        @NonNull
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

        ProductListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            compositeDisposable.add(
                    RxView.clicks(itemView)
                            .map(o -> itemView)
                            .subscribe(
                                    button -> productViewListener.onProductClickedListener(getAdapterPosition()),
                                    throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            )
            );
        }
    }
}
