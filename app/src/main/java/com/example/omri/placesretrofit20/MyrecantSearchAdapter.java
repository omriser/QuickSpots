package com.example.omri.placesretrofit20;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by omri on 05/02/2018.
 */

public class MyrecantSearchAdapter extends RecyclerView.Adapter<MyrecantSearchAdapter.ViewHolder> {



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView query;

        public ViewHolder(View view) {
            super(view);
            query = view.findViewById(R.id.query_text_view);
        }


    }
    ArrayList<String> recantQuerys;
    public MyrecantSearchAdapter(ArrayList<String> recantQuerys){
        this.recantQuerys = recantQuerys;
    }
    View view;
    @Override
    public MyrecantSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recant_search_query_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyrecantSearchAdapter.ViewHolder holder, int position) {

        holder.query.setText(recantQuerys.get(position));
    }

    @Override
    public int getItemCount() {
        return recantQuerys.size();
    }
}
