package org.apache.spark.deploy;
// no position
/**
 * This suite tests the fault tolerance of the Spark standalone scheduler, mainly the Master.
 * In order to mimic a real distributed cluster more closely, Docker is used.
 * Execute using
 * ./bin/spark-class org.apache.spark.deploy.FaultToleranceTest
 * <p>
 * Make sure that that the environment includes the following properties in SPARK_DAEMON_JAVA_OPTS
 * *and* SPARK_JAVA_OPTS:
 *   - spark.deploy.recoveryMode=ZOOKEEPER
 *   - spark.deploy.zookeeper.url=172.17.42.1:2181
 * Note that 172.17.42.1 is the default docker ip for the host and 2181 is the default ZK port.
 * <p>
 * In case of failure, make sure to kill off prior docker containers before restarting:
 *   docker kill $(docker ps -q)
 * <p>
 * Unfortunately, due to the Docker dependency this suite cannot be run automatically without a
 * working installation of Docker. In addition to having Docker, the following are assumed:
 *   - Docker can run without sudo (see http://docs.docker.io/en/latest/use/basics/)
 *   - The docker images tagged spark-test-master and spark-test-worker are built from the
 *     docker/ directory. Run 'docker/spark-test/build' to generate these.
 */
private  class FaultToleranceTest implements scala.App, org.apache.spark.Logging {
  static public  org.apache.spark.SparkConf conf () { throw new RuntimeException(); }
  static public  java.lang.String ZK_DIR () { throw new RuntimeException(); }
  static public  scala.collection.mutable.ListBuffer<org.apache.spark.deploy.TestMasterInfo> masters () { throw new RuntimeException(); }
  static public  scala.collection.mutable.ListBuffer<org.apache.spark.deploy.TestWorkerInfo> workers () { throw new RuntimeException(); }
  static public  org.apache.spark.SparkContext sc () { throw new RuntimeException(); }
  static public  org.apache.curator.framework.CuratorFramework zk () { throw new RuntimeException(); }
  static public  int numPassed () { throw new RuntimeException(); }
  static public  int numFailed () { throw new RuntimeException(); }
  static public  java.lang.String sparkHome () { throw new RuntimeException(); }
  static public  java.lang.String containerSparkHome () { throw new RuntimeException(); }
  static public  java.lang.String dockerMountDir () { throw new RuntimeException(); }
  static public  void afterEach () { throw new RuntimeException(); }
  static public  void test (java.lang.String name, scala.Function0<scala.runtime.BoxedUnit> fn) { throw new RuntimeException(); }
  static public  void addMasters (int num) { throw new RuntimeException(); }
  static public  void addWorkers (int num) { throw new RuntimeException(); }
  /** Creates a SparkContext, which constructs a Client to interact with our cluster. */
  static public  void createClient () { throw new RuntimeException(); }
  static public  java.lang.String getMasterUrls (scala.collection.Seq<org.apache.spark.deploy.TestMasterInfo> masters) { throw new RuntimeException(); }
  static public  org.apache.spark.deploy.TestMasterInfo getLeader () { throw new RuntimeException(); }
  static public  void killLeader () { throw new RuntimeException(); }
  static public  void delay (scala.concurrent.duration.Duration secs) { throw new RuntimeException(); }
  static public  void terminateCluster () { throw new RuntimeException(); }
  /** This includes Client retry logic, so it may take a while if the cluster is recovering. */
  static public  void assertUsable () { throw new RuntimeException(); }
  /**
   * Asserts that the cluster is usable and that the expected masters and workers
   * are all alive in a proper configuration (e.g., only one leader).
   */
  static public  void assertValidClusterState () { throw new RuntimeException(); }
  static public  void assertTrue (boolean bool, java.lang.String message) { throw new RuntimeException(); }
}
