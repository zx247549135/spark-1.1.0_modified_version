/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.shuffle.hash

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

import org.apache.spark._
import org.apache.spark.executor.ShuffleReadMetrics
import org.apache.spark.serializer.Serializer
import org.apache.spark.shuffle.FetchFailedException
import org.apache.spark.storage.{BlockId, BlockManagerId, ShuffleBlockId}
import org.apache.spark.util.CompletionIterator

private[hash] object BlockStoreShuffleFetcher extends Logging {
  def fetch[T](
      shuffleId: Int,
      reduceId: Int,
      context: TaskContext,
      serializer: Serializer,
      shuffleMetrics: ShuffleReadMetrics)
    : Iterator[T] =
  {
    logDebug("Fetching outputs for shuffle %d, reduce %d".format(shuffleId, reduceId))
    val blockManager = SparkEnv.get.blockManager

    val consolidateShuffleFiles =
      blockManager.conf.getBoolean("spark.shuffle.consolidateFiles", false)

    val startTime = System.currentTimeMillis
    val statuses = SparkEnv.get.mapOutputTracker.getServerStatuses(shuffleId, reduceId)
    logDebug("Fetching map output location for shuffle %d, reduce %d took %d ms".format(
      shuffleId, reduceId, System.currentTimeMillis - startTime))

    val splitsByAddress = new HashMap[BlockManagerId, ArrayBuffer[(Int,Int, Long)]]
    for (((address,fileid,size), index) <- statuses.zipWithIndex) {
      splitsByAddress.getOrElseUpdate(address, ArrayBuffer()) += ((index,fileid,size))
    }

    val blocksByAddress: Seq[(BlockManagerId, Seq[(BlockId, Long)])] = if(consolidateShuffleFiles)
      splitsByAddress.toSeq.map {
      case (address, splits) => {
        val splitsByFileID = new HashMap[Int, Long]
        splits.map(s => {if(splitsByFileID.contains(s._2))
                            splitsByFileID(s._2)+=s._3
                         else
                            splitsByFileID+=((s._2, s._3)) })
        (address, splitsByFileID.toSeq.map(s => (ShuffleBlockId(shuffleId, s._1, reduceId), s._2)))
      }
    }
    else
      splitsByAddress.toSeq.map {
        case (address, splits) =>
          (address, splits.map(s => (ShuffleBlockId(shuffleId, s._1, reduceId), s._3)))
      }
/*
    var flageStr=""
    for ((address, blockInfos) <- blocksByAddress) {
      val iterator = blockInfos.iterator
      while (iterator.hasNext) {
          val (blockId, size) = iterator.next()
          flageStr+=address
          flageStr+="   "
          flageStr+=reduceId
          flageStr+="   "
          flageStr+=blockId.asInstanceOf[ShuffleBlockId].mapId
          flageStr+="   "
          flageStr+=size
          flageStr+="\r\n"
        }
    }
    logDebug("$$$$$$$$$$$$$$$$$$"+flageStr)

    val blocksByAddressTwo: Seq[(BlockManagerId, Seq[(BlockId, Long)])] =splitsByAddress.toSeq.map {
      case (address, splits) =>
        (address, splits.map(s => (ShuffleBlockId(shuffleId, s._1, reduceId), s._3)))
    }

    var flageStr:String=""
    for ((address, blockInfos) <- blocksByAddress) {
        flageStr+="("+address+","+reduceId+","+blockInfos.map(_._2).sum+")  "
    }
    var flageStrTwo:String=""
    for ((address, blockInfos) <- blocksByAddressTwo) {
      flageStrTwo+="("+address+","+reduceId+","+blockInfos.map(_._2).sum+")  "
    }
    logDebug("$$$$$$$$$$$$$$$$$$"+flageStr+"||"+flageStrTwo)
*/

    def unpackBlock(blockPair: (BlockId, Option[Iterator[Any]])) : Iterator[T] = {
      val blockId = blockPair._1
      val blockOption = blockPair._2
      blockOption match {
        case Some(block) => {
          block.asInstanceOf[Iterator[T]]
        }
        case None => {
          blockId match {
            case ShuffleBlockId(shufId, mapId, _) =>
              val address = statuses(mapId.toInt)._1
              throw new FetchFailedException(address, shufId.toInt, mapId.toInt, reduceId)
            case _ =>
              throw new SparkException(
                "Failed to get block " + blockId + ", which is not a shuffle block")
          }
        }
      }
    }

    val blockFetcherItr = blockManager.getMultiple(blocksByAddress, serializer, shuffleMetrics)
    val itr = blockFetcherItr.flatMap(unpackBlock)

    val completionIter = CompletionIterator[T, Iterator[T]](itr, {
      context.taskMetrics.updateShuffleReadMetrics()
    })

    new InterruptibleIterator[T](context, completionIter)
  }
}
