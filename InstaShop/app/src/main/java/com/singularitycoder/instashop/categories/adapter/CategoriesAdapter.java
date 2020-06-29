package com.singularitycoder.instashop.categories.adapter;

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
import com.singularitycoder.instashop.categories.model.CategoriesItem;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private List<CategoriesItem> dashboardList = Collections.EMPTY_LIST;

    @Nullable
    private Context context;

    @Nullable
    private DashView dashView;

    public CategoriesAdapter(List<CategoriesItem> dashboardList, Context context) {
        this.dashboardList = dashboardList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_dashboard, parent, false);
        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoriesItem categoriesItem = dashboardList.get(position);
        if (holder instanceof DashboardViewHolder && null != holder) {
            DashboardViewHolder dashboardViewHolder = (DashboardViewHolder) holder;

            dashboardViewHolder.homeImage.setImageResource(categoriesItem.getIntHomeImage());
            dashboardViewHolder.homeTitle.setText(categoriesItem.getStrHomeTitle());
        }
    }

    @Override
    public int getItemCount() {
        return dashboardList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface DashView {
        void onDashItemClicked(int position);
    }

    public void setDashView(DashView dashView) {
        this.dashView = dashView;
    }

    public class DashboardViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.img_dash_stat_icon)
        ImageView homeImage;
        @Nullable
        @BindView(R.id.tv_dash_stat_title)
        TextView homeTitle;

        @NonNull
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            compositeDisposable.add(
                    RxView.clicks(itemView)
                            .map(o -> itemView)
                            .subscribe(
                                    button -> dashView.onDashItemClicked(getAdapterPosition()),
                                    throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            )
            );
        }
    }
}
