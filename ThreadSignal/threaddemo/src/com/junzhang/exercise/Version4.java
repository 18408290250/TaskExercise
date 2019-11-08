package com.junzhang.exercise;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Version4 {

    public static void main(String[] args) {
        final MyLock_1 myLock_1 = new MyLock_1();
        Object[] one = IntStream.rangeClosed('A', 'Z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] two = IntStream.rangeClosed('a', 'z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] three = IntStream.rangeClosed(1, 26)
                .mapToObj(x -> String.valueOf(x))
                .toArray();
        new Thread(() -> myLock_1.op(one,"1")).start();
        new Thread(() -> myLock_1.op(two,"2")).start();
        new Thread(() -> myLock_1.op(three,"3")).start();
    }
}

class MyLock_1{
    final Lock lock = new ReentrantLock();
    int flag  = 1;
    final Condition condition1 = lock.newCondition();
    final Condition condition2 = lock.newCondition();
    final Condition condition3 = lock.newCondition();


    //  不建议 run里面放 switch case
    public void op(Object[] data,String type) {
        lock.lock();
        try {
            for (int i = 0; i < data.length; i++) {
                switch (type){
                    case "1":
                        while (flag != 1) { // 防止虚假唤醒
                            condition1.await();
                        }
                        System.out.print(data[i] + " ");
                        flag = 2;
                        condition2.signal();
                        break;
                    case "2":
                        while (flag != 2) {
                            condition2.await();
                        }
                        System.out.print(data[i] + " ");
                        flag = 3;
                        condition3.signal();
                        break;
                    case "3":
                        while (flag != 3) {
                            condition3.await();
                        }
                        System.out.print(data[i] + " ");
                        flag = 1;
                        condition1.signal();
                        break;
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
