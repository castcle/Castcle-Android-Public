package com.castcle.components_android.ui.base

import com.castcle.common_model.model.feed.ContentUiModel

sealed class TemplateClicks(val deepLink: String) {

    class MenuClick(
        deeplink: String
    ) : TemplateClicks(deeplink)

    class AvatarClick(
        deeplink: String
    ) : TemplateClicks(deeplink)

    class LikeClick(
        deeplink: String = "",
        val contentUiModel: ContentUiModel
    ) : TemplateClicks(deeplink)
}
