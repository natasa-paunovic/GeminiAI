package hoods.com.jetai.photo_reasoning

sealed interface PhotoReasoningUIState {

    data object Initial:PhotoReasoningUIState
    data object Loading:PhotoReasoningUIState

    data class Success(
        val output:String
    ):PhotoReasoningUIState

    data class Error(
        val errorMsg:String
    ):PhotoReasoningUIState

}