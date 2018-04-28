package com.osidocker.open.micro.utils;

public class GlobalIdWorker {

    private long workerId;
    private long datacenterId;
    private long businessId;
    private long sequence;

    public GlobalIdWorker (long workerId, long datacenterId,long businessId, long sequence){
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
        }
        if (businessId > maxBusinessId || businessId < 0) {
            throw new IllegalArgumentException(String.format("business Id can't be greater than %d or less than 0",maxBusinessId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenter id bits %d,business id bits %d, worker id bits %d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits,businessIdBits, workerIdBits, sequenceBits, workerId);
        System.out.println();

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.businessId = businessId;
        this.sequence = sequence;
    }

    private long twepoch = 1523434843340L;

    private long workerIdBits = 4L;
    private long datacenterIdBits = 4L;
    private long businessIdBits = 8L;
    private long sequenceBits = 8L;
    private long obligateBits = 9L;
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private long maxBusinessId = -1L ^ (-1L << businessIdBits);

    private long businessIdShift = sequenceBits;
    private long workerIdShift = businessIdShift+businessIdBits;
    private long datacenterIdShift = workerIdShift + workerIdBits;
    private long timestampLeftShift = datacenterIdShift +  datacenterIdBits+obligateBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    public long getWorkerId(){
        return workerId;
    }

    public long getDatacenterId(){
        return datacenterId;
    }

    public long getTimestamp(){
        return System.currentTimeMillis();
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return  ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                (businessId << businessIdShift)|
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen(){
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        GlobalIdWorker  worker = new GlobalIdWorker (1,1,1,1);
        for (int i = 0; i < 1; i++) {
            long v = worker.nextId();
            System.out.println(v+" = "+Long.toBinaryString(v));
        }
    }

}