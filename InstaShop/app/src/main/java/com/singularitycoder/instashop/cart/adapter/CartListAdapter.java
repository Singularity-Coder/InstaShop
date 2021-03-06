package com.singularitycoder.instashop.cart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.instashop.R;
import com.singularitycoder.instashop.cart.model.CartItem;
import com.singularitycoder.instashop.helpers.HelperGeneral;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import static java.lang.String.valueOf;

public final class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private List<CartItem> productCartList = Collections.EMPTY_LIST;

    @Nullable
    private Context context;

    @Nullable
    private ProductViewListener productViewListener;

    @NonNull
    private final HelperGeneral helperGeneral = new HelperGeneral();

    private int qty = 1;

    public CartListAdapter(Context context, List<CartItem> productCartList) {
        this.context = context;
        this.productCartList = productCartList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart, viewGroup, false);
        return new ProductListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItem cartItem = productCartList.get(position);
        if (holder instanceof ProductListViewHolder && null != holder) {
            ProductListViewHolder productListViewHolder = (ProductListViewHolder) holder;
            productListViewHolder.tvProductCartName.setText(cartItem.getProductName());
            productListViewHolder.tvProductCartPrice.setText("$" + cartItem.getProductPrice());
            productListViewHolder.tvProductCartQty.setText(cartItem.getProductQty());
            helperGeneral.glideImage(context, cartItem.getProductImageUrl(), productListViewHolder.ivProductCartImage);

            qty = Integer.valueOf(cartItem.getProductQty());
            productListViewHolder.btnAddCartProduct.setOnClickListener(view -> {
                if (qty < 10) {
                    qty += 1;
                    productListViewHolder.tvProductCartQty.setText(valueOf(qty));
                    cartItem.setProductQty(valueOf(Integer.valueOf(cartItem.getProductQty()) + qty));
//                    productViewListener.onQuantityIncreased(position);
                }
            });
            productListViewHolder.btnSubtractCartProduct.setOnClickListener(view -> {
                if (qty > 1) {
                    qty -= 1;
                    productListViewHolder.tvProductCartQty.setText(valueOf(qty));
                    cartItem.setProductQty(valueOf(Integer.valueOf(cartItem.getProductQty()) - qty));
//                    productViewListener.onQuantityDecreased(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productCartList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public final void filterList(List<CartItem> list) {
        this.productCartList = list;
        notifyDataSetChanged();
    }

    public interface ProductViewListener {
        void onProductClickedListener(int position);

        void onRemoveProduct(int position);

        void onQuantityIncreased(int position);

        void onQuantityDecreased(int position);
    }

    public final void setProductViewListener(ProductViewListener productViewListener) {
        this.productViewListener = productViewListener;
    }

    final class ProductListViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.iv_cart_product_image)
        ImageView ivProductCartImage;
        @Nullable
        @BindView(R.id.tv_cart_product_name)
        TextView tvProductCartName;
        @Nullable
        @BindView(R.id.tv_cart_product_price)
        TextView tvProductCartPrice;
        @Nullable
        @BindView(R.id.tv_cart_remove_product)
        TextView tvRemoveCartProduct;
        @Nullable
        @BindView(R.id.tv_cart_quantity)
        TextView tvProductCartQty;
        @Nullable
        @BindView(R.id.btn_cart_add_item)
        ImageButton btnAddCartProduct;
        @Nullable
        @BindView(R.id.btn_cart_subtract_item)
        ImageButton btnSubtractCartProduct;

        @NonNull
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

        ProductListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Entire View
            compositeDisposable.add(
                    RxView.clicks(itemView)
                            .map(o -> itemView)
                            .subscribe(
                                    button -> productViewListener.onProductClickedListener(getAdapterPosition()),
                                    throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            )
            );

            compositeDisposable.add(
                    RxView.clicks(tvRemoveCartProduct)
                            .map(o -> tvRemoveCartProduct)
                            .subscribe(
                                    button -> productViewListener.onRemoveProduct(getAdapterPosition()),
                                    throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            )
            );
        }
    }
}
