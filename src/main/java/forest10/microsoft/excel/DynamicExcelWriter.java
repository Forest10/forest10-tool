package forest10.microsoft.excel;

import lombok.Data;
import com.google.common.base.Charsets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 描述:
 * support Dynamic Head and Content
 * 但是此工具为集合完数据之后才导出.不支持流式导出!!!!
 *
 * @author Forest10
 * @date 2018/03/29 16:05
 */
@Data
public class DynamicExcelWriter {
    /***
     * 导出的文件名字
     */
    private String fileName;


    /****should be LinkedList****/
    private LinkedList<String> excelHeadList;


    /****should be LinkedList****/
    private LinkedList<LinkedList<String>> excelContentList;

    public DynamicExcelWriter(LinkedList<String> excelHeadList, LinkedList<LinkedList<String>> excelContentList) {
        this.excelHeadList = excelHeadList;
        this.excelContentList = excelContentList;
    }

    /***导出表头**/
    private void exportHead(CSVPrinter csvPrinter) throws IOException {
        //Assert.check(CollectionUtils.isNotEmpty(excelHeadList), "excel表头不能为空!");
        csvPrinter.printRecord(CollectionUtils.isEmpty(excelHeadList) ? new LinkedList<>() : excelHeadList);
        csvPrinter.flush();
    }


    /***导出内容**/
    private void exportContent(CSVPrinter csvPrinter) throws IOException {
        //Assert.check(CollectionUtils.isNotEmpty(excelHeadList), "excel内容不能为空!");
        if (CollectionUtils.isEmpty(excelContentList)) {
            excelContentList = new LinkedList<>();
        }
        for (List<String> list : excelContentList) {
            csvPrinter.printRecord(CollectionUtils.isEmpty(list) ? new LinkedList<>() : list);
            //flush 每行
            csvPrinter.flush();
        }
        //flush所有行
        csvPrinter.flush();
    }

    /**
     * 导出Excel
     *
     * @param response response
     * @throws IOException
     */
    public void doExport(HttpServletResponse response) throws IOException {
        OutputStream os = null;
        CSVPrinter csvPrinter = null;
        try {
            os = response.getOutputStream();
            csvPrinter = new CSVPrinter(new OutputStreamWriter(os, Charsets.UTF_8), CSVFormat.EXCEL);
            //设置下载头(csv)
            responseSetProperties(response);
            //设置 BOM头
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            //导出 excel表头
            exportHead(csvPrinter);
            //导出实际数据
            exportContent(csvPrinter);
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(csvPrinter);
        }
    }


    private void responseSetProperties(HttpServletResponse response) throws UnsupportedEncodingException {
        // 设置文件后缀
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        fileName = StringUtils.isBlank(fileName) ? StringUtils.EMPTY : fileName;
        String fn = fileName + sdf.format(new Date()) + ".csv";
        // 读取字符编码
        String utf = "UTF-8";
        // 设置响应
        response.setContentType("text/csv;charset=utf-8");
        response.setCharacterEncoding(utf);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fn, utf));
    }


}