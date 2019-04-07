package com.osidocker.open.micro.concurrent;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.StampedLock;

public class StampedLockCUPDemo {
    static Thread[] holdCpuThreads = new Thread[3];
    static final StampedLock lock = new StampedLock();
    public static void main(String[] args) throws InterruptedException {
        new Thread() {
            public void run(){
                long readLong = lock.writeLock();
                LockSupport.parkNanos(610000000000L);
                lock.unlockWrite(readLong);
            }
        }.start();
        Thread.sleep(100);
        for( int i = 0; i < 3; ++i) {
            holdCpuThreads [i] = new Thread(new HoldCPUReadThread());
            holdCpuThreads [i].start();
        }
        Thread.sleep(10000);
        for(int i=0; i<3; i++) {
            holdCpuThreads [i].interrupt();
        }
    }

    private static class HoldCPUReadThread implements Runnable {

        public void run() {

            long lockR = lock.readLock();

            System.out.println(Thread.currentThread().getName() + " get read lock");

            lock.unlockRead(lockR);

        }

    }
 
}