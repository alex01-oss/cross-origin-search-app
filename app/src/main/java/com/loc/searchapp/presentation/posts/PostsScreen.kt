package com.loc.searchapp.presentation.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.loc.searchapp.R
import com.loc.searchapp.domain.model.Post
import com.loc.searchapp.presentation.Dimens.BasePadding
import com.loc.searchapp.presentation.Dimens.PostsSpacerSize
import com.loc.searchapp.presentation.Dimens.activeButtonSize
import com.loc.searchapp.presentation.common.components.AppDialog
import com.loc.searchapp.presentation.common.components.SharedTopBar
import com.loc.searchapp.presentation.posts.components.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    posts: List<Post> = emptyList(),
    onPostClick: (Post) -> Unit,
    onBackClick: () -> Unit,
    onAddNewPost: () -> Unit,
    viewModel: PostViewModel
) {
    var showDeleteDialog by remember { mutableStateOf<Post?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SharedTopBar(
                title = stringResource(id = R.string.blog),
                onBackClick = onBackClick,
                showBackButton = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onAddNewPost,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { paddingValues ->
        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(BasePadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.size(activeButtonSize),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(BasePadding)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(stringResource(id = R.string.add_post_warn))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(BasePadding),
                verticalArrangement = Arrangement.spacedBy(BasePadding)
            ) {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        onClick = { onPostClick(post) },
                        onDeleteClick = { showDeleteDialog = post }
                    )
                }
                item { Spacer(modifier = Modifier.height(PostsSpacerSize)) }
            }

        }
    }

    if (showDeleteDialog != null) {
        AppDialog(
            title = stringResource(id = R.string.delete_post),
            message = stringResource(id = R.string.delete_post_dialog),
            confirmLabel = stringResource(id = R.string.delete),
            onConfirm = {
                showDeleteDialog?.let {
                    viewModel.deletePost(it.id.toInt())
                }
                showDeleteDialog = null
            },
            dismissLabel = stringResource(id = R.string.delete),
            onDismiss = { showDeleteDialog = null }
        )
    }
}