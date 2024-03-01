package it.gov.pagopa.apiconfig.cache.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class JsonToXls {

    private static Font font = null;
    private static CellStyle cellStyle = null;

    private static Font getFont(Workbook workbook){
        if(font == null) {
            font = workbook.createFont();
            font.setBold(true);
        }
        return font;
    }

    private static CellStyle getHeaderStyle(Workbook workbook){
        if(cellStyle == null) {
            cellStyle = workbook.createCellStyle();
            cellStyle.setFont(getFont(workbook));
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return cellStyle;
    }

    private static String[] ignoredFields;

    private static int addObjectHeaders(Row headerRow,String prefix,Field field,int colNum){
        List<Field> fields = Arrays.stream(field.getType().getDeclaredFields()).toList();
        for (Field childField : fields) {
            if(childField.getType().isEnum()){
                Cell cell = headerRow.createCell(colNum++);
                cell.setCellStyle(getHeaderStyle(null));
                cell.setCellValue(prefix+"."+childField.getName());
            }else if(childField.getType().getName().startsWith("it.gov.pagopa.apiconfig.cache.model")){
                colNum = addObjectHeaders(headerRow,prefix+"."+childField.getName(),childField,colNum);
            }else{
                Cell cell = headerRow.createCell(colNum++);
                cell.setCellStyle(getHeaderStyle(null));
                cell.setCellValue(prefix+"."+childField.getName());
            }
        }
        return colNum;
    }
    private static void createHeader(Sheet sheet,AtomicInteger rowNum,Map<String, Object> keyMap){

        Optional<String> first = keyMap.keySet().stream().findFirst();
        if(first.isPresent()){
            Object o = keyMap.get(first.get());
            if(!o.getClass().equals(String.class)){
                Row headerRow = sheet.createRow(rowNum.getAndIncrement());

                Integer colNum = 0;
                List<Field> fields = Arrays.stream(o.getClass().getDeclaredFields()).toList();
                Cell cellid = headerRow.createCell(colNum++);
                cellid.setCellStyle(getHeaderStyle(sheet.getWorkbook()));
                cellid.setCellValue("identifier");
                for (Field field : fields) {
                    if(field.getType().getName().startsWith("it.gov.pagopa.apiconfig.cache.model")){
                        colNum = addObjectHeaders(headerRow,field.getName(),field,colNum);
                    }else{
                        Cell cell = headerRow.createCell(colNum++);
                        cell.setCellStyle(getHeaderStyle(sheet.getWorkbook()));
                        cell.setCellValue(field.getName());
                    }
                }
            }
        }

    }

    private static int addNullObjectToRow(Row row,Field field,int colNum){

        if(!field.getType().getName().startsWith("it.gov.pagopa.apiconfig.cache.model")){
            Cell cell = row.createCell(colNum++);
            cell.setCellValue("NULL");
            return colNum;
        }

        List<Field> fields = Arrays.stream(field.getType().getDeclaredFields()).toList();
        for (Field childField : fields) {
            if(childField.getType().isEnum()){
                Cell cell = row.createCell(colNum++);
                cell.setCellValue("NULL");
            }else if(childField.getType().getName().startsWith("it.gov.pagopa.apiconfig.cache.model")){
                colNum = addNullObjectToRow(row,childField,colNum);
            }else{
                Cell cell = row.createCell(colNum++);
                cell.setCellValue("NULL");
            }
        }
        return colNum;
    }
    private static Integer objectToCells(boolean maskPasswords,Row dataRow,Integer colNum,Object o,Field field){
        if(field != null && o == null){
            return addNullObjectToRow(dataRow,field,colNum);
        }
        String aClass = o.getClass().getName();
        if(!aClass.startsWith("it.gov.pagopa.apiconfig.cache.model") || o.getClass().isEnum()){
            Cell cellx = dataRow.createCell(colNum++);
            if(field!=null){
                if(maskPasswords && "password".equals(field.getName()) && !("PLACEHOLDER".equals(o.toString()))){
                    cellx.setCellValue("********");
                }else if("informativa".equals(field.getName())){
                    cellx.setCellValue("*informativa*");
                } else{
                    cellx.setCellValue(o.toString());
                }
            } else{
                cellx.setCellValue(o.toString());
            }
        }else{
            Field[] declaredFields = o.getClass().getDeclaredFields();
            for (Field f : declaredFields) {
                Method declaredMethod = null;
                try {
                    declaredMethod = o.getClass().getDeclaredMethod("get" + StringUtils.capitalize(f.getName()));
                    Object o1 = ReflectionUtils.invokeMethod(declaredMethod, o);
                    colNum = objectToCells(maskPasswords, dataRow, colNum, o1, f);
                } catch (NoSuchMethodException e) {
                    log.warn("NoSuchMethodException {}", f.getName());
                } catch (IllegalArgumentException e) {
                    log.warn("IllegalArgumentException {}", f.getName());
                }
            }
        }
        return colNum;

    }
    public static byte[] convert(Map<String,Object> cache,boolean maskPasswords) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet infoSheet = workbook.createSheet("Info");
            AtomicInteger infoRowNum = new AtomicInteger();

            Row row = infoSheet.createRow(infoRowNum.getAndIncrement());
            Cell cellid = row.createCell(0);
            cellid.setCellValue("identifier");
            cellid.setCellStyle(getHeaderStyle(workbook));
            Cell cellvalue = row.createCell(1);
            cellvalue.setCellValue("value");
            cellvalue.setCellStyle(getHeaderStyle(workbook));

//            Map<String,Object> cache2 = new HashMap<>();
//            Map<String,Object> channels = (Map<String,Object>)cache.get("channels");
//
//            Map<String,Object> channels2 = new HashMap<String,Object>();
//            channels2.put("60000000001_03_ONUS",channels.get("60000000001_03_ONUS"));
//            cache2.put("channels",channels2);

            List<String> sortedKeys = cache.keySet().stream().sorted().toList();
            sortedKeys.forEach((key)->{
                Object keyMap = cache.get(key);
                if(keyMap instanceof Map){
                    Sheet sheet = workbook.createSheet(key);
                    AtomicInteger rowNum = new AtomicInteger();
                    AtomicInteger colNum = new AtomicInteger();
                    createHeader(sheet,rowNum,(Map<String, Object>)keyMap);
                    Set<String> cacheItemKeys = ((Map<String, Object>) keyMap).keySet();
                    cacheItemKeys.forEach(k->{
                        Row dataRow = sheet.createRow(rowNum.getAndIncrement());
                        Cell cellx = dataRow.createCell(0);
                        cellx.setCellValue(k);
                        Object oo = ((Map<?, ?>) keyMap).get(k);
                        objectToCells(maskPasswords,dataRow,1,oo,null);
                    });
                }else{
                    Row rowX = infoSheet.createRow(infoRowNum.getAndIncrement());
                    Cell cellidX = rowX.createCell(0);
                    cellidX.setCellValue(key);
                    Cell cellvalueX = rowX.createCell(1);
                    cellvalueX.setCellValue(keyMap.toString());
                }
            });

            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            return fileOut.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
