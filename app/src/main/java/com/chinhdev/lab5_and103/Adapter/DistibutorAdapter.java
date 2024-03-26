package com.chinhdev.lab5_and103.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinhdev.lab5_and103.APIServer.APIServer;
import com.chinhdev.lab5_and103.MainActivity;
import com.chinhdev.lab5_and103.R;
import com.chinhdev.lab5_and103.model.DistributorModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DistibutorAdapter extends RecyclerView.Adapter<DistibutorAdapter.ViewHolder> {
    ArrayList<DistributorModel> list;
    Context context;
    Retrofit retrofit;
    APIServer apiServer;
    String url = "http://192.168.0.103:3000/";

    public DistibutorAdapter(ArrayList<DistributorModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_distibutor,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DistributorModel model = list.get(position);
        holder.tv_name.setText(model.getName());
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiServer = retrofit.create(APIServer.class);
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.create();
                builder.setMessage("Bạn có chắc chắn muốn xóa không ?");
                builder.setIcon(R.drawable.baseline_warning_24).setTitle("Cảnh báo");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<Void> call = apiServer.deleteDistributor(model.get_id());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()){
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });

                    }
                }).setNegativeButton("Cancel", null).show();
            }
        });
        holder.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_add,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView tv_tiltel = view.findViewById(R.id.tv_title);
                tv_tiltel.setText("Sửa Distributor");
                Button btnCancel = view.findViewById(R.id.btn_cancel);
                Button btnSave = view.findViewById(R.id.btn_save);
                TextInputEditText editText = view.findViewById(R.id.edt_name);
                editText.setText(model.getName());
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().trim().equalsIgnoreCase("")){
                            Toast.makeText(context, "Không đc bỏ trống", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String name = editText.getText().toString().trim();
                        DistributorModel modelNew = new DistributorModel(model.get_id(), name, model.getCreatedAt(), model.getUpdatedAt());
                        Call<Void> call = apiServer.putDistributor(model.get_id(), modelNew);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()){
                                    list.set(position,modelNew);
                                    alertDialog.dismiss();
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<DistributorModel> dataList) {
        list = dataList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView btn_del,btn_update;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            btn_del = itemView.findViewById(R.id.btn_delete);
            btn_update = itemView.findViewById(R.id.btn_edit);
        }
    }
}
