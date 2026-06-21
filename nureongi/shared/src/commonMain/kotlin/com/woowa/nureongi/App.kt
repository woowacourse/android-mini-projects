package com.woowa.nureongi

import androidx.compose.runtime.Composable
import com.woowa.nureongi.ui.navigation.NureongiAppRoute
import com.woowa.nureongi.ui.theme.NureongiTheme

@Composable
fun App() {
    NureongiTheme {
        NureongiAppRoute()
    }
}
