package dpi.atlas.util.ThreadPool;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Sep 25, 2009
 * Time: 8:33:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPool {
    private List idleThreads = Collections.synchronizedList(new ArrayList());
    private List busyThreads = Collections.synchronizedList(new ArrayList());
    int maxSize;

    public int getIdleCount() {
        return idleThreads.size();
    }

    public int getBusyCount() {
        return busyThreads.size();
    }

    public int getTotalCount() {
        return busyThreads.size() + idleThreads.size();
    }

    public ThreadPool(int initSize, int maxSize) {
        this.maxSize = maxSize;
        for (int i = 0; i < initSize; i++) {
            Runner thread = new Runner(this);
            thread.start();
            idleThreads.add(thread);
        }
        try {
            Thread.sleep(200); // Wait until all of threads being initialized.
        } catch (Exception e) {
        }
    }

    public synchronized void execute(Runnable task) throws NoIdleThreadException {
        Runner thread;
        if (idleThreads.size() > 0)
            thread = (Runner) idleThreads.remove(0);
        else if (idleThreads.size() == 0 && idleThreads.size() + busyThreads.size() >= maxSize)
            throw new NoIdleThreadException("IdleCount: " + idleThreads.size() +
                    ", BusyCount: " + busyThreads.size());
        else {
            thread = new Runner(this);
            thread.start();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        busyThreads.add(thread);
        thread.setTask(task);
        synchronized (thread) {
            thread.notify();
        }
    }

    static int id = 1;

    synchronized static String getUUID() {
        return String.valueOf(id++);
    }

    public List getIdleThreads() {
        return idleThreads;
    }

    public void setIdleThreads(List idleThreads) {
        this.idleThreads = idleThreads;
    }

    public List getBusyThreads() {
        return busyThreads;
    }

    public void setBusyThreads(List busyThreads) {
        this.busyThreads = busyThreads;
    }

    static ThreadPool pool = new ThreadPool(2, 4);

    public static void main(String[] a) {
        try {
            pool.execute(new Task1());
            pool.execute(new Task1());
            pool.execute(new Task1());
            pool.execute(new Task1());
//            pool.execute(new Task1());
            System.out.println("Busy = " + pool.getBusyCount());
            System.out.println("Idle = " + pool.getIdleCount());
            System.out.println("Total = " + pool.getTotalCount());

        } catch (Exception e) {
            e.printStackTrace();
        }
        new Monitor().start();

        try {
            Thread.sleep(10000);
            pool.execute(new Task1());
            pool.execute(new Task1());
            pool.execute(new Task1());
            pool.execute(new Task1());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Monitor extends Thread {
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
                    System.out.println("Idle: " + pool.getIdleCount() + ",Busy: " + pool.getBusyCount());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class Task1 implements Runnable {
        public void run() {
            int i = 0;
            try {
                while (true) {
                    if (i++ > 2)
                        return;
                    System.out.println(Thread.currentThread().getName() + " " + String.valueOf(i));
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}