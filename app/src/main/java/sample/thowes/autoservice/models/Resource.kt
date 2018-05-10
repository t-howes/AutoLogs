package sample.thowes.autoservice.models

class Resource<T> private constructor(val status: Status,
                                      val error: Throwable? = null,
                                      val data: T? = null){

  enum class Status {
    IDLE,
    LOADING,
    ERROR,
    SUCCESS
  }

  companion object {
    fun <T> idle(): Resource<T> {
      return Resource(Status.IDLE)
    }

    fun <T> loading(): Resource<T> {
      return Resource(Status.LOADING)
    }

    fun <T> error(error: Throwable): Resource<T> {
      return Resource(Status.ERROR, error = error)
    }

    fun <T> success(data: T): Resource<T> {
      return Resource(Status.SUCCESS, data = data)
    }
  }
}