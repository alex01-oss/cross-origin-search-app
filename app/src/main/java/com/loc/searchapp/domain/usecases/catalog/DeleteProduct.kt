package com.loc.searchapp.domain.usecases.catalog

import com.loc.searchapp.domain.repository.CatalogRepository
import javax.inject.Inject

class DeleteProduct @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(code: String) {
        catalogRepository.deleteProduct(code = code)
    }
}