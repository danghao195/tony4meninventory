package com.tony4men.tony4meninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createBill(View view){
        Intent intent = new Intent(this,CreateBillActivity.class);
        startActivity(intent);
    }
    public void openSetting(View view){

    }
    public void openList(View view){
        Intent intent = new Intent(this,BillListActivity.class);
        startActivity(intent);
    }
}