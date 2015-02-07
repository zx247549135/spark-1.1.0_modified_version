package org.apache.spark.util.random;
// no position
/**
 * Utility functions that help us determine bounds on adjusted sampling rate to guarantee exact
 * sample size with high confidence when sampling without replacement.
 */
private  class BinomialBounds {
  static public  double minSamplingRate () { throw new RuntimeException(); }
  /**
   * Returns a threshold <code>p</code> such that if we conduct n Bernoulli trials with success rate = <code>p</code>,
   * it is very unlikely to have more than <code>fraction * n</code> successes.
   */
  static public  double getLowerBound (double delta, long n, double fraction) { throw new RuntimeException(); }
  /**
   * Returns a threshold <code>p</code> such that if we conduct n Bernoulli trials with success rate = <code>p</code>,
   * it is very unlikely to have less than <code>fraction * n</code> successes.
   */
  static public  double getUpperBound (double delta, long n, double fraction) { throw new RuntimeException(); }
}
