package com.castcle.components_android.ui.base

sealed class TemplateClicks(val deepLink: String) {

    class MenuClick(
        deeplink: String
    ) : TemplateClicks(deeplink)

    class AvatarClick(
        deeplink: String
    ) : TemplateClicks(deeplink)
}
