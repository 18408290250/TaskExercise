package com.junzhang.exercise;

import java.util.stream.IntStream;

/**
 * 一个任务，一把锁，三个线程
 *
 * 抢到锁的线程 不一定应该执行业务代码
 */
public class Version2 {
    private static volatile int  index = 0;// 控制是否打印

    static void op(Object[] data,int type){
        for (int i = 0; i < data.length;) {
            synchronized (Version2.class) {
                while (index % 3 == type) {
                    System.out.print(data[i]);
                    index++;
                    i++; // 只有打过一个，才往下一个，标识也才往后 一个，不能在for里面 i++ (不满足条件未打印也下一个数)
                }
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

        new Thread(() -> Version2.op(one,0)).start();
        new Thread(() -> Version2.op(two,1)).start();
        new Thread(() -> Version2.op(three,2)).start();

    }
}
