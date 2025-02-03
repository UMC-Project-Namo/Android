package com.mongmong.namo.presentation.ui.home.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.CategoryModel
import com.mongmong.namo.domain.repositories.CategoryRepository
import com.mongmong.namo.domain.usecases.category.GetCategoriesUseCase
import com.mongmong.namo.presentation.enums.CategoryColor
import com.mongmong.namo.presentation.enums.SuccessType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel() {
    var isEditMode: Boolean = false

    private val _category = MutableLiveData<CategoryModel>()
    val category: LiveData<CategoryModel> = _category

    private val _categoryList = MutableLiveData<List<CategoryModel>>(emptyList())
    val categoryList: LiveData<List<CategoryModel>> = _categoryList

    private val _isComplete = MutableLiveData<Boolean>()
    val isComplete: LiveData<Boolean> = _isComplete

    private val _completeState = MutableLiveData<SuccessType>()
    val completeState: LiveData<SuccessType> = _completeState

    private val _color = MutableLiveData<CategoryColor?>(null)
    val color: LiveData<CategoryColor?> = _color

    private val _canDeleteCategory = MutableLiveData<Boolean>(true)
    val canDeleteCategory: LiveData<Boolean> = _canDeleteCategory

    /** 카테고리 조회 */
    fun getCategories() {
        viewModelScope.launch {
            Log.d("CategoryViewModel", "getCategories")
            _categoryList.value = getCategoriesUseCase.invoke()
        }
    }

    /** 카테고리 추가 */
    fun addCategory() {
        viewModelScope.launch {
            Log.d("CategoryViewModel", "addCategory ${_category.value}")
            _isComplete.postValue(repository.addCategory(
                category = _category.value!!
            ))
            _completeState.value = SuccessType.ADD
        }
    }

    /** 카테고리 수정 */
    fun editCategory() {
        viewModelScope.launch {
            Log.d("CategoryViewModel", "editCategory ${_category.value}")
            _isComplete.postValue(repository.editCategory(
                category = _category.value!!
            ))
            _completeState.value = SuccessType.EDIT
        }
    }

    /** 카테고리 삭제 */
    fun deleteCategory() {
        viewModelScope.launch {
            Log.d("CategoryViewModel", "deleteCategory ${_category.value}")
            _isComplete.postValue(repository.deleteCategory(
                category = _category.value!!
            ))
            _completeState.value = SuccessType.DELETE
        }
    }

    fun setCategory(category: CategoryModel) {
        _category.value = category
        if (category.colorId != 0) {
            _color.value = CategoryColor.findCategoryColorByColorId(category.colorId)
        }
    }

    fun setDeliable(canDelete: Boolean) {
        _canDeleteCategory.value = canDelete
    }

    fun updateTitle(title: String) {
        _category.value = _category.value?.copy(
            name = title
        )
    }

    fun updateCategoryColor(color: CategoryColor) {
        _color.value = color
        _category.value = _category.value?.copy(colorId = color.colorId)
    }

    fun updateIsShare(isShare: Boolean) {
        _category.value!!.isShare = isShare
    }

    fun isValidInput(): Boolean {
        return (!_category.value?.name.isNullOrEmpty()) && (_color.value != null)
    }
}