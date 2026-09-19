package com.example.promptxo.data.model

data class PostItem(
    val id: String = "",
    val thumbnail: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "All",
    val homeCategory: String = "All",
    val stepsNumbers: String = "1 Step",
    val step1title: String = "Step 1 Title: Generate Video Prompt",
    val step1description: String = "Click \"Generate Button\" to create a video prompt",
    val step1prompt: String = "",
    val step1toollink: String = "https://flow.ai",
    val step1toolname: String = "Try in Flow",
    val step2title: String = "",
    val step2description: String = "",
    val step2prompt: String = "",
    val step2toollink: String = "",
    val step2toolname: String = "",
    val has2bean: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class ImagePostItem(
    val id: String = "",
    val imagetitle: String = "",
    val imageprompt: String = "",
    val imagetoollink: String = "https://chatgpt.com",
    val imagetoolname: String = "Try in ChatGPT",
    val thumbnail: String = "",
    val categoryforall: String = "All",
    val categoryfornew: String = "",
    val categoryfortrending: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class AppPolicies(
    val whatsappchannel: String = "https://whatsapp.com/channel/",
    val rateus: String = "https://play.google.com/store/apps/details?id=com.aistudio.promptxo.vznq",
    val privatepolicies: String = "https://policies.google.com/privacy"
)
