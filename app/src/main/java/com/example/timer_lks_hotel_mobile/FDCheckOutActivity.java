package com.example.timer_lks_hotel_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FDCheckOutActivity extends AppCompatActivity {
    Spinner room, item;
    RadioButton food, drink;
    EditText price, qty, subtotal;
    Button submit;
    Context ctx;
    Session s;
    RequestQueue queue;

    List<Integer> IdRoom;
    List<String> RoomNumber;
    List<Integer> IdFd;
    List<String> FdName;
    List<Integer> FdPrice;
    int ROOMID,FDID;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdcheck_out);

        room = findViewById(R.id.RoomNumber);
        item = findViewById(R.id.Item);
        food = findViewById(R.id.food);
        drink = findViewById(R.id.drink);
        price = findViewById(R.id.Price);
        qty = findViewById(R.id.Qty);
        subtotal = findViewById(R.id.Subtotal);
        submit = findViewById(R.id.Submit);

        ctx = this;
        s = new Session(ctx);
        queue = Volley.newRequestQueue(ctx);

        IdRoom = new ArrayList<>();
        RoomNumber = new ArrayList<>();
        IdFd = new ArrayList<>();
        FdName = new ArrayList<>();
        FdPrice = new ArrayList<>();

        room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ROOMID = Integer.valueOf(IdRoom.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FDID =Integer.valueOf(IdFd.get(i));
                price.setText(String.valueOf(FdPrice.get(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0){
                    subtotal.setText("0");
                }else{
                    int quantity = Integer.parseInt(qty.getText().toString());
                    int pricee = Integer.parseInt(price.getText().toString());
                    subtotal.setText(String.valueOf(quantity * pricee));
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(food.isChecked() || drink.isChecked()) || qty.getText().length() == 0 || qty.getText().toString() == "0"){
                    Toast.makeText(getApplicationContext(), "All Field Must Be Filled", Toast.LENGTH_LONG).show();
                }else{
                    try{
                        JSONObject obj = new JSONObject();
                        obj.put("RoomID", ROOMID);
                        obj.put("FDID", FDID);
                        obj.put("Qty", Integer.parseInt(qty.getText().toString()));
                        obj.put("TotalPrice", Integer.parseInt(subtotal.getText().toString()));
                        obj.put("EmployeeID", s.getID());

                        JsonObjectRequest push = new JsonObjectRequest(Request.Method.POST, "http://192.168.73.123/api/fdcheckout", obj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getString("Response").equals("Success")) {
                                        AlertDialog dialog = new AlertDialog.Builder(ctx).create();
                                        dialog.setTitle("Success");
                                        dialog.setMessage("Checkout Success");
                                        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();

                                        food.setChecked(false);
                                        drink.setChecked(false);
                                        qty.getText().clear();
                                        subtotal.getText().clear();
                                    }
                                }catch (JSONException ex){
                                    ex.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        queue.add(push);
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }
                }
            }
        });

        loadRoom();
        loadItem();
    }

    protected void loadRoom(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.73.123/api/room", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        RoomNumber.add(obj.getString("RoomNumber"));
                        IdRoom.add(obj.getInt("ID"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, RoomNumber);
                room.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    protected void loadItem(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.73.123/api/fd", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        FdName.add(obj.getString("Name"));
                        IdFd.add(obj.getInt("ID"));
                        FdPrice.add(obj.getInt("Price"));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, FdName);
                item.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }
}