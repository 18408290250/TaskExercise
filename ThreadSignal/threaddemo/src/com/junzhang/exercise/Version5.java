package com.junzhang.exercise;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Version5 implements Runnable {
    private Object[] data;
    private Object prev;
    private Object self;

    private Version5(Object[] data, Object prev, Object self) {
        this.data = data;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        for (int i = 0;i<data.length;i++){
            synchronized (prev) {
                synchronized (self) {  // notify不会立即释放对象锁，只有等到同步块代码执行完毕后才会释放
                    System.out.print(data[i] + " ");
                    self.notify(); // 顺序唤醒下一个等待线程
                }
                try {
                   // prev.wait();  // 释放prev对象锁，当前线程进入休眠，等待其他线程的notify操作再次唤醒
                    // 最后一次需要唤醒所有的，否则会死锁
                    if(i == data.length -1){
                        prev.notifyAll();
                    }
                    else{
                        prev.wait();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static void main(String[] args) throws Exception {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();


        Object[] one = IntStream.rangeClosed('A', 'Z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] two = IntStream.rangeClosed('a', 'z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] three = IntStream.rangeClosed(1, 26)
                .mapToObj(x -> String.valueOf(x))
                .toArray();

//        Arrays.stream(third)
//                .forEach(x -> System.out.println(x + " "));


        new Thread(new Version5(one, c, a)).start();
        TimeUnit.SECONDS.sleep(1);


        new Thread(new Version5(two, a, b)).start();
        TimeUnit.SECONDS.sleep(1);


        new Thread(new Version5(three, b, c)).start();
        TimeUnit.SECONDS.sleep(1); //确保按顺序A、B、C执行

    }
}
