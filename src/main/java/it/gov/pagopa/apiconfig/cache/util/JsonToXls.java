package it.gov.pagopa.apiconfig.cache.util;

import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ReflectionUtils;

import javax.el.MethodNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class JsonToXls {
    private Workbook workbook;
    List<String> headers = new ArrayList<>();
    private boolean maskPasswords = true;

    public JsonToXls(boolean maskPasswords){
        workbook = new XSSFWorkbook();
        font = workbook.createFont();
        font.setBold(true);

        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        this.maskPasswords = maskPasswords;
    }


    private Font font = null;
    private CellStyle cellStyle = null;

    private Font getFont(){
        return font;
    }

    private CellStyle getHeaderStyle(){
        return cellStyle;
    }

    private int addObjectHeaders(Row headerRow,String prefix,Field field,int colNum){
        List<Field> fields = Arrays.stream(field.getType().getDeclaredFields()).toList();
        for (Field childField : fields) {
            if(childField.getType().isEnum()){
                Cell cell = headerRow.createCell(colNum++);
                cell.setCellStyle(getHeaderStyle());
                cell.setCellValue(prefix+"."+childField.getName());
                headers.add(prefix+"."+childField.getName());
            }else if(childField.getType().getName().startsWith("it.gov.pagopa.apiconfig.cache.model")){
                colNum = addObjectHeaders(headerRow,prefix+"."+childField.getName(),childField,colNum);
            }else{
                Cell cell = headerRow.createCell(colNum++);
                cell.setCellStyle(getHeaderStyle());
                cell.setCellValue(prefix+"."+childField.getName());
                headers.add(prefix+"."+childField.getName());
            }
        }
        return colNum;
    }
    private void createHeader(Sheet sheet,AtomicInteger rowNum,Map<String, Object> keyMap){

        Optional<String> first = keyMap.keySet().stream().findFirst();
        if(first.isPresent()){
            Object o = keyMap.get(first.get());
            if(!o.getClass().equals(String.class)){
                Row headerRow = sheet.createRow(rowNum.getAndIncrement());

                Integer colNum = 0;
                List<Field> fields = Arrays.stream(o.getClass().getDeclaredFields()).toList();
                Cell cellid = headerRow.createCell(colNum++);
                cellid.setCellStyle(getHeaderStyle());
                cellid.setCellValue("identifier");
                for (Field field : fields) {
                    if(field.getType().getName().startsWith("it.gov.pagopa.apiconfig.cache.model")){
                        colNum = addObjectHeaders(headerRow,field.getName(),field,colNum);
                    }else{
                        headers.add(field.getName());
                        Cell cell = headerRow.createCell(colNum++);
                        cell.setCellStyle(getHeaderStyle());
                        cell.setCellValue(field.getName());
                    }
                }
            }else{
                createBaseHeader(sheet,rowNum);
            }
        } else {
            Row headerRow = sheet.createRow(rowNum.getAndIncrement());
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("no values");
        }

    }

    private int addNullObjectToRow(Row row,Field field,int colNum){

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
    private void createBaseHeader(Sheet sheet,AtomicInteger rownNum){
        Row row = sheet.createRow(rownNum.getAndIncrement());
        Cell cellid = row.createCell(0);
        cellid.setCellValue("identifier");
        cellid.setCellStyle(getHeaderStyle());
        Cell cellvalue = row.createCell(1);
        cellvalue.setCellValue("value");
        cellvalue.setCellStyle(getHeaderStyle());
    }

    private int innerValues(String h,Row dataRow,Object oo,int colcount) throws InvocationTargetException, IllegalAccessException {
        if(h.contains(".")){
            String[] split = h.split("\\.");
            Method method = ReflectionUtils.findMethod(oo.getClass(), "get" + StringUtils.capitalize(split[0]));
            if(method==null){
                throw new MethodNotFoundException("no method found "+"get" + StringUtils.capitalize(split[0]));
            }
            Object invoke = method.invoke(oo);
            return innerValues(split[1],dataRow,invoke,colcount);
        }else{
            Cell cellx = dataRow.createCell(colcount++);
            if(h.equals("password") && maskPasswords){
                cellx.setCellValue("********");
            }else if(h.equals("informativa")){
                cellx.setCellValue("*informativa*");
            }else if(oo == null){
                cellx.setCellValue("NULL");
            }else{
                Method method = ReflectionUtils.findMethod(oo.getClass(), "get" + StringUtils.capitalize(h));
                if(method==null){
                    throw new MethodNotFoundException("no method found "+"get" + StringUtils.capitalize(h));
                }
                Object invoke = method.invoke(oo);
                cellx.setCellValue(ObjectUtils.firstNonNull(invoke,"NULL").toString());
            }
            return colcount;

        }
    }
    private void values(Row dataRow,Object oo,int colcount) throws InvocationTargetException, IllegalAccessException {
        for(String h : headers){
            colcount = innerValues(h,dataRow,oo,colcount);
        }
    }

    public byte[] convert(Map<String,Object> cache) {
        log.debug("Creating xlsx");
        try {

            Sheet infoSheet = workbook.createSheet("Info");
            AtomicInteger infoRowNum = new AtomicInteger();
            createBaseHeader(infoSheet,infoRowNum);

            List<String> sortedKeys = cache.keySet().stream().sorted().toList();
            sortedKeys.forEach((key)->{
                log.debug("Adding {} page",key);
                headers = new ArrayList<>();
                Object keyMap = cache.get(key);
                if(keyMap instanceof Map){
                    Optional<String> first = ((Map<String,Object>)keyMap).keySet().stream().findFirst();
                    if(first.isPresent()){
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
                            try {
                                values(dataRow,oo,1);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        log.warn(key+" ignored,no values");
                    }

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
            throw new AppException(AppError.INTERNAL_SERVER_ERROR,e);
        }
    }
}
