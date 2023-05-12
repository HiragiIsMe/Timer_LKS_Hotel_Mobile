package com.example.timer_lks_hotel_mobile;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    private SharedPreferences sharedPreferences;

    public Session(Context ctx){
        sharedPreferences = ctx.getSharedPreferences("my-data", Context.MODE_PRIVATE);
    }

    public void SetEmployee(int id, String name, String username){
        sharedPreferences.edit().putInt("id", id).commit();
        sharedPreferences.edit().putString("name", name).commit();
        sharedPreferences.edit().putString("username", username).commit();
    }

    public int getID(){
        return sharedPreferences.getInt("id", 0);
    }
    public String getName(){
        return  sharedPreferences.getString("name", "");
    }

    public String getUsername(){
        return sharedPreferences.getString("username", "");
    }
}
