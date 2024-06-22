# 实现一个自动加载变更class的功能

## maven 依赖包

```
<dependency>
    <groupId>com.taobao.arthas</groupId>
    <artifactId>arthas-spring-boot-starter</artifactId>
    <version>3.7.2</version>
</dependency>
<dependency>
    <groupId>com.taobao.arthas</groupId>
    <artifactId>arthas-client</artifactId>
    <version>3.7.2</version>
</dependency>
```

## 依赖文件

### SpringDcitsToolRunnable

```java
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.arthas.spring.ArthasProperties;
import com.example.onlyoffice_demo.utils.RedefineClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @className: SpringDcitsToolRunnable
 * @description:
 * @author: caoyangjie
 * @date: 2024/6/22
 **/
@Slf4j
@Service
public class SpringDcitsToolRunnable implements ApplicationRunner {
    @Value("${dcits.devtools.dir:/tmp}")
    private String watchDir;
    @Value("${dcits.devtools.refresh: 10}")
    private int refreshTime = 10;
    @Value("${dcits.devtools.delay: 5000}")
    private int delayTime = 5000;
    @Autowired
    private ArthasProperties arthasProperties;
    private AtomicBoolean clearFlag = new AtomicBoolean(true);

    private ConcurrentHashMap<String, WatchMonitor> allMonitor = new ConcurrentHashMap<>();
    private ConcurrentHashSet<String> updateClasses = new ConcurrentHashSet<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 持续观察是否有class需要更新
        watch(watchDir);
        // redefine executor
        executor();
    }

    private void executor() {
        ThreadFactoryBuilder builder = ThreadFactoryBuilder.create().setNamePrefix("AsyncThread-");
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, builder.build(), new ScheduledThreadPoolExecutor.DiscardPolicy());

        executorService.scheduleAtFixedRate(() -> {
            // 执行需要执行的任务
            try{
                if (CollectionUtil.isNotEmpty(updateClasses)) {
                    log.info("开始执行变更classes动态更新任务!");
                    // 记录变更快照,并清理待执行变更的集合
                    Set<String> snapshoot = new HashSet<>();
                    synchronized(this) {
                        snapshoot.addAll(updateClasses);
                        updateClasses.clear();
                    }
                    RedefineClassUtil.send(Optional.ofNullable(arthasProperties.getIp()).orElse("127.0.0.1"), arthasProperties.getTelnetPort(), "redefine", snapshoot);
                    log.info("执行变更classes动态更新任务完成!");
                }
                clearFlag.set(true);
                // clean工程后，没有来得及执行 install 会出现 allMonitor 为空的情况,此情况下,需要每隔 5秒执行一次判断，
                // 但凡有重新执行 install 生成 target/classes 目录,则需要重新监听
                if( !Paths.get(watchDir).toFile().exists() && !allMonitor.isEmpty() ) {
                    log.info("开始注销监听事件!");
                    allMonitor.values().forEach(watchMonitor -> {
                        watchMonitor.close();
                        watchMonitor=null;
                    });
                    allMonitor.clear();
                }
                if( allMonitor.isEmpty() && Paths.get(watchDir).toFile().exists() ) {
                    try {
                        watch(watchDir);
                        // 补偿,由于监听建立可能在 文件变化之后才触发,所以需要将 root 目录下所有的文件重新推送
                        Files.walk(Paths.get(watchDir)).forEach(file->{
                            if( file.toFile().isFile() ) {
                                addResource(file);
                            }
                        });
                    } catch (IOException e) {
                    }
                }
            } catch(Exception e){
            }
        }, 10, refreshTime, TimeUnit.SECONDS);
    }

    protected void watch(String watchDir) throws IOException {
        // clean 工程后， 目录不存在
        if( !Paths.get(watchDir).toFile().exists() ) {
            return;
        }
        WatchMonitor watchRoot = WatchMonitor.create(Paths.get(watchDir), WatchMonitor.ENTRY_CREATE, WatchMonitor.ENTRY_MODIFY, WatchMonitor.ENTRY_DELETE);
        allMonitor.putIfAbsent(watchDir,watchRoot);
        watchRoot.setWatcher(new DelayWatcher(new Watcher(),delayTime));
        watchRoot.start();
        Files.walk(Paths.get(watchDir)).filter(Files::isDirectory).forEach(dir -> {
            WatchMonitor watchMonitor = WatchMonitor.create(dir, WatchMonitor.ENTRY_CREATE, WatchMonitor.ENTRY_MODIFY, WatchMonitor.ENTRY_DELETE);
            allMonitor.putIfAbsent(dir.toFile().getAbsolutePath(),watchMonitor);
            watchMonitor.setWatcher(new DelayWatcher(new Watcher(),delayTime));
            watchMonitor.start();
        });
    }

    private class Watcher extends SimpleWatcher {
        @Override
        public void onCreate(WatchEvent<?> event, Path currentPath) {
            Path triggerPath = currentPath.resolve((Path) event.context());
            if (triggerPath.toFile().isDirectory()) {
                try{
                    // 可能子目录全部都被清理了,在这里重新建立监听
                    watch(triggerPath.toFile().getAbsolutePath());
                    WatchMonitor watchMonitor = WatchMonitor.create(triggerPath, WatchMonitor.ENTRY_CREATE, WatchMonitor.ENTRY_MODIFY, WatchMonitor.ENTRY_DELETE);
                    allMonitor.put(triggerPath.toFile().getAbsolutePath() ,watchMonitor);
                    watchMonitor.setWatcher(new DelayWatcher(new Watcher(),delayTime));
                    watchMonitor.start();
                } catch(Exception e){
                }
            } else {
                addResource(triggerPath);
            }
        }

        @Override
        public void onModify(WatchEvent<?> event, Path currentPath) {
            Path triggerPath = currentPath.resolve((Path) event.context());
            if (triggerPath.toFile().isFile() && triggerPath.getFileName().endsWith(".class")) {
                addResource(triggerPath);
            }
        }

        @Override
        public void onDelete(WatchEvent<?> event, Path currentPath) {
            // 这里是防止触发频率过高, 让 监听 和注销监听事件每隔 refresh 时间只触发一次
            // 因为 linux系统中, 文件监听是跟着内核中分配的文件ID来的,重新创建的同名路径文件，监听是不生效的.
            if( currentPath.resolve((Path) event.context()).toFile().isDirectory() && clearFlag.compareAndSet(true, false)) {
                log.info("开始注销监听事件!");
                allMonitor.values().forEach(watchMonitor -> {
                    watchMonitor.close();
                    watchMonitor=null;
                });
                allMonitor.clear();
                try {
                    watch(watchDir);
                    Files.walk(Paths.get(watchDir)).forEach(file->{
                        if( file.toFile().isFile() ) {
                            addResource(file);
                        }
                    });
                } catch (IOException e) {
                }
            }
        }
    }

    public void addResource(Path classPath) {
        if (classPath.getFileName().toString().endsWith(".class")) {
            log.info("待更新的class文件: {}",classPath.toFile().getAbsolutePath());
            updateClasses.add(classPath.toFile().getAbsolutePath());
        }
    }
}

```

### RedefineClassUtil

```java

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class RedefineClassUtil {

    public static void main(String[] args) {
        send("127.0.0.1", 18181, "redefine",null);
    }
    public static void send(String ip, int port, String command, Set<String> classPaths) {
        if (CollectionUtil.isEmpty(classPaths)) {
            log.info("没有要更新的class");
            return;
        }

        InputStream in = null;
        PrintStream out = null;
        TelnetClient telnet = null;
        List<String> updateFails = new ArrayList<>();
        try {
            telnet = new TelnetClient();
            telnet.connect(ip, port);
            // 建立连接超时时间
            telnet.setConnectTimeout(5000);
            // 总的连接超时时间
            telnet.setDefaultTimeout(10000);
            // 写入，读取超时时间
            telnet.setSoTimeout(3000);
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            // 读取 Telnet 服务器返回的输出
            byte[] buff = new byte[1024];
            int ret_read = 0;
            long start = System.currentTimeMillis();
            log.info("连接后，服务端响应内容读取开始");
            do {
                ret_read = in.read(buff);
                if (ret_read > 0) {
                    String ask = new String(buff, 0, ret_read);
                    String[] asks = ask.split("\n");
                    ask = asks.length > 1 ? asks[asks.length - 1] : ask;
                    if (ask.startsWith("[arthas@") && ask.endsWith("]$ ")) {
                        break;
                    }
                }
            } while (ret_read >= 0 && System.currentTimeMillis() - start < 3000);
            log.info("连接后，服务端响应内容读取结束。准备发送指令!");
            for (String classPath : classPaths) {
                String commandStr = StrFormatter.format("{} {}", command, classPath);
                log.info("execute command {}", commandStr);
                out.println(commandStr);
                out.flush();

                // 读取执行命令后的输出
                buff = new byte[1024];
                ret_read = 0;
                start = System.currentTimeMillis();
                do {
                    try{
                        ret_read = in.read(buff);
                        if (ret_read > 0) {
                            String commandResult = new String(buff, 0, ret_read);
                            if (commandResult.contains("success")) {
                                break;
                            }
                            String[] asks = commandResult.split("\n");
                            commandResult = asks.length > 1 ? asks[asks.length - 1] : commandResult;
                            if (commandResult.startsWith("[arthas@") && commandResult.endsWith("]$ ")) {
                                break;
                            }
                        }
                    } catch(Exception e){
                        updateFails.add(classPath);
                    }
                } while (ret_read >= 0 && System.currentTimeMillis() - start < 1000);
            }
        } catch (Exception e) {
            log.error("class文件更新失败的文件: {}, 原因: {}", CollectionUtil.join(updateFails,"\n"), e.getMessage());
        } finally {
            if( out!=null ) {
                out.println("exit");
                out.flush(); // 发送 exit 命令来结束 Telnet 会话
            }
            try {
                telnet.disconnect();
            } catch (IOException e) {
            }
            log.info("断开动态部署连接!");
        }
    }
}
```
### application.yml配置

```yaml
arthas:
  agent-id: devtools-arthas
  telnet-port: 18181

dcits:
  devtools:
    dir: /software/blacktechnolegy/spring-boot-only-office/onlyoffice_demo/target/classes`
```
