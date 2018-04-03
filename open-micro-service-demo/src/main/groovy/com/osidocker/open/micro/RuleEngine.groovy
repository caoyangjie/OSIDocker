package com.osidocker.open.micro

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.support.GenericGroovyApplicationContext
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.nio.file.*

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明 ：
 * @类修改者 ： caoyangjie
 * @类作者 ： caoyangjie
 * @创建日期 ： 16:14 2018/4/2
 * @修改说明 ：
 * @修改日期 ： 16:14 2018/4/2
 * @版本号 ： V1.0.0
 */
@Service
class RuleEngine implements ApplicationContextAware {
    private String RULE_SERVICE_GROOVY_PATH = "RuleServiceGroovy.groovy"
    private ApplicationContext parentContext

    private Resource ruleResource

    ApplicationContext ruleContext

    WatchService watchService

    private Long lastModified

    private Thread watchFileChangeThread

    @PostConstruct
    void init(){
        try{
            ruleResource = new ClassPathResource(RULE_SERVICE_GROOVY_PATH)
            lastModified = ruleResource.lastModified()
        }catch (IOException e){
            throw new RuntimeException(e)
        }
        reload()
        println("Rule engine initialized.")
        watchFileChange()
    }

    /**
     * 运行指定规则
     *
     * @param ruleName
     *            规则名字
     * @param param
     *            规则参数
     */
    Object run(String ruleName, Object request) {
        // 查找规则
        if (!ruleContext.containsBean(ruleName)) {
            System.out.println("Rule[" + ruleName + "] not found.");
            return
        }

        // 如果规则存在，运行规则
        IRuleService service = ruleContext.getBean(ruleName, IRuleService.class)
        if (null != service) {
            try {
                return service.run(request)
            } catch (Exception e) {
                println("Error occur while runing the Rule["
                        + ruleName + "]")
            }
        }
        return null
    }

    /**
     * 重新装载规则引擎，创建新的规则Context，并销毁旧Context。
     */
    private synchronized void reload() {
        if (!ruleResource.exists()) {
            throw new RuntimeException("Rule config not exist.")
        }
        ApplicationContext oldContext = this.ruleContext

        try {
            ApplicationContext newContext = new GenericGroovyApplicationContext(ruleResource)
            newContext.setParent(parentContext)
            this.ruleContext = newContext
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 销毁旧的规则Context
        if (null != oldContext ) {
            oldContext = null
        }
    }

    void watchFileChange(){
        watchService = FileSystems.getDefault().newWatchService()
        String ruleResourceLocation = Thread.currentThread().getContextClassLoader().getResource("")
        println(ruleResourceLocation)
        Path path = new File(new URI(ruleResourceLocation)).toPath()
        path.register(watchService
                , StandardWatchEventKinds.ENTRY_CREATE
                , StandardWatchEventKinds.ENTRY_MODIFY)
        watchFileChangeThread = new Thread(){
            @Override
            void run() {
                while (true){
                    WatchKey key = watchService.take()
                    for (WatchEvent<?> event: key.pollEvents()) {
                        System.out.println(event.context() + " comes to " + event.kind());
                    }
                    if( key != null ){
                        reload()
                    }
                    key.reset()
                }
            }
        }
        watchFileChangeThread.setDaemon(false)
        watchFileChangeThread.start()
        // 增加jvm关闭的钩子来关闭监听
        Runtime.getRuntime().addShutdownHook(new Thread({
            try {
                watchService.close()
            } catch (Exception e) {
            }
        }))
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.parentContext = applicationContext;
    }
}
