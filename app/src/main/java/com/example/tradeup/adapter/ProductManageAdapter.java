package com.example.tradeup.adapter;

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

    private List<Product> products;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
        void onMarkSoldClick(Product product);
    }

    public ProductManageAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_product, parent, false);
        return new ManageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageViewHolder holder, int position) {
        Product p = products.get(position);
        holder.title.setText(p.getTitle());
        holder.price.setText(p.getPrice() + "₫");
        holder.status.setText(p.getStatus() != null ? p.getStatus() : "Đang bán");

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(p.getImages().get(0)).into(holder.image);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(p));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(p));
        holder.btnMarkSold.setOnClickListener(v -> listener.onMarkSoldClick(p));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ManageViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, status;
        ImageView image;
        Button btnEdit, btnDelete, btnMarkSold;

        public ManageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.manageProductTitle);
            price = itemView.findViewById(R.id.manageProductPrice);
            status = itemView.findViewById(R.id.manageProductStatus);
            image = itemView.findViewById(R.id.manageProductImage);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
            btnMarkSold = itemView.findViewById(R.id.btnMarkAsSold);
        }
    }
}
