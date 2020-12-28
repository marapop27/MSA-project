package com.example.plantbuddy.helpers

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun showErrorSnackbar(coordinatorLayout: CoordinatorLayout,  text:String)
{
    val snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(Color.RED)
    val view  = snackbar.view

    val params = CoordinatorLayout.LayoutParams(view.layoutParams.width, view.layoutParams.height)
    params.gravity = Gravity.TOP
    snackbar.view.layoutParams = params
    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    snackbar.show()
}

fun showMessageSnackbar(coordinatorLayout: CoordinatorLayout,  text:String)
{
    val snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(Color.GREEN)
    val view  = snackbar.view

    val params = CoordinatorLayout.LayoutParams(view.layoutParams.width, view.layoutParams.height)
    params.gravity = Gravity.TOP
    snackbar.view.layoutParams = params
    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    snackbar.show()
}