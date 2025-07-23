package com.example.tradeup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;

import java.util.List;

public class ProductManageAdapter extends RecyclerView.Adapter<ProductManageAdapter.ManageViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductManageListener listener;

    public interface OnProductManageListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    public ProductManageAdapter(Context context, List<Product> productList, OnProductManageListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_product, parent, false);
        return new ManageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.status.setText(product.getStatus());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.avatar_border)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.image);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ManageViewHolder extends RecyclerView.ViewHolder {
        TextView title, status;
        ImageView image;
        Button btnEdit, btnDelete;

        public ManageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.manageTitle);
            status = itemView.findViewById(R.id.manageStatus);
            image = itemView.findViewById(R.id.manageImage);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
}
