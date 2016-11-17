package org.osidocker.open.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

public class JsonXmlChangeUtils {

	 public static String json2xml(String json){  
        StringReader input = new StringReader(json);  
        StringWriter output = new StringWriter();  
        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).repairingNamespaces(false).build();  
        try {  
            XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);  
            XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);  
            writer = new PrettyXMLEventWriter(writer);  
            writer.add(reader);  
            reader.close();  
            writer.close();  
        } catch( Exception e){  
            e.printStackTrace();  
        } finally {  
            try {  
                output.close();  
                input.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        if(output.toString().length()>=38){//remove <?xml version="1.0" encoding="UTF-8"?>  
            return output.toString().substring(39);  
        }  
        return output.toString();  
    }  
      
    /** 
     * xml string convert to json string 
     */  
    public static String xml2json(String xml){  
        StringReader input = new StringReader(xml);  
        StringWriter output = new StringWriter();  
        JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true).autoPrimitive(true).prettyPrint(true).build();  
        try {  
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);  
            XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);  
            writer.add(reader);  
            reader.close();  
            writer.close();  
        } catch( Exception e){  
            e.printStackTrace();  
        } finally {  
            try {  
                output.close();  
                input.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return output.toString();  
    }  
    
    public static void main(String[] args){
    	test_json2xml();
    	test_xml2json();
    }
    
    public static void test_json2xml(){  
        String json = "{\"Response\" : {\"CustID\" : 1300000428,\"CompID\" : 1100000324,\"Items\" : {\"Item\" : [ {\"Sku_ProductNo\" : \"sku_0004\",\"Wms_Code\" : 1700386977,\"Sku_Response\" : \"T\",\"Sku_Reason\" : null}, {\"Sku_ProductNo\" : \"0005\",\"Wms_Code\" : 1700386978,\"Sku_Response\" : \"T\",\"Sku_Reason\" : null}]}}}";  
        String xml = JsonXmlChangeUtils.json2xml(json);  
        System.out.println(xml);  
    }  
      
    public static void test_xml2json(){  
        String xml = "<Response><CustID>1300000428</CustID><CompID>1100000324</CompID><Items><Item><Sku_ProductNo>sku_0004</Sku_ProductNo><Wms_Code>1700386977</Wms_Code><Sku_Response>T</Sku_Response><Sku_Reason></Sku_Reason></Item><Item><Sku_ProductNo>0005</Sku_ProductNo><Wms_Code>1700386978</Wms_Code><Sku_Response>T</Sku_Response><Sku_Reason></Sku_Reason></Item></Items></Response>";  
        String json = JsonXmlChangeUtils.xml2json(xml);  
        System.out.println(json);  
    }  
}
