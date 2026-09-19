package com.example.promptxo.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptxo.ui.PromptXoViewModel
import com.example.promptxo.ui.components.CategorySelector
import com.example.promptxo.ui.components.ImageGridCard
import com.example.promptxo.ui.components.NativeAdSlotView
import com.example.promptxo.ui.components.PromptSearchBar
import com.example.ui.theme.PromptXoDarkBg
import com.example.ui.theme.PromptXoPrimary
import com.example.ui.theme.PromptXoTextMuted
import com.example.ui.theme.PromptXoTextSecondary

@Composable
fun ImagesScreen(
    viewModel: PromptXoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val searchQuery by viewModel.imageSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedImageCategory.collectAsState()
    val allImages by viewModel.filteredImagePosts.collectAsState()
    val visibleCount by viewModel.imageVisibleCount.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    val displayedImages = allImages.take(visibleCount)
    val hasMore = allImages.size > visibleCount

    val categories = listOf("All", "Trending", "New", "Portrait", "Video")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PromptXoDarkBg)
    ) {
        // Search bar
        PromptSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setImageSearch(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Categories chips
        CategorySelector(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.setImageCategory(it) },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            displayedImages.forEachIndexed { index, imagePost ->
                val isFav = favoriteIds.contains(imagePost.id)

                item(key = imagePost.id, span = { GridItemSpan(1) }) {
                    ImageGridCard(
                        imagePost = imagePost,
                        isFavorite = isFav,
                        onFavoriteToggle = { viewModel.toggleFavoriteImage(imagePost) },
                        onClick = {
                            viewModel.adMobManager.handlePostClick(activity) {
                                viewModel.navigateToImageDetail(imagePost)
                            }
                        }
                    )
                }

                // Requirement: "char photo ke baad ek native ad show hoga poora native ad show hoga uske neeche"
                if ((index + 1) % 4 == 0) {
                    item(key = "native_ad_$index", span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            NativeAdSlotView()
                        }
                    }
                }
            }

            // Requirement: "aur jaise hi 10 posten ho jayengi na, 10 post ke baad neeche load more ka button hoga. Load more ke button ke baad rewarded ad show hoga. Uske baad 6 posten aur show ho jayengi."
            if (hasMore) {
                item(key = "load_more_button", span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                viewModel.adMobManager.showRewardedAd(
                                    activity = activity,
                                    title = "Watch Ad to Load More Images"
                                ) {
                                    viewModel.loadMoreImagePosts()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PromptXoPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rewarded Ad",
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Load More Images (Watch Ad)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlocks 6 more creative image prompts",
                            color = PromptXoTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (displayedImages.isEmpty()) {
                item(key = "empty_state", span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No images found for \"$searchQuery\"",
                            color = PromptXoTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
