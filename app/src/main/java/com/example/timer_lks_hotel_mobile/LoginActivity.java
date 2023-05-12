package com.example.timer_lks_hotel_mobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText username, password;
    Session s;
    Context ctx;
    RequestQueue queue;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        ctx = this;
        s = new Session(ctx);
        queue = Volley.newRequestQueue(ctx);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().length() == 0 || password.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "Username And Password Must Be Filled", Toast.LENGTH_LONG).show();
                }else{
                    StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.73.123/api/employee", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                          if(response != null){
                              try {
                                  JSONObject obj = new JSONObject(response);
                                  s.SetEmployee(obj.getInt("ID"), obj.getString("Name"), obj.getString("Username"));
                                  Intent next = new Intent(LoginActivity.this, FDCheckOutActivity.class);
                                  startActivity(next);
                                  finish();
                              } catch (JSONException e) {
                                  e.printStackTrace();
                                  Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_LONG).show();
                              }
                          }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialog dialog = new AlertDialog.Builder(ctx).create();
                            dialog.setTitle("Error");
                            dialog.setMessage(error.toString());
                            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            String usernameValue = username.getText().toString();
                            String passwordValue = password.getText().toString();
                            Map<String, String> params = new HashMap<>();
                            params.put("Username", usernameValue);
                            params.put("Password", passwordValue);

                            return params;
                        }
                    };
                    queue.add(request);
                }
            }
        });
    }
}