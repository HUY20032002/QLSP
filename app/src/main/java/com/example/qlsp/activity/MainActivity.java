package com.example.qlsp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlsp.R;
import com.example.qlsp.adapter.ProductAdapter;
import com.example.qlsp.entify.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnLogout,btnGiohang;
    TextView txtEmail,txtName ;
    FirebaseUser user;
    FirebaseAuth auth;

    ImageView imgAvatar;
    private RecyclerView rcvProduct;
    private ProductAdapter mProductAdapter;
    private List<Product> mListProduct;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //imgAvatar=findViewById(R.id.img_avatar);
        btnLogout = findViewById(R.id.logout);
        auth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.text_email);
        //txtName = findViewById(R.id.txt_name);
        btnGiohang = findViewById(R.id.btnOder);
        user = auth.getCurrentUser();







        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            //String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            txtEmail.setText(email);
            //txtName.setText(name);
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference Product_reference = FirebaseDatabase.getInstance().getReference().child("Product");
                Product_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Product product = snapshot.getValue(Product.class);
                            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        initUi();
        getListProductFromDB();

    }
    private void initUi() {
        rcvProduct = findViewById(R.id.rcv_exercise);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvProduct.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvProduct.addItemDecoration(dividerItemDecoration);
        mListProduct = new ArrayList<>();
        mProductAdapter = new ProductAdapter(mListProduct, new ProductAdapter.IClickListener() {
            @Override
            public void onClickAddItem(Product product) {
                addProductToOrder(product);

            }
        });
        rcvProduct.setAdapter(mProductAdapter);
    }
    private void getListProductFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    mListProduct.add(product);
                    mProductAdapter.notifyDataSetChanged();
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
    private void addProductToOrder(Product product) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Order").push();
        productRef.setValue(product);
        Toast.makeText(MainActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
    }

}

