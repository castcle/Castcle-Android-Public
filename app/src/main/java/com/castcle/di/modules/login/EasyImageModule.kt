package com.castcle.di.modules.login

import android.content.Context
import dagger.Module
import dagger.Provides
import pl.aprilapps.easyphotopicker.EasyImage

@Module
class EasyImageModule {

    @Provides
    fun easyImage(context: Context): EasyImage = EasyImage
        .Builder(context)
        .allowMultiple(false)
        .build()
}
