package com.loc.searchapp.presentation.products_navigator

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.loc.searchapp.R
import com.loc.searchapp.domain.model.Product
import com.loc.searchapp.presentation.account.AccountScreen
import com.loc.searchapp.presentation.auth.AuthViewModel
import com.loc.searchapp.presentation.auth.LoginScreen
import com.loc.searchapp.presentation.auth.RegisterScreen
import com.loc.searchapp.presentation.cart.CartScreen
import com.loc.searchapp.presentation.product_details.DetailsScreen
import com.loc.searchapp.presentation.product_details.DetailsEvent
import com.loc.searchapp.presentation.product_details.DetailsViewModel
import com.loc.searchapp.presentation.home.HomeScreen
import com.loc.searchapp.presentation.home.HomeViewModel
import com.loc.searchapp.presentation.nvgraph.Route
import com.loc.searchapp.presentation.products_navigator.components.BottomNavigationItem
import com.loc.searchapp.presentation.products_navigator.components.NewsBottomNavigation
import com.loc.searchapp.presentation.search.SearchScreen
import com.loc.searchapp.presentation.search.SearchViewModel
import com.loc.searchapp.presentation.shared_vm.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductsNavigator() {
    val bottomNavigationItems = listOf(
        BottomNavigationItem(icon = R.drawable.home, text = "Home"),
        BottomNavigationItem(icon = R.drawable.search, text = "Search"),
        BottomNavigationItem(icon = R.drawable.shopping_cart, text = "Cart"),
        BottomNavigationItem(icon = R.drawable.person, text = "Account"),
    )

    val navController = rememberNavController()
    val backstackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    selectedItem = remember(key1 = backstackState) {
        when (backstackState?.destination?.route) {
            Route.HomeScreen.route -> 0
            Route.SearchScreen.route -> 1
            Route.CartScreen.route -> 2
            Route.AccountScreen.route -> 3
            else -> 0
        }
    }

    val isBottomBarVisible = remember(key1 = backstackState) {
        backstackState?.destination?.route == Route.HomeScreen.route ||
                backstackState?.destination?.route == Route.SearchScreen.route ||
                backstackState?.destination?.route == Route.CartScreen.route ||
                backstackState?.destination?.route == Route.AccountScreen.route
    }

//    LOCAL VM
    val homeViewModel: HomeViewModel = hiltViewModel()
    val searchViewModel: SearchViewModel = hiltViewModel()
    val catalogDetailsViewModel: DetailsViewModel = hiltViewModel()

//    GLOBAL VM
    val authViewModel: AuthViewModel = hiltViewModel()
    val productViewModel: ProductViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NewsBottomNavigation(
                    items = bottomNavigationItems,
                    selected = selectedItem,
                    onItemClick = { index ->
                        when (index) {
                            0 -> navigateToTab(
                                navController = navController,
                                route = Route.HomeScreen.route
                            )

                            1 -> navigateToTab(
                                navController = navController,
                                route = Route.SearchScreen.route
                            )

                            2 -> navigateToTab(
                                navController = navController,
                                route = Route.CartScreen.route
                            )

                            3 -> navigateToTab(
                                navController = navController,
                                route = Route.AccountScreen.route
                            )
                        }
                    },
                )
            }
        }
    ) {
        val bottomPadding = it.calculateBottomPadding()
        NavHost(
            navController = navController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            composable(route = Route.HomeScreen.route) {
                val products = homeViewModel.catalogFlow.collectAsLazyPagingItems()

                HomeScreen(
                    navigateToSearch = {
                        navigateToTab(
                            navController = navController,
                            route = Route.SearchScreen.route
                        )
                    },
                    navigateToDetails = { product ->
                        navigateToDetails(
                            navController = navController,
                            product = product
                        )
                    },
                    products = products,
                    authViewModel = authViewModel,
                    productViewModel = productViewModel
                )
            }

            composable(route = Route.SearchScreen.route) {
                val state = searchViewModel.state.value

                SearchScreen(
                    state = state,
                    event = { event ->
                        searchViewModel.viewModelScope.launch {
                            searchViewModel.onEvent(event)
                        }
                    },
                    navigateToDetails = {
                        navigateToDetails(
                            navController = navController,
                            product = it
                        )
                    },
                    searchViewModel = searchViewModel,
                    productViewModel = productViewModel
                )
            }

            composable(route = Route.CartScreen.route) {
                val cartItems = productViewModel.cartItems.collectAsState()

                CartScreen(
                    navigateToDetails = { cartItem ->
                        navigateToDetails(
                            navController = navController,
                            product = cartItem.product
                        )
                    },
                    cartItems = cartItems,
                    viewModel = productViewModel,
                    cartModified = productViewModel.cartModified,
                    onCartUpdated = { productViewModel.cartModified = false },
                    authViewModel = authViewModel,
                    onAuthClick = {
                        navController.navigate(Route.LoginScreen.route)
                    },
                )
            }

            composable(route = Route.AccountScreen.route) {
                AccountScreen(
                    onAuthClick = {
                        navController.navigate(Route.LoginScreen.route)
                    },
                    viewModel = authViewModel
                )
            }

            composable(route = Route.ProductDetailsScreen.route) {
                if (catalogDetailsViewModel.sideEffect != null) {
                    Toast.makeText(
                        LocalContext.current,
                        catalogDetailsViewModel.sideEffect,
                        Toast.LENGTH_SHORT).show()
                }
                catalogDetailsViewModel.onEvent(DetailsEvent.RemoveSideEffect)

                navController.previousBackStackEntry?.savedStateHandle?.get<Product?>("product")
                    ?.let { product ->
                        DetailsScreen(
                            event = catalogDetailsViewModel::onEvent,
                            navigateUp = { navController.navigateUp() },
                            product = product,
                        )
                    }
            }

            composable(route = Route.LoginScreen.route) {
                LoginScreen(
                    navController = navController,
                    onRegisterClick = {
                        navController.navigate(Route.RegisterScreen.route)
                    },
                    viewModel = authViewModel
                )
            }

            composable(route = Route.RegisterScreen.route) {
                RegisterScreen(
                    navController = navController,
                    onLoginClick = {
                        navController.navigate(Route.LoginScreen.route)
                    },
                    viewModel = authViewModel
                )
            }
        }
    }
}

private fun navigateToTab(
    navController: NavController,
    route: String
) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { homeScreen ->
            popUpTo(homeScreen) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}

private fun navigateToDetails(
    navController: NavController,
    product: Product

) {
    navController.currentBackStackEntry?.savedStateHandle?.set("product", product)
    navController.navigate(
        route = Route.ProductDetailsScreen.route
    )
}