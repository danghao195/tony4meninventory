package com.tony4men.tony4meninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tony4men.tony4meninventory.helper.DBInventoryHelper;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

public class CreateBillActivity extends AppCompatActivity {
    EditText titleEditText;
    EditText typeEditText;
    EditText descriptionEditText;
    DBInventoryHelper dbInventoryHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        typeEditText = (EditText)findViewById(R.id.typeEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        dbInventoryHelper = new DBInventoryHelper(this);
    }

    public void back(View view){
        finish();
    }
    public void save(View view){
        String titleString = titleEditText.getText().toString();
//        if(isFilenameValid(titleString)) {
            Intent intent = new Intent(this, ProductListActivity.class);

            intent.putExtra("title", titleString);
            String typeString = typeEditText.getText().toString();
            intent.putExtra("type", typeString);
            String descriptionString = descriptionEditText.getText().toString();
            intent.putExtra("description", descriptionString);
            long billId = dbInventoryHelper.insertContact(titleString, typeString, descriptionString, new Timestamp(System.currentTimeMillis()));
            intent.putExtra("billid", billId);
            startActivity(intent);
            finish();
//        }else{
//            Toast.makeText(this,
//                    "Nhập vào tiêu đề ko có ký tự đặc biệt nhé!", Toast.LENGTH_LONG)
//                    .show();
//        }
    }
    public static boolean isFilenameValid(String file) {
        final File aFile = new File(file);
        boolean isValid = true;
        try {
            if (aFile.createNewFile()) {
                aFile.delete();
            }
        } catch (IOException e) {
            isValid = false;
        }
        return isValid;
    }
}