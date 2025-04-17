package com.loc.searchapp.presentation.common

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loc.searchapp.domain.model.Product
import com.loc.searchapp.domain.repository.CatalogRepository

class CatalogPagingSource(
    private val repository: CatalogRepository,
    private val token: String?,
    private val searchQuery: String = "",
    private val searchType: String = "code"
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val response = repository.getCatalog(
                searchQuery = searchQuery,
                searchType = searchType,
                page = page,
                token = token
            )

            val catalog = response

            LoadResult.Page(
                data = catalog.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (catalog.items.isEmpty()) null else page + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}