import java.util.Arrays;
import java.util.concurrent.*;

public class WorkerThread extends Thread {
	int pid;
	float [][] matrix;
	int runCount = -2;
	int periodCount = 0;
	int overrun = 0;
	int doWorkCount;
	int period;
	
	private Semaphore sem;
	private volatile boolean running = true;
	
	// Assign sem, pid, period, doWorkCount and fill array
	public WorkerThread(Semaphore sem, int pid, int period, int doWorkCount) {
		this.sem = sem;
		this.pid = pid;
		this.period = period;
		this.doWorkCount = doWorkCount;
		
		matrix = new float [10][10];
		for(int i = 0; i < matrix.length; i++) {
			Arrays.fill(matrix[i], 1);
		}
	}
	
	public void doWork() { // Multiply columns by rows
		int [] columns = {0, 5, 1, 6, 2, 7, 3, 8, 4, 9};
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				matrix[columns[i]][j] *= 1;
			}
		}
	}
	
	public void getStats() { // Print stats for thread
		System.out.println("Thread " + pid + " ran " + runCount + " times");
		System.out.println("Overrun " + overrun + " times\n______________");
	}
	
	public void exit() { // Release semaphore and exit thread
		sem.release();
		running = false;
	}
	
	public void run() {
		while(running) {
			sem.acquireUninterruptibly(); // Wait until semaphore is acquired
			for(int i = 0; i < doWorkCount; i++) {
				doWork();
			}
			runCount += 1;
		}
	}
}
