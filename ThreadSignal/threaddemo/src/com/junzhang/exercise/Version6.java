package com.junzhang.exercise;


import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class Version6 {
    // 信号量控制的是线程并发的数量
    // 参数permits就是允许同时运行的线程数目

    // one 初始信号量数量为1
    static Semaphore one = new Semaphore(1);
    // two,three 初始信号量数量为0
    static Semaphore two = new Semaphore(0);
    static Semaphore three = new Semaphore(0);

    static void op(Object[] data,int type){
        for(int i = 0;i<data.length;i++){
            try {
                if(type == 1){
                    one.acquire(); // one 获取信号执行,one 信号量减1,当one 为0时将无法继续获得该信号量
                    System.out.print(data[i]);
                    two.release(); // two 释放信号，two 信号量加1（初始为0），此时可以获取two 信号量
                }
                else if(type == 2){
                    two.acquire();
                    System.out.print(data[i]);
                    three.release();
                }
                else if(type == 3){
                    three.acquire();
                    System.out.print(data[i]);
                    one.release();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
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

        new Thread(() -> Version6.op(one,1)).start();
        new Thread(() -> Version6.op(two,2)).start();
        new Thread(() -> Version6.op(three,3)).start();

    }

}
