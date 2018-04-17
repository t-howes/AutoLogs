package sample.thowes.autoservice.dagger.module

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@Singleton
class AppModule(private val context: Context) {

  @Provides
  @Singleton
  fun provideContext(): Context = context
}