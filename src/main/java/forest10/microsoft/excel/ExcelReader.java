package forest10.microsoft.excel;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述:
 * Excel导入通用类
 *
 * @author Forest10
 * @date 2018/03/29 15:56
 */

public class ExcelReader {

    private POIFSFileSystem fs;
    // xls Excel
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private HSSFRow row;

    // xlsx Excel
    private XSSFWorkbook xFwb;
    private XSSFSheet xFSheet;
    private XSSFRow xFRow;


    private int sheetSize = 0;

    /**
     * 设置sheet页码
     *
     * @param sheetSize index(start 0)
     */
    public void setSheetSize(int sheetSize) {
        this.sheetSize = sheetSize;
    }


    /**
     * 自我关闭输入流
     *
     * @param excelPath   Excel的位置
     * @param inputStream WARNING:inputStream can not be repeatable!!!
     * @return Excel的表头
     * @throws IOException
     */
    public LinkedList<String> readExcelTitleSafeLy(String excelPath, InputStream inputStream) throws IOException {
        LinkedList<String> title = new LinkedList<>();
        try {
            title = readExcelTitle(excelPath, inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return title;
    }

    /**
     * 自我关闭输入流
     *
     * @param excelPath   Excel的位置
     * @param inputStream WARNING:inputStream can not be repeatable!!!
     * @return Excel的内容
     * @throws IOException
     */
    public LinkedHashMap<Integer, LinkedList<String>> readExcelContentSafeLy(String excelPath, InputStream inputStream) throws IOException {
        LinkedHashMap<Integer, LinkedList<String>> content = new LinkedHashMap<>();
        try {
            content = readExcelContent(excelPath, inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return content;
    }


    /**
     * 读取 xls Excel表格表头的内容
     *
     * @param excelPath
     * @param inputStream WARNING:inputStream can not be repeatable!!! AND this method can
     *                    not be close stream;
     * @return String 表头内容的数组
     */
    public LinkedList<String> readExcelTitle(String excelPath, InputStream inputStream) throws IOException {
        boolean isXLS = setPublicAndJudge(excelPath, inputStream);
        LinkedList<String> title = new LinkedList<>();
        // 标题总列数
        int colNum;
        String cellFormatValue;
        if (isXLS) {
            colNum = row.getPhysicalNumberOfCells();
            for (int i = 0; i < colNum; i++) {
                cellFormatValue = getCellFormatValue(row.getCell(i));
                // 如果是空表头就直接 空+ index
                if (StringUtils.isEmpty(cellFormatValue)) {
                    title.add("空" + i);
                } else {
                    title.add(cellFormatValue);
                }
            }
        } else {
            // 标题总列数
            colNum = xFRow.getPhysicalNumberOfCells();
            for (int i = 0; i < colNum; i++) {
                cellFormatValue = getCellFormatValue(xFRow.getCell((short) i));
                // 如果是空表头就直接 空+ index
                if (StringUtils.isEmpty(cellFormatValue)) {
                    title.add("空" + i);
                } else {
                    title.add(cellFormatValue);
                }
            }
        }


        return title;
    }

    /**
     * 设置公共属性和判断后缀
     *
     * @param excelPath
     * @param inputStream
     * @return boolean
     * @throws IOException
     */
    private boolean setPublicAndJudge(String excelPath, InputStream inputStream) throws IOException {
        boolean isXLS = false;
        if (StringUtils.endsWithIgnoreCase(excelPath, "xls")) {
            fs = new POIFSFileSystem(inputStream);
            wb = new HSSFWorkbook(fs);
            sheet = wb.getSheetAt(sheetSize);
            // 得到表头
            row = sheet.getRow(0);
            isXLS = true;
        } else if (StringUtils.endsWithIgnoreCase(excelPath, "xlsx")) {
            xFwb = new XSSFWorkbook(inputStream);
            xFSheet = xFwb.getSheetAt(0);
            // 得到表头
            xFRow = xFSheet.getRow(0);
        } else {
            throw new RuntimeException("未能识别的后缀!");
        }
        return isXLS;
    }


    /**
     * 读取 Excel 的实际数据,从第二行开始
     *
     * @param excelPath
     * @param inputStream WARNING:inputStream can not be repeatable!!!
     * @return Excel 的实际数据,从第二行开始(LinkedHashMap)
     * @throws IOException
     */
    public LinkedHashMap<Integer, LinkedList<String>> readExcelContent(String excelPath, InputStream inputStream)
            throws IOException {
        boolean isXLS = setPublicAndJudge(excelPath, inputStream);
        // 得到总行数
        int rowNum;
        // 总列数
        int colNum;
        // data
        LinkedList<String> rowCells;
        // 第几行, data
        LinkedHashMap<Integer, LinkedList<String>> content = new LinkedHashMap<>();
        if (isXLS) {
            // 得到总行数
            rowNum = sheet.getLastRowNum();
            // 得到总列数
            colNum = row.getPhysicalNumberOfCells();
            // 正文内容应该从第二行开始,第一行为表头的标题
            for (int i = 1; i <= rowNum; i++) {
                rowCells = new LinkedList<>();
                row = sheet.getRow(i);
                int j = 0;
                while (j < colNum) {
                    if (Objects.nonNull(xFRow)
                            && StringUtils.isNotBlank(getCellFormatValue(row.getCell((short) j)).trim())) {
                        rowCells.add(getCellFormatValue(row.getCell((short) j)).trim());
                    } else {
                        rowCells.add(StringUtils.EMPTY);
                    }
                    j++;
                }
                // 如果第一列是空的就不加入
                if (StringUtils.isEmpty(rowCells.getFirst())) {
                    continue;
                }
                content.put(i - 1, rowCells);
            }
        } else {
            // 得到总行数
            rowNum = xFSheet.getLastRowNum();
            // 总列数
            colNum = xFRow.getPhysicalNumberOfCells();
            // 正文内容应该从第二行开始,第一行为表头的标题
            for (int i = 1; i <= rowNum; i++) {
                rowCells = new LinkedList<>();
                xFRow = xFSheet.getRow(i);
                int j = 0;
                while (j < colNum) {
                    if (Objects.nonNull(xFRow)
                            && StringUtils.isNotBlank((getCellFormatValue(xFRow.getCell((short) j)).trim()))) {
                        rowCells.add(getCellFormatValue(xFRow.getCell((short) j)).trim());
                    } else {
                        rowCells.add(StringUtils.EMPTY);
                    }
                    j++;
                }
                // 如果第一列是空的就不加入
                if (StringUtils.isEmpty(rowCells.getFirst())) {
                    continue;
                }
                content.put(i - 1, rowCells);
            }
        }


        return content;
    }


    /**
     * 根据HSSFCell类型设置数据 处理2003的xls Excel
     *
     * @param cell
     * @return cell value
     */
    private String getCellFormatValue(HSSFCell cell) {


        if (Objects.isNull(cell)) {
            return StringUtils.EMPTY;
        }
        String cellValue;
        // 判断当前Cell的Type
        switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
            case HSSFCell.CELL_TYPE_NUMERIC:
                cellValue = handleNUMERICCellValue(cell);
                break;
            case HSSFCell.CELL_TYPE_FORMULA: {
                cellValue = handleDate(cell);
                break;
            }
            // 如果当前Cell的Type为STRIN
            case HSSFCell.CELL_TYPE_STRING:
                // 取得当前的Cell字符串
                cellValue = cell.getRichStringCellValue().getString();
                break;
            // 默认的Cell值
            default:
                cellValue = StringUtils.EMPTY;
        }
        return cellValue;
    }

    /**
     * @param cell
     * @return cell value
     */
    private String handleNUMERICCellValue(Cell cell) {
        String cellValue;
        // 当前值
        Object inputVal;
        if (HSSFDateUtil.isCellDateFormatted(cell)) {
            SimpleDateFormat sdf;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            }
            Date date = cell.getDateCellValue();
            cellValue = sdf.format(date);
        } else {
            double doubleVal = cell.getNumericCellValue();
            long longVal = Math.round(cell.getNumericCellValue());
            if (Double.parseDouble(longVal + ".0") == doubleVal) {
                inputVal = longVal;
            } else {
                inputVal = doubleVal;
            }
            cellValue = String.valueOf(inputVal);
        }
        return cellValue;
    }

    /**
     * 处理日期
     *
     * @param cell
     * @return date -> String
     */
    private String handleDate(Cell cell) {
        String cellValue;
        // 判断当前的cell是否为Date
        if (HSSFDateUtil.isCellDateFormatted(cell)) {
            SimpleDateFormat sdf;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            Date date = cell.getDateCellValue();
            cellValue = sdf.format(date);
        }
        // 如果是纯数字
        else {
            DecimalFormat df = new DecimalFormat("#");
            // 取得当前Cell的数值
            cellValue = String.valueOf(df.format(cell.getNumericCellValue()));
        }
        return cellValue;
    }


    /**
     * 根据XSSFCell类型设置数据
     *
     * @param cell
     * @return cell value
     */
    private String getCellFormatValue(XSSFCell cell) {


        if (Objects.isNull(cell)) {
            return StringUtils.EMPTY;
        }
        String cellValue;
        // 判断当前Cell的Type
        switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
            case XSSFCell.CELL_TYPE_NUMERIC:
                cellValue = handleNUMERICCellValue(cell);
                break;
            // 公式型
            case XSSFCell.CELL_TYPE_FORMULA: {
                cellValue = handleDate(cell);
                break;
            }
            // 如果当前Cell的Type为STRIN
            case HSSFCell.CELL_TYPE_STRING:
                // 取得当前的Cell字符串
                cellValue = cell.getRichStringCellValue().getString();
                break;
            // 默认的Cell值
            default:
                cellValue = StringUtils.EMPTY;
        }
        return cellValue;
    }
}

