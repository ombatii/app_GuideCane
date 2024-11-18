package com.ombati.guidecaneapp.presentation.side_effects


sealed class GuideCaneSideEffects {
    data class ShowSnackBarMessage(val message: String) : GuideCaneSideEffects()
    object NavigateToUpdateUser : GuideCaneSideEffects()
    object NavigateToViewHistory : GuideCaneSideEffects()

    object NavigateToProfile : GuideCaneSideEffects()

    object NavigateToAddUser : GuideCaneSideEffects()


}