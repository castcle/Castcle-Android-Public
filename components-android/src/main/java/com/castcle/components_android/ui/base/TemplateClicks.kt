package com.castcle.components_android.ui.base

import com.castcle.common_model.model.feed.ContentUiModel
import com.castcle.common_model.model.setting.SettingMenuType

sealed class TemplateClicks(val deepLink: String) {

    class MenuClick(
        val menuType: SettingMenuType
    ) : TemplateClicks("")

    class AvatarClick(
        deeplink: String
    ) : TemplateClicks(deeplink)

    class LikeClick(
        deeplink: String = "",
        val contentUiModel: ContentUiModel
    ) : TemplateClicks(deeplink)
}
