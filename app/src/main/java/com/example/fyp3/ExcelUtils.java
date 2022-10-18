package com.example.fyp3;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.fyp3.Model.AttendanceClass;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcelUtils {

    /**
     * Export Data into Excel Workbook
     *
     * @param context  - Pass the application context
     * @param fileName - Pass the desired fileName for the output excel Workbook
     * @param dataList - Contains the actual data to be displayed in excel
     */

    public static String TAG = "EXCEL";
    public static XSSFWorkbook workbook;

    private static XSSFCell cell;
    private static XSSFSheet sheet;
    private static String EXCEL_SHEET_NAME = "Sheet1";
//    private static int currentWeek;

    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 List<AttendanceClass> dataList, Integer currentWeek) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new XSSFWorkbook();

//        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 400));
        sheet.setColumnWidth(1, (15 * 400));
        sheet.setColumnWidth(2, (15 * 400));
        sheet.setColumnWidth(3, (15 * 400));

        setHeaderRow(currentWeek);
        fillDataIntoExcel(dataList, currentWeek);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }

    /**
     * Checks if Storage is READ-ONLY
     *
     * @return boolean
     */
    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    /**
     * Setup header cell style
     */
    private static CellStyle setHeaderCellStyle() {
        XSSFCellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
//        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        return headerCellStyle;
    }

    /**
     * Setup Header Row
     */
    private static void setHeaderRow(Integer currentWeek) {
        XSSFRow headerRow = sheet.createRow(0);

        cell = headerRow.createCell(0);
        cell.setCellValue("Matrix Number");
        cell.setCellStyle(setHeaderCellStyle());

        cell = headerRow.createCell(1);
        cell.setCellValue("Student Name");
        cell.setCellStyle(setHeaderCellStyle());

        cell = headerRow.createCell(2);
        cell.setCellValue("Overall Percentage");
        cell.setCellStyle(setHeaderCellStyle());


        int j=3;
        for(int i=1; i<=currentWeek ; i++){
            cell = headerRow.createCell(j);
            cell.setCellValue("week "+i);
            cell.setCellStyle(setHeaderCellStyle());
            j++;
        }

    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(List<AttendanceClass> dataList, Integer currentWeek) {
        XSSFCellStyle presentStyle = workbook.createCellStyle();
        presentStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        presentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        presentStyle.setBorderTop(BorderStyle.THIN);
        presentStyle.setBorderBottom(BorderStyle.THIN);
        presentStyle.setBorderLeft(BorderStyle.THIN);
        presentStyle.setBorderRight(BorderStyle.THIN);
        XSSFCellStyle absentStyle = workbook.createCellStyle();
        absentStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
        absentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        absentStyle.setBorderTop(BorderStyle.THIN);
        absentStyle.setBorderBottom(BorderStyle.THIN);
        absentStyle.setBorderLeft(BorderStyle.THIN);
        absentStyle.setBorderRight(BorderStyle.THIN);
        XSSFCellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setBorderTop(BorderStyle.THIN);
        defaultStyle.setBorderBottom(BorderStyle.THIN);
        defaultStyle.setBorderLeft(BorderStyle.THIN);
        defaultStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            XSSFRow rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getStudentId());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getStudentName());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getPercentage());

            List<String> week = dataList.get(i).getWeek();
//            List<Integer> weekInt = new ArrayList<>();
//            for(String s : week)
//            {
//                weekInt.add(Integer.valueOf(s));
//            }

//            Collections.sort(week);
//            Collections.reverse(week);


            if(week.isEmpty()){
                for(int j=3; j<currentWeek+3; j++){
                    cell = rowData.createCell(j);
                    cell.setCellValue("P");
                    cell.setCellStyle(presentStyle);
                }
            }else{
                int y=3;
                for(int j=1; j<=currentWeek; j++){
                    for(int x=0; x<week.size(); x++){
                        if(week.get(x).equals(String.valueOf(j))){
                            cell = rowData.createCell(y);
                            cell.setCellValue("A");
                            cell.setCellStyle(absentStyle);
                            break;
                        }else {
                            cell = rowData.createCell(y);
                            cell.setCellValue("P");
                            cell.setCellStyle(presentStyle);
                        }
                    }
                    y++;
                }

//                int y=3;
//                for(int j=0; j<week.size(); j++){
//                    for(int x=1; x<=currentWeek; x++){
//                        int b = Integer.parseInt(week.get(j));
//                        Log.e(TAG, "B: "+b);
//                        if(week.get(j).contains("1"))
//                        if(Integer.parseInt(week.get(j)) <= x){
//                            if(week.get(j).equals(String.valueOf(x))){
//                                cell = rowData.createCell(y);
//                                cell.setCellValue("A");
//                                cell.setCellStyle(absentStyle);
//                                Log.d(TAG, "absent");
//                            }else {
//                                cell = rowData.createCell(y);
//                                cell.setCellValue("P");
//                                cell.setCellStyle(presentStyle);
////                                break;
//                            }
//                        }
//                        y++;
//                    }
//                }
            }


        }
    }

    /**
     * Store Excel Workbook in external storage
     *
     * @param context  - application context
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    private static boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;

//        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName+".xlsx");
        File[] root = new File[0];
        String dir="";
        root = context.getExternalMediaDirs();

        for (File value : root) {

            if (value.getName().contains(context.getPackageName())) {
                dir = value.getAbsolutePath();
            }

        }

//        File file = new File(dir+"/"+fileName+".xlsx");
        File file = new File("/storage/emulated/0/Download/"+fileName+".xlsx");
        FileOutputStream fileOutputStream = null;


        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file " + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return isSuccess;
    }

}
