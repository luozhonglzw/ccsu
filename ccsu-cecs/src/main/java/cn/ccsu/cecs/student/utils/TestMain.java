package cn.ccsu.cecs.student.utils;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TestMain {

    ThreadLocal<UserO> threadLocal = ThreadLocal.withInitial(() -> new UserO());

    public static void main(String[] args) {
        TestMain testMain = new TestMain();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 线程 的threadLocal 的UserO ：" + testMain.threadLocal.get());
            }, String.valueOf(i)).start();
        }

        try {
            SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserO userO = testMain.threadLocal.get();
        System.out.println("gc after : " + userO);
    }

    public void thread() {
        TestMain testMain = new TestMain();
        UserO userO = testMain.threadLocal.get();
        System.out.println("gc before : " + userO);

        System.gc();
        try {
            SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserO o = testMain.threadLocal.get();
        System.out.println("gc after : " + o);
    }

    @Test
    public void test(){
        UserO userO = new UserO();
        System.out.println("gc before :" + userO);
        userO = null;
        System.gc();
        try { SECONDS.sleep(1); } catch (Exception e) { e.printStackTrace(); }
        System.out.println("gc after :" + userO);
    }
}

class UserO {
    private Integer id;
    private String name;
}
