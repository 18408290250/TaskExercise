package com.junzhang.exercise;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * 一个任务，一把锁，三个线程
 *
 * 抢到锁的线程 不一定应该执行业务代码
 */
public class Version3 {
    private static Lock lock = new ReentrantLock();// 保证线程的访问的互斥,同步
    private static volatile int  index = 0;// 控制是否打印

    static void op(Object[] data,int thread){
        for (int i = 0; i < data.length;) {
            try {
                lock.lock();
                while (index % 3 == thread) {
                    System.out.print(data[i]);
                    index ++;
                    i++;
                }
            } finally {
                lock.unlock();// unlock()操作必须放在finally块中
            }
        }
    }



    public static void main(String[] args) {

        Object[] one = IntStream.rangeClosed('A', 'Z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] two = IntStream.rangeClosed('a', 'z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] three = IntStream.rangeClosed(1, 26)
                .mapToObj(x -> String.valueOf(x))
                .toArray();

        new Thread(() -> Version3.op(one,0)).start();
        new Thread(() -> Version3.op(two,1)).start();
        new Thread(() -> Version3.op(three,2)).start();

    }
}
