package com.task.aliessa.appsinnovatetask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.task.aliessa.appsinnovatetask.R;
import com.task.aliessa.appsinnovatetask.model.Friend;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Essa on 11/2/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

ArrayList<Friend> friends = new ArrayList<>();
Context context;
    public Adapter(Context context , ArrayList<Friend> friend){
friends = friend;
this.context =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.Txt_FriendName.setText(friends.get(position).getName());
        Picasso.with(context).load("https://graph.facebook.com/"+friends.get(position).getId()+"/picture?type=large").into(holder.Img_FriendImg);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
@BindView(R.id.Txt_FriendName)
        TextView Txt_FriendName;
@BindView(R.id.Img_FriendImg)
        ImageView Img_FriendImg;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }



    }
}
