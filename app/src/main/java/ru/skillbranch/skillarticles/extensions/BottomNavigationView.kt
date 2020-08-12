package ru.skillbranch.skillarticles.extensions

import androidx.core.view.forEach
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.selectDestination(destination: NavDestination) {
    val item = menu.findItem(destination.id)
    menu.forEach { it.isCheckable = item != null }
    item?.isChecked = true
}