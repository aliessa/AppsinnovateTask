package com.task.aliessa.appsinnovatetask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.task.aliessa.appsinnovatetask.adapter.Adapter;
import com.task.aliessa.appsinnovatetask.model.Friend;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsList extends AppCompatActivity {
@BindView(R.id.listView)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");

        JSONArray friendslist;
        ArrayList<Friend> friends = new ArrayList<>();
        System.out.println("friiiiiiisiz"+friends.size());
        try {
            friendslist = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++) {
                friends.add(new Friend(friendslist.getJSONObject(l).getString("id"),friendslist.getJSONObject(l).getString("name")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setAdapter(new Adapter(this,friends));
//        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, friends); // simple textview for list item
//        ListView listView = (ListView) findViewById(R.id.listView);
//        listView.setAdapter(adapter);
    }
}
