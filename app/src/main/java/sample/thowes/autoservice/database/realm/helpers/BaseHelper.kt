package sample.thowes.autoservice.database.realm.helpers

import io.realm.Realm

open class BaseHelper(protected val realm: Realm) {

  fun closeRealm() {
    realm.close()
  }
}