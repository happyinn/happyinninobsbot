package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 排序数据类
 * 
 * 用于定义媒体文件和文件夹的排序方式和顺序
 * 
 * @property by 排序依据
 * @property order 排序顺序
 */

import kotlin.comparisons.reversed as kotlinReversed

data class Sort(
    val by: By,        // 排序依据
    val order: Order,   // 排序顺序
) {
    /**
     * 排序依据枚举
     * 
     * 定义了可以用来排序的不同属性
     */
    enum class By {
        TITLE,  // 按标题排序
        LENGTH, // 按长度/时长排序
        PATH,   // 按路径排序
        SIZE,   // 按大小排序
        DATE,   // 按日期排序
    }

    /**
     * 排序顺序枚举
     * 
     * 定义了升序和降序两种排序顺序
     */
    enum class Order {
        ASCENDING,  // 升序
        DESCENDING, // 降序
    }

    /**
     * 字符串比较器
     * 
     * 实现了智能字符串比较，支持数字自然排序
     */
    private val stringComparator = Comparator<String> { str1, str2 ->
        var str1Marker = 0
        var str2Marker = 0
        val str1Length = str1.length
        val str2Length = str2.length

        while (str1Marker < str1Length && str2Marker < str2Length) {
            val thisChunk = getChunk(str1, str1Length, str1Marker)
            str1Marker += thisChunk.length

            val thatChunk = getChunk(str2, str2Length, str2Marker)
            str2Marker += thatChunk.length

            // 如果两个块都包含数字字符，则按数字排序
            val result: Int
            if (thisChunk[0].isDigit() && thatChunk[0].isDigit()) {
                // 先比较数字长度
                val thisChunkLength = thisChunk.length
                val lengthDiff = thisChunkLength - thatChunk.length
                // 如果长度相同，比较每个数字字符
                if (lengthDiff == 0) {
                    for (i in 0 until thisChunkLength) {
                        val charDiff = thisChunk[i] - thatChunk[i]
                        if (charDiff != 0) {
                            return@Comparator charDiff
                        }
                    }
                    result = 0
                } else {
                    result = lengthDiff
                }
            } else {
                // 否则按字符串正常比较
                result = thisChunk.compareTo(thatChunk)
            }

            if (result != 0) {
                return@Comparator result
            }
        }

        // 如果一个字符串是另一个的前缀，较短的字符串排在前面
        return@Comparator str1Length - str2Length
    }

    /**
     * 创建视频比较器
     * 
     * 根据排序依据和顺序创建视频比较器
     * 
     * @return 视频比较器
     */
    fun videoComparator(): Comparator<Video> {
        val videoTitleComparator: Comparator<Video> = Comparator { video1, video2 ->
            return@Comparator stringComparator.compare(
                video1.displayName.lowercase(),
                video2.displayName.lowercase(),
            )
        }

        val videoPathComparator: Comparator<Video> = Comparator { video1, video2 ->
            return@Comparator stringComparator.compare(
                video1.path.lowercase(),
                video2.path.lowercase(),
            )
        }

        // 根据排序依据选择比较器
        val comparator = when (by) {
            By.TITLE -> videoTitleComparator
            By.LENGTH -> compareBy<Video> { it.duration }.then(videoTitleComparator)
            By.PATH -> videoPathComparator
            By.SIZE -> compareBy<Video> { it.size }.then(videoTitleComparator)
            By.DATE -> compareBy<Video> { it.dateModified }.then(videoTitleComparator)
        }

        // 根据排序顺序返回比较器或其反转
        return when (order) {
            Order.ASCENDING -> comparator
            Order.DESCENDING -> comparator.reversedCompat()
        }
    }

    /**
     * 创建文件夹比较器
     * 
     * 根据排序依据和顺序创建文件夹比较器
     * 
     * @return 文件夹比较器
     */
    fun folderComparator(): Comparator<Folder> {
        val folderNameComparator: Comparator<Folder> = Comparator { folder1, folder2 ->
            return@Comparator stringComparator.compare(
                folder1.name.lowercase(),
                folder2.name.lowercase(),
            )
        }

        val folderPathComparator: Comparator<Folder> = Comparator { folder1, folder2 ->
            return@Comparator stringComparator.compare(
                folder1.path.lowercase(),
                folder2.path.lowercase(),
            )
        }

        // 根据排序依据选择比较器
        val comparator = when (by) {
            By.TITLE -> folderNameComparator
            By.LENGTH -> compareBy<Folder> { it.mediaList.size }.then(folderNameComparator)
            By.PATH -> folderPathComparator
            By.SIZE -> compareBy<Folder> { it.mediaSize }.then(folderNameComparator)
            By.DATE -> compareBy<Folder> { it.dateModified }.then(folderNameComparator)
        }

        // 根据排序顺序返回比较器或其反转
        return when (order) {
            Order.ASCENDING -> comparator
            Order.DESCENDING -> comparator.reversedCompat()
        }
    }

    /**
     * 从字符串中获取一个连续的块
     * 
     * 连续的块可以是连续的数字或连续的非数字字符
     * 
     * @param string 源字符串
     * @param length 字符串长度
     * @param marker 当前位置
     * @return 提取的块
     */
    private fun getChunk(string: String, length: Int, marker: Int): String {
        var current = marker
        val chunk = StringBuilder()
        var c = string[current]
        chunk.append(c)
        current++
        if (c.isDigit()) {
            // 如果是数字，提取所有连续的数字
            while (current < length) {
                c = string[current]
                if (!c.isDigit()) {
                    break
                }
                chunk.append(c)
                current++
            }
        } else {
            // 如果不是数字，提取所有连续的非数字字符
            while (current < length) {
                c = string[current]
                if (c.isDigit()) {
                    break
                }
                chunk.append(c)
                current++
            }
        }
        return chunk.toString()
    }
}

/**
 * 反转比较器的扩展函数
 * 
 * 提供了一个兼容的方式来反转比较器
 * 
 * @return 反转后的比较器
 */
fun <T> Comparator<T>.reversedCompat(): Comparator<T> = kotlinReversed()