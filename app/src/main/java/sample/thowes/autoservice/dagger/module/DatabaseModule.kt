package sample.thowes.autoservice.dagger.module

import dagger.Module
import dagger.Provides
import io.realm.Realm
import sample.thowes.autoservice.database.realm.helpers.CarHelper
import sample.thowes.autoservice.database.stub.CarDb
import javax.inject.Singleton

@Module
class DatabaseModule {

  @Singleton
  @Provides
  fun provideRealm(): Realm {
    return Realm.getDefaultInstance()
  }

  @Singleton
  @Provides
  fun provideCarDb(realm: Realm): CarDb {
    return CarHelper(realm)
  }
}