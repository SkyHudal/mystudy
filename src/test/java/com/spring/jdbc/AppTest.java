package com.spring.jdbc;


import com.spring.jdbc.core.SkyTemplate;
import com.spring.test.pojo.Person;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(3);
        new Thread(countDownLatch::countDown).start();
        new Thread(countDownLatch::countDown).start();
        new Thread(countDownLatch::countDown).start();
        countDownLatch.await();
        System.out.println("执行完毕");

        //创建一个固定的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //创建一个线程的线程池
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        //创建一个没有上限的线程池
        ExecutorService executorService2 = Executors.newCachedThreadPool();
        //延时执行的线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);


    }


    private void select() {
        SkyTemplate skyTemplate = new SkyTemplate("db/db.properties");
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("name", "xuxiao");
        List<Person> result = skyTemplate.findList("selectDemo", params);
        result.forEach(System.out::println);
    }
}
