package com.loc.searchapp.navigation.graph

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import com.loc.searchapp.feature.account.presentation.AccountScreen
import com.loc.searchapp.feature.auth.viewmodel.AuthViewModel
import com.loc.searchapp.feature.cart.presentation.CartScreen
import com.loc.searchapp.feature.catalog.presentation.CatalogScreen
import com.loc.searchapp.feature.catalog.viewmodel.ProductViewModel
import com.loc.searchapp.feature.details.presentation.DetailsScreen
import com.loc.searchapp.feature.details.viewmodel.DetailsViewModel
import com.loc.searchapp.feature.home.presentation.HomeScreen
import com.loc.searchapp.feature.home.viewmodel.HomeViewModel
import com.loc.searchapp.feature.posts.viewmodel.PostViewModel
import com.loc.searchapp.feature.search.presentation.SearchScreen
import com.loc.searchapp.feature.search.viewmodel.SearchViewModel
import com.loc.searchapp.navigation.utils.navigateToTab

fun NavGraphBuilder.homeScreens(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(route = Route.HomeScreen.route) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        val postViewModel: PostViewModel = hiltViewModel()

        HomeScreen(
            authViewModel = authViewModel,
            postViewModel = postViewModel,
            viewModel = homeViewModel,
            onCategoryClick = {
                navController.navigate(Route.CatalogScreen.route)
            },
            onPostClick = { post ->
                navController.navigate(Route.PostDetailedScreen.createRoute(post.id))
            },
        )
    }

    composable(route = Route.CatalogScreen.route) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        val productViewModel: ProductViewModel = hiltViewModel()
        val products = homeViewModel.catalogFlow.collectAsLazyPagingItems()

        CatalogScreen(
            products = products,
            authViewModel = authViewModel,
            productViewModel = productViewModel,
            onAuthClick = {
                navController.navigate(Route.LoginScreen.route)
            },
            navigateToSearch = {
                navigateToTab(navController, Route.SearchScreen.route)
            },
            navigateToDetails = { product ->
                navController.navigate(Route.ProductDetailsScreen.createRoute(product.code))
            },
            onBackClick = {
                navController.popBackStack()
            },
        )
    }

    composable(route = Route.SearchScreen.route) {
        val searchViewModel: SearchViewModel = hiltViewModel()
        val productViewModel: ProductViewModel = hiltViewModel()
        val homeViewModel: HomeViewModel = hiltViewModel()
        val state = searchViewModel.state.value

        SearchScreen(
            state = state,
            event = searchViewModel::onEvent,
            viewModel = searchViewModel,
            productViewModel = productViewModel,
            homeViewModel = homeViewModel,
            navigateToDetails = { product ->
                navController.navigate(Route.ProductDetailsScreen.createRoute(product.code))
            },
        )
    }

    composable(route = Route.CartScreen.route) {
        val productViewModel: ProductViewModel = hiltViewModel()
        val cartItems = productViewModel.cartItems.collectAsState()
        val cartModified = productViewModel.cartModified.collectAsState()

        CartScreen(
            cartItems = cartItems,
            viewModel = productViewModel,
            cartModified = cartModified,
            onCartUpdated = { productViewModel.resetCartModified() },
            authViewModel = authViewModel,
            onAuthClick = {
                navController.navigate(Route.LoginScreen.route)
            },
            navigateToDetails = { cartItem ->
                navController.navigate(Route.ProductDetailsScreen.createRoute(cartItem.product.code))
            },
        )
    }

    composable(
        route = Route.ProductDetailsScreen.route,
        arguments = listOf(navArgument("code") { type = NavType.StringType })
    ) { backStackEntry ->
        val code = backStackEntry.arguments?.getString("code") ?: return@composable
        val detailsViewModel: DetailsViewModel = hiltViewModel()
        val productViewModel: ProductViewModel = hiltViewModel()

        LaunchedEffect(code) {
            detailsViewModel.loadProduct(code)
        }

        val state by detailsViewModel.state
        val localCartChanges = productViewModel.localCartChanges.collectAsState().value

        DetailsScreen(
            state = state,
            onBackClick = { navController.navigateUp() },
            localCartChanges = localCartChanges,
            productViewModel = productViewModel,
        )
    }

    composable(route = Route.AccountScreen.route) {
        val productViewModel: ProductViewModel = hiltViewModel()

        AccountScreen(
            onAuthClick = {
                navController.navigate(Route.LoginScreen.route)
            },
            onLanguageClick = {
                navController.navigate(Route.LanguageScreen.route)
            },
            onAddPostClick = {
                navController.navigate(Route.PostsScreen.route)
            },
            onAboutClick = {
                navController.navigate(Route.AboutScreen.route)
            },
            onLogout = {
                navController.navigate(Route.LoginScreen.route)
            },
            viewModel = authViewModel,
            productViewModel = productViewModel,
        )
    }
}