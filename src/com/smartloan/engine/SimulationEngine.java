package com.smartloan.engine;
public class SimulationEngine implements Runnable {
    private final Object lock = new Object();
    private boolean ready = false;
    @Override
    public void run() {
        synchronized(lock) {
            try {
                while(!ready) { lock.wait(1000); break; }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
}
