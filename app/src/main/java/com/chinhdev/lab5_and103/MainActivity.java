package com.chinhdev.lab5_and103;


import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinhdev.lab5_and103.APIServer.APIServer;
import com.chinhdev.lab5_and103.Adapter.DistibutorAdapter;
import com.chinhdev.lab5_and103.model.DistributorModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Retrofit retrofit;
    String url = "http://192.168.0.103:3000/";
    APIServer apiServer;
    ArrayList<DistributorModel> list;
    DistibutorAdapter adapter;
    EditText edt_search;
    FloatingActionButton button_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcv_distributor);
        edt_search = findViewById(R.id.ed_search);
        button_add = findViewById(R.id.btn_add);

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiServer = retrofit.create(APIServer.class);
        handleCallData();
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String key = edt_search.getText().toString().trim();
                    Call<ArrayList<DistributorModel>> call = apiServer.searchDistributor(key);
                    call.enqueue(new Callback<ArrayList<DistributorModel>>() {
                        @Override
                        public void onResponse(Call<ArrayList<DistributorModel>> call, Response<ArrayList<DistributorModel>> response) {
                            if(response.isSuccessful()){
                                list = response.body();
                                adapter.setData(list);
                                recyclerView.setAdapter(adapter);
                                Toast.makeText(MainActivity.this, "hhhh", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "loi", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<DistributorModel>> call, Throwable t) {
                            Log.e("loi",t.getMessage());
                        }
                    });
                }
                return false;
            }
        });
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_add,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView tv_tiltel = view.findViewById(R.id.tv_title);
                tv_tiltel.setText("Thêm Distributor");
                Button btnCancel = view.findViewById(R.id.btn_cancel);
                Button btnSave = view.findViewById(R.id.btn_save);
                TextInputEditText editText = view.findViewById(R.id.edt_name);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().trim().equalsIgnoreCase("")){
                            Toast.makeText(MainActivity.this, "Không đc bỏ trống", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String name = editText.getText().toString().trim();
                        DistributorModel model = new DistributorModel();
                        model.setName(name);
                        Call<Void> call = apiServer.postDistributor(model);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()){
                                    handleCallData();
                                    alertDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    Log.e("loi", "onResponse: "+response.code() );
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("loi",t.getMessage());

                            }
                        });
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

    }

    public void handleCallData() {
        Call<ArrayList<DistributorModel>> call = apiServer.getDistributor();
        call.enqueue(new Callback<ArrayList<DistributorModel>>() {
            @Override
            public void onResponse(Call<ArrayList<DistributorModel>> call, Response<ArrayList<DistributorModel>> response) {
                if (response.isSuccessful()){
                    list = response.body();
                    adapter = new DistibutorAdapter(list,MainActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(adapter);
                }else {
                    Toast.makeText(MainActivity.this, "That bai ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DistributorModel>> call, Throwable t) {
                Log.d("loi",t.getMessage());
            }
        });
    }
}