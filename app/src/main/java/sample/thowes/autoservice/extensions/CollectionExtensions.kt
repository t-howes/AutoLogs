package sample.thowes.autoservice.extensions

fun <T> MutableList<T>.clearAndAdd(items: Collection<T>) {
  clear()
  addAll(items)
}