package ru.skillbranch.skillarticles.viewmodels.base

import androidx.lifecycle.SavedStateHandle

interface IViewModelState {
    fun save(outState: SavedStateHandle)
    fun restore(savedState: SavedStateHandle) : IViewModelState {
        return this
    }
}