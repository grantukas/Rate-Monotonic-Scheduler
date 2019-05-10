# Rate-Monotonic-Scheduler
Class project for Chapman CPSC-380 (Operating Systems)

4 threads, T1 period of 1 unit, T2 period of 2 units, T3 period of 4 units, T4 period of 16 units. Each thread must execute a doWork function each time it runs. Scheduler has a major frame of 16 units and must run 10 times. The scheduler is woken up by a timer to schedule each thread and track how many times each thread runs and when an overrun condition occurs. The do work function is design to be inefficient and cause overruns (multiplying matrix rows by columns). The purpose of this assignment is to test the cases in which a thread overruns and analyze how overruns affect the other threads and scheduling.
