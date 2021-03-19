package com.tony4men.tony4meninventory.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.tony4men.tony4meninventory.BuildConfig;
import com.tony4men.tony4meninventory.model.Config;
import com.tony4men.tony4meninventory.model.ProductEntity;
import com.tony4men.tony4meninventory.model.ProductEntityListAndBillNameEntity;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class UtilFileMail {
    Timestamp timestamp ;
    String fileName = "";

    public  String createExcelEachOther(List<ProductEntityListAndBillNameEntity> eachotherEntities){

        timestamp = new Timestamp(System.currentTimeMillis());
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("dataInventory");
        int countRow = 0;
        int sizeEachother = eachotherEntities.size();
        Row rowTitle = sheet.createRow(++countRow);
        rowTitle.createCell(0).setCellValue("Mã sản phẩm");
        rowTitle.createCell(1).setCellValue("Số lượng thực thế");
        if(sizeEachother > 0){
            for(int j = 0; j < sizeEachother; j++){
                List<ProductEntity> entities = eachotherEntities.get(j).getEntities();
                int size = entities.size();
                long productTotal = 0l;
//                Row rowBillName = sheet.createRow(++countRow);
//                rowBillName.createCell(0).setCellValue("Phiếu " + eachotherEntities.get(j).getBillName() );
                for(int i = 0; i < size; i++){
                    Row row = sheet.createRow(++countRow);
                    row.createCell(0).setCellValue(entities.get(i).getProductCode());
                    row.createCell(1).setCellValue(entities.get(i).getRealAmount());
                    productTotal += entities.get(i).getRealAmount().longValue();
                }
                // Tổng sản phẩm mỗi phiếu
//                rowBillName.createCell(1).setCellValue(productTotal);
            }

        }

//        Row row = sheet.createRow(0);
//        row.createCell(0).setCellValue("Tổng số : ");
//        row.createCell(1).setCellValue(productTotal);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "FolderName");
        if(!folder.exists()) {
            folder.mkdir();
        }
        fileName = "Tony4man"+timestamp+".xlsx";
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
        return fileName;
    }

    public  String createExcel(List<ProductEntity> entities){
        long productTotal = 0l;
        timestamp = new Timestamp(System.currentTimeMillis());
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("dataInventory");
        int size = entities.size();
        Row rowTitle = sheet.createRow(1);
        rowTitle.createCell(0).setCellValue("Mã sản phẩm");
        rowTitle.createCell(1).setCellValue("Số lượng thực thế");
//        rowTitle.createCell(2).setCellValue("Số lượng gốc");
//        rowTitle.createCell(3).setCellValue("Giá");
//        rowTitle.createCell(4).setCellValue("Ngày sửa");
//        rowTitle.createCell(5).setCellValue("Tên sản phẩm");
        for(int i = 0; i < size; i++){
            Row row = sheet.createRow(i+2);
            row.createCell(0).setCellValue(entities.get(i).getProductCode());
            row.createCell(1).setCellValue(entities.get(i).getRealAmount());
            productTotal += entities.get(i).getRealAmount().longValue();
//            row.createCell(2).setCellValue(entities.get(i).getOriginAmount());
//            row.createCell(3).setCellValue(entities.get(i).getPrice());
//            row.createCell(4).setCellValue(entities.get(i).getUpdateDate());
//            row.createCell(5).setCellValue(entities.get(i).getProductName());
        }
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Tổng số : ");
        row.createCell(1).setCellValue(productTotal);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "FolderName");
        if(!folder.exists()) {
            folder.mkdir();
        }
        fileName = "Tony4man"+timestamp+".xlsx";
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
        return fileName;
    }

    public void sendMessage(String fileName, Activity activity) {
        String filename= Config.FolderName + fileName;
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = FileProvider.getUriForFile(activity,
                BuildConfig.APPLICATION_ID + ".fileprovider", filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {Config.RECIPIENT};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        activity.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }


}
