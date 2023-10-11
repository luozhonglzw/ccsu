package cn.ccsu.cecs.student.controller.teacher;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMain {

//    public static void main(String[] args) {
//        int[] array = new int[]{1, 12, 1, 3, 3, 4, 2, 12, 5, 4};
//        bubblingSort(array);
//        System.out.println(Arrays.toString(array));
//    }

    public static void bubblingSort(int[] sortArray) {
        for (int i = 0; i < sortArray.length; i++) {
            for (int i1 = 0; i1 < sortArray.length - 1; i1++) {
                if (sortArray[i1] > sortArray[i1 + 1]) {
                    int temp = sortArray[i1];
                    sortArray[i1] = sortArray[i1 + 1];
                    sortArray[i1 + 1] = temp;
                }
            }
        }
    }

    public void selectionSort(int[] sortArray) {
        for (int i = 0; i < sortArray.length - 1; i++) {
            for (int j = i; j < sortArray.length - 1; j++) {
                if (sortArray[j] < sortArray[i]) {
                    int temp = sortArray[j];
                    sortArray[j] = sortArray[i];
                    sortArray[i] = temp;
                }
            }
        }
    }


    int count = 0;

    public static void main(String[] args) {
        CountDownLatch countDownLatcha = new CountDownLatch(100);
        CountDownLatch countDownLatchb = new CountDownLatch(100);
        CountDownLatch countDownLatchc = new CountDownLatch(100);

        AtomicInteger count = new AtomicInteger(0);

        new Thread(() -> {
            while (countDownLatcha.getCount() != 0 && count.get() <= 100) {
                if (countDownLatcha.getCount() == countDownLatchb.getCount()
                        && countDownLatcha.getCount() == countDownLatchc.getCount()) {
                    System.out.println(Thread.currentThread().getName() + "\tcount=" + count.getAndIncrement());
                    countDownLatcha.countDown();
                }
            }
        }, "t1").start();

        new Thread(() -> {
            while (countDownLatchb.getCount() != 0 && count.get() <= 100) {
                if (countDownLatcha.getCount() + 1 == countDownLatchb.getCount()
                        && countDownLatcha.getCount() + 1 == countDownLatchc.getCount()) {
                    System.out.println(Thread.currentThread().getName() + "\tcount=" + count.getAndIncrement());
                    countDownLatchb.countDown();
                }
            }
        }, "t2").start();

        new Thread(() -> {
            while (countDownLatchc.getCount() != 0 && count.get() <= 100) {
                if (countDownLatcha.getCount() == countDownLatchb.getCount()
                        && countDownLatchb.getCount() + 1 == countDownLatchc.getCount()) {
                    System.out.println(Thread.currentThread().getName() + "\tcount=" + count.getAndIncrement());
                    countDownLatchc.countDown();
                }
            }
        }, "t3").start();
    }

    public int num = 0;

    @Test
    public void test() {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(100);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    int await = cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + "\tcount=" + await);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }

    }

}
