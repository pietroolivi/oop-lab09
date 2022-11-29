package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a standard implementation of the calculation.
 * 
 */
public final class MultiThreadedSumMatrix implements SumMatrix {
    private final int numWorkers;
    private final List<Double> list;

     /**
     * 
     * @param n
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.numWorkers = n;
        this.list = new ArrayList<>();
    }

    @Override
    public double sum(final double[][] matrix) {
        for (final double[] array : matrix) {
            for (final double elem : array) {
                list.add(elem);
            }
        }
        final int elemPerWorker = list.size() % this.numWorkers + list.size() / this.numWorkers;
        final List<Worker> workers = new ArrayList<>(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            workers.add(new Worker(list, i * elemPerWorker, elemPerWorker));
        }
        for (final Worker w: workers) {
            w.start();
        }
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getRes();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

    private class Worker extends Thread {
        private final List<Double> list;
        private final int startPos;
        private final int numElem;
        private long res;

         /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startPos
         *            the initial position for this worker
         * @param numElem
         *            the no. of elems to sum up for this worker
         */
        Worker(final List<Double> list, final int startPos, final int numElem) {
            this.list = list;
            this.startPos = startPos;
            this.numElem = numElem;
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getRes() {
            return this.res;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startPos + " to position " + (startPos + numElem - 1)); // NOPMD
            for (int i = startPos; i < list.size() && i < startPos + numElem; i++) {
                this.res += this.list.get(i);
            }
        }
    }
}
