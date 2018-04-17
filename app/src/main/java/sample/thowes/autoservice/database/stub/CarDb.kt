package sample.thowes.autoservice.database.stub

import sample.thowes.autoservice.database.realm.live.RealmLiveData
import sample.thowes.autoservice.models.Car

interface CarDb {
  fun getCars(): RealmLiveData<Car>
}