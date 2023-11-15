@file:Suppress("unused")
@file:JvmName("StringExt")

package com.common.ktx

/** 从对象[String]以及候选对象[strArray]中按先后顺序获取非空[String]对象，若全部为空返回`""` */
fun String?.orEmpty(vararg strArray: String?): String {
    return this ?: (strArray.firstOrNull {
        null != it
    } ?: "")
}