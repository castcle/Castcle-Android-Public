package com.castcle.networking.api.notification

import androidx.paging.*
import com.castcle.common_model.model.notification.*
import com.castcle.networking.api.feed.datasource.*
import com.castcle.networking.service.operators.ApiOperators
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NotificationRepository {
    suspend fun getNotification(
        source: String
    ): Flow<PagingData<NotificationPayloadModel>>

    fun putReadAllNotification(): Completable

    fun getBadgesNotification(): Single<NotificationUiModel>

    fun readNotification(readNotificationId: String): Completable
}

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationRepository {

    override suspend fun getNotification(source: String): Flow<PagingData<NotificationPayloadModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE_V1,
                prefetchDistance = DEFAULT_PREFETCH_V1
            ),
            pagingSourceFactory = {
                NotificationPagingDataSource(notificationApi, source)
            }
        ).flow
    }

    override fun putReadAllNotification(): Completable {
        return notificationApi
            .putReadAllNotification()
            .lift(ApiOperators.mobileApiError())
            .ignoreElements()
    }

    override fun getBadgesNotification(): Single<NotificationUiModel> {
        return notificationApi
            .getNotificationBadges()
            .lift(ApiOperators.mobileApiError())
            .map {
                it.toNotificationBadgeModel()
            }.firstOrError()
    }

    override fun readNotification(readNotificationId: String): Completable {
        return notificationApi
            .putReadNotification(readNotificationId)
            .lift(ApiOperators.mobileApiError())
            .ignoreElements()
    }
}
