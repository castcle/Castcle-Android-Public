package com.castcle.data.repository

import com.castcle.common_model.model.userprofile.CreateCastResponse
import com.castcle.networking.service.exception.RemoteException
import io.reactivex.functions.Function
import retrofit2.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class CreateContentMapper @Inject constructor() :
    Function<Response<CreateCastResponse>, CreateCastResponse> {
    override fun apply(response: Response<CreateCastResponse>): CreateCastResponse {
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
        progressResponse: Response<CreateCastResponse>
    ): CreateCastResponse {
        return progressResponse.body() ?: throw RemoteException(
            code = progressResponse.code(),
            msg = progressResponse.message()
        )
    }
}
