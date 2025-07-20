package com.example.tradeup.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;
import com.example.tradeup.ui.product.ProductDetailActivity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnItemClickListener listener;
    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.productList = list;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(v);

    }
    public ProductAdapter(Context context, List<Product> list, OnItemClickListener listener) {
        this.context = context;
        this.productList = list;
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.title.setText(p.getTitle());
        holder.price.setText(p.getPrice() + "₫");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(p);
            }
        });

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            Glide.with(context).load(p.getImages().get(0)).into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", p.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvProductTitle);
            price = itemView.findViewById(R.id.tvProductPrice);
            image = itemView.findViewById(R.id.imgProductThumb);
        }
    }
}
