package com.osidocker.open.micro.utils;

import com.osidocker.open.micro.annotation.MapperCell;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class FastExcel {
    /**
     * 时日类型的数据默认格式化方式
     */
    private              DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private       int      startRow;
    private       String   sheetName;
    private final String   excelFilePath;
    private final Workbook workbook;

    /**
     * 构造方法，传入需要操作的excel文件路径
     *
     * @param excelFilePath 需要操作的excel文件的路径
     * @throws IOException            IO流异常
     * @throws InvalidFormatException 非法的格式异常
     */
    public FastExcel(String excelFilePath) throws IOException, InvalidFormatException {
        this.startRow = 0;
        this.sheetName = "Sheet1";
        this.excelFilePath = excelFilePath;
        this.workbook = createWorkbook();
    }

    /**
     * 通过数据流操作excel，仅用于读取数据
     *
     * @param inputStream excel数据流
     * @throws IOException            IO流异常
     * @throws InvalidFormatException 非法的格式异常
     */
    public FastExcel(InputStream inputStream) throws IOException, InvalidFormatException {
        this.startRow = 0;
        this.sheetName = "Sheet1";
        this.excelFilePath = "";
        this.workbook = WorkbookFactory.create(inputStream);
    }

    /**
     * 通过数据流操作excel
     *
     * @param inputStream excel数据流
     * @param outFilePath 输出的excel文件路径
     * @throws IOException            IO流异常
     * @throws InvalidFormatException 非法的格式异常
     */
    public FastExcel(InputStream inputStream, String outFilePath) throws IOException, InvalidFormatException {
        this.startRow = 0;
        this.sheetName = "Sheet1";
        this.excelFilePath = outFilePath;
        this.workbook = WorkbookFactory.create(inputStream);
    }

    /**
     * 开始读取的行数，这里指的是标题内容行的行数，不是数据开始的那行
     *
     * @param startRow 开始行数
     */
    public void setStartRow(int startRow) {
        if (startRow < 1) {
            throw new RuntimeException("最小为1");
        }
        this.startRow = --startRow;
    }

    /**
     * 设置需要读取的sheet名字，不设置默认的名字是Sheet1，也就是excel默认给的名字，所以如果文件没有自已修改，这个方法也就不用调了
     *
     * @param sheetName 需要读取的Sheet名字
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * 设置时间数据格式
     *
     * @param format 格式
     */
    public void setFormat(String format) {
        this.format = new SimpleDateFormat(format);
    }

    /**
     * 解析读取excel文件
     *
     * @param clazz 对应的映射类型
     * @param <T>   泛型
     * @return 读取结果
     */
    public <T> List<T> parse(Class<T> clazz) {
        List<T> resultList = null;
        try {
            Sheet sheet = workbook.getSheet(this.sheetName);
            if (null != sheet) {
                resultList = new ArrayList<T>(sheet.getLastRowNum() - 1);
                Row row = sheet.getRow(this.startRow);

                Map<String, Field> fieldMap = new HashMap<String, Field>();
                Map<String, String> titleMap = new HashMap<String, String>();

                Field[] fields = clazz.getDeclaredFields();
                //这里开始处理映射类型里的注解
                for (Field field : fields) {
                    if (field.isAnnotationPresent(MapperCell.class)) {
                        MapperCell mapperCell = field.getAnnotation(MapperCell.class);
                        fieldMap.put(mapperCell.cellName(), field);
                    }
                }

                for (Cell title : row) {
                    CellReference cellRef = new CellReference(title);
                    titleMap.put(cellRef.getCellRefParts()[2], title.getRichStringCellValue().getString());
                }

                for (int i = this.startRow + 1; i <= sheet.getLastRowNum(); i++) {
                    T t = clazz.newInstance();
                    Row dataRow = sheet.getRow(i);
                    for (Cell data : dataRow) {
                        CellReference cellRef = new CellReference(data);
                        String cellTag = cellRef.getCellRefParts()[2];
                        String name = titleMap.get(cellTag);
                        Field field = fieldMap.get(name);
                        if (null != field) {
                            field.setAccessible(true);
                            getCellValue(data, t, field);
                        }
                    }
                    resultList.add(t);
                }
            } else {
                throw new RuntimeException("sheetName:" + this.sheetName + " is not exist");
            }
        } catch (InstantiationException e) {
            log.error("初始化异常", e);
        } catch (IllegalAccessException e) {
            log.error("初始化异常", e);
        } catch (ParseException e) {
            log.error("时间格式化异常:{}", e);
        } catch (Exception e) {
            log.error("其他异常", e);
        }
        return resultList;
    }


    private void getCellValue(Cell cell, Object o, Field field) throws IllegalAccessException, ParseException {
        log.debug("cell:{}, field:{}, type:{}", cell.getCellType(), field.getName(), field.getType().getName());
        
        switch (cell.getCellType()) {
            case 3:
                break;
            case 4:
                field.setBoolean(o, cell.getBooleanCellValue());
                break;
            case 5:
                field.setByte(o, cell.getErrorCellValue());
                break;
            case 2:
                field.set(o, cell.getCellFormula());
                break;
            case 0:
                if (DateUtil.isCellDateFormatted(cell)) {
                    if (field.getType().getName().equals(Date.class.getName())) {
                        field.set(o, cell.getDateCellValue());
                    } else {
                        field.set(o, format.format(cell.getDateCellValue()));
                    }
                } else {
                    if (field.getType().isAssignableFrom(Integer.class) || field.getType().getName().equals("int")) {
                        field.setInt(o, (int) cell.getNumericCellValue());
                    } else if (field.getType().isAssignableFrom(Short.class) || field.getType().getName().equals("short")) {
                        field.setShort(o, (short) cell.getNumericCellValue());
                    } else if (field.getType().isAssignableFrom(Float.class) || field.getType().getName().equals("float")) {
                        field.setFloat(o, (float) cell.getNumericCellValue());
                    } else if (field.getType().isAssignableFrom(Byte.class) || field.getType().getName().equals("byte")) {
                        field.setByte(o, (byte) cell.getNumericCellValue());
                    } else if (field.getType().isAssignableFrom(Double.class) || field.getType().getName().equals("double")) {
                        field.setDouble(o, cell.getNumericCellValue());
                    } else if (field.getType().isAssignableFrom(String.class)) {
                        String s = String.valueOf(cell.getNumericCellValue());
                        if (s.contains("E")) {
                            s = s.trim();
                            BigDecimal bigDecimal = new BigDecimal(s);
                            s = bigDecimal.toPlainString();
                        }
                        //防止整数判定为浮点数
                        if (s.endsWith(".0")){
                            s = s.substring(0, s.indexOf(".0"));
                        }
                        field.set(o, s);
                    } else {
                        field.set(o, cell.getNumericCellValue());
                    }
                }
                break;
            case 1:
                if (field.getType().getName().equals(Date.class.getName())) {
                    field.set(o, format.parse(cell.getRichStringCellValue().getString()));
                } else {
                    field.set(o, cell.getRichStringCellValue().getString());
                }
                break;
            default:
                field.set(o, cell.getStringCellValue());
                break;
        }
    }

    private Workbook createWorkbook() throws IOException, InvalidFormatException {
        Workbook workbook;
        File file = new File(this.excelFilePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            log.warn("文件:{} 不存在！创建此文件！", this.excelFilePath);
            if (!file.createNewFile()) {
                throw new IOException("文件创建失败");
            }
            workbook = new XSSFWorkbook();
        } else {
            Files.deleteIfExists(file.toPath());
            workbook = createWorkbook();
        }
        return workbook;
    }

    /**
     * 将数据写入excel文件
     *
     * @param list 数据列表
     * @param <T>  泛型
     * @return 写入结果
     */
    public <T> boolean createExcel(List<T> list) {
        if (null == this.excelFilePath || "".equals(this.excelFilePath)){
            throw new NullPointerException("excelFilePath is null");
        }
        boolean result = false;
        FileOutputStream fileOutputStream = null;
        if (null != list && !list.isEmpty()) {
            T test = list.get(0);
            Map<String, Field> fieldMap = new HashMap<String, Field>();
            Map<Integer, String> titleMap = new TreeMap<Integer, String>();
            Map<String, Function> funcMap = new HashMap<>();
            Field[] fields = test.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MapperCell.class)) {
                    MapperCell mapperCell = field.getAnnotation(MapperCell.class);
                    fieldMap.put(mapperCell.cellName(), field);
                    titleMap.put(mapperCell.order(), mapperCell.cellName());
                    try {
                        funcMap.put(mapperCell.cellName(), (Function) mapperCell.convert().newInstance());
                    } catch (InstantiationException e) {
                    } catch (IllegalAccessException e) {
                    }
                }
            }
            try {
                Sheet sheet = workbook.createSheet(this.sheetName);
                Collection<String> values = titleMap.values();
                String[] s = new String[values.size()];
                values.toArray(s);
                //生成标题行
                Row titleRow = sheet.createRow(0);
                for (int i = 0; i < s.length; i++) {
                    Cell cell = titleRow.createCell(i);
                    cell.setCellValue(s[i]);
                }
                //生成数据行
                for (int i = 0, length = list.size(); i < length; i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < s.length; j++) {
                        Cell cell = row.createCell(j);
                        for (Map.Entry<String, Field> data : fieldMap.entrySet()) {
                            if (data.getKey().equals(s[j])) {
                                Field field = data.getValue();
                                field.setAccessible(true);
                                Function<Object,String> value = funcMap.get(data.getKey());
                                cell.setCellValue(value.apply(field.get(list.get(i))));
                                break;
                            }
                        }
                    }
                }
                File file = new File(this.excelFilePath);
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new IOException("文件创建失败");
                    }
                }
                fileOutputStream = new FileOutputStream(file);
                workbook.write(fileOutputStream);
                result = true;
            } catch (IOException e) {
                log.error("流异常", e);
            } catch (IllegalAccessException e) {
                log.error("反射异常", e);
            } catch (Exception e) {
                log.error("其他异常", e);
            } finally {
                if (null != fileOutputStream) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        log.error("关闭流异常", e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取指定单元格的值
     *
     * @param rowNumber  行数，从1开始
     * @param cellNumber 列数，从1开始
     * @return 该单元格的值
     */
    public String getCellValue(int rowNumber, int cellNumber) {
        String result;
        checkRowAndCell(rowNumber, cellNumber);
        Sheet sheet = this.workbook.getSheet(this.sheetName);
        Row row = sheet.getRow(--rowNumber);
        Cell cell = row.getCell(--cellNumber);
        switch (cell.getCellType()) {
            case 3:
                result = cell.getStringCellValue();
                break;
            case 4:
                result = String.valueOf(cell.getBooleanCellValue());
                break;
            case 5:
                result = String.valueOf(cell.getErrorCellValue());
                break;
            case 2:
                result = cell.getCellFormula();
                break;
            case 0:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = format.format(cell.getDateCellValue());
                } else {
                    result = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case 1:
                result = cell.getRichStringCellValue().getString();
                break;
            default:
                result = cell.getStringCellValue();
                break;
        }
        return result;
    }

    private void checkRowAndCell(int rowNumber, int cellNumber) {
        if (rowNumber < 1) {
            throw new RuntimeException("rowNumber less than 1");
        }
        if (cellNumber < 1) {
            throw new RuntimeException("cellNumber less than 1");
        }
    }
}
