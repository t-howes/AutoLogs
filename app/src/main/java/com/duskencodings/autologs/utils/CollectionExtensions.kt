package com.duskencodings.autologs.utils

fun <T> MutableList<T>.clearAndAdd(items: Collection<T>) {
  clear()
  addAll(items)
}