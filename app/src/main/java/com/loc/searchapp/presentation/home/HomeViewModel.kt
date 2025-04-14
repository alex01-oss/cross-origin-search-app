package com.loc.searchapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.searchapp.domain.model.Product
import com.loc.searchapp.domain.usecases.catalog.CatalogUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalogUseCases: CatalogUseCases,
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            _products.value = catalogUseCases.getCatalog()
        }
    }

    fun addToCart(product: Product, onCartUpdated: () -> Unit) {
        viewModelScope.launch {
            catalogUseCases.addProduct(product)
            val updatedList = _products.value.map {
                if (it.code == product.code) it.copy(isInCart = true) else it
            }
            _products.value = updatedList
            onCartUpdated()
        }
    }

    fun removeFromCart(code: String, onCartUpdated: () -> Unit) {
        viewModelScope.launch {
            catalogUseCases.deleteProduct(code)
            val updatedList = _products.value.map {
                if (it.code == code) it.copy(isInCart = false) else it
            }
            _products.value = updatedList
            onCartUpdated()
        }
    }
}
