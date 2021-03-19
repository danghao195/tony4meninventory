package com.tony4men.tony4meninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tony4men.tony4meninventory.helper.DBInventoryHelper;
import com.tony4men.tony4meninventory.helper.DBProductInventoryHelper;
import com.tony4men.tony4meninventory.model.ProductEntity;

import java.sql.Timestamp;

public class ProductActivity extends AppCompatActivity {
    public EditText productCodeEditText;
    public EditText productNameEditText;
    public EditText originAmountEditText;
    public EditText realAmountEditText;
    public EditText priceEditText;
    ProductEntity productEntity;
    int position;
    long billid;
    DBInventoryHelper dbInventoryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        productCodeEditText = (EditText)findViewById(R.id.maHangValue);
        productNameEditText = (EditText)findViewById(R.id.tenHangValue);
        originAmountEditText = (EditText)findViewById(R.id.soLuongGocValue);
        realAmountEditText = (EditText)findViewById(R.id.soLuongThucValue);
        priceEditText = (EditText)findViewById(R.id.giaValue);
        dbInventoryHelper = new DBInventoryHelper(this);
        Intent intent = getIntent();
        productEntity = (ProductEntity) intent.getSerializableExtra("data");
        position = intent.getIntExtra("position",0);

        billid= intent.getLongExtra("billid", -1l);
        if(productEntity != null){
            productCodeEditText.setText(productEntity.getProductCode());
            productNameEditText.setText(productEntity.getProductName());
            originAmountEditText.setText("" + productEntity.getOriginAmount());
            realAmountEditText.setText("" + productEntity.getRealAmount());
            priceEditText.setText(String.format("" + productEntity.getPrice(), 2.5));
        } else{
            productEntity = new ProductEntity();
        }
        if(billid != -1l) {
            productEntity.setBillId(billid);
        }
    }

    public void back(View view){
        finish();
    }

    public void save (View view){
        final  Intent dataIntent = new Intent();
        if(productEntity != null){
            productEntity.setProductCode(productCodeEditText.getText().toString());
            productEntity.setProductName(productNameEditText.getText().toString());
            String originAmount = originAmountEditText.getText().toString();
            if(originAmount != null && !"".equals(originAmount)) {
                productEntity.setOriginAmount(Long.parseLong(originAmount));
            }
            String readAmount = realAmountEditText.getText().toString();
            if(readAmount != null && !"".equals(readAmount)) {
                productEntity.setRealAmount(Long.parseLong(readAmount));
            }
            String price = priceEditText.getText().toString();
            productEntity.setPrice(( "".equals(price))? 0:Double.parseDouble(price));
            productEntity.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        }
        dataIntent.putExtra("dataUpdate", productEntity);
        dataIntent.putExtra("position", position);
        setResult(Activity.RESULT_OK, dataIntent);

        finish();
    }
}