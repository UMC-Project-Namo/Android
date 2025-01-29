package com.mongmong.namo.domain.model

import java.io.Serializable

data class CategoryModel(
    var categoryId: Long = 0,
    var name: String = "",
    var colorId: Int = 0,
    var basicCategory: Boolean = false,
    var isShare: Boolean = false,
) : Serializable {

    fun convertCategoryToScheduleCategory() : ScheduleCategoryInfo {
        return ScheduleCategoryInfo(
            categoryId = this.categoryId,
            colorId = this.colorId,
            name = this.name
        )
    }
}