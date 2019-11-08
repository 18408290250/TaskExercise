package com.junzhang.exercise;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Version4_1 {
    public static void main(String[] args) {
        final MyLock myLock = new MyLock();
        Object[] one = IntStream.rangeClosed('A', 'Z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] two = IntStream.rangeClosed('a', 'z')
                .mapToObj(x -> Character.toString((char) x))
                .toArray();

        Object[] three = IntStream.rangeClosed(1, 26)
                .mapToObj(x -> String.valueOf(x))
                .toArray();
        new Thread(() -> myLock.op1(one)).start();
        new Thread(() -> myLock.op2(two)).start();
        new Thread(() -> myLock.op3(three)).start();
    }
}

class MyLock{
    final Lock lock = new ReentrantLock();
    int flag  = 1;
    final Condition condition1 = lock.newCondition();
    final Condition condition2 = lock.newCondition();
    final Condition condition3 = lock.newCondition();


    public void op1(Object[] data)
    {
        lock.lock();
        try {
            for (int i = 0; i < data.length; i++) {
                while (flag != 1) { // 防止虚假唤醒
                    condition1.await(); //  第一个线程释放lock锁
                }
                System.out.print(data[i] + " ");
                flag = 2;
                condition2.signal();  // 第一个线程执行完唤醒第二个线程
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void op2(Object[] data)
    {
        lock.lock();
        try {
            for (int i = 0; i < data.length; i++) {
                while (flag != 2) {
                    condition2.await(); // 第二个线程释放lock锁
                }
                System.out.print(data[i] + " ");
                flag = 3;
                condition3.signal(); // 第二个线程执行完唤醒第三个线程
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void op3(Object[] data)
    {
        lock.lock();
        try
        {
            for (int i = 0; i < data.length; i++) {
                while (flag != 3) {
                    condition3.await(); // 第三个线程释放lock锁
                }
                System.out.print(data[i] + " ");
                flag = 1;
                condition1.signal(); // 第三个线程执行完唤醒第一个线程
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
