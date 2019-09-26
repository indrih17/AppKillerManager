package com.thelittlefireman.appkillermanager.ui

enum class ProgressOfEliminatingOptimizations(val value: Int) {
    NotStarted(0),
    UserDenied(1),
    UserAgreed(2),
    Completed(3);
}

fun convertToProgress(value: Int) =
    when (value) {
        ProgressOfEliminatingOptimizations.NotStarted.value ->
            ProgressOfEliminatingOptimizations.NotStarted

        ProgressOfEliminatingOptimizations.UserDenied.value ->
            ProgressOfEliminatingOptimizations.UserDenied

        ProgressOfEliminatingOptimizations.UserAgreed.value ->
            ProgressOfEliminatingOptimizations.UserAgreed

        ProgressOfEliminatingOptimizations.Completed.value ->
            ProgressOfEliminatingOptimizations.Completed

        else ->
            throw IllegalArgumentException("Unknown value: $value")
    }