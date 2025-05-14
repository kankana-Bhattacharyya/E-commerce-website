import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class SharedResource {
    private int counter = 0;
    private ReentrantLock lock = new ReentrantLock();

    public void incrementCounter(String threadName) {
        lock.lock();
        try {
            counter++;
            System.out.println("Thread " + threadName + ": Counter incremented to " + counter);
        } finally {
            lock.unlock();
        }
    }

    public int getCounter() {
        lock.lock();
        try {
            return counter;
        } finally {
            lock.unlock();
        }
    }
}

class Task implements Runnable {
    private SharedResource sharedResource;
    private String threadName;

    public Task(SharedResource sharedResource, String threadName) {
        this.sharedResource = sharedResource;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            sharedResource.incrementCounter(threadName);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}

public class MultithreadingExample {
    public static void main(String[] args) throws InterruptedException {
        SharedResource sharedResource = new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(new Task(sharedResource, "Thread-1"));
        executor.submit(new Task(sharedResource, "Thread-2"));
        executor.submit(new Task(sharedResource, "Thread-3"));

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Final counter value: " + sharedResource.getCounter());
    }
}
