package com.osidocker.open.micro.utils;

import cn.hutool.core.collection.CollectionUtil;
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
import java.util.stream.Stream;

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
    private static Map<String, String> tidBuffer = new HashMap<>();
    private static Map<Long, String> bufferMap = new HashMap<>();
    private static ConcurrentHashMap<String, Deque<AnalysisTag>> analysisMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Deque<ResourceTag>> resourceMap = new ConcurrentHashMap<>();
    private static StringBuffer buffer = new StringBuffer("");
    // 时间敏感精度
    private static double precision = 0.00001;

    public static void main(String[] args) {
        tidNames = analysisStackFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/5044.jstack");
//        tidNames = analysisStackFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/80619.jstack_new");
//        tidNames = analysisStackFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/976.jstack");
        tidWithRId = new ConcurrentHashMap<>();
        analysisStraceFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/7696.strace", analysisMap, resourceMap);
//        analysisStraceFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/four_min.strace", analysisMap, resourceMap);
//        analysisStraceFile("/software/workspace/OSIDocker/open-micro-service-demo/src/main/resources/strace0509.log", analysisMap, resourceMap);
//        timeFilter(analysisMap, null, "18:00:23.155440", "18:02:33.211292");
//        resourceFilter(resourceMap, null);
//        StringBuffer buffer = new StringBuffer("");
//        threadFilter("37542", "18:00:23.155440", "18:02:33.211292", buffer );
//        System.out.println(buffer.toString());
//        bufferMap.keySet().stream().sorted().forEach(time->{
//            System.out.print(bufferMap.get(time));
//        });
//        retrospect("9456", "13:09:17.125824", "13:09:18.830982",0);
//        resFilter("83402", "22:16:22.931076", "22:16:25.237999",0);
//        resFilter("29433", "22:30:46.477706", "22:30:47.886147",0);
//        resFilter("84538", "22:16:23.440361", "22:16:24.163479",0);
//        resFilter("81742", "22:16:23.441743", "22:16:24.191295",0);
        resFilter("7393","13:09:15.212047","13:09:18.250768",0);
        System.out.println(buffer.toString());
    }

//    /**
//     * 追溯 id，调用链
//     * @param id
//     * @param startTime
//     * @param endTime
//     */
//    public static void retrospect(String id, String startTime, String endTime, int space) {
//        if( StringUtil.isEmpty(id) ) {
//            return;
//        }
//        AnalysisTag atag = null;
//        List<AnalysisTag> ats = new ArrayList<>();
//        while ( (atag = analysisMap.get(id).pollFirst())!=null ) {
//            if( atag.getStartTime()!=null && atag.between(startTime, endTime) ) {
//                // 寻找在此次激活的过程中执行了哪些 wakeup 操作
//                AnalysisTag finalAtag = atag;
//                ats.add(atag);
//                ats.stream().filter(t->t.isWakeUp()).forEach(t->{
////                    ResourceTag rtag = resourceMap.get(t.getRid()).stream().filter(rt-> rt.getWakeUpResource()!=null && rt.getWakeUpResource().getStartTime().equals(t.getStartTime()) ).findFirst().orElse(new ResourceTag());
//                    resourceMap.get(t.getRid()).forEach(rtag->{
//                        if( rtag.getWaitResource()!=null && rtag.getWakeUpResource()!=null && !rtag.getWaitResource().getTid().equals(rtag.getWakeUpResource().getTid()) ) {
////                        if( subtract(rtag.getWaitResource().getEndTime(), rtag.getWakeUpResource().getEndTime()) < 2000 ) {
//                            if( !rtag.getWakeUpResource().getTid().equals(id) && check(startTime, endTime, rtag.getWaitResource().getEndTime()) ) {
//                                buffer.append(appendSpace(space,finalAtag.getCostTime())).append(id).append(StrFormatter.format(" #{}[资源ID:{},开始时间:{},结束时间:{}\n",  finalAtag.getThreadName(), finalAtag.getRid(), finalAtag.getStartTime(), finalAtag.getEndTime()));
//                                retrospect(rtag.getWaitResource().getTid(), startTime, endTime, space+4);
//                            }
////                        }
//                        }
//                    });
//                });
//                ats.clear();
//            }
//            if( !StringUtil.isEmpty(atag.getEndTime()) && check(startTime, endTime, atag.getEndTime()) ) {
//                ats.add(atag);
//            }
//        }
//    }

    private static String appendSpace(int space, String costTime) {
        return Stream.iterate("耗时: "+costTime+",line="+space+" ",s->" ").limit(space+1).collect(Collectors.joining());
    }

    public static void resFilter(String id, String startTime, String endTime, int space) {
        if( StringUtil.isEmpty(id) ) {
            return;
        }
        AnalysisTag atag = null;
        while ( (atag = analysisMap.get(id).pollLast())!=null ) {
            if( !StringUtil.isEmpty(atag.getEndTime()) && StraceAnalysis.subtract(startTime,atag.getEndTime())>0L) {
                break;
            }
            if( atag.check(startTime, endTime) ) {
                String rid = atag.getRid();
                if( StringUtil.isEmpty(rid) ) {
                    continue;
                }
                ResourceTag rtag = null;
                while( (rtag=resourceMap.get(rid).pollLast())!=null ) {
                    if( rtag.getWakeUpResource()!=null && rtag.getWakeUpResource().inner(startTime, atag.endTime) ) {
                        buffer.append(appendSpace(space, atag.costTime)).append(id).append(StrFormatter.format(" # {}, [资源ID: {} ,开始时间: {} ,结束时间:{} ]\n", atag.getThreadName(), atag.getRid(), atag.getStartTime(),atag.getEndTime()));
                        resFilter(rtag.getWakeUpResource().getTid(), startTime, atag.getEndTime(), space+1);
                        break;
                    }
                }
            }
        }
    }

    @SneakyThrows
    protected static void resourceFilter(ConcurrentHashMap<String, Deque<ResourceTag>> resourceMap, String resourceId) {
        resourceMap.forEach((rid,deque)->{
            StringBuffer buffer = new StringBuffer("# 资源名称 : "+ rid +"\n\n| 资源ID | 等待发起 | 发起时间 | 唤醒发起 | 唤醒时间 |\n| --- | --- | --- | --- | --- |\n" );
            ResourceTag tag = deque.pollFirst();
            if( tag==null ){
                return;
            }
            do {
                buffer.append(tag.toString());
            } while ( (tag = deque.pollFirst())!=null );
            if( resourceId==null ) {
                write("rid/"+rid, buffer.toString() );
            } else {
                if( rid.equalsIgnoreCase(resourceId) ) {
                    System.out.println(buffer.toString());
                }
            }

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
                timeFilter( analysisMap, null, startTime, endTime );
                if( rt.check( startTime, endTime ) ) {
                    if ( tid.equalsIgnoreCase(rt.getWakeUpResource().getTid()) ) {
                        continue;
                    }
                    bufferMap.put(Long.parseLong(rt.getWakeUpResource().getStartTime().replaceAll(":","").replace(".","")), rt.invoke());
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

    protected static void timeFilter(ConcurrentHashMap<String, Deque<AnalysisTag>> analysisMap, String threadIds, String startTime, String endTime) {
        if(!CollectionUtil.isEmpty(tidBuffer)) {
            return;
        }
        startTime = StringUtil.isEmpty(startTime )?"13:00:17.000000":startTime;
        endTime = StringUtil.isEmpty(endTime)?"13:19:19.999999":endTime;

        String finalStartTime = startTime;
        String finalEndTime = endTime;
        analysisMap.forEach((tid, deque)->{
            if( StringUtil.isEmpty(tid) ) {
                return;
            }
            AnalysisTag tag = deque.pollFirst();
            if( tag==null ) {
                return;
            }
            StringBuffer buffer = new StringBuffer("# 线程名称 : "+tidNames.get(tag.getTid())+"\n\n| 线程ID | 操作 | 开始时间 | 结束时间 | 花费时间 | 说明 |\n| --- | --- | --- | --- | --- | --- |\n");
            boolean isCheck = false;
            do {
                if( tag!=null && check(finalStartTime, finalEndTime, tag.getStartTime()) ) {
                    isCheck = true;
                    buffer.append(tag.toString());
                }
            } while ( (tag = deque.pollFirst())!=null );
            if( isCheck && StringUtil.isEmpty(threadIds)) {
                write("tid/"+tid, buffer.toString());
                tidBuffer.computeIfAbsent(tid,id->buffer.toString());
            }else if( isCheck ) {
                System.out.println(buffer.toString());
            }
        });
    }

    @SneakyThrows
    protected static void analysisStraceFile(String straceFile, ConcurrentHashMap<String, Deque<AnalysisTag>> analysisMap, ConcurrentHashMap<String, Deque<ResourceTag>> resourceMap ) {
        Files.readAllLines(Paths.get(straceFile)).stream().forEach(line->{
            List<String> all = Splitter.on(" ").splitToList(StringUtils.stripStart(line, null));
            all = all.stream().filter(l->!StringUtil.isEmpty(l)).collect(Collectors.toList());
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
                try{
                    tag = deque.removeLast();
                } catch(Exception e){
                }
                if( tag==null ) {
                    return;
                }
                if( StringUtil.isEmpty(tag.getEndTime()) ){
                    tag.setEndTime(time);
                    tag.setCostTime(all.stream().skip(3).filter(l->l.startsWith("<")&&l.endsWith(">")).findAny().orElse(""));
                    String isNum = all.get(all.size()-2);
                    if ( !StringUtil.isNumeric(isNum) ) {
                        tag.setComment(all.stream().skip(3).collect(Collectors.joining(" ")));
                    } else {
                        tag.setOperatorResult(isNum);
                        tag.setComment(all.stream().skip(3).collect(Collectors.joining(" ")));
                    }
                }
            } else if ( line.contains("<unfinished ...>") || !StringUtil.isEmpty(operator) ) {
                tag = new AnalysisTag();
                tag.setTid(tid);
                tag.setThreadName(tidNames.get(tid));
                tag.setStartTime(time);
                if( line.contains("futex(") ) {
                    tag.setRid(operator.replace("futex(","").replace(",",""));
                    tag.setWait(extOperator.contains("FUTEX_WAIT")?true:false);
                    tag.setWakeUp(extOperator.contains("FUTEX_WAKE")?true:false);
                    Deque<ResourceTag> resourceDeque = resourceMap.computeIfAbsent(tag.getRid(), rid->new ConcurrentLinkedDeque<ResourceTag>());
                    ResourceTag rt;
                    if( tag.isWait() ) {
                        rt = new ResourceTag();
                        rt.setRid(tag.getRid());
                        rt.setWaitResource(tag);
                    } else {
                        rt = resourceDeque.pollLast();
                        if( rt!=null ){
                            if( !rt.getWaitResource().getTid().equalsIgnoreCase(tid) && rt.getWakeUpResource()==null ) {
                                rt.setWakeUpResource(tag);
                            } else {
                                try {
                                    resourceDeque.addLast(rt);
                                    rt = (ResourceTag) rt.clone();
                                    rt.setWakeUpResource(tag);
                                } catch (CloneNotSupportedException e) {
                                }
                            }
                        }
                    }
                    if( rt!=null ) {
                        resourceDeque.addLast(rt);
                        rstDeque.addLast(rt);
                    }
                } else {
                    tag.setOperator(operator);
                }
            } else {
                if( tag==null && operator.equals("---") ) {
                    System.err.println(line);
                    return;
                }
            }
            if( tag!=null ) {
                deque.addLast(tag);
            }
        });
    }

    private static Long subtract(String waitEndTime, String wakeupEndTime) {
        Long startNum = Long.parseLong(wakeupEndTime.replaceAll(":","").replace(".",""));
        Long endNum = Long.parseLong(waitEndTime.replaceAll(":","").replace(".",""));
        return endNum - startNum;
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
            Files.write(Paths.get("/home/caoyangjie/桌面/tmp/"+tid+".md"), values.getBytes(StandardCharsets.UTF_8));
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
        private boolean wakeUp;
        private String rid;
        private String startTime;
        private String endTime;
        private String costTime;
        private String operator;
        private String operatorResult;
        private String comment;

        public void setCostTime(String costTime) {
            this.costTime = costTime.replace("<","").replace(">","");
        }

        public boolean check(String startTime, String endTime) {
            boolean flag = StringUtil.isEmpty(costTime) ? false : Float.parseFloat(costTime)>=precision;
//            boolean flag = !StringUtil.isEmpty(this.endTime);
            return flag && StraceAnalysis.check(startTime, endTime, this.endTime) && this.comment.contains(" = 0");
        }

        public boolean between(String startTime, String endTime) {
            return StraceAnalysis.check(startTime, endTime, this.startTime);
        }

        public boolean costMoreThan(Float val) {
            return StringUtil.isEmpty(costTime) ? false : Float.parseFloat(costTime.replace("<","").replace(">",""))>val;
        }

        public boolean inner(String startTime, String endTime) {
            return StraceAnalysis.check(startTime, endTime, this.startTime);
        }

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
    private static class ResourceTag implements Cloneable {
        private String rid;
        private AnalysisTag waitResource;
        private AnalysisTag wakeUpResource;
        private boolean check(String startTime, String endTime){
            boolean flag = waitResource!=null && wakeUpResource!=null;
            if( flag ) {
                flag = flag && StraceAnalysis.check(waitResource.getStartTime(), waitResource.getEndTime(), wakeUpResource.getStartTime());
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
            return StrFormatter.format(" 线程ID：[{}({})] 尝试唤醒的线程ID：[{}({})]  等待资源 [{}], 睡眠时间 -> [{}] 发起唤醒时间 -> [{}] ,并在 -> [{}]将其唤醒,结果为：[{}]!\n 执行动作 [{}@{}] 唤醒-> [{}@{}],结果：[{}]\n{}\n",
                     wakeUpResource.getTid(), "", waitResource.getTid(), "", rid, waitResource.getStartTime(), wakeUpResource.getStartTime(),  waitResource.getEndTime(), wakeUpResource.getComment()
                    , wakeUpResource.getTid(), wakeUpResource.getThreadName(), waitResource.getTid(), waitResource.getThreadName(), "0".equals(wakeUpResource.getOperatorResult())?"成功":"失败"
                    , "0".equals(wakeUpResource.getOperatorResult()) ? tidBuffer.get(wakeUpResource.getTid()) : "\n"
                    );
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
