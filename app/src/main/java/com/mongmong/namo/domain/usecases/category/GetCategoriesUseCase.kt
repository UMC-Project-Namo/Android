package com.mongmong.namo.domain.usecases.category

import android.util.Log
import com.mongmong.namo.domain.model.CategoryModel
import com.mongmong.namo.domain.repositories.CategoryRepository
import javax.inject.Inject

open class GetCategoriesUseCase @Inject constructor(private var categoryRepository: CategoryRepository) {
    suspend operator fun invoke(): List<CategoryModel> {
        Log.d("GetCategoriesUseCase", "getCategories")
        return categoryRepository.getCategories()
    }
}