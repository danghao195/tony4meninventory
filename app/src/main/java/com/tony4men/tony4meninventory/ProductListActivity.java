package com.tony4men.tony4meninventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tony4men.tony4meninventory.adapter.ProductAdapter;
import com.tony4men.tony4meninventory.helper.DBInventoryHelper;
import com.tony4men.tony4meninventory.model.Config;
import com.tony4men.tony4meninventory.model.ProductEntity;
import com.tony4men.tony4meninventory.util.PermissionUtil;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private long realAmountCount = 0;
    private List<ProductEntity> productEntities;
    RecyclerView productRecyclerView;
    ProductAdapter productAdapter;
    BarcodeBroadcast barcodeBroadcast;
    private Button imageBtn;
    RelativeLayout mainLayout;
    private int xDelta;
    private int yDelta;
    private long billid;
    private int position;
    String title = "";
    String fileName;
    int checkMoveX;
    int checkMoveY;
    int intentFrom;
    DBInventoryHelper dbInventoryHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mainLayout = (RelativeLayout) findViewById(R.id.main);
        imageBtn = (Button) findViewById(R.id.imageButton);
        imageBtn.setOnTouchListener(onTouchListener());
        imageBtn.setText("100");
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageBtn
                .getLayoutParams();
        layoutParams.leftMargin = 500;
        layoutParams.topMargin = 800;
        imageBtn.setLayoutParams(layoutParams);
        productRecyclerView = (RecyclerView) findViewById(R.id.productRecyclerView);
        registerForContextMenu(productRecyclerView);
//        productRecyclerView.setOnCreateContextMenuListener(this);
        dbInventoryHelper = new DBInventoryHelper(this);

        Intent intent = getIntent();
         intentFrom = intent.getIntExtra(Contents.INTENT_TO_PRODUCT_ACTIVITY_STRING, 0);
        productEntities = new LinkedList<ProductEntity>();
        billid= intent.getLongExtra("billid", -1l);
        if( intentFrom == Contents.INTENT_FROM_BILL_LIST_ACTIVITY){
            position = intent.getIntExtra("position", -1);
            productEntities.addAll(dbInventoryHelper.getAllProductByBill(billid));
        }

        title = intent.getStringExtra("title");
        setTitleCustom(title+"");
        fileName = title+ ".xlsx";
        productAdapter = new ProductAdapter(productEntities);
        realAmountCount = realAmountCount();
        imageBtn.setText(""+ realAmountCount);
        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        barcodeBroadcast = new BarcodeBroadcast();
        IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        registerReceiver(barcodeBroadcast,filter);
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
    private long realAmountCount(){
        long result = 0;
        long size = productEntities.size();
        for(int i = 0; i< size; i++){
            result += productEntities.get(i).getRealAmount();
        }
        return result;
    }

    @Override
    public void openContextMenu(View view) {
        super.openContextMenu(view);
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
                            Toast.makeText(ProductListActivity.this,
                                    "thanks for new location!", Toast.LENGTH_SHORT)
                                    .show();
//                            Intent editProductIntent = new Intent(ProductListActivity.this, ProductActivity.class);
//                            editProductIntent.putExtra("position", 0);
//                            editProductIntent.putExtra("billid", billid);
//                            (ProductListActivity.this).startActivityForResult(editProductIntent,1);
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
    public void createExcelAndSendMainBtn(View view ){
        if(PermissionUtil.hasPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionUtil.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        createExcel(productEntities);
        sendMessage();
    }

    private void createExcel(List<ProductEntity> entities){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("dataInventory");
        int size = entities.size();
        Row rowTitle = sheet.createRow(0);
        rowTitle.createCell(0).setCellValue("Mã sản phẩm");
        rowTitle.createCell(1).setCellValue("Số lượng thực thế");
        rowTitle.createCell(2).setCellValue("Số lượng gốc");
        rowTitle.createCell(3).setCellValue("Giá");
        rowTitle.createCell(4).setCellValue("Ngày sửa");
        rowTitle.createCell(5).setCellValue("Tên sản phẩm");
        for(int i = 0; i < size; i++){
            Row row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(entities.get(i).getProductCode());
            row.createCell(1).setCellValue(entities.get(i).getRealAmount());
            row.createCell(2).setCellValue(entities.get(i).getOriginAmount());
            row.createCell(3).setCellValue(entities.get(i).getPrice());
            row.createCell(4).setCellValue(entities.get(i).getUpdateDate());
            row.createCell(5).setCellValue(entities.get(i).getProductName());
        }

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "FolderName");
        if(!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, fileName);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String filename= Config.FolderName + fileName;
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".fileprovider", filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {Config.RECIPIENT};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(barcodeBroadcast);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_TV_NETWORK :
            case  KeyEvent.KEYCODE_TV_ANTENNA_CABLE:
            case KeyEvent.ACTION_DOWN:
                Intent intent = new Intent ("nlscan.action.SCANNER_TRIG");
                intent.putExtra("EXTRA_SCAN_MODE",3);
                intent.putExtra("SCAN_TIMEOUT", 4);// SCAN_TIMEOUT value: int, 1-9; unit: second
//                intent.putExtra("SCAN_TYPE ", 2);/
                this.sendBroadcast(intent);
                break;
            case KeyEvent.KEYCODE_BACK:
                if(intentFrom == Contents.INTENT_FROM_BILL_LIST_ACTIVITY) {
                    final Intent dataIntent = new Intent();
                    long productTotal = 0;
                    for (ProductEntity productEntity : productEntities) {
                        productTotal += productEntity.getRealAmount().longValue();
                    }
                    dataIntent.putExtra("productTotal", productTotal);
                    dataIntent.putExtra("position", position);
                    setResult(Activity.RESULT_OK, dataIntent);
                }
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add:
                            Intent editProductIntent = new Intent(ProductListActivity.this, ProductActivity.class);
                            editProductIntent.putExtra("position", 0);
                            editProductIntent.putExtra("billid", billid);
                            (ProductListActivity.this).startActivityForResult(editProductIntent,1);
                break;
            case R.id.exit:
                if(intentFrom == Contents.INTENT_FROM_BILL_LIST_ACTIVITY) {
                    final Intent dataIntent = new Intent();
                    long productTotal = 0;
                    for (ProductEntity productEntity : productEntities) {
                        productTotal += productEntity.getRealAmount().longValue();
                    }
                    dataIntent.putExtra("productTotal", productTotal);
                    dataIntent.putExtra("position", position);
                    setResult(Activity.RESULT_OK, dataIntent);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(productAdapter.isShowContextMenu()) {
            getMenuInflater().inflate(R.menu.contexts_menu, menu);
            productAdapter.setShowContextMenu(false);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.edit:
                Intent editProductIntent = new Intent(this, ProductActivity.class);
                editProductIntent.putExtra("position", productAdapter.getSelectedPosition());
                ProductEntity productEntity = productEntities.get(productAdapter.getSelectedPosition());
                editProductIntent.putExtra("data",productEntity);
                startActivityForResult(editProductIntent,2);
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Xác nhận xóa mã sản phẩm !");
                builder.setMessage("Bạn có thật sự muốn xóa mã sản phẩm này ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = productAdapter.getSelectedPosition();
                        dbInventoryHelper.deleteProductByCodeAndBillId(productEntities.get(selectedPosition).getProductCode(),
                                productEntities.get(selectedPosition).getBillId());
                        productEntities.remove(selectedPosition);
                        productAdapter.notifyItemRemoved(selectedPosition);
                        Toast.makeText(getApplicationContext(), "Đã xóa thành công "+ selectedPosition, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
//                Toast.makeText(this, "Result: OK" + , Toast.LENGTH_SHORT).show();
                int position = data.getIntExtra("position", 0);
                ProductEntity productEntity = (ProductEntity) data.getSerializableExtra("dataUpdate");
//                billlEntities.get(position).setProductCode(productEntity.getProductCode() );
                if (productEntities.size() == 0){
                    productEntities.add(productEntity);
                } else {
                    productEntities.add(0, productEntity);
                }
                long origin = productEntity.getOriginAmount()==null?0l:productEntity.getOriginAmount();

                long real = productEntity.getRealAmount()==null?0l:productEntity.getRealAmount();
                long image = productEntity.getImage()==null?0l:productEntity.getImage();
                double price = productEntity.getPrice();
                long productId = dbInventoryHelper.insertProduct(productEntity.getBillId(),productEntity.getProductCode(),
                        productEntity.getProductName(), origin, real,image, price,  new Timestamp(System.currentTimeMillis()));
                productEntities.get(0).setProductId(productId);
                productAdapter.notifyItemInserted(0);
                productRecyclerView.scrollToPosition(0);

                realAmountCount = realAmountCount();
                imageBtn.setText(""+ realAmountCount);
            } else {
                Toast.makeText(this, "Result: Fail", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
//                Toast.makeText(this, "Result: OK" + , Toast.LENGTH_SHORT).show();
                int position = data.getIntExtra("position", 0);
                ProductEntity productEntity = (ProductEntity) data.getSerializableExtra("dataUpdate");
//                billlEntities.get(position).setProductCode(productEntity.getProductCode() );
                if (productEntities.size() == 0){
                    long origin = productEntity.getOriginAmount()==null?0l:productEntity.getOriginAmount();

                    long real = productEntity.getRealAmount()==null?0l:productEntity.getRealAmount();
                    realAmountCount += real;
                    imageBtn.setText(""+ realAmountCount);
                    long image = productEntity.getImage()==null?0l:productEntity.getImage();
                    double price = productEntity.getPrice();
                    long productId = dbInventoryHelper.insertProduct(productEntity.getBillId(),productEntity.getProductCode(),
                            productEntity.getProductName(), origin, real,image, price,  new Timestamp(System.currentTimeMillis()));
                    productEntity.setProductId(productId);
                    productEntities.add(productEntity);
                    productAdapter.notifyItemInserted(position);
                } else {
                    long origin = productEntity.getOriginAmount()==null?0l:productEntity.getOriginAmount();

                    long real = productEntity.getRealAmount()==null?0l:productEntity.getRealAmount();
                    long image = productEntity.getImage()==null?0l:productEntity.getImage();
                    double price = productEntity.getPrice();
                    dbInventoryHelper.updateProduct(productEntity.getProductId(), productEntity.getBillId(),productEntity.getProductCode(),
                            productEntity.getProductName(), origin, real,image, price,  new Timestamp(System.currentTimeMillis()));
                    realAmountCount += real - productEntities.get(position).getRealAmount();
                    imageBtn.setText(""+ realAmountCount);
                    productEntities.set(position, productEntity);
                    productAdapter.notifyItemChanged(position);
                }
                realAmountCount = realAmountCount();
                imageBtn.setText(""+ realAmountCount);
            } else {
                Toast.makeText(this, "Result: Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class BarcodeBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String scanResult_1=intent.getStringExtra("SCAN_BARCODE1");
            final String scanResult_2=intent.getStringExtra("SCAN_BARCODE2");
            final int barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1); // -1:unknown
            final String scanStatus=intent.getStringExtra("SCAN_STATE");
            if("ok".equals(scanStatus)){
                //Success
                    int size = productEntities.size();
                    boolean isDuplication = false;
                    for (int i = 0; i < size; i++) {
                        if (scanResult_1 != null && scanResult_1.equals(productEntities.get(i).getProductCode())) {
                            long real = productEntities.get(i).getRealAmount() + 1;
                            productEntities.get(i).setRealAmount(productEntities.get(i).getRealAmount() + 1);
                            long origin = productEntities.get(i).getOriginAmount();
                            long image = productEntities.get(i).getImage();
                            double price = productEntities.get(i).getPrice();

                            dbInventoryHelper.updateProduct(productEntities.get(i).getProductId(), productEntities.get(i).getBillId(), productEntities.get(i).getProductCode(),
                                    productEntities.get(i).getProductName(), origin, real, image, price, new Timestamp(System.currentTimeMillis()));

                            ProductEntity productEntity = productEntities.get(i);
                            if(i != 0 && size > 1){
                                productEntities.remove(i);

                                productEntities.add(0, productEntity);
                                productAdapter.notifyItemMoved(i, 0);
                                productRecyclerView.scrollToPosition(0);
                            }
                                productAdapter.notifyItemChanged(0);


                            isDuplication = true;
                            break;
                        }
                    }
                    if (!isDuplication) {
                        ProductEntity productEntity = new ProductEntity();
                        productEntity.setProductCode(scanResult_1);
                        productEntity.setProductName("Không tên");
                        productEntity.setImage(1L);
                        productEntity.setOriginAmount(0L);
                        productEntity.setRealAmount(1L);
                        productEntity.setPrice(50000);
                        productEntity.setBillId(billid);
                        productEntity.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                        productEntities.add(0, productEntity);
                        long origin = productEntity.getOriginAmount();

                        long real = productEntity.getRealAmount();
                        long image = productEntity.getImage();
                        double price = productEntity.getPrice();
                        long productId = dbInventoryHelper.insertProduct(productEntity.getBillId(), productEntity.getProductCode(),
                                productEntity.getProductName(), origin, real, image, price, new Timestamp(System.currentTimeMillis()));
                        productEntities.get(0).setProductId(productId);
                        productAdapter.notifyItemInserted(0);
                        productRecyclerView.scrollToPosition(0);
                    }
//                realAmountCount = realAmountCount();
                imageBtn.setText(""+ ++realAmountCount);
            }else{
                //Failure, e.g. operation timed out
            }
        }
    }
}