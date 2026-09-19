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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.promptxo.ui.components.HomePostCard
import com.example.promptxo.ui.components.NativeAdSlotView
import com.example.promptxo.ui.components.PromptSearchBar
import com.example.ui.theme.PromptXoDarkBg
import com.example.ui.theme.PromptXoPrimary
import com.example.ui.theme.PromptXoTextMuted
import com.example.ui.theme.PromptXoTextSecondary

@Composable
fun HomeScreen(
    viewModel: PromptXoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val searchQuery by viewModel.homeSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedHomeCategory.collectAsState()
    val allFilteredPosts by viewModel.filteredHomePosts.collectAsState()
    val visibleCount by viewModel.homeVisibleCount.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    // Show up to visibleCount items
    val displayedPosts = allFilteredPosts.take(visibleCount)
    val hasMore = allFilteredPosts.size > visibleCount

    val categories = listOf("All", "Cartoon", "Faceless", "Finance", "Documentary")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PromptXoDarkBg)
    ) {
        // Search bar
        PromptSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setHomeSearch(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Categories horizontal list
        CategorySelector(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.setHomeCategory(it) },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Posts List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(displayedPosts, key = { _, post -> post.id }) { index, post ->
                val isFav = favoriteIds.contains(post.id)

                HomePostCard(
                    post = post,
                    isFavorite = isFav,
                    onFavoriteToggle = { viewModel.toggleFavoritePost(post) },
                    onClick = {
                        // User rule: 1st click opens directly, 2nd click shows interstitial ad!
                        viewModel.adMobManager.handlePostClick(activity) {
                            viewModel.navigateToPostDetail(post)
                        }
                    }
                )

                // User rule: "har 2 poston ke baad ek native ad show hoga"
                if ((index + 1) % 2 == 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    NativeAdSlotView()
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // User rule: Load More button after visible posts, triggers rewarded ad and loads 3 more posts!
            if (hasMore) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                viewModel.adMobManager.showRewardedAd(
                                    activity = activity,
                                    title = "Watch Ad to Load More Prompts"
                                ) {
                                    viewModel.loadMoreHomePosts()
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
                                text = "Load More (Watch Ad)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlocks 3 more video prompts",
                            color = PromptXoTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (displayedPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No prompts found for \"$searchQuery\"",
                            color = PromptXoTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
