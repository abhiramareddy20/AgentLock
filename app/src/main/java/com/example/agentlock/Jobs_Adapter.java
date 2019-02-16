package com.example.agentlock;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class Jobs_Adapter extends RecyclerView.Adapter<Jobs_Adapter.ProductViewHolder> {
    private Context mCtx;

    private List<Jobss> productList;



    public Jobs_Adapter(Context mCtx, List<Jobss> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
        Log.e ("loading","2");
    }

    @NonNull
    @Override
    public Jobs_Adapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_jobs, null);
        Log.e ("loading","3");


        return new ProductViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull Jobs_Adapter.ProductViewHolder holder, int position) {

        Jobss product = productList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getTitle());
       /* holder.textViewShortDesc.setText(product.getOrdered_by ());*/
        holder.textViewShortDesc.setText(String.valueOf(product.getQuantity ()));
        holder.textViewPrice.setText(String.valueOf(product.getPrice()));

        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getImage()));

        holder.relativeLayout.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (view.getContext (),product_Descreption.class);
                view.getContext ().startActivity (i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;
        RelativeLayout relativeLayout;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.size);
            textViewShortDesc = itemView.findViewById(R.id.name);
            /*textViewRating = itemView.findViewById(R.id.order);*/
            textViewPrice = itemView.findViewById(R.id.qty);
            imageView = itemView.findViewById(R.id.imageView);

            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }

}
