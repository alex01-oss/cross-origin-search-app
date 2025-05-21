package com.loc.searchapp.core.domain.usecases.catalog

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.loc.searchapp.core.domain.model.catalog.Product
import com.loc.searchapp.core.domain.repository.CatalogRepository
import com.loc.searchapp.core.utils.CatalogPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCatalogPaging @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(
        token: String?,
        searchType: String = "code",
        searchQuery: String = ""
    ): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CatalogPagingSource(
                    repository = catalogRepository,
                    token = token?.let { "Bearer $it" },
                    searchQuery = searchQuery,
                    searchType = searchType
                )
            }
        ).flow
    }
}
