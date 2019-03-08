package com.duskencodings.autologs.extensions

fun <T> MutableList<T>.clearAndAdd(items: Collection<T>) {
  clear()
  addAll(items)
}