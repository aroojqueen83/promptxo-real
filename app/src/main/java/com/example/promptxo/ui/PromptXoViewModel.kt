package com.example.promptxo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.promptxo.ads.AdMobManager
import com.example.promptxo.data.local.AppDatabase
import com.example.promptxo.data.local.FavoriteEntity
import com.example.promptxo.data.model.AppPolicies
import com.example.promptxo.data.model.ImagePostItem
import com.example.promptxo.data.model.PostItem
import com.example.promptxo.data.remote.FirebaseRepository
import com.example.promptxo.ui.components.ScreenTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Main : ScreenDestination()
    data class PostDetail(val post: PostItem) : ScreenDestination()
    data class ImageDetail(val imagePost: ImagePostItem) : ScreenDestination()
}

class PromptXoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseRepository()
    val adMobManager = AdMobManager(application.applicationContext)

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "promptxo_db"
    ).fallbackToDestructiveMigration().build()
    private val favoriteDao = db.favoriteDao()

    // Navigation state
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _currentDestination = MutableStateFlow<ScreenDestination>(ScreenDestination.Main)
    val currentDestination: StateFlow<ScreenDestination> = _currentDestination.asStateFlow()

    // Search and filters
    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery: StateFlow<String> = _homeSearchQuery.asStateFlow()

    private val _selectedHomeCategory = MutableStateFlow("All")
    val selectedHomeCategory: StateFlow<String> = _selectedHomeCategory.asStateFlow()

    private val _imageSearchQuery = MutableStateFlow("")
    val imageSearchQuery: StateFlow<String> = _imageSearchQuery.asStateFlow()

    private val _selectedImageCategory = MutableStateFlow("All")
    val selectedImageCategory: StateFlow<String> = _selectedImageCategory.asStateFlow()

    // Pagination counts
    // Requirement Home: starts at 5 posts, Load More adds 3 posts after rewarded ad
    private val _homeVisibleCount = MutableStateFlow(5)
    val homeVisibleCount: StateFlow<Int> = _homeVisibleCount.asStateFlow()

    // Requirement Images: starts at 10 posts, Load More adds 6 posts after rewarded ad
    private val _imageVisibleCount = MutableStateFlow(10)
    val imageVisibleCount: StateFlow<Int> = _imageVisibleCount.asStateFlow()

    // App Policies
    private val _appPolicies = MutableStateFlow(AppPolicies())
    val appPolicies: StateFlow<AppPolicies> = _appPolicies.asStateFlow()

    // Raw data flows from Firestore / Seed
    private val _rawHomePosts = MutableStateFlow<List<PostItem>>(repository.defaultHomePosts)
    private val _rawImagePosts = MutableStateFlow<List<ImagePostItem>>(repository.defaultImagePosts)

    // Favorites from Room
    val favoriteIds: StateFlow<List<String>> = favoriteDao.getAllFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFavorites: StateFlow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Home Posts
    val filteredHomePosts: StateFlow<List<PostItem>> = combine(
        _rawHomePosts,
        _homeSearchQuery,
        _selectedHomeCategory
    ) { posts, query, category ->
        posts.filter { post ->
            val matchesCategory = category.equals("All", ignoreCase = true) ||
                    post.category.equals(category, ignoreCase = true) ||
                    post.homeCategory.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    post.title.contains(query, ignoreCase = true) ||
                    post.description.contains(query, ignoreCase = true) ||
                    post.step1prompt.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultHomePosts.sortedByDescending { it.createdAt })

    // Filtered Image Posts
    val filteredImagePosts: StateFlow<List<ImagePostItem>> = combine(
        _rawImagePosts,
        _imageSearchQuery,
        _selectedImageCategory
    ) { images, query, category ->
        images.filter { img ->
            val matchesCategory = when (category.lowercase()) {
                "all" -> true
                "trending" -> img.categoryfortrending.isNotBlank()
                "new" -> img.categoryfornew.isNotBlank()
                else -> img.imagetitle.contains(category, ignoreCase = true) ||
                        img.imageprompt.contains(category, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    img.imagetitle.contains(query, ignoreCase = true) ||
                    img.imageprompt.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultImagePosts.sortedByDescending { it.createdAt })

    init {
        // Collect real-time updates from Firebase
        viewModelScope.launch {
            repository.getHomePostsFlow().collect { posts ->
                if (posts.isNotEmpty()) {
                    _rawHomePosts.value = posts
                }
            }
        }

        viewModelScope.launch {
            repository.getImagePostsFlow().collect { images ->
                if (images.isNotEmpty()) {
                    _rawImagePosts.value = images
                }
            }
        }

        viewModelScope.launch {
            _appPolicies.value = repository.getAppPolicies()
        }
    }

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
        _currentDestination.value = ScreenDestination.Main
    }

    fun navigateToPostDetail(post: PostItem) {
        _currentDestination.value = ScreenDestination.PostDetail(post)
    }

    fun navigateToImageDetail(imagePost: ImagePostItem) {
        _currentDestination.value = ScreenDestination.ImageDetail(imagePost)
    }

    fun navigateBack() {
        _currentDestination.value = ScreenDestination.Main
    }

    fun setHomeSearch(query: String) {
        _homeSearchQuery.value = query
    }

    fun setHomeCategory(category: String) {
        _selectedHomeCategory.value = category
    }

    fun setImageSearch(query: String) {
        _imageSearchQuery.value = query
    }

    fun setImageCategory(category: String) {
        _selectedImageCategory.value = category
    }

    // Home: Load More clicked -> rewarded ad shown -> adds 3 posts
    fun loadMoreHomePosts() {
        _homeVisibleCount.value += 3
    }

    // Images: Load More clicked -> rewarded ad shown -> adds 6 posts
    fun loadMoreImagePosts() {
        _imageVisibleCount.value += 6
    }

    fun toggleFavoritePost(post: PostItem) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(post.id)
            if (isFav) {
                favoriteDao.deleteFavoriteById(post.id)
            } else {
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        id = post.id,
                        type = "post",
                        title = post.title,
                        thumbnail = post.thumbnail,
                        prompt = post.step1prompt
                    )
                )
            }
        }
    }

    fun toggleFavoriteImage(imagePost: ImagePostItem) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(imagePost.id)
            if (isFav) {
                favoriteDao.deleteFavoriteById(imagePost.id)
            } else {
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        id = imagePost.id,
                        type = "image",
                        title = imagePost.imagetitle,
                        thumbnail = imagePost.thumbnail,
                        prompt = imagePost.imageprompt
                    )
                )
            }
        }
    }
}
