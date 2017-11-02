package com.task.aliessa.appsinnovatetask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    @BindView(R.id.Btn_Load_contact)
    Button Btn_loadContact;
    @BindView(R.id.Btn_Map)
    Button Btn_Map;
    @BindView(R.id.Btn_Calender)
    Button Btn_Calender;
    @BindView(R.id.Btn_share_photo)
    Button Btn_share_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Btn_Map.setOnClickListener(this);
        Btn_loadContact.setOnClickListener(this);
        Btn_Calender.setOnClickListener(this);
        Btn_share_photo.setOnClickListener(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        getLoginDetails(loginButton);
    }

    protected void getLoginDetails(LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        login_result.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),33
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Intent intent = new Intent(MainActivity.this, FriendsList.class);
                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    System.out.println("][][][]" + rawName.getJSONObject(0).getString("id"));
                                    intent.putExtra("jsondata", rawName.toString());
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();


            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }


    private void getFacebookFriends(final AccessToken token) {
        GraphRequest graphRequest = new GraphRequest(token, "/{user-id}/friendlists");
        graphRequest.setCallback(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logOut();
                try {

                    ArrayList<String> facebookFriends = new ArrayList<>();

                    JSONObject jsonObject = graphResponse.getJSONObject();

                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        String facebookID = data.getJSONObject(i).getString("id");
                        facebookFriends.add(facebookID);
                        System.out.println(facebookFriends.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });
        graphRequest.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Btn_Load_contact:
                Intent intent = new Intent(this, LoadContactActivity.class);
                startActivity(intent);
                break;
            case R.id.Btn_Map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.Btn_Calender:
                intent = new Intent(this, AddCalanderActivity.class);
                startActivity(intent);
                break;
            case R.id.Btn_share_photo:
                intent = new Intent(this, PhotoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
