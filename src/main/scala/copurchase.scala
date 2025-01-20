import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.HashPartitioner

object Main extends App{
  val spark = SparkSession.builder.appName("CoPurchaseAnalysisRDD").config("spark.executor.memory", "6g").config("spark.executor.cores", "4").config("spark.driver.memory", "4g").getOrCreate()

  val startTime = System.nanoTime()

  val datasetPath = "gs://bucket-scp/order_products.csv"
  val ordersRDD: RDD[String] = spark.sparkContext.textFile(datasetPath)

  val parsedRDD: RDD[(Int, Int)] = ordersRDD.map(line => {
    val parts = line.split(",")
    (parts(0).toInt, parts(1).toInt)
  })

  val numExecutors = spark.conf.get("spark.executor.instances").toInt
  val coresPerExecutor = spark.conf.get("spark.executor.cores").toInt
  val numPartitions = (numExecutors * coresPerExecutor * 2).max(4)
  val partitionedRDD: RDD[(Int, Int)] = parsedRDD.partitionBy(new HashPartitioner(numPartitions))

 val groupedByOrderRDD: RDD[(Int, Iterable[Int])] = partitionedRDD.groupByKey()

  val productPairsRDD: RDD[(Int, Int)] = groupedByOrderRDD.flatMap {
    case (_, products) =>
      val productList = products.toList
      for {
        i <- productList
        j <- productList if i < j
      } yield (i, j)
  }

  val coPurchaseCountsRDD: RDD[((Int, Int), Int)] = productPairsRDD.map(pair => (pair, 1)).reduceByKey(_ + _)

  val sortedResultsRDD: RDD[((Int, Int), Int)] = coPurchaseCountsRDD.sortByKey()

  val outputRDD: RDD[String] = sortedResultsRDD.map {
    case ((product1, product2), count) => s"$product1,$product2,$count"
  }

  val repartitionedRDD = outputRDD.repartition(1)

  val outputPath = "gs://bucket-scp/output"
  repartitionedRDD.saveAsTextFile(outputPath)

  val endTime = System.nanoTime()

  val wallClockTime = (endTime - startTime) / 1e9
  println(f"Elapsed time: $wallClockTime%.2f seconds")

  val speedUp =  660.91 / wallClockTime
  val numberOfNodes = 2
  val strongScalingEfficiency = speedUp / numberOfNodes
  println(f"Strong Scaling Efficiency: $strongScalingEfficiency%.2f")
}