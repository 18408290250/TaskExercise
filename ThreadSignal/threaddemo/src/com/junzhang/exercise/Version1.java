package com.junzhang.exercise;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 三个任务，每个线程执行自己的任务，只需要判断是否该本次线程打印（设置状态）
 * 每个线程执行自己的run里面的业务代码，不存在加锁的概念，只是看当前这个线程是否需要打印
 *
 *      不需要锁
 */
public class Version1 {
    // 线程通信种使用到变量，需要volatile保证线程之间对变量的可见性
    // 不加锁，但是在循环执行，++操作需要保证原子性
    private static volatile AtomicInteger index = new AtomicInteger(0);// 控制是否打印

    static void op1(Object[] data){
        for (int i = 0; i < data.length;) {
            while (index.get() % 3 == 0) {
                System.out.print(data[i]);
               //  index ++;
                index.getAndIncrement();
                i++;
            }
        }
    }

    static void op2(Object[] data){
        for (int i = 0; i < data.length;) {
            while (index.get() % 3 == 1) {
                System.out.print(data[i]);
                //  index ++;
                index.getAndIncrement();
                i++;
            }
        }
    }

    static void op3(Object[] data){
        for (int i = 0; i < data.length;) {
            while (index.get() % 3 == 2) {
                System.out.print(data[i]);
                //  index ++;
                index.getAndIncrement();
                i++;
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

        new Thread(() -> Version1.op1(one)).start();
        new Thread(() -> Version1.op2(two)).start();
        new Thread(() -> Version1.op3(three)).start();

    }
}
