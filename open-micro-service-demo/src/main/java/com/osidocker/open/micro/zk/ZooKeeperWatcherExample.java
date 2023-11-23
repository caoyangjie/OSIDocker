package com.osidocker.open.micro.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ZooKeeperWatcherExample {
    public static void main(String[] args) {
        // 创建ZkClient实例
        ZkClient zkClient = new ZkClient("localhost:2181");

        // 创建Watcher实例，用于监听节点变化
        IZkChildListener childListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                // 节点变化的处理逻辑
//                System.out.println("节点变化：" + parentPath + ", 当前子节点：" + currentChilds);
                currentChilds.forEach(p->{
                    System.out.println("获取到值："+zkClient.readData(parentPath+"/"+p));
                });
            }
        };

        // 注册Watcher，在指定节点上监听子节点变化
        zkClient.subscribeChildChanges("/path", childListener);

        // 触发节点变化的线程
        Thread triggerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 修改指定节点，触发节点变化事件
                zkClient.createPersistent("/path/abcd",true);
                zkClient.writeData("/path/abcd","fjdsjfdslfsa".getBytes(StandardCharsets.UTF_8));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                zkClient.deleteRecursive("/path");
            }
        });

        // 启动触发线程
        triggerThread.start();
    }
}
