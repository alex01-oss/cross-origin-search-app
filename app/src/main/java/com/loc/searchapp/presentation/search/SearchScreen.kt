package com.loc.searchapp.presentation.search

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.paging.compose.collectAsLazyPagingItems
import com.loc.searchapp.domain.model.Product
import com.loc.searchapp.presentation.Dimens.MediumPadding1
import com.loc.searchapp.presentation.common.base.ProductViewModel
import com.loc.searchapp.presentation.common.components.ProductsList
import com.loc.searchapp.presentation.home.HomeViewModel
import com.loc.searchapp.presentation.search.components.BurgerMenu
import com.loc.searchapp.presentation.search.components.SearchTopSection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: SearchState,
    event: (SearchEvent) -> Unit,
    navigateToDetails: (Product) -> Unit,
    viewModel: SearchViewModel,
    homeViewModel: HomeViewModel,
    productViewModel: ProductViewModel,
) {
    val lazyProducts = viewModel.products.collectAsLazyPagingItems()
    val localCartChanges = productViewModel.localCartChanges.collectAsState().value
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val menu = homeViewModel.menu.collectAsState().value

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier
                    .width(300.dp)
                    .statusBarsPadding(),
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                BurgerMenu(
                    menu = menu,
                    onOpenUrl = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    },
                    onNavigateToSearch = { searchType ->
                        event(SearchEvent.ChangeSearchType(searchType))
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = MediumPadding1),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            SearchTopSection(
                                searchQuery = state.searchQuery,
                                placeholder = state.placeholder,
                                onQueryChange = { query ->
                                    event(
                                        SearchEvent.UpdateSearchQuery(
                                            query,
                                            state.searchType,
                                            state.page
                                        )
                                    )
                                },
                                onSearch = { event(SearchEvent.SearchProducts) },
                                onBurgerClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open()
                                        else drawerState.close()
                                    }
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier
                        .padding(horizontal = MediumPadding1)
                        .padding(top = paddingValues.calculateTopPadding())
                        .fillMaxSize()
                ) {
                    ProductsList(
                        items = lazyProducts,
                        onClick = navigateToDetails,
                        onAdd = { productViewModel.addToCart(it.code) },
                        onRemove = { productViewModel.removeFromCart(it.code) },
                        localCartChanges = localCartChanges,
                        showShimmerOnFirstLoad = false
                    )
                }
            }
        )
    }
}