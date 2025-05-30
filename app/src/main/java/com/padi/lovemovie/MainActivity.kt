package com.padi.lovemovie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.padi.lovemovie.item.VideoSheet
import com.padi.lovemovie.page.ComingSoonPage
import com.padi.lovemovie.page.DetailPage
import com.padi.lovemovie.page.HotPage
import com.padi.lovemovie.page.RankPage
import com.padi.lovemovie.ui.theme.爱搜片Theme
import com.padi.lovemovie.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

data class BottomNavItem(
    val route: String, val label: String, val icon: ImageVector,
)


val LocalMainNavController = staticCompositionLocalOf<NavHostController> {
    error("没有默认值")
}


val LocalBottomSheet = staticCompositionLocalOf<SharedViewModel> {
    error("没有默认值")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            爱搜片Theme {
                val homeNavController = rememberNavController()
                val mainNavController = rememberNavController()
                val sharedViewModel: SharedViewModel = viewModel()
                CompositionLocalProvider(
                    LocalMainNavController provides mainNavController,
                    LocalBottomSheet provides sharedViewModel
                ) {
                    NavHost(
                        navController = mainNavController,
                        startDestination = "home",
                    ) {
                        composable("home") {
                            val sheetState = rememberModalBottomSheetState(
                                skipPartiallyExpanded = false,
                            )
                            val showSheet = rememberSaveable {
                                mutableStateOf(false)
                            }
                            VideoSheet(showSheet.value, "", sheetState) {
                                showSheet.value = false
                            }
                            Scaffold(
                                topBar = {
                                    TopAppBar(title = { Text("爱搜片") }, actions = {
                                        IconButton(onClick = {
                                            showSheet.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null
                                            )
                                        }
                                    })
                                },
                                bottomBar = {
                                    val navBackStackEntry =
                                        homeNavController.currentBackStackEntryAsState().value
                                    val currentRoute = navBackStackEntry?.destination?.route
                                    val items = listOf(
                                        BottomNavItem(
                                            route = "home",
                                            label = "首页",
                                            icon = Icons.Default.Home
                                        ), BottomNavItem(
                                            route = "hot",
                                            label = "热榜",
                                            icon = Icons.Default.Search
                                        ), BottomNavItem(
                                            route = "profile",
                                            label = "我的",
                                            icon = Icons.Default.Person
                                        )
                                    )
                                    NavigationBar {
                                        items.forEach { item ->
                                            NavigationBarItem(
                                                selected = currentRoute == item.route,
                                                onClick = {
                                                    homeNavController.navigate(item.route) {
                                                        popUpTo(homeNavController.graph.startDestinationId)
                                                        launchSingleTop = true
                                                    }
                                                },
                                                alwaysShowLabel = false,
                                                icon = {
                                                    Icon(
                                                        item.icon,
                                                        contentDescription = item.label
                                                    )
                                                },
                                                label = { Text(item.label) },
                                            )
                                        }
                                    }
                                },
                            ) { innerPadding ->
                                fun slideInFromBottom() = fadeIn() + slideInVertically { it }
                                fun slideOutToTop() = fadeOut() + slideOutVertically { -it }
                                NavHost(
                                    navController = homeNavController,
                                    startDestination = "home",
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable(
                                        "home", enterTransition = {
                                            slideInFromBottom()
                                        },
                                        exitTransition = {
                                            slideOutToTop()
                                        }) {
                                        HomeTabLayoutWithPager()
                                    }
                                    composable(
                                        "hot", enterTransition = {
                                            slideInFromBottom()
                                        },
                                        exitTransition = {
                                            slideOutToTop()
                                        }) {
                                        LazyColumn {
                                            items(15) { index ->
                                                ListItem(
                                                    headlineContent = { Text("测试${index}") },
                                                    modifier = Modifier
                                                        .padding(top = 8.dp)
                                                        .clickable {

                                                        },
                                                    leadingContent = {
                                                        Icon(
                                                            Icons.Default.Notifications,
                                                            contentDescription = null
                                                        )
                                                    },
                                                )
                                            }
                                        }
                                    }
                                    composable(
                                        "profile", enterTransition = {
                                            slideInFromBottom()
                                        },
                                        exitTransition = {
                                            slideOutToTop()
                                        }) {
                                        SettingsScreen()
                                    }
                                }
                            }
                        }
                        composable(
                            route = "detail/{videoUrl}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern =
                                        "android-app://androidx.navigation/detail/{videoUrl}"
                                }
                            )) { backStackEntry ->
                            val content = backStackEntry.arguments?.getString("videoUrl")
                            DetailPage(content) { mainNavController.popBackStack() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTabLayoutWithPager() {
    val tabs = mapOf<String, ImageVector>(
        "热门推荐" to Icons.Default.ShoppingCart,
        "豆瓣排行" to Icons.Default.FavoriteBorder,
        "即将上映" to Icons.Default.DateRange
    )
    val pageState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pageState.currentPage,
        ) {
            tabs.keys.forEachIndexed { index, title ->
                Tab(selected = pageState.currentPage == index, onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(index)
                    }
                }, content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = tabs.values.elementAt(index),
                            contentDescription = title,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(title)
                    }
                })
            }
        }

        HorizontalPager(state = pageState) { page ->
            when (page) {
                0 -> HotPage()
                1 -> RankPage()
                2 -> ComingSoonPage()
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val notificationsEnabled = remember { mutableStateOf(true) }
    val darkModeEnabled = remember { mutableStateOf(false) }
    val selectedLanguage = remember { mutableStateOf("English") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        ListItem(
            headlineContent = { Text("通知设置") },
            modifier = Modifier.padding(top = 8.dp),
            leadingContent = {
                Icon(Icons.Default.Notifications, contentDescription = null)
            })

        Divider()

        ListItem(headlineContent = { Text("启用通知") }, trailingContent = {
            Switch(
                checked = notificationsEnabled.value,
                onCheckedChange = { notificationsEnabled.value = it })
        })

        // 分组2: 外观
        ListItem(
            headlineContent = { Text("外观") },
            modifier = Modifier.padding(top = 16.dp),
            leadingContent = {
                Icon(Icons.Default.Build, contentDescription = null)
            })

        Divider()

        ListItem(headlineContent = { Text("深色模式") }, trailingContent = {
            Switch(
                checked = darkModeEnabled.value,
                onCheckedChange = { darkModeEnabled.value = it })
        })

        ListItem(
            headlineContent = { Text("语言") },
            supportingContent = { Text(selectedLanguage.value) },
            trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable {

            })

        // 分组3: 其他
        ListItem(
            headlineContent = { Text("其他设置") },
            modifier = Modifier.padding(top = 16.dp),
            leadingContent = {
                Icon(Icons.Default.MailOutline, contentDescription = null)
            })

        Divider()

        ListItem(
            headlineContent = { Text("清除缓存") },
            leadingContent = {
                Icon(Icons.Default.Close, contentDescription = null)
            },
            trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable { /* 处理清除缓存 */ })

        ListItem(
            headlineContent = { Text("隐私政策") },
            leadingContent = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable { /* 打开隐私政策 */ })

        ListItem(
            headlineContent = { Text("关于应用") },
            leadingContent = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) },
            modifier = Modifier.clickable { /* 打开关于页面 */ })
    }
}

