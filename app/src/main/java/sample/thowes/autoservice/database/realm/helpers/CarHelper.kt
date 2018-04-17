package sample.thowes.autoservice.database.realm.helpers

import io.realm.Realm
import sample.thowes.autoservice.database.realm.live.RealmLiveData
import sample.thowes.autoservice.database.stub.CarDb
import sample.thowes.autoservice.models.Car

class CarHelper(realm: Realm) : BaseHelper(realm), CarDb {

  override fun getCars(): RealmLiveData<Car> {
    return RealmLiveData(realm.where(Car::class.java).findAllAsync())
  }
}