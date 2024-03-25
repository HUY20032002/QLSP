package com.example.qlsp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qlsp.R;
import com.example.qlsp.adapter.OrderAdapter;
import com.example.qlsp.entify.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rcvOrder;
    private List<Product> mListProduct;
    private OrderAdapter mOrderAdapter;
    Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Product product = getIntent().getParcelableExtra("product");
        btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        initUI();
        getListProductFromDB();
    }
    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Product product : mListProduct) {
            double price = product.getPrice();
            int quantity = product.getQuantity();
            double productTotalPrice = price * quantity;
            totalPrice += productTotalPrice;
        }
        return totalPrice;
    }
    private void initUI() {
        rcvOrder = findViewById(R.id.rv_order);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvOrder.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvOrder.addItemDecoration(dividerItemDecoration);
        mListProduct = new ArrayList<>();
        mOrderAdapter = new OrderAdapter(mListProduct, new OrderAdapter.IClickListener() {
            @Override
            public void onClickAddItem(Product product) {
                int position = findProductPositionInList(product);
                if (position != -1) {
                    mOrderAdapter.increaseQuantity(position);
                } else {
                    product.setQuantity(1); // Số lượng ban đầu là 1 khi thêm vào đơn hàng
                    mListProduct.add(product);
                    mOrderAdapter.notifyItemInserted(mListProduct.size() - 1);
                }
            }
        });
        rcvOrder.setAdapter(mOrderAdapter);
    }

    private int findProductPositionInList(Product product) {
        for (int i = 0; i < mListProduct.size(); i++) {
            Product item = mListProduct.get(i);
            if (item.getName() != null && product.getName() != null && item.getName().equals(product.getName())) {
                return i;
            }
        }
        return -1;
    }

    private void getListProductFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Order");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    int position = findProductPositionInList(product);

                    if (position != -1) {
                        Product existingProduct = mListProduct.get(position);
                        existingProduct.setQuantity(existingProduct.getQuantity() + 1);
                        mOrderAdapter.notifyItemChanged(position);
                    } else {
                        product.setQuantity(1); // Số lượng ban đầu là 1 khi thêm vào đơn hàng
                        mListProduct.add(product);
                        mOrderAdapter.notifyItemInserted(mListProduct.size() - 1);
                    }
                    TextView textView_price = findViewById(R.id.textView_totalPrice);
                    textView_price.setText("Tổng tiền: " + calculateTotalPrice() + "$");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}