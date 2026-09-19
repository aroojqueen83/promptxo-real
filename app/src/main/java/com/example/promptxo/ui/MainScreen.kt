package com.example.promptxo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.promptxo.ui.components.PromptXoBottomNav
import com.example.promptxo.ui.components.PromptXoDrawerContent
import com.example.promptxo.ui.components.PromptXoTopBar
import com.example.promptxo.ui.components.ScreenTab
import com.example.promptxo.ui.screens.FavoritesScreen
import com.example.promptxo.ui.screens.HomeScreen
import com.example.promptxo.ui.screens.ImageDetailScreen
import com.example.promptxo.ui.screens.ImagesScreen
import com.example.promptxo.ui.screens.PostDetailScreen
import com.example.ui.theme.PromptXoDarkBg
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: PromptXoViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentDestination by viewModel.currentDestination.collectAsState()
    val appPolicies by viewModel.appPolicies.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Intercept back button when on detail screens or open drawer
    BackHandler(enabled = currentDestination !is ScreenDestination.Main || drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (currentDestination !is ScreenDestination.Main) {
            viewModel.navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PromptXoDrawerContent(
                appPolicies = appPolicies,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                val isDetail = currentDestination !is ScreenDestination.Main
                PromptXoTopBar(
                    showBackButton = isDetail,
                    onBackClick = { viewModel.navigateBack() },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                // Bottom bar shown on top-level tabs (Home, Images, Favourite)
                if (currentDestination is ScreenDestination.Main) {
                    PromptXoBottomNav(
                        selectedTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            },
            containerColor = PromptXoDarkBg
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(PromptXoDarkBg)
            ) {
                when (val destination = currentDestination) {
                    is ScreenDestination.Main -> {
                        when (currentTab) {
                            ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
                            ScreenTab.IMAGES -> ImagesScreen(viewModel = viewModel)
                            ScreenTab.FAVOURITE -> FavoritesScreen(viewModel = viewModel)
                        }
                    }
                    is ScreenDestination.PostDetail -> {
                        PostDetailScreen(
                            post = destination.post,
                            viewModel = viewModel,
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }
                    is ScreenDestination.ImageDetail -> {
                        ImageDetailScreen(
                            imagePost = destination.imagePost,
                            viewModel = viewModel,
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }
                }
            }
        }
    }
}
