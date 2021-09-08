package com.castcle.data.repository

import com.castcle.common_model.model.userprofile.UserProfileResponse
import com.castcle.networking.service.exception.RemoteException
import io.reactivex.functions.Function
import retrofit2.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class UserProfileMapper @Inject constructor() :
    Function<Response<UserProfileResponse>, UserProfileResponse> {
    override fun apply(response: Response<UserProfileResponse>): UserProfileResponse {
        return when (response.code()) {
            HttpURLConnection.HTTP_OK -> getUserProfileProgress(response)
            else -> {
                throw RemoteException(
                    code = response.code(),
                    msg = response.message()
                )
            }
        }
    }

    private fun getUserProfileProgress(
        progressResponse: Response<UserProfileResponse>
    ): UserProfileResponse {
        return progressResponse.body() ?: throw RemoteException(
            code = progressResponse.code(),
            msg = progressResponse.message()
        )
    }
}
