package com.example.tradeup.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;
import com.example.tradeup.ui.product.ProductDetailActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final ProductAdapter.OnItemClickListener listener;
    public ProductAdapter(Context context, List<Product> productList,ProductAdapter.OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.title.setText(p.getTitle());

        // ✅ Sửa lỗi parse và hiển thị giá
        try {
            long priceValue = Long.parseLong(p.getPrice());
            NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
            holder.price.setText(format.format(priceValue) + "₫");
        } catch (NumberFormatException e) {
            holder.price.setText("N/A");
        }

        holder.views.setText(p.getViews() + " lượt xem");
        holder.likes.setText(p.getLikes() + " lượt thích");

        // ✅ Set click vào item để tăng views và mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(p.getId());
            ref.child("views").setValue(p.getViews() + 1);

            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", p.getId());
            context.startActivity(intent);
        });

        // ✅ Hiển thị ảnh từ Base64
        if (p.getImages() != null && !p.getImages().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(p.getImages().get(0), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageView.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.avatar_border);
        }

        // ✅ Xử lý like
        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            boolean liked = false;

            @Override
            public void onClick(View v) {
                if (!liked) {
                    holder.likeIcon.setImageResource(R.drawable.heart_tink);
                    int newLikes = p.getLikes() + 1;
                    FirebaseDatabase.getInstance().getReference("products")
                            .child(p.getId()).child("likes").setValue(newLikes);
                    holder.likes.setText(newLikes + " lượt thích");
                    liked = true;
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, views, likes;
        ImageView imageView, likeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.productPrice);
            views = itemView.findViewById(R.id.productViews);
            likes = itemView.findViewById(R.id.productLikes);
            imageView = itemView.findViewById(R.id.productImage);
            likeIcon = itemView.findViewById(R.id.likeIcon);
        }
    }
}
