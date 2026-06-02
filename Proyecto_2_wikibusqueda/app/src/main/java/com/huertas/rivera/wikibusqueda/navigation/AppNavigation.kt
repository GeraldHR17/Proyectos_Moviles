package com.huertas.rivera.wikibusqueda.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huertas.rivera.wikibusqueda.ui.screens.ArticleScreen
import com.huertas.rivera.wikibusqueda.ui.screens.FavoritosScreen
import com.huertas.rivera.wikibusqueda.ui.screens.SearchScreen
import com.huertas.rivera.wikibusqueda.util.Routes
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val wikiViewModel: WikiViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH,
        modifier = modifier
    ) {
        composable(Routes.SEARCH) {
            SearchScreen(
                wikiViewModel = wikiViewModel,
                onArticleClick = { articleKey, articleTitle, articleDesc ->
                    // Modificado: Ahora enviamos también la descripción
                    navController.navigate("${Routes.ARTICLE}/$articleKey/$articleTitle/$articleDesc")
                },
                onNavigateToFavoritos = {
                    navController.navigate(Routes.FAVORITOS)
                }
            )
        }

        composable(Routes.FAVORITOS) {
            FavoritosScreen(
                viewModel = wikiViewModel,
                onBackClick = { navController.popBackStack() },
                onArticleClick = { articleKey, articleTitle, articleDesc ->
                    // Modificado: Al volver al artículo desde favoritos también mandamos su descripción
                    navController.navigate("${Routes.ARTICLE}/$articleKey/$articleTitle/$articleDesc")
                }
            )
        }

        composable(
            route = "${Routes.ARTICLE}/{articleKey}/{articleTitle}/{articleDesc}", // Agregado {articleDesc}
            arguments = listOf(
                navArgument("articleKey") { type = NavType.StringType },
                navArgument("articleTitle") { type = NavType.StringType },
                navArgument("articleDesc") { type = NavType.StringType } // Agregado
            )
        ) { backStackEntry ->
            val articleKey = backStackEntry.arguments?.getString("articleKey") ?: ""
            val articleTitle = backStackEntry.arguments?.getString("articleTitle") ?: ""
            val articleDesc = backStackEntry.arguments?.getString("articleDesc") ?: "Información de Wikipedia" // Agregado

            ArticleScreen(
                articleKey = articleKey,
                articleTitle = articleTitle,
                articleDesc = articleDesc, // <-- Debes pasarle esto a tu ArticleScreen
                viewModel = wikiViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
