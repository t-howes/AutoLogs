package sample.thowes.autoservice.models

import io.realm.RealmObject
import sample.thowes.autoservice.extensions.today
import java.util.*

data class Car(val name: String? = null,
               val year: Int,
               val make: String,
               val model: String,
               val miles: Int? = 0,
               val lastUpdate: String? = Calendar.getInstance().today()) : RealmObject()