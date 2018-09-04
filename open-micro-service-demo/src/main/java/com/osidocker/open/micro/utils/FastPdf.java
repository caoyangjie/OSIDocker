/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.utils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于11:23 2017/7/13
 * @修改说明：
 * @修改日期： 修改于11:23 2017/7/13
 * @版本号： V1.0.0
 */
public class FastPdf {
    private static Logger logger = LoggerFactory.getLogger(FastPdf.class);
    /**
     * 创建PDF文件
     * @param templateName  模版名称
     * @param templatePath  模版目录
     * @param fileName      pdf文件名
     * @param waterMark     水印
     * @param paramMap      模版替换数据
     * @throws Exception
     */
    public static void contractHandler(String templateName,String templatePath,String fileName,String waterMark,Object paramMap) throws Exception{
        waterMark="";
        if(new File(templatePath+"/"+fileName+".pdf").exists()){
            logger.info("文件已经存在,不再执行生成动作!");
            System.out.println("文件已经存在,不再执行生成动作!");
            return;//文件已经存在,不执行生成动作!
        }
        String localHtmlUrl = templatePath +"/"+ fileName + ".html";
        logger.info("localHtmlUrl={}",localHtmlUrl);
        String localPdfPath = templatePath + "/";
        logger.info("localPdfPath={}",localPdfPath);
        // 判断本地路径是否存在如果不存在则创建
        File localFile = new File(localPdfPath);
        if (!localFile.exists()) {
            localFile.mkdirs();
        }
        String localPdfUrl = localFile + "/" + fileName + "_water.pdf";
        String waterPdfUrl = localFile + "/" + fileName + ".pdf";
        templateName=templateName+".ftl";
        htmHandler(templatePath, templateName, localHtmlUrl, paramMap);// 生成html合同
        pdfHandler(localHtmlUrl, localPdfUrl,templatePath);// 根据html合同生成pdf合同
        setWatermark(new BufferedOutputStream(new FileOutputStream(new File(waterPdfUrl))),templatePath,localPdfUrl,waterMark);
        deleteFile(localHtmlUrl);// 删除html格式合同
        deleteFile(localPdfUrl);// 删除未加水印的PDF文件

        System.out.println("PDF生成成功");
    }

    /**
     * 生成html格式合同
     */
    public static void htmHandler(String templatePath, String templateName,
                                   String htmUrl, Object paramMap) throws Exception {
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(templatePath));

        Template template = cfg.getTemplate(templateName);
        template.setEncoding("UTF-8");

        File outHtmFile = new File(htmUrl);

        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outHtmFile),"UTF-8"));
        template.process(paramMap, out);
        out.close();
    }

    /**
     * 生成pdf格式合同
     */
    public static void pdfHandler(String htmUrl, String pdfUrl,String templatePath)
            throws DocumentException, IOException, DocumentException {
        File htmFile = new File(htmUrl);
        File pdfFile = new File(pdfUrl);
        String url = htmFile.toURI().toURL().toString();
        OutputStream os = new FileOutputStream(pdfFile);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
        renderer.getSharedContext().setDPI(1920*2);
        ITextFontResolver fontResolver = renderer
                .getFontResolver();
        // 解决中文支持问题
        fontResolver.addFont(templatePath+"simsun.ttc",
                BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.getSharedContext().setBaseURL("file:/"+templatePath);
        renderer.layout();
        renderer.createPDF(os);
        os.close();
    }

    public static void setWatermark(BufferedOutputStream bos,String templatePath, String input,
                                    String waterMarkName) throws DocumentException,
            IOException, DocumentException {

        PdfReader reader = new PdfReader(input);
        PdfStamper stamper = new PdfStamper(reader, bos);
        int total = reader.getNumberOfPages() + 1;
        BaseFont base = BaseFont.createFont(templatePath+"simsun.ttc,1", "Identity-H", true);// 使用系统字体
        PdfGState gs = new PdfGState();
        for (int i = 1; i < total; i++) {
            final PdfContentByte content = stamper.getOverContent(i);// 在内容上方加水印
//            content = stamper.getUnderContent(i);//在内容下方加水印
            gs.setFillOpacity(0.5f);
            content.setGState(gs);
            content.beginText();
            content.setColorFill(Color.cyan);
            if(waterMarkName.length()>10){
                content.setFontAndSize(base, 30);
            }else{
                content.setFontAndSize(base, 40);
            }
            content.setTextMatrix(70, 200);
            Stream.iterate(1,x->x+1).limit(1).forEach(index->{
                int xPoint = (index % 3)+1;
//                content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, 300,500*xPoint, 55);
                content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, 600,500*xPoint, 55);
//                content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, 900,500*xPoint, 55);
            });
            content.setColorFill(Color.BLACK);
            content.setFontAndSize(base, 20);
            content.showTextAligned(Element.ALIGN_CENTER,
                     waterMarkName + "", 600, 10, 0);
            content.endText();
        }
        stamper.close();
    }

    /**
     * 删除文件
     */
    private static void deleteFile(String fileUrl) {
        File file = new File(fileUrl);
        file.delete();
    }

    public static void main(String[] args) throws Exception {
//        String templateName = "201";
//        String templateName = "index";
        String templateName = "report";
        HashMap paramMap = new HashMap<>();
        paramMap.put("ZJHKZH", "271003********279975");
        paramMap.put("KYYE", "79244.95");
        paramMap.put("LXFS", "配置web.xml中LXFS属性，例如(张小凡，123,4567,8909)");
        paramMap.put("KHWD", "2****1");
        paramMap.put("CSKSRQ", "2016年10月31日00时00分");
        paramMap.put("KSRQ", "2017-03-14");
        paramMap.put("YE","94444.95");
        paramMap.put("KHZH","271**********07279975");
        paramMap.put("AH", "(2015)****字第0***0号");
        paramMap.put("CKH", "(2017)法YH****9控字第*号");
        paramMap.put("YDJAH", "(2015)***执字第00020号");
        paramMap.put("KZCS", "01");
        paramMap.put("XM", "張三豐");
        paramMap.put("FYMC", "****人民法院");
        paramMap.put("JSRQ", "2017-06-14");
        paramMap.put("KZZT", "1");
        paramMap.put("SE", "100");
        paramMap.put("LCZH", "987234234");
        paramMap.put("DATE", "2017年03月24日09时39分");
        paramMap.put("CKWH", "(2015)*****字第0**20-1**0号裁定书");
        paramMap.put("SKSE", "100");
        paramMap.put("CSJSRQ", "2016年10月31日 00时00分");
        paramMap.put("username","caoyangjie");
//        paramMap.put("bootstrap",DOMAIN+"css/bootstrap.min.css");
//        paramMap.put("style",DOMAIN+"css/style.css");
        String TEMPLATE = "F:\\platform\\workspace\\yuancredit\\yanshu\\trunk\\code\\yuancredit-parent\\yuancredit-laboratory\\src\\test\\resource/templates/";//模板存储路径
        String json = new String(Files.readAllBytes(Paths.get(TEMPLATE+"\\data.txt")), StandardCharsets.UTF_8);

        paramMap = JsonTools.jsonStr2Map(json);
        contractHandler(templateName,TEMPLATE,"pdfDemo","测试商户001", paramMap);
    }
}
