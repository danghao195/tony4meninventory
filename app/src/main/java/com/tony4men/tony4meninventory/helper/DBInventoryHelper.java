package com.tony4men.tony4meninventory.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tony4men.tony4meninventory.model.BillEntity;
import com.tony4men.tony4meninventory.model.ProductEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DBInventoryHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "tony4meninventory.db";
    public static final String BILL_TABLE = "bill";
    public static final String CONTACTS_COLUMN_BILL_ID = "bill_id";
    public static final String CONTACTS_COLUMN_BILL_NAME = "bill_name";
    public static final String CONTACTS_COLUMN_BILL_TYPE = "bill_type";
    public static final String CONTACTS_COLUMN_DESCRIPTION = "description";
    public static final String CONTACTS_COLUMN_UPDATE_DATE = "update_date";

    public static final String PRODUCT_TABLE = "product";
    public static final String CONTACTS_COLUMN_PRODUCT_ID = "product_id";
//    public static final String CONTACTS_COLUMN_BILL_ID = "bill_id";
    public static final String CONTACTS_COLUMN_PRODUCT_CODE = "product_code";
    public static final String CONTACTS_COLUMN_PRODUCT_NAME = "product_name";
    public static final String CONTACTS_COLUMN_ORIGIN_AMOUNT = "origin_amount";
    public static final String CONTACTS_COLUMN_REAL_AMOUNT = "real_amount";
    public static final String CONTACTS_COLUMN_IMAGE = "image";
    public static final String CONTACTS_COLUMN_PRICE = "price";
    public DBInventoryHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS   "+ BILL_TABLE + " ( "+ CONTACTS_COLUMN_BILL_ID +" integer  primary key AUTOINCREMENT, " +
                CONTACTS_COLUMN_BILL_NAME +" text," +
                CONTACTS_COLUMN_BILL_TYPE+" text," +
                CONTACTS_COLUMN_DESCRIPTION + " text, " +
                CONTACTS_COLUMN_UPDATE_DATE+" localtime)");

        db.execSQL("create table IF NOT EXISTS  "+ PRODUCT_TABLE + " ( "+ CONTACTS_COLUMN_PRODUCT_ID +" integer  primary key AUTOINCREMENT, " +
                CONTACTS_COLUMN_BILL_ID +" integer," +
                CONTACTS_COLUMN_PRODUCT_CODE +" text," +
                CONTACTS_COLUMN_PRODUCT_NAME +" text," +
                CONTACTS_COLUMN_ORIGIN_AMOUNT +" integer," +
                CONTACTS_COLUMN_REAL_AMOUNT + " integer, " +
                CONTACTS_COLUMN_IMAGE +" integer," +
                CONTACTS_COLUMN_PRICE +" text," +
                CONTACTS_COLUMN_UPDATE_DATE+" localtime)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BILL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        onCreate(db);
    }
    public long insertContact  (String name, String phone, String email, Timestamp street)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_BILL_NAME, name);
        contentValues.put(CONTACTS_COLUMN_BILL_TYPE, phone);
        contentValues.put(CONTACTS_COLUMN_DESCRIPTION, email);
        contentValues.put(CONTACTS_COLUMN_UPDATE_DATE, ""+street);
        return db.insert(BILL_TABLE, null, contentValues);
//        return true;
    }
    public long productTotalByBillId(Long billId){
        long productTotal = 0;
        String selectQuery = "Select sum( "+ CONTACTS_COLUMN_REAL_AMOUNT +" ) FROM " + PRODUCT_TABLE + " WHERE " + PRODUCT_TABLE + "." + CONTACTS_COLUMN_BILL_ID + " = ?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{""+billId});
        if(cursor.moveToFirst()){
            do{
                ProductEntity productEntity = new ProductEntity();
                if(cursor.getString(0) != null) {
                    productTotal = Long.parseLong(cursor.getString(0));
                }
            }while (cursor.moveToNext());
        }

        db.close();
        return productTotal;
    }
    public List<BillEntity> getAllBill(){
        List<BillEntity> billEntityList = new ArrayList<>();
        //Select All Query
        String selectQuery = "Select * FROM " + BILL_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                BillEntity billEntity = new BillEntity();
                billEntity.setBillId(Long.parseLong(cursor.getString(0)));
                billEntity.setBillName(cursor.getString(1));
                billEntity.setBillType(cursor.getString(2));
                String dateString = cursor.getString(4);
                Timestamp timestamp =  Timestamp.valueOf(dateString);
                billEntity.setUpdateDate(timestamp);
                billEntity.setDescription(cursor.getString(3));
                billEntity.setProductTotal(productTotalByBillId(Long.parseLong(cursor.getString(0))));
                billEntityList.add(billEntity);
            }while (cursor.moveToNext());
        }


        return billEntityList;
    }
    public long insertProduct  (Long billId,String productCode,String name, Long originAmount, Long realAmount, Long image, Double price, Timestamp updateDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_BILL_ID, billId);
        contentValues.put(CONTACTS_COLUMN_PRODUCT_CODE, productCode);
        contentValues.put(CONTACTS_COLUMN_PRODUCT_NAME, name);
        contentValues.put(CONTACTS_COLUMN_ORIGIN_AMOUNT, originAmount);
        contentValues.put(CONTACTS_COLUMN_REAL_AMOUNT, realAmount);
        contentValues.put(CONTACTS_COLUMN_IMAGE, (image == null)?0l:image);
        contentValues.put(CONTACTS_COLUMN_PRICE, price);
        contentValues.put(CONTACTS_COLUMN_UPDATE_DATE, ""+updateDate);
        long result = db.insert(PRODUCT_TABLE, null, contentValues);
        db.close();
        return result;
    }

    public long updateProduct  (Long productId, Long billId,String productCode,String name, Long originAmount, Long realAmount, Long image, Double price, Timestamp updateDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(CONTACTS_COLUMN_BILL_ID, billId);
        contentValues.put(CONTACTS_COLUMN_PRODUCT_CODE, productCode);
        contentValues.put(CONTACTS_COLUMN_PRODUCT_NAME, name);
        contentValues.put(CONTACTS_COLUMN_ORIGIN_AMOUNT, (originAmount == null)?0l:originAmount);
        contentValues.put(CONTACTS_COLUMN_REAL_AMOUNT, (realAmount == null)?0l:realAmount);
        contentValues.put(CONTACTS_COLUMN_IMAGE, (image == null)?0l:image);
        contentValues.put(CONTACTS_COLUMN_PRICE, (price == null)?0l:price);
        contentValues.put(CONTACTS_COLUMN_UPDATE_DATE, ""+updateDate);
        long result = db.update(PRODUCT_TABLE,  contentValues,CONTACTS_COLUMN_PRODUCT_CODE+" = ? and "+ CONTACTS_COLUMN_BILL_ID+ " = ? " , new String[]{"" + productCode, "" + billId});
        db.close();
        return result;
    }
    public long deleteBillById( Long billId){
        SQLiteDatabase db = this.getWritableDatabase();
        long deletedNumber = 0;
        deletedNumber = db.delete(BILL_TABLE, CONTACTS_COLUMN_BILL_ID+ " = ? " , new String[]{ "" + billId});
        return deletedNumber;
    }

    public long deleteProductByBillId( Long billId){
        SQLiteDatabase db = this.getWritableDatabase();
        long deletedNumber = 0;
        deletedNumber = db.delete(PRODUCT_TABLE, CONTACTS_COLUMN_BILL_ID+ " = ? " , new String[]{ "" + billId});
        return deletedNumber;
    }

    public long deleteProductByCodeAndBillId(String productCode, Long billId){
        SQLiteDatabase db = this.getWritableDatabase();
        long deletedNumber = 0;
        deletedNumber = db.delete(PRODUCT_TABLE,CONTACTS_COLUMN_PRODUCT_CODE+" = ? and "+ CONTACTS_COLUMN_BILL_ID+ " = ? " , new String[]{"" + productCode, "" + billId});
        return deletedNumber;
    }

    public long deleteProductById( Long productId){
        SQLiteDatabase db = this.getWritableDatabase();
        long deletedNumber = 0;
        deletedNumber = db.delete(PRODUCT_TABLE,CONTACTS_COLUMN_PRODUCT_ID+" = ? " , new String[]{"" + productId});
        return deletedNumber;
    }

    public List<ProductEntity> getAllProductByBills(List billIds){
        List<ProductEntity> productEntityList = new ArrayList<>();
//        String inClause = billIds.toString();
//        inClause = "(";
        StringBuilder inClauseSql = new StringBuilder();
        inClauseSql.append("(");
        int size = billIds.size();
        String billStr[] = new String [size];
        for(int i = 0; i<size; i++){
            billStr[i] = billIds.get(i).toString();
            if(i == 0){
                inClauseSql.append("?");
            } else {
                inClauseSql.append(",?");
            }
        }
        inClauseSql.append(")");
//        inClause = inClause.replace("[","(");
//        inClause = inClause.replace("]",")");
        String selectQuery = "Select * FROM " + PRODUCT_TABLE + " WHERE " + PRODUCT_TABLE + "." + CONTACTS_COLUMN_BILL_ID + " in " + inClauseSql.toString() ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, billStr);
        if(cursor.moveToFirst()){
            do{
                ProductEntity productEntity = new ProductEntity();
                productEntity.setProductId(Long.parseLong(cursor.getString(0)));
                productEntity.setBillId(Long.parseLong(cursor.getString(1)));
                productEntity.setProductCode(cursor.getString(2));
                productEntity.setProductName(cursor.getString(3));
                productEntity.setOriginAmount(Long.parseLong(cursor.getString(4)));
                productEntity.setRealAmount(Long.parseLong(cursor.getString(5)));
                productEntity.setImage(Long.parseLong(cursor.getString(6)));
                productEntity.setPrice(Double.parseDouble(cursor.getString(7)));
                String dateString = cursor.getString(8);
                Timestamp timestamp =  Timestamp.valueOf(dateString);
                productEntity.setUpdateDate(timestamp);
                productEntityList.add(productEntity);
            }while (cursor.moveToNext());
        }

        db.close();
        return gopProduct(productEntityList);
    }

    private List<ProductEntity> gopProduct(List<ProductEntity> productEntities){
        int size = productEntities.size();
        List<ProductEntity> gopProductEntityList = new ArrayList<>();
        gopProductEntityList.add(productEntities.get(0));
        boolean isDuplicate = false;
        for( int i = 1; i < size; i++ ){
            for(int j = 0; j < i; j++){
                if(productEntities.get(i).getProductCode().equals(productEntities.get(j).getProductCode())){
                    isDuplicate = true;
                    gopProductEntityList.get(j).setOriginAmount(gopProductEntityList.get(j).getOriginAmount()
                            + productEntities.get(i).getOriginAmount());
                    gopProductEntityList.get(j).setRealAmount( gopProductEntityList.get(j).getRealAmount()
                            +productEntities.get(i).getRealAmount());
                    break;
                }
            }
            if(!isDuplicate){
                gopProductEntityList.add(productEntities.get(i));
            }
            isDuplicate = false;

        }
        return gopProductEntityList;
    }

    public List<ProductEntity> getAllProductByBill(Long billId){
        List<ProductEntity> productEntityList = new ArrayList<>();
        //Select All Query
        String selectQuery = "Select * FROM " + PRODUCT_TABLE + " WHERE " + PRODUCT_TABLE + "." + CONTACTS_COLUMN_BILL_ID + " = ? ORDER BY "+ DBProductInventoryHelper.CONTACTS_COLUMN_UPDATE_DATE +" DESC;" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{""+billId});
        if(cursor.moveToFirst()){
            do{
                ProductEntity productEntity = new ProductEntity();
                productEntity.setProductId(Long.parseLong(cursor.getString(0)));
                productEntity.setBillId(Long.parseLong(cursor.getString(1)));
                productEntity.setProductCode(cursor.getString(2));
                productEntity.setProductName(cursor.getString(3));
                productEntity.setOriginAmount(Long.parseLong(cursor.getString(4)));
                productEntity.setRealAmount(Long.parseLong(cursor.getString(5)));
                productEntity.setImage(Long.parseLong(cursor.getString(6)));
                productEntity.setPrice(Double.parseDouble(cursor.getString(7)));
                String dateString = cursor.getString(8);
                Timestamp timestamp =  Timestamp.valueOf(dateString);
                productEntity.setUpdateDate(timestamp);
                productEntityList.add(productEntity);
            }while (cursor.moveToNext());
        }

        db.close();
        return productEntityList;
    }
}
