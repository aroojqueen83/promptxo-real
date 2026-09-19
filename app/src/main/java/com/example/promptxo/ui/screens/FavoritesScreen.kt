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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.promptxo.data.model.ImagePostItem
import com.example.promptxo.data.model.PostItem
import com.example.promptxo.ui.PromptXoViewModel
import com.example.promptxo.ui.components.CategorySelector
import com.example.ui.theme.PromptXoBadgeBg
import com.example.ui.theme.PromptXoBorder
import com.example.ui.theme.PromptXoCardBg
import com.example.ui.theme.PromptXoDarkBg
import com.example.ui.theme.PromptXoHeartRed
import com.example.ui.theme.PromptXoPrimary
import com.example.ui.theme.PromptXoSearchBg
import com.example.ui.theme.PromptXoSurface
import com.example.ui.theme.PromptXoTextMuted
import com.example.ui.theme.PromptXoTextPrimary
import com.example.ui.theme.PromptXoTextSecondary

@Composable
fun FavoritesScreen(
    viewModel: PromptXoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val favorites by viewModel.allFavorites.collectAsState()
    val allHomePosts by viewModel.filteredHomePosts.collectAsState()
    val allImages by viewModel.filteredImagePosts.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "Video Prompts", "Image Prompts")

    val filteredList = favorites.filter { fav ->
        when (selectedFilter) {
            "Video Prompts" -> fav.type == "post"
            "Image Prompts" -> fav.type == "image"
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PromptXoDarkBg)
    ) {
        // Filter tabs
        CategorySelector(
            categories = filterTabs,
            selectedCategory = selectedFilter,
            onCategorySelected = { selectedFilter = it },
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .background(PromptXoSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Empty Favorites",
                            tint = PromptXoTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorites yet",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the heart icon on any post or image prompt to save it here for quick access.",
                        color = PromptXoTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PromptXoCardBg),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(PromptXoBorder)),
                        onClick = {
                            viewModel.adMobManager.handlePostClick(activity) {
                                if (item.type == "post") {
                                    val matchedPost = allHomePosts.find { it.id == item.id }
                                        ?: PostItem(
                                            id = item.id,
                                            title = item.title,
                                            thumbnail = item.thumbnail,
                                            step1prompt = item.prompt
                                        )
                                    viewModel.navigateToPostDetail(matchedPost)
                                } else {
                                    val matchedImg = allImages.find { it.id == item.id }
                                        ?: ImagePostItem(
                                            id = item.id,
                                            imagetitle = item.title,
                                            thumbnail = item.thumbnail,
                                            imageprompt = item.prompt
                                        )
                                    viewModel.navigateToImageDetail(matchedImg)
                                }
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(item.thumbnail)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .networkCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PromptXoSearchBg)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = item.title,
                                        color = PromptXoTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            if (item.type == "post") {
                                                viewModel.toggleFavoritePost(PostItem(id = item.id))
                                            } else {
                                                viewModel.toggleFavoriteImage(ImagePostItem(id = item.id))
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Remove Favorite",
                                            tint = PromptXoHeartRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.prompt,
                                    color = PromptXoTextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (item.type == "post") PromptXoBadgeBg else Color(0xFF0284C7))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (item.type == "post") "Video Prompt" else "Image Prompt",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
