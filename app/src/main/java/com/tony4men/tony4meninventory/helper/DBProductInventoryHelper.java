package com.tony4men.tony4meninventory.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.tony4men.tony4meninventory.model.BillEntity;
import com.tony4men.tony4meninventory.model.ProductEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DBProductInventoryHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "tony4meninventory.db";
    public static final String PRODUCT_TABLE = "product";
    public static final String CONTACTS_COLUMN_PRODUCT_ID = "product_id";
    public static final String CONTACTS_COLUMN_BILL_ID = "bill_id";
    public static final String CONTACTS_COLUMN_PRODUCT_CODE = "product_code";
    public static final String CONTACTS_COLUMN_PRODUCT_NAME = "product_name";
    public static final String CONTACTS_COLUMN_ORIGIN_AMOUNT = "origin_amount";
    public static final String CONTACTS_COLUMN_REAL_AMOUNT = "real_amount";
    public static final String CONTACTS_COLUMN_IMAGE = "image";
    public static final String CONTACTS_COLUMN_PRICE = "price";
    public static final String CONTACTS_COLUMN_UPDATE_DATE = "update_date";
    public DBProductInventoryHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        onCreate(db);
    }
    public boolean checkTable(){
        SQLiteDatabase db = this.getWritableDatabase();
//        SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = 'YourTableName'
        String selectQuery = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = 'product'";
        Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    return true;
                }
            }
            db.close();
            return false;

    }
    public long insertContact  (Long billId,String productCode,String name, Long originAmount, Long realAmount, Long image, Double price, Timestamp updateDate)
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
    public long updateContact  (Long productId, Long billId,String productCode,String name, Long originAmount, Long realAmount, Long image, Double price, Timestamp updateDate)
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
        long result = db.update(PRODUCT_TABLE,  contentValues,CONTACTS_COLUMN_PRODUCT_ID+" = ? and "+ CONTACTS_COLUMN_BILL_ID+ " = ? " , new String[]{"" + productId, "" + billId});
        db.close();
        return result;
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
        String selectQuery = "Select * FROM " + PRODUCT_TABLE + " WHERE " + PRODUCT_TABLE + "." + CONTACTS_COLUMN_BILL_ID + " = ?" ;
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
