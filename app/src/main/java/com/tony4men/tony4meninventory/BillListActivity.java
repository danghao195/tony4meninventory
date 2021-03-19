package com.tony4men.tony4meninventory;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tony4men.tony4meninventory.adapter.BillAdapter;
import com.tony4men.tony4meninventory.helper.DBInventoryHelper;
import com.tony4men.tony4meninventory.helper.DBProductInventoryHelper;
import com.tony4men.tony4meninventory.model.BillEntity;
import com.tony4men.tony4meninventory.model.ProductEntity;
import com.tony4men.tony4meninventory.model.ProductEntityListAndBillNameEntity;
import com.tony4men.tony4meninventory.util.UtilFileMail;

import java.util.ArrayList;
import java.util.List;

public class BillListActivity extends AppCompatActivity {

    DBInventoryHelper dbInventoryHelper;
    DBProductInventoryHelper dbProductInventoryHelper;
    UtilFileMail utilFileMail;
    private List<BillEntity> billlEntities;
    RecyclerView productRecyclerView;
    BillAdapter billAdapter;
    private Button totalImage;
    RelativeLayout mainLayout;
    private int xDelta;
    private int yDelta;
    String title = "";
    String fileName;
    int checkMoveX;
    int checkMoveY;
    private SparseBooleanArray itemStateArray;
    Button selectAllOrDesSelectedButton;
    public boolean isSelectAll = true;
    BillAdapter.CallbackSelectAllItem callbackSelectAllItem;
    long productTotal = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);
        setTitleCustom("Danh sách phiếu");
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        dbInventoryHelper = new DBInventoryHelper(this);
        dbProductInventoryHelper = new DBProductInventoryHelper(this);
        selectAllOrDesSelectedButton = (Button) findViewById(R.id.selectAllOrDesSelectedButton);
        totalImage = (Button) findViewById(R.id.imageBillButton);
        totalImage.setVisibility(View.INVISIBLE);
        totalImage.setOnTouchListener(onTouchListener());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) totalImage
                .getLayoutParams();
        layoutParams.leftMargin = 500;
        layoutParams.topMargin = 700;
        totalImage.setLayoutParams(layoutParams);
        productRecyclerView = (RecyclerView) findViewById(R.id.productRecyclerView);
        utilFileMail = new UtilFileMail();
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        fileName = title+ ".xlsx";
        billlEntities = new ArrayList<>();
        billlEntities.addAll(dbInventoryHelper.getAllBill());
        productTotal = 0;
        billAdapter = new BillAdapter(billlEntities);
        billAdapter.callbackSelectAllItem = new BillAdapter.CallbackSelectAllItem() {
            @Override
            public void selectAllOrDesSelected(boolean isSelectAll, int position, boolean isSelected) {
                BillListActivity.this.isSelectAll = isSelectAll;
//                int size = billlEntities.size();
//                itemStateArray = new SparseBooleanArray();

                if (isSelectAll) {
//                    for(int i = 0; i < size; i++){
//                        itemStateArray.put(i, true);
//                    }
//                    productAdapter.setItemStateArray(itemStateArray);
                    productTotal = 0;
                    totalImage.setVisibility(View.INVISIBLE);
                    selectAllOrDesSelectedButton.setText("Chọn hết");
                } else {
//                    productAdapter.setItemStateArray(itemStateArray);
                    if(isSelected){
                        productTotal += billlEntities.get(position).getProductTotal().longValue();
                    } else {
                        productTotal -= billlEntities.get(position).getProductTotal().longValue();
                    }

                    totalImage.setVisibility(View.VISIBLE);
                    totalImage.setText(""+productTotal);
                    selectAllOrDesSelectedButton.setText("Bỏ chọn");
                }
//                productAdapter.notifyDataSetChanged();
            }
        };
        productRecyclerView.setAdapter(billAdapter);
        itemStateArray= billAdapter.getItemStateArray();
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
    }
    public void setTitleCustom(String title){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }
    public void xoaBill(View view){
//        dbInventoryHelper.deleteBillById()
        ArrayList<String> bills = new  ArrayList();
        int size = billlEntities.size();
        for( int i = size-1 ; i >= 0; i--){
            if( itemStateArray.get(i)){
                bills.add("" + billlEntities.get(i).getBillId());
                dbInventoryHelper.deleteBillById(billlEntities.get(i).getBillId());
                dbInventoryHelper.deleteProductByBillId(billlEntities.get(i).getBillId());
                billlEntities.remove(i);
                billAdapter.notifyItemRemoved(i);
            }
        }
        setItemStateArrayAdapter();
    }
    public void gopBill(View view){
        ArrayList<String> bills = new  ArrayList();
        int size = billlEntities.size();
        for( int i = 0 ; i< size; i++){
            if( itemStateArray.get(i)){
                bills.add("" + billlEntities.get(i).getBillId());
            }
        }
//        bills.add("1");
//        bills.add("2");
        ArrayList<ProductEntity> products = new ArrayList<>();
        products = (ArrayList<ProductEntity>) dbProductInventoryHelper.getAllProductByBills(bills);
        ArrayList<ProductEntityListAndBillNameEntity> productEachOther = new ArrayList<>();

        for( int i = 0 ; i< size; i++){
            if( itemStateArray.get(i)){
//                bills.add("" + billlEntities.get(i).getBillId());
                ProductEntityListAndBillNameEntity   productEntityListAndBillNameEntity = new ProductEntityListAndBillNameEntity(
                        (ArrayList<ProductEntity>) dbProductInventoryHelper.getAllProductByBill(billlEntities.get(i).getBillId().longValue()),
                        billlEntities.get(i).getBillName());
                productEachOther.add (productEntityListAndBillNameEntity);//getAllProductByBill
            }
        }
//        utilFileMail.sendMessage(utilFileMail.createExcel(products),this);
        utilFileMail.sendMessage(utilFileMail.createExcelEachOther(productEachOther),this);
    }
    private void setItemStateArrayAdapter(){
        int size = billlEntities.size();
        itemStateArray = new SparseBooleanArray();
        for(int i = 0; i < size; i++){
            itemStateArray.put(i, false);
        }
        billAdapter.setItemStateArray(itemStateArray);
    }
    public void selectAllOrDesSelected(View view){
        int size = billlEntities.size();
        itemStateArray = new SparseBooleanArray();
        if (isSelectAll) {
            for(int i = 0; i < size; i++){
                itemStateArray.put(i, true);
            }
            billAdapter.setItemStateArray(itemStateArray);
            selectAllOrDesSelectedButton.setText("Bỏ chọn");
        } else {
            billAdapter.setItemStateArray(itemStateArray);
            selectAllOrDesSelectedButton.setText("Chọn hết");
        }
        billAdapter.notifyDataSetChanged();
        isSelectAll = !isSelectAll;
    }
    private View.OnTouchListener onTouchListener() {

        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();
                        checkMoveX = x;
                        checkMoveY = y;
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        if(checkMoveX == x && checkMoveY == y){
                            Toast.makeText(BillListActivity.this,
                                    "thanks for new location!", Toast.LENGTH_SHORT)
                                    .show();
//                            Intent createBillIntent = new Intent(BillListActivity.this, CreateBillActivity.class);
//                            createBillIntent.putExtra("position", billlEntities.size());
//                            (BillListActivity.this).startActivity(createBillIntent);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
//            case KeyEvent.KEYCODE_TV_NETWORK :
//            case  KeyEvent.KEYCODE_TV_ANTENNA_CABLE:
//                Intent intent = new Intent ("nlscan.action.SCANNER_TRIG");
//                intent.putExtra("SCAN_TIMEOUT", 4);// SCAN_TIMEOUT value: int, 1-9; unit: second
////                intent.putExtra("SCAN_TYPE ", 2);/
//                this.sendBroadcast(intent);
//                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Contents.INTENT_FROM_BILL_LIST_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                int position = data.getIntExtra("position", 0);
//                ProductEntity productEntity = (ProductEntity) data.getSerializableExtra("dataUpdate");
//                billlEntities.get(position).setProductCode(productEntity.getProductCode() );
                long productTotal = data.getLongExtra("productTotal",0);
                billlEntities.get(position).setProductTotal(productTotal);
                billAdapter.notifyItemChanged(position);
                if (billlEntities.size() == position){
//                    billlEntities.add(productEntity);
                } else {
//                    billlEntities.set(position, productEntity);
                }
//                productAdapter.notifyItemChanged(position);
//            } else {
////                Toast.makeText(this, "Result: Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
