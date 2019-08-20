package com.osidocker.open.micro.concurrent;

import com.osidocker.open.micro.utils.FileSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

/**
 * @author Administrator
 * @creato 2019-04-28 20:06
 */
public class PdfFileSearchTest {

    public static void main(String[] args){
        Phaser phaser = new Phaser(4);
        getSearchPaths().parallelStream().forEach(path->{
            new FileSearch(path,"pdf",phaser).run();
        });
    }

    private static List<String> getSearchPaths() {
        List<String> filePath = new ArrayList<>();
//        filePath.add("E:\\tools");
        filePath.add("F:\\platform");
        filePath.add("F:\\HelpContext");
        filePath.add("G:\\0我的书单");
        filePath.add("G:\\platform");
        return filePath;
    }
}
