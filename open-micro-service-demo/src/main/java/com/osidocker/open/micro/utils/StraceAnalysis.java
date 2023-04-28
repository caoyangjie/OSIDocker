package com.osidocker.open.micro.utils;

import cn.hutool.core.text.StrFormatter;
import com.google.common.base.Splitter;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * @className: StraceAnalysis
 * @description:
 * @author: caoyangjie
 * @date: 2023/4/28
 **/
public class StraceAnalysis {
    private static Map<String, String> tidNames = null;
    private static Map<String, Deque<ResourceTag>> tidWithRId = null;
    private static List<String> rtl = new ArrayList<>();
    private static List<ResourceTag> resourceTags = new ArrayList<>();
    private static List<String> allBuffer = new ArrayList<>();

    public static void main(String[] args) {
        tidNames = analysisStackFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/5044.jstack");
        tidWithRId = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Deque<AnalysisTag>> analysisMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Deque<ResourceTag>> resourceMap = new ConcurrentHashMap<>();
        analysisStraceFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/7696.strace", analysisMap, resourceMap);
//        timeFilter(analysisMap);
//        resourceFilter(resourceMap);
        StringBuffer buffer = new StringBuffer("");
        threadFilter("9456", null, null, buffer );
        System.out.println(buffer.toString());
    }

    @SneakyThrows
    protected static void resourceFilter(ConcurrentHashMap<String, Deque<ResourceTag>> resourceMap) {
        resourceMap.forEach((rid,deque)->{
            StringBuffer buffer = new StringBuffer("# 资源名称 : "+ rid +"\n\n| 资源ID | 等待发起 | 发起时间 | 唤醒发起 | 唤醒时间 |\n| --- | --- | --- | --- | --- |\n" );
            ResourceTag tag = deque.pollFirst();
            if( tag==null ){
                return;
            }
            do {
                buffer.append(tag.toString());
            } while ( (tag = deque.pollFirst())!=null );
            write("rid/"+rid, buffer.toString() );
        });
    }

    protected static void threadFilter(String tid, String startTime, String endTime, StringBuffer buffer) {
        ResourceTag rt;
        Deque<ResourceTag> drt = new ConcurrentLinkedDeque<>();
        while ( (rt = tidWithRId.get(tid).pollLast())!=null ) {
            drt.addLast(rt);
            buffer = buffer==null?new StringBuffer(""):buffer;
            if ( !StringUtil.isEmpty(rt.getWaitResource().getStartTime()) && !StringUtil.isEmpty(rt.getWaitResource().getEndTime()) && rt.getWakeUpResource()!=null && !StringUtil.isEmpty(rt.getWakeUpResource().getEndTime()) ) {
                startTime = startTime==null?rt.getWaitResource().getStartTime():startTime;
                endTime = endTime==null?rt.getWaitResource().getEndTime():before(endTime,rt.getWakeUpResource().getEndTime())?endTime:rt.getWakeUpResource().getEndTime();
//                endTime = endTime==null?rt.getWaitResource().getEndTime():endTime;
//                startTime = rt.getWaitResource().getStartTime();
//                endTime = rt.getWaitResource().getEndTime();
                if( rt.check( startTime, endTime ) ) {
                    if ( tid.equalsIgnoreCase(rt.getWakeUpResource().getTid()) ) {
                        continue;
                    }
                    buffer.append(rt.invoke());
                    resourceTags.add(rt);
                    String waitTid = rt.getWakeUpResource().getTid();
                    if( !rtl.contains( waitTid ) ) {
                        rtl.add(waitTid);
                    }
                    threadFilter(waitTid, startTime, endTime, buffer);
                }
            }
        }
    }

    protected static void timeFilter(ConcurrentHashMap<String, Deque<AnalysisTag>> analysisMap) {
        String startTime = "13:00:17.000000";
        String endTime = "13:19:19.999999";

        analysisMap.forEach((tid,deque)->{
            if( StringUtil.isEmpty(tid) ) {
                return;
            }
            AnalysisTag tag = deque.pollFirst();
            StringBuffer buffer = new StringBuffer("# 线程名称 : "+tidNames.get(tag.getTid())+"\n\n| 线程ID | 操作 | 开始时间 | 结束时间 | 花费时间 | 说明 |\n| --- | --- | --- | --- | --- | --- |\n");
            boolean isCheck = false;
            do {
                if( tag!=null && check( startTime, endTime, tag.getStartTime()) ) {
                    isCheck = true;
                    buffer.append(tag.toString());
                }
            } while ( (tag = deque.pollFirst())!=null );
            if( isCheck ) {
                write("tid/"+tid, buffer.toString());
            }
        });
    }

    @SneakyThrows
    protected static void analysisStraceFile(String straceFile, ConcurrentHashMap<String, Deque<AnalysisTag>> analysisMap, ConcurrentHashMap<String, Deque<ResourceTag>> resourceMap ) {
        Files.readAllLines(Paths.get(straceFile)).stream().forEach(line->{
            List<String> all = Splitter.on(" ").splitToList(StringUtils.stripStart(line, null));
            if( all.size()<4 || !StringUtil.isNumeric(all.get(0))) {
                return;
            }
            String tid = all.get(0);
            String time = all.get(1);
            String operator = all.get(2);
            String extOperator = all.get(3);
            Deque<AnalysisTag> deque = analysisMap.computeIfAbsent(tid,l->new ConcurrentLinkedDeque<>());
            Deque<ResourceTag> rstDeque = tidWithRId.computeIfAbsent(tid, t-> new ConcurrentLinkedDeque<>());
            AnalysisTag tag = null;
            if( !StringUtil.isEmpty(operator) && operator.startsWith("<") ) {
                tag = deque.removeLast();
                if( tag==null ) {
                    return;
                }
                tag.setEndTime(time);
                tag.setCostTime(all.stream().skip(3).filter(l->l.startsWith("<")&&l.endsWith(">")).findAny().orElse(""));
                String isNum = all.get(all.size()-1);
                if ( !StringUtil.isNumeric(isNum) ) {
                    tag.setComment(all.stream().skip(3).collect(Collectors.joining(" ")));
                }
            } else if ( line.contains("<unfinished ...>") || !StringUtil.isEmpty(operator) ) {
                tag = new AnalysisTag();
                tag.setTid(tid);
                tag.setThreadName(tidNames.get(tid));
                tag.setStartTime(time);
                if( line.contains("futex(") ) {
                    tag.setRid(operator.replace("futex(","").replace(",",""));
                    tag.setWait(extOperator.contains("FUTEX_WAIT")?true:false);
                    Deque<ResourceTag> resourceDeque = resourceMap.computeIfAbsent(tag.getRid(), rid->new ConcurrentLinkedDeque<ResourceTag>());
                    ResourceTag rt;
                    if( tag.isWait() ) {
                        rt = new ResourceTag();
                        rt.setRid(tag.getRid());
                        rt.setWaitResource(tag);
                    } else {
                        rt = resourceDeque.pollLast();
                        if( rt==null ){
                            return;
                        }
                        if( !rt.getWaitResource().getTid().equalsIgnoreCase(tid) ) {
                            rt.setWakeUpResource(tag);
                        }
                    }
                    resourceDeque.addLast(rt);
                    rstDeque.addLast(rt);
                } else {
                    tag.setOperator(operator);
                }
            } else {
                if( tag==null && operator.equals("---") ) {
                    System.err.println(line);
                    return;
                }
            }
            deque.addLast(tag);
        });
    }

    private static boolean check(String start, String end, String rightNow) {
        Long startNum = Long.parseLong(start.replaceAll(":","").replace(".",""));
        Long endNum = Long.parseLong(end.replaceAll(":","").replace(".",""));
        Long rightNowNum = Long.parseLong(rightNow.replaceAll(":","").replace(".",""));
        return rightNowNum>=startNum && rightNowNum<=endNum;
    }

    private static boolean before(String left, String right) {
        Long startNum = Long.parseLong(left.replaceAll(":","").replace(".",""));
        Long endNum = Long.parseLong(right.replaceAll(":","").replace(".",""));
        return startNum <= endNum;
    }

    @SneakyThrows
    private static void write(String tid, String values) {
        if( !StringUtil.isEmpty(tid) ) {
            Files.write(Paths.get("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/output/"+tid+".md"), values.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SneakyThrows
    protected static Map<String, String> analysisStackFile(String stackFile) {
        Map<String, String> tidNames = new HashMap<>();
        List<String> lines =  Files.readAllLines(Paths.get(stackFile));
        lines.stream().filter(line->!line.startsWith("\"Attach") &&line.startsWith("\"") && line.contains("#")).forEach(line->{
            List<String> all = Splitter.on(" ").splitToList(line);
            String threadName = all.get(0);
            String nid = null;
            try{
                 nid = all.stream().filter(key->key.startsWith("nid=")).findFirst().get();
                if( !StringUtil.isEmpty(threadName) && !StringUtil.isEmpty(nid) ){
                    Integer tid = Integer.parseInt(nid.split("=")[1].substring(2),16);
                    tidNames.put(tid+"",all.get(0));
                }
            } catch(Exception e){
                System.out.println("");
            } finally {
            }
        });
        return tidNames;
    }

    @Data
    private static class AnalysisTag {
        private String tid;
        private String threadName;
        private boolean wait;
        private String rid;
        private String startTime;
        private String endTime;
        private String costTime;
        private String operator;
        private String comment;

        @Override
        public String toString() {
            return "|" + tid +
                    (StringUtil.isEmpty(rid) ? "|" + operator : (wait ? "| wait ":"| wakeup") + StrFormatter.format("[({})](../rid/{}.md)", rid, rid)) +
                    "|" + startTime +
                    "|" + endTime +
                    "|" + costTime +
                    "|" + comment +
                    "|\n";
        }
    }

    @Data
    private static class ResourceTag {
        private String rid;
        private AnalysisTag waitResource;
        private AnalysisTag wakeUpResource;
        private boolean check(String startTime, String endTime){
            boolean flag = waitResource!=null && wakeUpResource!=null;
            if( flag ) {
                flag = flag && StraceAnalysis.before( waitResource.getStartTime(), endTime);
                flag = flag && StraceAnalysis.check( startTime, endTime, wakeUpResource.getStartTime());
            }
            return flag;
        }

        @Override
        public String toString() {
            return "|" + rid +
                    StrFormatter.format( "| [{}](../tid/{}.md)", waitResource.tid, waitResource.tid ) +
                    "|" + waitResource.startTime +
                    (wakeUpResource==null?"|   ": StrFormatter.format( "| [{}](../tid/{}.md)", wakeUpResource.tid, wakeUpResource.tid )) +
                    (wakeUpResource==null?"|    ":"|" + wakeUpResource.startTime) +
                    "|\n";
        }

        public String invoke(){
            return StrFormatter.format(" 线程ID：[{}] 主动在 [{}] 尝试唤醒在 [{}] 等待 [{}] 资源的线程ID：[{}],并在[{}]将其唤醒!\n",
                     wakeUpResource.getTid(), wakeUpResource.getStartTime(), waitResource.getStartTime(), rid, waitResource.getTid(), waitResource.getEndTime()
                    );
        }
    }
}
