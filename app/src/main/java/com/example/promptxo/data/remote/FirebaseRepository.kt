package com.example.promptxo.data.remote

import android.util.Log
import com.example.promptxo.data.model.AppPolicies
import com.example.promptxo.data.model.ImagePostItem
import com.example.promptxo.data.model.PostItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Default sample posts matching user's exact design and screenshots
    val defaultHomePosts = listOf(
        PostItem(
            id = "post_1",
            title = "Kids Educational Video",
            description = "Create unique 8-second 3D educational videos for kids.",
            category = "Cartoon",
            homeCategory = "Cartoon",
            stepsNumbers = "1 Step",
            thumbnail = "https://images.unsplash.com/photo-1596464716127-f2a829822301?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1 Title: Generate Video Prompt",
            step1description = "Click \"Generate Button\" to create a video prompt",
            step1prompt = "Create a completely new 8-second Pixar-style 3D children's educational video every time this prompt is used. The video must teach one clear educational concept based on the ABC letters with joyful toddlers and vibrant nursery setting.",
            step1toolname = "Try in Flow",
            step1toollink = "https://flow.ai",
            has2bean = false
        ),
        PostItem(
            id = "post_2",
            title = "How to Make Glass Bottle",
            description = "Use this prompt to create a realistic slow-motion shatter simulation.",
            category = "Faceless",
            homeCategory = "Faceless",
            stepsNumbers = "1 Step",
            thumbnail = "https://images.unsplash.com/photo-1527061011665-3652c757a4d4?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1 Title: Generate Glass Prompt",
            step1description = "Click \"Generate Button\" to create the dynamic scene prompt",
            step1prompt = "Hyper-realistic slow-motion 4K simulation of a red glass bottle dropping onto textured concrete steps, explosive glass shatter dynamics, liquid splatter in mid-air, 1000fps cinematic lighting.",
            step1toolname = "Try in Flow",
            step1toollink = "https://flow.ai",
            has2bean = false
        ),
        PostItem(
            id = "post_3",
            title = "How to Generate Viral AI Shorts",
            description = "Use this prompt to generate high-retention viral YouTube Shorts scripts.",
            category = "Finance",
            homeCategory = "Finance",
            stepsNumbers = "1 Step",
            thumbnail = "https://images.unsplash.com/photo-1598550476439-6847785fcea6?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1 Title: Generate Script & Hook",
            step1description = "Click \"Generate Button\" to write engaging viral hook",
            step1prompt = "Generate a viral 40-second faceless short script with an irresistible psychological hook in the first 3 seconds, dynamic pacing cues, and visual B-roll prompts for Runway Gen-2.",
            step1toolname = "Try in ChatGPT",
            step1toollink = "https://chatgpt.com",
            has2bean = false
        ),
        PostItem(
            id = "post_4",
            title = "Retro 80s Anime Cityscape",
            description = "Generate nostalgic Japanese anime lo-fi aesthetic city scenes.",
            category = "Cartoon",
            homeCategory = "Cartoon",
            stepsNumbers = "2 Step",
            thumbnail = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1: Background Scenery Prompt",
            step1description = "Generate the detailed retro cyberpunk anime environment",
            step1prompt = "1980s retro anime style, detailed Tokyo street during evening rain, glowing neon signs, puddles reflecting streetlamps, Studio Ghibli cel-shaded aesthetic.",
            step1toolname = "Try in Midjourney",
            step1toollink = "https://midjourney.com",
            step2title = "Step 2: Animation Movement Prompt",
            step2description = "Add subtle parallax and rain animation",
            step2prompt = "Subtle cinematic camera pan through foggy neon alleyway, continuous gentle rain drops hitting puddles, atmospheric lo-fi mood.",
            step2toolname = "Try in Flow",
            step2toollink = "https://flow.ai",
            has2bean = true
        ),
        PostItem(
            id = "post_5",
            title = "Documentary Voiceover & Storyboard",
            description = "Craft cinematic historical documentary video prompts.",
            category = "Documentary",
            homeCategory = "Documentary",
            stepsNumbers = "1 Step",
            thumbnail = "https://images.unsplash.com/photo-1461360370896-922624d12aa1?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1 Title: Generate Documentary Outline",
            step1description = "Generate narration script and archive footage descriptions",
            step1prompt = "Deep, captivating BBC Earth documentary style narration script about ancient civilisations with dramatic visual cues for AI video generation.",
            step1toolname = "Try in ChatGPT",
            step1toollink = "https://chatgpt.com",
            has2bean = false
        ),
        PostItem(
            id = "post_6",
            title = "Cinematic Drone Nature Landscape",
            description = "Create realistic sweeping aerial footage prompts for video models.",
            category = "Faceless",
            homeCategory = "Faceless",
            stepsNumbers = "1 Step",
            thumbnail = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1 Title: Generate Aerial Video Prompt",
            step1description = "Create camera flight path and lighting commands",
            step1prompt = "Drone footage sweeping low over mist-covered pine forest towards a majestic alpine mountain peak at sunrise, golden light rays breaking through clouds, photorealistic 8K.",
            step1toolname = "Try in Sora",
            step1toollink = "https://sora.com",
            has2bean = false
        ),
        PostItem(
            id = "post_7",
            title = "Crypto Market Breakdown Short",
            description = "High retention animated finance chart video prompts.",
            category = "Finance",
            homeCategory = "Finance",
            stepsNumbers = "1 Step",
            thumbnail = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1 Title: Generate Finance Script",
            step1description = "Generate punchy breakdown explaining market trend",
            step1prompt = "Fast-paced, clear 30-second financial education script explaining compound interest with visual motion graphics instructions for animated 3D coins and charts.",
            step1toolname = "Try in ChatGPT",
            step1toollink = "https://chatgpt.com",
            has2bean = false
        ),
        PostItem(
            id = "post_8",
            title = "Cyberpunk Car Chase Animation",
            description = "High octane sci-fi vehicle chase video prompt.",
            category = "Cartoon",
            homeCategory = "Cartoon",
            stepsNumbers = "2 Step",
            thumbnail = "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&auto=format&fit=crop&q=80",
            step1title = "Step 1: Vehicle Concept",
            step1description = "Create futuristic hovercar model prompt",
            step1prompt = "Futuristic sports car speeding across neon wet highway at night, trail of purple and cyan exhaust particles.",
            step1toolname = "Try in Flow",
            step1toollink = "https://flow.ai",
            step2title = "Step 2: Camera Dynamics",
            step2description = "High-speed chase camera tracking",
            step2prompt = "Low-angle tracking shot alongside spinning tires, sparks flying as car drifts around corner, cinematic motion blur.",
            step2toolname = "Try in Kling",
            step2toollink = "https://klingai.com",
            has2bean = true
        )
    )

    // Default sample image posts matching user's exact screenshots (vintage 1980s portrait style)
    val defaultImagePosts = listOf(
        ImagePostItem(
            id = "img_1",
            imagetitle = "Late 1980s Vintage Couple",
            imageprompt = "**Use my uploaded photo as the primary identity reference. Transform the scene into an authentic late-1980s South Asian vintage photograph, while preserving the exact identity, facial features, face shape, skin tone, hairstyle characteristics, body proportions, and natural grain of analog 35mm film.**",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = "Trending"
        ),
        ImagePostItem(
            id = "img_2",
            imagetitle = "Vintage Motorcycle Rebel",
            imageprompt = "Vintage 1980s portrait of a handsome young man with curly hair leaning against a classic Royal Enfield motorcycle, striped buttoned shirt, pleated brown trousers, golden hour analog film look with grain.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "",
            categoryfortrending = "Trending"
        ),
        ImagePostItem(
            id = "img_3",
            imagetitle = "90s College Friends Campus",
            imageprompt = "Two college students in late 80s/early 90s vintage denim jackets and band tees walking outside university brick building, authentic kodak gold 200 color tones, natural grain, candid street photography.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = ""
        ),
        ImagePostItem(
            id = "img_4",
            imagetitle = "Vintage Rotary Phone Girl",
            imageprompt = "1980s classic cinema portrait of an Indian girl talking on a vintage rotary telephone, floral scarf tied around neck, vintage movie posters on wallpaper background, warm lamp glow.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "",
            categoryfortrending = "Trending"
        ),
        ImagePostItem(
            id = "img_5",
            imagetitle = "Retro Denim Jacket Portrait",
            imageprompt = "Candid 35mm film headshot portrait of a man in denim jacket leaning against a vintage car, soft morning sun flare, authentic retro grain texture.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = ""
        ),
        ImagePostItem(
            id = "img_6",
            imagetitle = "Classic Living Room Portrait",
            imageprompt = "1980s family living room portrait of a relaxed man sitting in wooden armchair next to antique lamp, vintage wallpaper, authentic retro Kodachrome color profile.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "",
            categoryfortrending = "Trending"
        ),
        ImagePostItem(
            id = "img_7",
            imagetitle = "90s Cyberpunk Portrait",
            imageprompt = "Cinematic portrait in neon rain drenched alley, purple and cyan highlights, reflective wet leather jacket, sharp 8K digital render.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = ""
        ),
        ImagePostItem(
            id = "img_8",
            imagetitle = "Anime Character Concept",
            imageprompt = "Original anime character design, soft pastel aesthetic, cherry blossom petals floating, gentle warm sunset rim light.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "",
            categoryfortrending = "Trending"
        ),
        ImagePostItem(
            id = "img_9",
            imagetitle = "Classic Studio Portrait",
            imageprompt = "Fine art black and white studio portrait with dramatic chiaroscuro Rembrandt lighting, rich contrast, timeless aesthetic.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = ""
        ),
        ImagePostItem(
            id = "img_10",
            imagetitle = "Vintage Bollywood Cine Film",
            imageprompt = "Cinematic 1970s Bollywood film aesthetic, rich sepia and warm tones, classic heroine styling with embroidered dupatta, vintage film scratches.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = "Trending"
        ),
        ImagePostItem(
            id = "img_11",
            imagetitle = "Analog Film Street Cafe",
            imageprompt = "Parisian outdoor cafe in morning sunlight, steaming espresso cup on marble table, authentic 35mm Portra 400 film grain.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "New",
            categoryfortrending = ""
        ),
        ImagePostItem(
            id = "img_12",
            imagetitle = "Fantasy Ethereal Portrait",
            imageprompt = "Ethereal fairy queen with luminescent floral crown, glowing fireflies in mystical enchanted forest, volumetric fairy lights.",
            imagetoollink = "https://chatgpt.com",
            imagetoolname = "Try in ChatGPT",
            thumbnail = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=800&auto=format&fit=crop&q=80",
            categoryforall = "All",
            categoryfornew = "",
            categoryfortrending = "Trending"
        )
    )

    private fun extractTimestamp(doc: com.google.firebase.firestore.DocumentSnapshot): Long {
        val candidateFieldNames = listOf(
            "createAs", "create as", "create_as", "createas",
            "createdAt", "created_at", "createAt", "create_at",
            "timestamp", "time", "date", "created", "datetime"
        )
        for (field in candidateFieldNames) {
            if (doc.contains(field)) {
                val value = doc.get(field) ?: continue
                when (value) {
                    is com.google.firebase.Timestamp -> return value.toDate().time
                    is java.util.Date -> return value.time
                    is Number -> {
                        val num = value.toLong()
                        return if (num in 1..9999999999L) num * 1000L else num
                    }
                    is String -> {
                        val str = value.trim()
                        val num = str.toLongOrNull()
                        if (num != null) {
                            return if (num in 1..9999999999L) num * 1000L else num
                        }
                        val formats = listOf(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "yyyy-MM-dd HH:mm:ss",
                            "yyyy-MM-dd",
                            "dd/MM/yyyy HH:mm:ss",
                            "dd-MM-yyyy HH:mm:ss",
                            "MM/dd/yyyy HH:mm:ss"
                        )
                        for (fmt in formats) {
                            try {
                                val sdf = java.text.SimpleDateFormat(fmt, java.util.Locale.US)
                                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                val parsed = sdf.parse(str)
                                if (parsed != null) return parsed.time
                            } catch (_: Exception) { }
                        }
                    }
                }
            }
        }
        return 0L
    }

    fun getHomePostsFlow(): Flow<List<PostItem>> = callbackFlow {
        try {
            val listener = firestore.collection("Post")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(
                            "FirebaseRepo",
                            "FIRESTORE Post LISTENER ERROR: code=${error.code}, message=${error.message}",
                            error
                        )
                        // Do NOT show local/default posts when Firebase fails.
                        // This makes sure the screen only shows real Firebase data.
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val posts = snapshot.documents.mapNotNull { doc ->
                            try {
                                PostItem(
                                    id = doc.id,
                                    thumbnail = doc.getString("thumbnail") ?: "",
                                    title = doc.getString("title") ?: "",
                                    description = doc.getString("description") ?: "",
                                    category = doc.getString("category") ?: "All",
                                    homeCategory = doc.getString("homeCategory") ?: doc.getString("category") ?: "All",
                                    stepsNumbers = doc.getString("StepsNumbers") ?: doc.getString("stepsNumbers") ?: "1 Step",
                                    step1title = doc.getString("step1title") ?: "Step 1 Title: Generate Video Prompt",
                                    step1description = doc.getString("step1description") ?: "Click \"Generate Button\" to create a video prompt",
                                    step1prompt = doc.getString("step1prompt") ?: "",
                                    step1toollink = doc.getString("step1toollink") ?: "https://flow.ai",
                                    step1toolname = doc.getString("step1toolname") ?: "Try in Flow",
                                    step2title = doc.getString("step2title") ?: "",
                                    step2description = doc.getString("step2description") ?: "",
                                    step2prompt = doc.getString("step2prompt") ?: "",
                                    step2toollink = doc.getString("step2toollink") ?: "",
                                    step2toolname = doc.getString("step2toolname") ?: "",
                                    has2bean = doc.getBoolean("has2bean") ?: false,
                                    createdAt = extractTimestamp(doc)
                                )
                            } catch (e: Exception) {
                                Log.e(
                                    "FirebaseRepo",
                                    "ERROR parsing Post document ${doc.id}: ${e.message}",
                                    e
                                )
                                null
                            }
                        }.sortedByDescending { it.createdAt }

                        Log.d(
                            "FirebaseRepo",
                            "FIREBASE Post SNAPSHOT: documents=${snapshot.documents.size}, parsed=${posts.size}"
                        )
                        snapshot.documents.forEach { doc ->
                            Log.d(
                                "FirebaseRepo",
                                "FIREBASE Post: id=${doc.id}, title=${doc.getString("title")}"
                            )
                        }

                        // Show exactly what came from Firebase.
                        trySend(posts)
                    } else {
                        Log.w("FirebaseRepo", "Firebase Post collection is empty.")
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "FIRESTORE Post INIT ERROR: ${e.message}", e)
            trySend(emptyList())
            awaitClose { }
        }
    }

    fun getImagePostsFlow(): Flow<List<ImagePostItem>> = callbackFlow {
        try {
            val listener = firestore.collection("imagepost")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(
                            "FirebaseRepo",
                            "FIRESTORE imagepost LISTENER ERROR: code=${error.code}, message=${error.message}",
                            error
                        )
                        // Do NOT show local/default images when Firebase fails.
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val images = snapshot.documents.mapNotNull { doc ->
                            try {
                                ImagePostItem(
                                    id = doc.id,
                                    imagetitle = doc.getString("imagetitle") ?: "",
                                    imageprompt = doc.getString("imageprompt") ?: "",
                                    imagetoollink = doc.getString("imagetoollink") ?: "https://chatgpt.com",
                                    imagetoolname = doc.getString("imagetoolname") ?: "Try in ChatGPT",
                                    thumbnail = doc.getString("thumbnail") ?: "",
                                    categoryforall = doc.getString("categoryforall") ?: "All",
                                    categoryfornew = doc.getString("categoryfornew") ?: "",
                                    categoryfortrending = doc.getString("categoryfortrending") ?: "",
                                    createdAt = extractTimestamp(doc)
                                )
                            } catch (e: Exception) {
                                Log.e(
                                    "FirebaseRepo",
                                    "ERROR parsing imagepost document ${doc.id}: ${e.message}",
                                    e
                                )
                                null
                            }
                        }.sortedByDescending { it.createdAt }

                        Log.d(
                            "FirebaseRepo",
                            "FIREBASE imagepost SNAPSHOT: documents=${snapshot.documents.size}, parsed=${images.size}"
                        )
                        snapshot.documents.forEach { doc ->
                            Log.d(
                                "FirebaseRepo",
                                "FIREBASE imagepost: id=${doc.id}, title=${doc.getString("imagetitle")}"
                            )
                        }

                        // Show exactly what came from Firebase.
                        trySend(images)
                    } else {
                        Log.w("FirebaseRepo", "Firebase imagepost collection is empty.")
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "FIRESTORE imagepost INIT ERROR: ${e.message}", e)
            trySend(emptyList())
            awaitClose { }
        }
    }

    suspend fun getAppPolicies(): AppPolicies {
        return try {
            val doc = firestore.collection("apppolices").limit(1).get().await()
            if (!doc.isEmpty) {
                val data = doc.documents[0]
                AppPolicies(
                    whatsappchannel = data.getString("whatsappchannel") ?: "https://whatsapp.com/channel/promptxo",
                    rateus = data.getString("rateus") ?: "market://details?id=com.arslanaziz.promptxo",
                    privatepolicies = data.getString("privatepolicies") ?: "https://policies.google.com/privacy"
                )
            } else {
                AppPolicies()
            }
        } catch (e: Exception) {
            Log.w("FirebaseRepo", "Could not fetch app policies, using defaults", e)
            AppPolicies()
        }
    }
}
