package com.example.tradeup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnLikeClickListener {
        void onLikeClick(Product product);
    }

    private Context context;
    private List<Product> productList;
    private OnLikeClickListener likeClickListener;

    public ProductAdapter(Context context, List<Product> productList, OnLikeClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.likeClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.title.setText(p.getTitle());
        holder.price.setText(p.getPrice());

        Glide.with(context).load(p.getImages().get(0)).into(holder.image);

        holder.like.setOnClickListener(v -> {
            boolean liked = holder.like.getTag() != null && (boolean) holder.like.getTag();
            if (liked) {
                holder.like.setImageResource(R.drawable.heart);
                holder.like.setTag(false);
            } else {
                holder.like.setImageResource(R.drawable.heart_tink);
                holder.like.setTag(true);
                likeClickListener.onLikeClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image, like;
        TextView title, price;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.productImage);
            like = itemView.findViewById(R.id.likeIcon);
            title = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.productPrice);
        }
    }
}
