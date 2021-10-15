package com.castcle.usecase.setting

import android.app.Activity
import com.castcle.common.lib.schedulers.RxSchedulerProvider
import com.castcle.data.error.SettingsError.LogoutError
import com.castcle.data.model.dao.feed.CommentDao
import com.castcle.data.model.dao.user.UserDao
import com.castcle.data.storage.AppPreferences
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.splashscreen.SplashScreenActivity
import com.castcle.usecase.base.CompletableUseCase
import io.reactivex.Completable
import javax.inject.Inject

class LogoutCompletableUseCase @Inject constructor(
    rxSchedulerProvider: RxSchedulerProvider,
    private val appPreferences: AppPreferences,
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val sessionManagerRepository: SessionManagerRepository,
) : CompletableUseCase<Activity>(
    rxSchedulerProvider.io(),
    rxSchedulerProvider.main(),
    ::LogoutError
) {

    override fun create(input: Activity): Completable {
        return Completable.merge(
            listOf(
                // Clear current session
                Completable.fromAction { sessionManagerRepository.clearSession() },

                appPreferences.clearAll(),

                // Clear database
                Completable.fromAction {
                    commentDao.deleteComment()
                    userDao.deleteUser()
                }
            )
        ).doOnComplete {
            (input as? OnBoardActivity)?.run {
                SplashScreenActivity.start(input)
                finish()
            }
        }
    }
}
