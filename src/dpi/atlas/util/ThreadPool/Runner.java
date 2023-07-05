package dpi.atlas.util.ThreadPool;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Sep 25, 2009
 * Time: 9:37:50 AM
 * To change this template use File | Settings | File Templates.
 */
class Runner extends Thread {
    Runnable task;
    ThreadPool pool;

    public Runner(ThreadPool pool) {
        this.pool = pool;
    }
  
    public void run() {
        while (true) {
            try {
                synchronized (this) {
                    this.wait();
                }
                task.run();
                task = null;
                pool.getBusyThreads().remove(this);
                pool.getIdleThreads().add(this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public Runnable getTask() {
        return task;
    }
}
