package org.osidocker.open.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class Generator {
    private static final String configProPath = "F:\\cao_yj_workspace\\WCYC_Work\\zgw\\com.wcyc.zgw.web\\src\\main\\resources\\generator\\config.properties";
    private static String targetJavaProject;
    private static String targetModelPackage;
    private static String targetServicePackage;
    private static String targetBaseService;
    
    static{
        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(configProPath);
            if (fis != null){
                properties.load(fis);
            }
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String value = properties.getProperty(name);
                System.out.println(name+"="+ value);
            }
            //TODO  开始生成文件
            targetJavaProject = properties.getProperty("targetJavaProject");
            targetModelPackage = properties.getProperty("targetModelPackage");
            targetServicePackage = properties.getProperty("targetServicePackage");
            targetBaseService = properties.getProperty("targetBaseService");
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
    
    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(
                Generator.class.getResourceAsStream("/generator/generatorConfig.xml"));
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String string : warnings) {
            System.out.println(string);
        }
        System.out.println("完成通用Mapper类生成!");
        
//        doBuildService(configProPath,true);
        
//        System.out.println("完成通用Service类生成");
    }
    
    protected static void doBuildService(String propPath,boolean override){
        String pojoPath = targetModelPackage.replaceAll("\\.", "\\\\");
        String servicePackage = targetServicePackage.replaceAll("\\.", "\\\\");
        File fl = new File(targetJavaProject+File.separatorChar+pojoPath);
        if(fl.isDirectory()){
            File[] fList = fl.listFiles();
            int i = 0;
            while(fList.length>i){
                File fi = fList[i];
                String flNme = fi.getName();
                if(fi.isFile()&&flNme.indexOf(".java")!=-1){
                    buildFile(flNme.replace(".java", ""),targetJavaProject+File.separatorChar+servicePackage,override);
                }
                i++;
            }
        }
    }
    
    protected static void buildFile(String fileName,String serviceFilePath,boolean override){
        File servFile = new File(serviceFilePath+File.separatorChar+fileName+"Service.java");
        try {
            //如果存在，则删除后重新创建：等价于覆盖
            if(override){
                servFile.delete();
            }
            if(!servFile.exists()){
                servFile.createNewFile();
                FileOutputStream fops = new FileOutputStream(servFile);
                fops.write(writeString(fileName));
                fops.flush();
                fops.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    protected static byte[] writeString(String fleNme){
        StringBuffer sb = new StringBuffer();
        sb.append("package "+targetServicePackage+";\r\n");
        sb.append("\r\n");
        sb.append("import "+targetBaseService+";\r\n");
        sb.append("import "+targetModelPackage+"."+fleNme+";\r\n");
        sb.append("import org.springframework.stereotype.Service;\r\n");
        sb.append("\r\n");
        sb.append("@Service");
        sb.append("\r\n");
        sb.append("public class "+fleNme+"Service extends OMBaseService<"+fleNme+"> {\r\n");
        sb.append("}");
        System.out.println(sb.toString());
        return sb.toString().getBytes();
    }
}
