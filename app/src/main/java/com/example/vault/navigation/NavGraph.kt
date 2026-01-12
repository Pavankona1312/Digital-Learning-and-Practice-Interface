package com.example.vault.navigation

import ChapterScreen
import QuestionScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vault.ui.home.HomeScreen
import com.example.vault.ui.subject.SubjectScreen
import loadMockQuestionBitmap

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {

        // HOME
        composable("home") {
            HomeScreen(onOpenQuestion = {
                navController.navigate("question")
            })
        }

        // SUBJECT (Physics / Chem / Maths)
        composable("topics/{subjectId}") { backStack ->
            val subjectId =
                backStack.arguments?.getString("subjectId") ?: return@composable

            SubjectScreen(
                subjectId = subjectId,
                userId = "user_test",
                onBack = { navController.popBackStack() },
                onChapterClick = { chapterId ->
                    navController.navigate("chapter/$chapterId")
                }
            )
        }

        // CHAPTER (Kinematics)
        composable("chapter/{chapterId}") { backStack ->
            val chapterId =
                backStack.arguments?.getString("chapterId") ?: return@composable

            ChapterScreen(
                chapterId = chapterId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("question") {
            val context = LocalContext.current
            val bitmap = remember {
                loadMockQuestionBitmap(context)
            }
            QuestionScreen(
                questionBitmap = bitmap
            )
        }

    }
}

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route)
                    }

                }
            )
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}


data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNav(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Physics", Icons.Default.Build, "topics/P"),
        BottomNavItem("Chemistry", Icons.Default.Clear, "topics/C"),
        BottomNavItem("Maths", Icons.Default.Info, "topics/M"),
        BottomNavItem("Exam", Icons.Default.DateRange, "topics/E")
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected =
                currentRoute == item.route ||
                        currentRoute?.startsWith(item.route) == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(item.icon, contentDescription = item.label)
                },
                label = {
                    Text(text = item.label, fontSize = 12.sp)
                },
                alwaysShowLabel = true
            )
        }
    }
}
