package com.osidocker.open.micro;

import com.baidu.aip.ocr.AipOcr;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.osidocker.open.micro.filters.HystrixRequestContextFilter;
import com.osidocker.open.micro.utils.DataUtils;
import org.json.JSONObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;

/**
 * micro service模板服务
 *
 */
@SpringBootApplication
@ServletComponentScan
@MapperScan(value = {"com.osidocker.open.**.mapper"})
@EnableCaching
@EnableSwagger2
public class App {

    public static void main(String[] args) {
//        SpringApplication.run(new Object[]{App.class,new ClassPathResource("GroovyApplication.groovy")}, args);
        SpringApplication.run(new Object[]{App.class}, args);
//        Map<String,String> map = new HashMap<>();
//        map.put("appid","wx2f3df7a1e61ab90d");
//        map.put("partnerid","1514672641");
//        map.put("prepayid","wx18091927458689ed1ebb41483380179379");
//        map.put("package", "Sign=WXPay");
//        map.put("noncestr", "73839A3F4AFA5696BF371B3DCF018947");
//        map.put("timestamp","1537233573");
//        try {
//            String sign = WXPayUtil.generateSignature(map, "bjhljf96321478bjhljf96321478bjhl", WXPayConstants.SignType.MD5);
//            System.out.println(sign);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 传入可选参数调用接口
        /*AipOcr client = new AipOcr("14279554", "8ouRrPcriKFGzRWbjkIri0RO", "lQLF4e0PuOQclrumcjYloc23uGLSsdDr");
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        options.put("detect_risk", "false");

        String idCardSide = "front";

        // 参数为本地图片路径
        String image = "D://test//1.jpg";
        JSONObject res = client.idcard(image, idCardSide, options);
        System.out.println(res);

        idCardSide = "back";
        image = "D://test//2.jpg";
        res = client.idcard(image, idCardSide, options);
        System.out.println(res);*/
        // 参数为本地图片二进制数组
//        byte[] file = readImageFile(image);
//        res = client.idcard(file, idCardSide, options);
//        System.out.println(res.toString(2));
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
                new HystrixRequestContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }


    @Bean
    public ServletRegistrationBean indexServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        registration.addUrlMappings("/hystrix.stream");
        return registration;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private RestTemplateBuilder builder;

    @Bean
    public RestTemplate restTemplate() {
        return builder.build();
    }
}
