package com.loc.searchapp.presentation.details.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.loc.searchapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = { },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.Transparent,
            actionIconContentColor = colorResource(id = R.color.body),
            navigationIconContentColor = colorResource(id = R.color.body)
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onAddToCartClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_shopping_cart),
                    contentDescription = null
                )
            }
        }
    )
}