package com.example.qlsp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlsp.R;
import com.example.qlsp.entify.Product;

import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private List<Product> mListProduct;
    private IClickListener mClickListener;
    private int[] imageIds = {R.drawable.img2, R.drawable.img1, R.drawable.img4, R.drawable.img3};

    private HashMap<Product, Integer> mProductHashMap;

    public interface  IClickListener {
        void onClickAddItem(Product product);
    }
    public ProductAdapter(List<Product> mListProduct, IClickListener listener) {
        this.mListProduct = mListProduct;
        this.mClickListener = listener;
        this.mProductHashMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mListProduct.get(position);
        if(product == null)
            return;
        int imageIndex = position % imageIds.length;
        holder.imageView_product.setImageResource(imageIds[imageIndex]);
        holder.textView_name.setText("Tên: " + product.getName());
        holder.textView_descrip.setText("Mô tả: " + product.getDescription());
        holder.textView_price.setText("Giá: " + product.getPrice() + "$");
        holder.button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProductHashMap.containsKey(product)){
                    int quantity = mProductHashMap.get(product);
                    quantity++;
                    mProductHashMap.put(product, quantity);
                }else {
                    mProductHashMap.put(product, 1);
                }
                mClickListener.onClickAddItem(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListProduct != null){
            return mListProduct.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private TextView textView_descrip;
        private TextView textView_price;
        private ImageButton button_add;
        private ImageView imageView_product;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.textView_NAME);
            textView_descrip = itemView.findViewById(R.id.textView_Description);
            textView_price = itemView.findViewById(R.id.textView_Price);
            button_add = itemView.findViewById(R.id.btn_add);
            imageView_product = itemView.findViewById(R.id.item_image_view);
        }
    }
}
