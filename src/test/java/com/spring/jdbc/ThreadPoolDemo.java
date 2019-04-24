package com.spring.jdbc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ThreadPoolDemo implements Runnable{

    static ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    public void run() {

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        for (int i = 0 ; i < 100 ;i++){
            executorService.execute(new ThreadPoolDemo());
        }
        executorService.shutdown();

    }
}
