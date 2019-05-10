import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.ArrayList;

public class Scheduler extends TimerTask {
	Semaphore sem1;
	Semaphore sem2;
	Semaphore sem3;
	Semaphore sem4;
	
	WorkerThread t1;
	WorkerThread t2;
	WorkerThread t3;
	WorkerThread t4;
	
	int majorFramePeriods = 0;
	
	// Lists to hold threads & semaphores for scheduling
	ArrayList<WorkerThread> threadList = new ArrayList<WorkerThread>();
	ArrayList<Semaphore> semList = new ArrayList<Semaphore>();
	
	public Scheduler() {
		semList.add(sem1 = new Semaphore(1));
		semList.add(sem2 = new Semaphore(1));
		semList.add(sem3 = new Semaphore(1));
		semList.add(sem4 = new Semaphore(1));
		
		threadList.add(t1 = new WorkerThread(sem1, 1, 1, 1));
		threadList.add(t2 = new WorkerThread(sem2, 2, 2, 2));
		threadList.add(t3 = new WorkerThread(sem3, 3, 4, 4));
		threadList.add(t4 = new WorkerThread(sem4, 4, 16, 16));
		
		// Set highest priorities then start threads
		t1.setPriority(10);
		t2.setPriority(9);
		t3.setPriority(8);
		t4.setPriority(7);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
	
	public void run() {
		majorFramePeriods += 1;
		
		if(majorFramePeriods > 160) { // 16 time units/frame, 10 frames = 160, end of scheduler
			t1.exit();
			t2.exit();
			t3.exit();
			t4.exit();
			
			sem1.release();
			sem2.release();
			sem3.release();
			sem4.release();
			
			// Notify main of completion, join threads, get stats
			synchronized(Scheduler.class) {
				Scheduler.class.notify();
			}
			try {
				t1.join();
				t2.join();
				t3.join();
				t4.join();
				
				t1.getStats();
				t2.getStats();
				t3.getStats();
				t4.getStats();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		
		// Schedule and run threads, check sem queue to indicate overrun
		for(int i = 0; i < threadList.size(); i++) {
			WorkerThread tempThread = threadList.get(i);
			Semaphore tempSem = semList.get(i);
			if(tempThread.periodCount == 0) {
				if(!tempSem.hasQueuedThreads()) {
					tempThread.overrun += 1;
				}
				tempSem.release();
				tempThread.periodCount = tempThread.period;
			}
			tempThread.periodCount -= 1;
		}		
	}

	public static void main(String[] args){		
		Scheduler sched = new Scheduler();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(sched, 0, 10); // Wake scheduler every 10ms to run

		synchronized(Scheduler.class) { // Wait on scheduler to complete
			try {
				Scheduler.class.wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		timer.cancel();
	}
}
