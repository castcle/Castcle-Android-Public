package com.castcle.data.error

sealed class NavigationError(
    cause: Throwable?
) : AppError(cause) {

    class UnsupportedNavigationError(
        currentGraph: String?,
        currentDestination: String?
    ) : NavigationError(
        RuntimeException("Unsupported navigation on $currentGraph at $currentDestination")
    )
}
