@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.database.CommentEntity
import com.example.data.database.PostEntity
import com.example.data.database.StoryEntity
import com.example.data.database.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuraUiState
import com.example.ui.viewmodel.AuraViewModel
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

val LavenderLight = Color(0xFFD1C4E9)

val LocalAuraViewModel = androidx.compose.runtime.compositionLocalOf<com.example.ui.viewmodel.AuraViewModel?> { null }

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontStyle: androidx.compose.ui.text.font.FontStyle? = null,
    fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
    fontFamily: androidx.compose.ui.text.font.FontFamily? = null,
    letterSpacing: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    textDecoration: androidx.compose.ui.text.style.TextDecoration? = null,
    textAlign: androidx.compose.ui.text.style.TextAlign? = null,
    lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    overflow: androidx.compose.ui.text.style.TextOverflow = androidx.compose.ui.text.style.TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (androidx.compose.ui.text.TextLayoutResult) -> Unit = {},
    style: androidx.compose.ui.text.TextStyle = LocalTextStyle.current
) {
    val viewModel = LocalAuraViewModel.current
    val translatedText = if (viewModel != null) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val lang = state.appLanguage
        viewModel.getTranslatedText(text)
    } else {
        text
    }

    androidx.compose.material3.Text(
        text = translatedText,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

@Composable
fun Text(
    text: androidx.compose.ui.text.AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontStyle: androidx.compose.ui.text.font.FontStyle? = null,
    fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
    fontFamily: androidx.compose.ui.text.font.FontFamily? = null,
    letterSpacing: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    textDecoration: androidx.compose.ui.text.style.TextDecoration? = null,
    textAlign: androidx.compose.ui.text.style.TextAlign? = null,
    lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    overflow: androidx.compose.ui.text.style.TextOverflow = androidx.compose.ui.text.style.TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (androidx.compose.ui.text.TextLayoutResult) -> Unit = {},
    style: androidx.compose.ui.text.TextStyle = LocalTextStyle.current
) {
    val viewModel = LocalAuraViewModel.current
    val translatedText = if (viewModel != null) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val lang = state.appLanguage
        val rawText = text.text
        val translatedRaw = viewModel.getTranslatedText(rawText)
        if (translatedRaw != rawText) {
            androidx.compose.ui.text.AnnotatedString(translatedRaw, text.spanStyles, text.paragraphStyles)
        } else {
            text
        }
    } else {
        text
    }

    androidx.compose.material3.Text(
        text = translatedText,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

// --- Shared Helper: Premium Gradient Backdrops for posts & stories ---
val PremiumGradients = listOf(
    // 0. Lavender Dream
    Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    // 1. Aurora Cosmic Sky
    Brush.radialGradient(colors = listOf(Color(0xFFFF007F), Color(0xFF7C4DFF))),
    // 2. Neon Ocean Sparkle
    Brush.linearGradient(colors = listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    // 3. Electric Fire Sunset
    Brush.sweepGradient(colors = listOf(Color(0xFFE65100), Color(0xFFF50057), Color(0xFFE65100))),
    // 4. Midnight Obsidian Depth
    Brush.verticalGradient(colors = listOf(Color(0xFF2C3E50), Color(0xFF000000))),
    // 5. Pastel Rose Lilac (Light)
    Brush.linearGradient(colors = listOf(Color(0xFFF197C0), Color(0xFFE5B5E6))),
    // 6. Warm Apricot Honey
    Brush.linearGradient(colors = listOf(Color(0xFFFAD961), Color(0xFFF76B1C))),
    // 7. Cherry Glow Blast
    Brush.radialGradient(colors = listOf(Color(0xFFFF758C), Color(0xFFFF7EB3))),
    // 8. Emerald Forest Silk
    Brush.linearGradient(colors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    // 9. Cyberpunk Indigo Neon
    Brush.verticalGradient(colors = listOf(Color(0xFFDE12B3), Color(0xFF2F0177))),
    // 10. Arctic Glacier Chill (Light)
    Brush.linearGradient(colors = listOf(Color(0xFF80D0C7), Color(0xFF0093E9))),
    // 11. Steel Charcoal Smoke
    Brush.verticalGradient(colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))),
    // 12. Soft Velvet Lilac
    Brush.radialGradient(colors = listOf(Color(0xFFC471ED), Color(0xFFF64F59))),
    // 13. Lemon Peach Sorbet (Light)
    Brush.linearGradient(colors = listOf(Color(0xFFF6D365), Color(0xFFFDA085))),
    // 14. Lunar Dusk Void
    Brush.radialGradient(colors = listOf(Color(0xFF232526), Color(0xFF414345))),
    // 15. Rainbow Aura Wave (Light)
    Brush.linearGradient(colors = listOf(Color(0xFF85FFBD), Color(0xFFFFFB7D)))
)

val GradientNames = listOf(
    "Lavender Dream",
    "Aurora Cosmic Sky",
    "Neon Ocean Sparkle",
    "Electric Fire Sunset",
    "Midnight Obsidian Depth",
    "Pastel Rose Lilac",
    "Warm Apricot Honey",
    "Cherry Glow Blast",
    "Emerald Forest Silk",
    "Cyberpunk Indigo Neon",
    "Arctic Glacier Chill",
    "Steel Charcoal Smoke",
    "Soft Velvet Lilac",
    "Lemon Peach Sorbet",
    "Lunar Dusk Void",
    "Rainbow Aura Wave"
)

// --- Shared UI Component: Adaptive Avatar System with pristine Fallbacks ---
@Composable
fun ProfileAvatar(
    avatarId: String,
    modifier: Modifier = Modifier,
    size: Int = 40,
    showOnlineStatus: Boolean = false,
    fallbackName: String? = null
) {
    val char = if (!fallbackName.isNullOrBlank()) {
        fallbackName.trim().firstOrNull()?.uppercaseChar() ?: 'A'
    } else {
        if (avatarId.isNotEmpty() && !avatarId.startsWith("content://") && !avatarId.startsWith("http://") && !avatarId.startsWith("https://") && !avatarId.startsWith("file://") && !avatarId.contains("/")) {
            avatarId.last().uppercaseChar()
        } else {
            'A'
        }
    }
    val fallbackBg = remember(avatarId) {
        val hash = avatarId.hashCode()
        when (hash % 4) {
            0 -> Brush.linearGradient(colors = listOf(Color(0xFF7C4DFF), Color(0xFF9575CD)))
            1 -> Brush.linearGradient(colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB)))
            2 -> Brush.linearGradient(colors = listOf(Color(0xFF00E5FF), Color(0xFF00B0FF)))
            else -> Brush.linearGradient(colors = listOf(Color(0xFF00E676), Color(0xFF69F0AE)))
        }
    }

    val isUri = avatarId.startsWith("content://") || avatarId.startsWith("http://") || avatarId.startsWith("https://") || avatarId.startsWith("file://") || avatarId.contains("/")
    var loadFailed by remember(avatarId) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size.dp)
            .testTag("profile_avatar_$avatarId")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(1.5.dp, LavenderPrimary.copy(alpha = 0.4f), CircleShape)
        ) {
            if (isUri && !loadFailed) {
                AsyncImage(
                    model = avatarId,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    onError = {
                        loadFailed = true
                    }
                )
            } else {
                // Since we don't have actual asset files, draw fallback avatar directly
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fallbackBg),
                    contentAlignment = Alignment.Center
                ) {
                    val hasLetter = char != 'A' || (!fallbackName.isNullOrBlank() && fallbackName.trim().firstOrNull()?.uppercaseChar() == 'A')
                    if (hasLetter) {
                        Text(
                            text = char.toString(),
                            color = Color.White,
                            fontSize = (size * 0.4).sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar Placeholder",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                }
            }
        }

        if (showOnlineStatus) {
            val dotSize = (size * 0.21).coerceAtLeast(8.0).dp
            val dotOffset = (size * 0.04).dp
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .align(Alignment.BottomEnd)
                    .offset(x = dotOffset, y = dotOffset)
                    .background(Color(0xFF4CAF50), CircleShape)
                    .border(1.2.dp, Color.White, CircleShape)
            )
        }
    }
}

// --- AUTOMATED LANGUAGE TRANSLATION HELPER FOR AURA ---
val GlobalLanguagesList = listOf(
    "English (US)", "বাংলা (BD)", "Español (ES)", "हिन्दी (Hindi)", "العربية (Arabic)", 
    "中文 (Chinese)", "Português", "Русский (Russian)", "日本語 (Japanese)", "Deutsch (German)",
    "Français (French)", "한국어 (Korean)", "Italiano (Italian)", "Türkçe (Turkish)", 
    "Tiếng Việt (Vietnamese)", "Polski (Polish)", "Українська (Ukrainian)", "Nederlands (Dutch)", 
    "ไทย (Thai)", "Ελληνικά (Greek)", "Svenska (Swedish)", "Norsk (Norwegian)", 
    "Dansk (Danish)", "Suomi (Finnish)", "Čeština (Czech)", "Magyar (Hungarian)", 
    "Română (Romanian)", "Slovenčina (Slovak)", "Български (Bulgarian)", "עברית (Hebrew)",
    "Bahasa Indonesia", "Bahasa Melayu", "Tagalog (Filipino)", "Kiswahili", "Amharic", 
    "IsiZulu", "IsiXhosa", "Afrikaans", "Yorùbá", "Igbo", "Hausa", 
    "فارسی (Persian)", "اردو (Urdu)", "پښتو (Pashto)", "Kurdî (Kurdish)", "Azərbaycanca", 
    "ქართული (Georgian)", "Հայերեն (Armenian)", "Қазақ тілі (Kazakh)", "Oʻzbekcha (Uzbek)",
    "Türkmençe (Turkmen)", "Tojikī (Tajik)", "Кыргызча (Kyrgyz)", "Монгол (Mongolian)", 
    "नेपाली (Nepali)", "සිंहल (Sinhala)", "தமிழ் (Tamil)", "తెలుగు (Telugu)", 
    "ಕನ್ನಡ (Kannada)", "മലയാളം (Malayalam)", "मराठी (Marathi)", "ગુજરાતી (Gujarati)", 
    "ਪੰਜਾਬੀ (Punjabi)", "ଓଡ଼ιয়া (Odia)", "অসমীয়া (Assamese)", "मैथिली (Maithili)", 
    "संस्कृतम् (Sanskrit)", "မြန်မာဘာသာ (Burmese)", "ភាសាខ្មែរ (Khmer)", "ພາສາລາວ (Lao)",
    "བོད་སྐད (Tibetan)", "Soomaali (Somali)", "Oromoo", "ትግርኛ (Tigrinya)", 
    "Malagasy", "Chishona (Shona)", "Chichewa", "Sesotho", "Setswana", 
    "Xitsonga", "Tshivenda", "Siswati", "Sindhi", "Slovenščina", 
    "Lietuvių (Lithuanian)", "Latviešu (Latvian)", "Eesti (Estonian)", "Euskara (Basque)", 
    "Català (Catalan)", "Galego (Galician)", "Kinyarwanda", "Kirundi", 
    "Wolof", "Lingála", "Akan", "Gaeilge (Irish)", "Cymraeg (Welsh)", 
    "Malti (Maltese)", "Esperanto", "Latin", "Corsu (Corsican)"
)

object AuraTranslator {
    private data class TargetTranslation(
        val welcomeTitle: String,
        val welcomeDesc: String,
        val getStartedText: String,
        val alreadyHaveProfileText: String
    )

    private val dictionary = mapOf(
        "english" to TargetTranslation(
            "Join Aura",
            "Connect with friends, family and communities of people who share your interests. Fully secure lifetime free social cloud.",
            "Get started",
            "I already have a profile"
        ),
        "বাংলা" to TargetTranslation(
            "আওরাতে যোগ দিন",
            "বন্ধু, পরিবার এবং একই আগ্রহের মানুষের সাথে যুক্ত থাকুন। লাইফটাইম সুরক্ষিত ও সম্পূর্ণ বিজ্ঞাপন-মুক্ত সোশ্যাল মিডিয়া।",
            "শুরু করুন",
            "আমার একটি প্রোফাইল আছে"
        ),
        "español" to TargetTranslation(
            "Únete a Aura",
            "Conéctate con amigos, familiares y comunidades. Red social en la nube totalmente segura y gratis de por vida.",
            "Comenzar",
            "Ya tengo un perfil"
        ),
        "hindi" to TargetTranslation(
            "आभा में शामिल हों",
            "उन दोस्तों, परिवार और समुदायों से जुड़ें जो आपके हितों को साझा करते हैं। पूर्णतः सुरक्षित और मुफ्त सोशल क्लाउड।",
            "शुरू करें",
            "मेरा पहले से ही एक प्रोफ़ाइल है"
        ),
        "हिन्दी" to TargetTranslation(
            "आभा में शामिल हों",
            "उन दोस्तों, परिवार और समुदायों से जुड़ें जो आपके हितों को साझा करते हैं। पूर्णतः सुरक्षित और मुफ्त सोशल क्लाउड।",
            "शुरू करें",
            "मेरा पहले से ही एक प्रोफ़ाइल है"
        ),
        "arabic" to TargetTranslation(
            "انضم إلى أورا",
            "تواصل مع الأصدقاء والعائلة والمجتمعات التي تشاركك اهتماماتك. سحابة اجتماعية مجانية وآمنة تمامًا مدى الحياة.",
            "ابدأ الآن",
            "لدي حساب بالفعل"
        ),
        "العربية" to TargetTranslation(
            "انضم إلى أورا",
            "تواصل مع الأصدقاء والعائلة والمجتمعات التي تشاركك اهتماماتك. سحابة اجتماعية مجانية وآمنة تمامًا مدى الحياة.",
            "ابدأ الآن",
            "لدي حساب بالفعل"
        ),
        "chinese" to TargetTranslation(
            "加入 Aura",
            "与分享您兴趣的朋友、家人和社区建立联系。完全安全、终身免费的社交云。",
            "开始使用",
            "我已有个人资料"
        ),
        "中文" to TargetTranslation(
            "加入 Aura",
            "与分享您兴趣的朋友、家人和社区建立联系。完全安全、终身免费的社交云。",
            "开始使用",
            "我已有个人资料"
        ),
        "português" to TargetTranslation(
            "Junte-se ao Aura",
            "Conecte-se com amigos, familiares e comunidades que compartilham seus interesses. Nuvem social totalmente segura e gratuita para toda a vida.",
            "Começar",
            "Já tenho um perfil"
        ),
        "portuguese" to TargetTranslation(
            "Junte-se ao Aura",
            "Conecte-se com amigos, familiares e comunidades que compartilham seus interesses. Nuvem social totalmente segura e gratuita para toda a vida.",
            "Começar",
            "Já tenho um perfil"
        ),
        "русский" to TargetTranslation(
            "Присоединяйтесь к Aura",
            "Общайтесь с друзьями, семьями и единомышленниками. Полностью безопасное и пожизненно бесплатное социальное облако.",
            "Начать",
            "У меня уже есть профиль"
        ),
        "russian" to TargetTranslation(
            "Присоединяйтесь к Aura",
            "Общайтесь с друзьями, семьями и единомышленниками. Полностью безопасное и пожизненно бесплатное social cloud.",
            "Начать",
            "У меня уже есть профиль"
        ),
        "japanese" to TargetTranslation(
            "オーラに参加する",
            "同じ趣味を持つ友人、家族、コミュニティとつながりましょう。生涯無料で完全に安全なソーシャルクラウド。",
            "始める",
            "アカウントを持っています"
        ),
        "日本語" to TargetTranslation(
            "オーラに参加する",
            "同じ趣味を持つ友人、家族、コミュニティとつながりましょう。生涯無料で完全に安全なソーシャルクラウド。",
            "始める",
            "アカウントを持っています"
        ),
        "deutsch" to TargetTranslation(
            "Tritt Aura bei",
            "Verbinde dich mit Freunden, Familie und Communities, die deine Interessen teilen. Lebenslang kostenlose und sichere Social Cloud.",
            "Loslegen",
            "Ich habe bereits ein Profil"
        ),
        "german" to TargetTranslation(
            "Tritt Aura bei",
            "Verbinde dich mit Freunden, Familie und Communities, die deine Interessen teilen. Lebenslang kostenlose und sichere Social Cloud.",
            "Loslegen",
            "Ich habe bereits ein Profil"
        ),
        "français" to TargetTranslation(
            "Rejoindre Aura",
            "Connectez-vous avec vos amis, votre famille et des communautés qui partagent vos intérêts. Cloud social entièrement sécurisé et gratuit à vie.",
            "Commencer",
            "J'ai déjà un profil"
        ),
        "french" to TargetTranslation(
            "Rejoindre Aura",
            "Connectez-vous avec vos amis, votre famille et des communautés qui partagent vos intérêts. Cloud social entièrement sécurisé et gratuit à vie.",
            "Commencer",
            "J'ai déjà un profil"
        ),
        "한국어" to TargetTranslation(
            "Aura에 가입하세요",
            "관심사를 공유하는 친구, 가족, 커뮤니티와 소통하세요. 평생 안전하고 무료인 소셜 클라우드.",
            "시작하기",
            "이미 프로필이 있습니다"
        ),
        "korean" to TargetTranslation(
            "Aura에 가입하세요",
            "관심사를 공유하는 친구, 가족, 커뮤니티와 소통하세요. 평생 안전하고 무료인 소셜 클라우드.",
            "시작하기",
            "이미 프로필이 있습니다"
        ),
        "italiano" to TargetTranslation(
            "Unisciti ad Aura",
            "Connettiti con amici, familiari e community che condividono i tuoi interessi. Social cloud completamente sicuro e gratuito a vita.",
            "Inizia",
            "Ho già un profilo"
        ),
        "italian" to TargetTranslation(
            "Unisciti ad Aura",
            "Connettiti con amici, familiari e community che condividono i tuoi interessi. Social cloud completamente sicuro e gratuito a vita.",
            "Inizia",
            "Ho già un profilo"
        ),
        "türkçe" to TargetTranslation(
            "Aura'ya Katılın",
            "İlgi alanlarınızı paylaşan arkadaşlarınızla, ailenizle ve topluluklarla bağlantı kurun. Ömür boyu ücretsiz ve güvenli sosyal bulut.",
            "Başla",
            "Zaten bir profilim var"
        ),
        "turkish" to TargetTranslation(
            "Aura'ya Katılın",
            "İlgi alanlarınızı paylaşan arkadaşlarınızla, ailenizle ve topluluklarla bağlantı kurun. Ömür boyu ücretsiz ve güvenli sosyal bulut.",
            "Başla",
            "Zaten bir profilim var"
        ),
        "tiếng việt" to TargetTranslation(
            "Tham gia Aura",
            "Kết nối với bạn bè, gia đình và cộng đồng chia sẻ cùng sở thích. Đám mây mạng xã hội bảo mật và miễn phí trọn đời.",
            "Bắt đầu",
            "Tôi đã có hồ sơ"
        ),
        "vietnamese" to TargetTranslation(
            "Tham gia Aura",
            "Kết nối với bạn bè, gia đình và cộng đồng chia sẻ cùng sở thích. Đám mây mạng xã hội bảo mật và miễn phí trọn đời.",
            "Bắt đầu",
            "Tôi đã có hồ sơ"
        ),
        "polski" to TargetTranslation(
            "Dołącz do Aura",
            "Połącz się ze znajomymi, rodziną i społecznościami dzielącymi Twoje zainteresowania. Bezpieczna, bezpłatna chmura społecznościowa.",
            "Rozpocznij",
            "Mam już profil"
        ),
        "polish" to TargetTranslation(
            "Dołącz do Aura",
            "Połącz się ze znajomymi, rodziną i społecznościami dzielącymi Twoje zainteresowania. Bezpieczna, bezpłatna chmura społecznościowa.",
            "Rozpocznij",
            "Mam już profil"
        ),
        "українська" to TargetTranslation(
            "Приєднуйтесь до Aura",
            "Спілкуйтеся з друзями, родиною та спільнотами, які поділяють ваші інтереси. Повністю безпечна та безкоштовна соціальна хмара.",
            "Почати",
            "У мене вже є профіль"
        ),
        "ukrainian" to TargetTranslation(
            "Приєднуйтесь до Aura",
            "Спілкуйтеся з друзями, родиною та спільнотами, які поділяють ваші інтереси. Повністю безпечна та безкоштовна соціальна хмара.",
            "Почати",
            "У мене вже є профіль"
        ),
        "nederlands" to TargetTranslation(
            "Sluit je aan bij Aura",
            "Maak verbinding met vrienden, familie en communities die jouw interesses delen. Volledig veilige, levenslang gratis sociale cloud.",
            "Aan de slag",
            "Ik heb al een profiel"
        ),
        "dutch" to TargetTranslation(
            "Sluit je aan bij Aura",
            "Maak verbinding met vrienden, familie en communities die jouw interesses delen. Volledig veilige, levenslang gratis sociale cloud.",
            "Aan de slag",
            "Ik heb al een profiel"
        ),
        "thai" to TargetTranslation(
            "เข้าร่วม Aura",
            "เชื่อมต่อกับเพื่อน ครอบครัว และชุมชนของผู้คนที่มีความสนใจเหมือนกัน เมฆโซเชียลที่ปลอดภัยและฟรีตลอดชีพ",
            "เริ่มต้นใช้งาน",
            "ฉันมีโปรไฟล์อยู่แล้ว"
        ),
        "ไทย" to TargetTranslation(
            "เข้าร่วม Aura",
            "เชื่อมต่อกับเพื่อน ครอบครัว และชุมชนของผู้คนที่มีความสนใจเหมือนกัน เมฆโซเชียลที่ปลอดภัยและฟรีตลอดชีพ",
            "เริ่มต้นใช้งาน",
            "ฉันมีโปรไฟล์อยู่แล้ว"
        ),
        "greek" to TargetTranslation(
            "Γίνετε μέλος του Aura",
            "Συνδεθείτε με φίλους, οικογένεια και κοινότητες ανθρώπων που μοιράζονται τα ενδιαφέροντά σας. Ασφαλές, δωρεάν κοινωνικό σύννεφο.",
            "Ξεκινήστε",
            "Έχω ήδη προφίλ"
        ),
        "ελληνικά" to TargetTranslation(
            "Γίνετε μέλος του Aura",
            "Συνδεθείτε με φίλους, οικογένεια και κοινότητες ανθρώπων που μοιράζονται τα ενδιαφέροντά σας. Ασφαλές, δωρεάν κοινωνικό σύννεφο.",
            "Ξεκινήστε",
            "Έχω ήδη προφίλ"
        ),
        "svenska" to TargetTranslation(
            "Gå med i Aura",
            "Anslut med vänner, familie och gemenskaper som delar dina intressen. Helt säkert och gratis socialt moln för livet.",
            "Kom igång",
            "Jag har allerede en profil"
        ),
        "swedish" to TargetTranslation(
            "Gå med i Aura",
            "Anslut med vänner, familie och gemenskaper som delar dina intressen. Helt säkert och gratis socialt moln för livet.",
            "Kom igång",
            "Jag har allerede en profil"
        ),
        "norsk" to TargetTranslation(
            "Bli med i Aura",
            "Koble til venner, familie og fellesskap som deler dine interesser. Helt sikker og livstids gratis sosial sky.",
            "Kom i gang",
            "Jeg har allerede en profil"
        ),
        "norwegian" to TargetTranslation(
            "Bli med i Aura",
            "Koble til venner, familie og fellesskap som deler dine interesser. Helt sikker og livstids gratis sosial sky.",
            "Kom i gang",
            "Jeg har allerede en profil"
        ),
        "dansk" to TargetTranslation(
            "Tilmeld dig Aura",
            "Forbind med venner, familie og fællesskaber, der deler dine interesser. Helt sikkert og gratis socialt sky for livet.",
            "Kom i gang",
            "Jeg har allerede en profil"
        ),
        "danish" to TargetTranslation(
            "Tilmeld dig Aura",
            "Forbind med venner, familie og fællesskaber, der deler dine interesser. Helt sikkert og gratis socialt sky for livet.",
            "Kom i gang",
            "Jeg har allerede en profil"
        ),
        "suomi" to TargetTranslation(
            "Liity Auraan",
            "Yhdistä ystäviesi, perheesi ja kiinnostuksesi jakavien yhteisöjen kanssa. Täysin turvallinen ja eliniän ilmainen sosiaalinen pilvi.",
            "Aloita",
            "Minulla on jo profiili"
        ),
        "finnish" to TargetTranslation(
            "Liity Auraan",
            "Yhdistä ystäviesi, perheesi ja kiinnostuksesi jakavien yhteisöjen kanssa. Täysin turvallinen ja eliniän ilmainen sosiaalinen pilvi.",
            "Aloita",
            "Minulla on jo profiili"
        ),
        "čeština" to TargetTranslation(
            "Připojte se k Aura",
            "Spojte se s přáteli, rodinou a komunitami lidí, kteří sdílejí vaše zájmy. Zcela bezpečný a doživotně bezplatný sociální cloud.",
            "Začít",
            "Již mám profil"
        ),
        "czech" to TargetTranslation(
            "Připojte se k Aura",
            "Spojte se s přáteli, rodinou a komunitami lidí, kteří sdílejí vaše zájmy. Zcela bezpečný a doživotně bezplatný sociální cloud.",
            "Začít",
            "Již mám profil"
        ),
        "magyar" to TargetTranslation(
            "Csatlakozz az Aurához",
            "Kapcsolódj barátaiddal, családoddal és a veled azonos érdeklődésű közösségekkel. Teljesen biztonságos, ingyenes közösségi felhő.",
            "Kezdés",
            "Már van profilom"
        ),
        "hungarian" to TargetTranslation(
            "Csatlakozz az Aurához",
            "Kapcsolódj barátaiddal, családoddal és a veled azonos érdeklődésű közösségekkel. Teljesen biztonságos, ingyenes közösségi felhő.",
            "Kezdés",
            "Már van profilom"
        ),
        "română" to TargetTranslation(
            "Alătură-te Aura",
            "Conectează-te cu prieteni, familie și comunități care îți împărtășesc interesele. Cloud social securizat și gratuit pe viață.",
            "Să începem",
            "Am deja un profil"
        ),
        "romanian" to TargetTranslation(
            "Alătură-te Aura",
            "Conectează-te cu prieteni, familie și comunități care îți împărtășesc interesele. Cloud social securizat și gratuit pe viață.",
            "Să începem",
            "Am deja un profil"
        ),
        "slovenčina" to TargetTranslation(
            "Pridajte sa k Aura",
            "Spojte sa s priateľmi, rodinou a komunitami ľudí, ktorí zdieľajú vaše zájmy. Úplne bezpečný, doživotne bezplatný sociálny cloud.",
            "Začať",
            "Už mám profil"
        ),
        "slovak" to TargetTranslation(
            "Pridajte sa k Aura",
            "Spojte sa s priateľmi, rodinou a komunitami ľudí, ktorí zdieľajú vaše zájmy. Úplne bezpečný, doživotne bezplatný sociálny cloud.",
            "Začať",
            "Už mám profil"
        ),
        "български" to TargetTranslation(
            "Присъединете се към Aura",
            "Свържете се с приятели, семейство и общности от хора, които споделят вашите интереси. Напълно защитен безплатен социален облак.",
            "Започнете",
            "Вече имам профiл"
        ),
        "bulgarian" to TargetTranslation(
            "Присъединете се към Aura",
            "Свържете се с приятели, семейство и общности от хора, които споделят вашите интереси. Напълно защитен безплатен социален облак.",
            "Започнете",
            "Вече имам профiл"
        ),
        "hebrew" to TargetTranslation(
            "הצטרפו לאורה",
            "התחברו לחברים, משפחה וקהילות של אנשים שחولקים את תחומי העניין שלכם. ענן חברתי מאובטח وחינמי לכל החיים.",
            "מתחילים",
            "כבר יש לי פרופיל"
        ),
        "עברית" to TargetTranslation(
            "הצטרפו לאורה",
            "התחברו לחברים, משפחה וקהילות של אנשים שחولקים את תחומי העניין שלכם. ענן חברתי מאובטח وחינמי לכל החיים.",
            "מתחילים",
            "כבר יש לי פרופיל"
        ),
        "indonesia" to TargetTranslation(
            "Bergabung dengan Aura",
            "Terhubung dengan teman, keluarga, dan komunitas yang memiliki minat yang sama. Cloud sosial yang sepenuhnya aman dan gratis seumur hidup.",
            "Mulai",
            "Saya sudah punya profil"
        ),
        "melayu" to TargetTranslation(
            "Sertai Aura",
            "Berhubung dengan rakan, keluarga dan komuniti yang berkongsi minat anda. Awan sosial yang selamat dan percuma seumur hidup.",
            "Mula",
            "Saya sudah mempunyai profil"
        ),
        "malay" to TargetTranslation(
            "Sertai Aura",
            "Berhubung dengan rakan, keluarga dan komuniti yang berkongsi minat anda. Awan sosial yang selamat dan percuma seumur hidup.",
            "Mula",
            "Saya sudah mempunyai profil"
        ),
        "tagalog" to TargetTranslation(
            "Sumali sa Aura",
            "Kumonekta sa mga kaibigan, pamilya at komunidad na kapareho mo ng interes. Ligtas at libreng social cloud habang-buhay.",
            "Magsimula",
            "May profile na ako"
        ),
        "filipino" to TargetTranslation(
            "Sumali sa Aura",
            "Kumonekta sa mga kaibigan, pamilya at komunidad na kapareho mo ng interes. Ligtas at libreng social cloud habang-buhay.",
            "Magsimula",
            "May profile na ako"
        ),
        "kiswahili" to TargetTranslation(
            "Jiunge na Aura",
            "Ungana na marafiki, familia na jumuiya za watu wanaoshiriki mambo unayopenda. Wingu la kijamii salama na la bure maisha yote.",
            "Anza",
            "Tayari nina wasifu"
        ),
        "swahili" to TargetTranslation(
            "Jiunge na Aura",
            "Ungana na marafiki, familia na jumuiya za watu wanaoshiriki mambo unayopenda. Wingu la kijamii salama na la bure maisha yote.",
            "Anza",
            "Tayari nina wasifu"
        ),
        "amharic" to TargetTranslation(
            "Auraን ይቀላቀሉ",
            "የእርስዎን ፍላጎቶች ከሚጋሩ ጓደኞች፣ ቤተሰብ እና ማህበረሰቦች ጋር ይገናኙ። ሙሉ በሙሉ ደህንነቱ የተጠበቀ የህይወት ዘመን ነፃ ማህበራዊ ደመና።",
            "ይጀምሩ",
            "ቀድሞውኑ መገለጫ አለኝ"
        ),
        "isizulu" to TargetTranslation(
            "Joyina i-Aura",
            "Xhuma nabangani, umndeni nemiphakathi yabantu ababelana ngezinto ozithandayo. Isikhala sokuxhumana esiphephile nesimahhala impilo yonke.",
            "Qalisa",
            "Sengivele nginalo iphrofayili"
        ),
        "zulu" to TargetTranslation(
            "Joyina i-Aura",
            "Xhuma nabangani, umndeni nemiphakathi yabantu ababelana ngezinto ozithandayo. Isikhala sokuxhumana esiphephile nesimahhala impilo yonke.",
            "Qalisa",
            "Sengivele nginalo iphrofayili"
        ),
        "isixhosa" to TargetTranslation(
            "Joyina i-Aura",
            "Qhagamshelana nabahlobo, usapho kunye noluntu lwabantu ababelana ngezinto ozithandayo. Lifu lentlalontle elikhuselekileyo nelikhululekileyo ubomi bonke.",
            "Qalisa",
            "Sendimana nayo iprofayile"
        ),
        "xhosa" to TargetTranslation(
            "Joyina i-Aura",
            "Qhagamshelana nabahlobo, usapho kunye noluntu lwabantu ababelana ngezinto ozithandayo. Lifu lentlalontle elikhuselekileyo nelikhululekileyo ubomi bonke.",
            "Qalisa",
            "Sendimana nayo iprofayile"
        ),
        "afrikaans" to TargetTranslation(
            "Sluit aan by Aura",
            "Skakel met vriende, familie en gemeenskappe wat jou belangstellings deel. Veilige, lewenslange gratis sosiale wolk.",
            "Begin nou",
            "Ek het reeds 'n profiel"
        ),
        "yorùbá" to TargetTranslation(
            "Darapọ mọ Aura",
            "Sopọ pẹlu awọn ọrẹ, ẹbi ati awọn agbegbe ti awọn eniyan ti o pin awọn ire rẹ. Awọsanma awujọ ọfẹ ti o ni aabo fun igbesiaye.",
            "Bẹrẹ",
            "Mo ti ni profaili tẹlẹ"
        ),
        "yoruba" to TargetTranslation(
            "Darapọ mọ Aura",
            "Sopọ pẹlu awọn ọrẹ, ẹbi ati awọn agbegbe ti awọn eniyan ti o pin awọn ire rẹ. Awọsanma awujọ ọfẹ ti o ni aabo fun igbesiaye.",
            "Bẹrẹ",
            "Mo ti ni profaili tẹlẹ"
        ),
        "igbo" to TargetTranslation(
            "Soro na Aura",
            "Soro na ndị enyi, ezinụlọ na ndị nwere mmasị gị. Igwe ojii mmekọrịta dị mma yana n'efu maka ndụ gị niile.",
            "Malite",
            "Enwerem profaili mbu"
        ),
        "hausa" to TargetTranslation(
            "Shiga Aura",
            "Haɗa tare da abokai, dangi da al'ummomin mutanen da ke raba bukatun ku. Amintaccen gajimaren zamantakewa kyauta na rayuwa.",
            "Fara yanzu",
            "Ina da bayanin martaba"
        ),
        "persian" to TargetTranslation(
            "به اورا بپیوندید",
            "با دوستان، خانواده و انجمن‌های همفکر خود در ارتباط باشید. ابری کاملاً امن و رایگان برای تمام عمر.",
            "شروع کنید",
            "من قبلاً یک حساب دارم"
        ),
        "فارسی" to TargetTranslation(
            "به اورا بپیوندید",
            "با دوستان، خانواده و انجمن‌های همفکر خود در ارتباط باشید. ابری کاملاً امن و رایگان برای تمام عمر.",
            "شروع کنید",
            "من قبلاً یک حساب دارم"
        ),
        "urdu" to TargetTranslation(
            "اورا میں شامل ہوں",
            "ان دوستوں، خاندان اور برادریوں سے جڑیں جو آپ کی دلچسپیوں کا اشتراک کرتے ہیں۔ مکمل طور پر محفوظ اور زندگی بھر مفت سوشل کلاؤڈ۔",
            "شروع کریں",
            "میرا پروفائل پہلے سے موجود ہے"
        ),
        "اردو" to TargetTranslation(
            "اورا میں شامل ہوں",
            "ان دوستوں، خاندان اور برادریوں سے جڑیں جو آپ کی دلچسپیوں کا اشتراک کرتے ہیں۔ مکمل طور پر محفوظ اور زندگی بھر مفت سوشل کلاؤڈ۔",
            "شروع کریں",
            "میرا پروفائل پہلے سے موجود ہے"
        ),
        "pashto" to TargetTranslation(
            "له اورا سره یوځای شئ",
            "له خپلو ملګرو, کورنۍ او د ورته ګټو لرونکو ټولنو سره وصل شئ. په بشپړ ډول خوندي او د ژوند لپاره وړیا ټولنیز کلاوډ.",
            "پیل کړئ",
            "زه دمخه یو پروفایل لرم"
        ),
        "پښتو" to TargetTranslation(
            "له اورا سره یوځای شئ",
            "له خپلو ملګرو, کورنۍ او د ورته ګټو لرونکو ټولنو سره وصل شئ. په بشپړ ډول خوندي او د ژوند لپاره وړیا ټولنیز کلاوډ.",
            "پیل کړئ",
            "زه دمخه یو پروفایل لرم"
        ),
        "kurd" to TargetTranslation(
            "Tev li Aura bibe",
            "Bi heval, malbat û civakên ku berjewendiyên we parve dikin re têkilî daynin. Ewra civakî ya bi tevahî ewle û belaş a jiyanê.",
            "Dest pê bike",
            "Min jixwe profilek heye"
        ),
        "azerbaijani" to TargetTranslation(
            "Aura-ya Qoşulun",
            "Maraqlarınızı paylaşan dostlar, ailə və icmalarla əlaqə yaradın. Tamamilə təhlükəsiz, ömürlük pulsuz sosial bulud.",
            "Başlayın",
            "Artıq profilim var"
        ),
        "azərbaycanca" to TargetTranslation(
            "Aura-ya Qoşulun",
            "Maraqlarınızı paylaşan dostlar, ailə və icmalarla əlaqə yaradın. Tamamilə təhlükəsiz, ömürlük pulsuz sosial bulud.",
            "Başlayın",
            "Artıq profilim var"
        ),
        "georgian" to TargetTranslation(
            "შემოუერთდით Aura-ს",
            "დაუკავშირდით მეგობრებს, ოჯახს და თემებს, ვინც თქვენს ინტერესებს იზიარებს. სრულიად უსაფრთხო და უვადოდ უფასო სოციალური ღრუბელი.",
            "დაწყება",
            "უკვე მაქვს პროფილი"
        ),
        "ქართული" to TargetTranslation(
            "შემოუერთდით Aura-ს",
            "დაუკავშირდით მეგობრებს, ოჯახს და თემებს, ვინც თქვენს ინტერესებს იზიარებს. სრულიად უსაფრთხო და უვადოდ უფასო სოციალური ღრუბელი.",
            "დაწყება",
            "უკვე მაქვს პროფილი"
        ),
        "armenian" to TargetTranslation(
            "Միացեք Aura-ին",
            "Կապվեք ընկերների, ընտանիքի և համայնքների հետ, ովքեր կիսում են ձեր հետաքրքրությունները: Անվտանգ և անվճար սոցիալական ամպ ողջ կյանքի համար:",
            "Սկսել",
            "Ես արդեն ունեմ պրոֆիլ"
        ),
        "հայերեն" to TargetTranslation(
            "Միացեք Aura-ին",
            "Կապվեք ընկերների, ընտանիքի և համայնքների հետ, ովքեր կիսում են ձեր հետաքրքրությունները: Անվտանգ և անվճար սոցիալական ամպ ողջ կյանքի համար:",
            "Սկսել",
            "Ես արդեն ունեմ պրոֆիլ"
        ),
        "kazakh" to TargetTranslation(
            "Aura-ға қосылыңыз",
            "Қызығушылықтарыңызды бөлісетін достарыңызбен, отбасыңызбен жəне қауымдастықтармен байланысыңыз. Өмір бойы тегін əрі қауіпсіз әлеуметтік бұлт.",
            "Бастау",
            "Менде профиль бар"
        ),
        "қазақ" to TargetTranslation(
            "Aura-ға қосылыңыз",
            "Қызығушылықтарыңызды бөлісетін достарыңызбен, отбасыңызбен жəне қауымдастықтармен байланысыңыз. Өмір бойы тегін əрі қауіпсіз әлеуметтік бұлт.",
            "Бастау",
            "Менде профиль бар"
        ),
        "uzbek" to TargetTranslation(
            "Aura-ga qo'shiling",
            "Qiziqishlaringizni baham ko'radigan do'stlar, oila va jamoalar bilan bog'laning. Mutlaqo xavfsiz va umrbod bepul ijtimoiy bulut.",
            "Boshlash",
            "Menda profil bor"
        ),
        "oʻzbek" to TargetTranslation(
            "Aura-ga qo'shiling",
            "Qiziqishlaringizni baham ko'radigan do'stlar, oila va jamoalar bilan bog'laning. Mutlaqo xavfsiz va umrbod bepul ijtimoiy bulut.",
            "Boshlash",
            "Menda profil bor"
        ),
        "turkmen" to TargetTranslation(
            "Aura Goşulyň",
            "Gyzgylanmalaryňyzy paýlaşýan dostlaryňyz, maşgalaňyz we jemgyýetleriňiz bilen baglanyşyň. Ömürlik mugt we howpsuz durmuş buludy.",
            "Başlaň",
            "Meniň profilim bar"
        ),
        "türkmen" to TargetTranslation(
            "Aura Goşulyň",
            "Gyzgylanmalaryňyzy paýlaşýan dostlaryňyz, maşgalaňyz we jemgyýetleriňiz bilen baglanyşyň. Ömürlik mugt we howpsuz durmuş buludy.",
            "Başlaň",
            "Meniň profilim bar"
        ),
        "tajik" to TargetTranslation(
            "Ба Aura ҳамроҳ шавед",
            "Бо дӯстон, оила ва ҷомеаҳое, ки таваҷҷӯҳи шуморо шариканд, пайваст шавед. Абри иҷтимоии комилан бехатар ва ройгон барои ҳаёт.",
            "Оғоз кунед",
            "Ман аллакай профил дорам"
        ),
        "tojik" to TargetTranslation(
            "Ба Aura ҳамроҳ шавед",
            "Бо дӯстон, оила ва ҷомеаҳое, ки таваҷҷӯҳи шуморо шариканд, пайваст шавед. Абри иҷтимоии комилан бехатар ва ройгон барои ҳаёт.",
            "Оғоз кунед",
            "Ман аллакай профил дорам"
        ),
        "kyrgyz" to TargetTranslation(
            "Aura'га кошулуңуз",
            "Кызыгууларыңызды бөлүшкөн достор, үй-бүлө жана коомчулуктар менен байланышыңыз. Абсолюттук коопсуз жана өмүр бою акысыз социалдык булут.",
            "Баштоо",
            "Менде профиль бар"
        ),
        "кыргыз" to TargetTranslation(
            "Aura'га кошулуңуз",
            "Кызыгууларыңызды бөлүшкөн достор, үй-бүлө жана коомчулуктар менен байланышыңыз. Абсолюттук коопсуз жана өмүр бою акысыз социалдык булут.",
            "Баштоо",
            "Менде профиль бар"
        ),
        "mongolian" to TargetTranslation(
            "Aura-д нэгдэх",
            "Таны сонирхлыг хуваалцах найз нөхөд, гэр бүл, хамт олонтой холбогдоорой. Насан туршдаа үнэ төлбөргүй бөгөөд аюулгүй нийгмийн сүлжээ.",
            "Эхлэх",
            "Надад аль хэдийн профайл бий"
        ),
        "монгол" to TargetTranslation(
            "Aura-д нэгдэх",
            "Таны сонирхлыг хуваалцах найз нөхөд, гэр бүл, хамт олонтой холбогдоорой. Насан туршдаа үнэ төлбөргүй бөгөөд аюулгүй нийгмийн сүлжээ.",
            "Эхлэх",
            "Надад аль хэдийн профайл бий"
        ),
        "nepali" to TargetTranslation(
            "Aura मा सामेल हुनुहोस्",
            "तपाईंका रुचिहरू साझा गर्ने साथीहरू, परिवार र समुदायहरूसँग जोड्नुहोस्। पूर्ण रूपमा सुरक्षित र जीवनभर निःशुल्क सामाजिक क्लाउड।",
            "सुरु गर्नुहोस्",
            "मेरो पहिले नै प्रोफाइल छ"
        ),
        "नेपाली" to TargetTranslation(
            "Aura मा सामेल हुनुहोस्",
            "तपाईंका रुचिहरू साझा गर्ने साथीहरू, परिवार र समुदायहरूसँग जोड्नुहोस्। पूर्ण रूपมา सुरक्षित र जीवनभर निःशुल्क सामाजिक क्लाउड।",
            "सुरु गर्नुहोस्",
            "मेरो पहिले नै प्रोफाइल छ"
        ),
        "sinhala" to TargetTranslation(
            "Aura සමඟ එක්වන්න",
            "ඔබේ උനන්දුව බෙදාගන්නා පවුලේ අය, මිතුරන් සහ ප්‍රජාවන් සමඟ සම්බන්ධ වන්න. ජීවිත කාලයටම නොමිලේ සහ සම්පූර්ණයෙන්ම ආරක්ෂිත සමාජ වලාකුළ.",
            "ආරම්භ කරන්න",
            "මට දැනටමත් පැතිකඩක් ඇත"
        ),
        "සිංහල" to TargetTranslation(
            "Aura සමඟ එක්වන්න",
            "ඔබේ උനන්දුව බෙදාගන්නා පවුලේ අය, මිතුරන් සහ ප්‍රජාවන් සමඟ සම්බන්ධ වන්න. ජීවිත කාලයටම නොමිලේ සහ සම්ပූර්ණයෙන්ම ආරක්ෂිත සමාජ වලාකුළ.",
            "ආරම්භ කරන්න",
            "මට දැනටමත් පැතිකඩක් ඇත"
        ),
        "tamil" to TargetTranslation(
            "அவுராவில் இணையுங்கள்",
            "உங்கள் ஆர்வங்களைப் பகிர்ந்து கொள்ளும் நண்பர்கள், குடும்பத்தினர் மற்றும் சமூகங்களுடன் இணையுங்கள். முற்றிலும் பாதுகாப்பான, வாழ்நாள் முழுவதும் இலவச சமூக மேகம்.",
            "தொடங்குங்கள்",
            "எனக்கு ஏற்கனவே கணக்கு உள்ளது"
        ),
        "தமிழ்" to TargetTranslation(
            "அவுராவில் இணையுங்கள்",
            "உங்கள் ஆர்வங்களைப் பகிர்ந்து கொள்ளும் நண்பர்கள், குடும்பத்தினர் மற்றும் சமூகங்களுடன் இணையுங்கள். முற்றிலும் பாதுகாப்பான, வாழ்நாள் முழுவதும் இலவச சமூக மேகம்.",
            "தொடங்குங்கள்",
            "எனக்கு ஏற்கனவே கணக்கு உள்ளது"
        ),
        "telugu" to TargetTranslation(
            "Auraలో చేరండి",
            "మీ ఆసక్తులను పంచుకునే స్నేహితులు, குடும்ப సభ్యులు మరియు సంఘాలతో కనెక్ట్ అవ్వండి. సామాజిక క్లೌడ్ పూర్తిగా సురક્ષิตం.",
            "ప్రారంభించండి",
            "నాకు ఇప్పటికే ప్రొఫైల్ ఉంది"
        ),
        "తెలుగు" to TargetTranslation(
            "Auraలో చేరండి",
            "మీ ఆసక్తులను పంచుకునే స్నేహితులు, குடும்ப సభ్యులు మరియు సంఘాలతో కనెక్ట్ అవ్వండి. సాমাజిక క్లೌడ్ పూర్తిగా సురક્ષิตం.",
            "ప్రారంభించండి",
            "నాకు ఇప్పటికే ప్రొఫైల్ ఉంది"
        ),
        "kannada" to TargetTranslation(
            "Aura ಗೆ ಸೇರಿಕೊಳ್ಳಿ",
            "ನಿಮ್ಮ ಆಸಕ್ತಿಗಳನ್ನು ಹಂಚಿಕೊಳ್ಳುವ ಸ್ನೇಹಿತರು, ಕುಟುಂಬ ಮತ್ತು ಸಮೂಹಗಳೊಂದಿಗೆ ಸಂಪರ್ಕ ಸಾಧಿಸಿ. ಜೀವಿತಾವಧಿಯ ಉಚಿತ ಕ್ಲೌಡ್.",
            "ಪ್ರಾರಂಭಿಸಿ",
            "ನನಗೆ ಈಗಾಗಲೇ ಪ್ರೊಫೈಲ್ ಇದೆ"
        ),
        "ಕನ್ನಡ" to TargetTranslation(
            "Aura ಗೆ ಸೇರಿಕೊಳ್ಳಿ",
            "ನಿಮ್ಮ ಆಸಕ್ತಿಗಳನ್ನು ಹಂಚಿಕೊಳ್ಳುವ ಸ್ನೇಹಿತರು, ಕುಟುಂಬ ಮತ್ತು ಸಮೂಹಗಳೊಂದಿಗೆ ಸಂಪರ್క ಸಾಧಿಸಿ. ಜೀವಿತಾವಧಿಯ ಉಚಿತ ಕ್ಲೌಡ್.",
            "ಪ್ರಾರಂಭಿಸಿ",
            "ನನಗೆ ಈಗಾಗಲೇ ಪ್ರೊಫೈಲ್ ಇದೆ"
        ),
        "malayalam" to TargetTranslation(
            "Aura-യിൽ ചേരുക",
            "നിങ്ങളുടെ താൽപ്പര്യങ്ങൾ പങ്കിടുന്ന സുഹൃത്തുക്കൾ, കുടുംബം, കമ്മ്യൂണിറ്റികൾ എന്നിവരുമായി ബന്ധപ്പെടുക. സോഷ്യൽ ക്ലൗഡ് പൂർണ്ണമായും സൌജന്യമാണ്.",
            "ആരംഭിക്കുക",
            "എനിക്ക് ഇതിനകം പ്രൊഫൈൽ ഉണ്ട്"
        ),
        "മലയാളം" to TargetTranslation(
            "Aura-യിൽ ചേരുക",
            "നിങ്ങളുടെ താൽപ്പര്യങ്ങൾ പങ്കിടുന്ന സുഹൃത്തുക്കൾ, കുടുംബം, കമ്മ്യൂणीറ്റികൾ എന്നിവരുമായി ബന്ധപ്പെടുക. സോഷ്യൽ ക്ലൗഡ് പൂർണ്ണമായും സൌജന്യമാണ്.",
            "ആരംഭിക്കുക",
            "എനിക്ക് ഇതിനകം പ്രൊഫൈൽ ഉണ്ട്"
        ),
        "marathi" to TargetTranslation(
            "Aura मध्ये सामील व्हा",
            "तुमच्या आवडीनिवडी शेअर करणाऱ्या मित्र, कुटुंब आणि समुदायांशी जोडा. पूर्णपणे सुरक्षित आणि आजीवन मोफत सोशल क्लाउड.",
            "सुरू करा",
            "माझे आधीच प्रोफाइल आहे"
        ),
        "मराठी" to TargetTranslation(
            "Aura मध्ये सामील व्हा",
            "तुमच्या आवडीनिवडी शेअर करणाऱ्या मित्र, कुटुंब आणि समुदायांशी जोडा. पूर्णपणे सुरक्षित आणि आजीवन मोफत सोशल क्लाउड.",
            "सुरू करा",
            "माझे आधीच प्रोफाइल आहे"
        ),
        "gujarati" to TargetTranslation(
            "Aura માં જોડાઓ",
            "તમારી રુચિઓ શેર કરતા મિત્રો, કુટુંબીજનો અને સમુદાયો સાથે જોડાઓ. સંપૂર્ણ સુરક્ષિત સોશિયલ ક્લાઉડ.",
            "શરૂ કરો",
            "મારી પાસે પહેલેથી પ્રોફાઇલ છે"
        ),
        "ગુજરાતી" to TargetTranslation(
            "Aura માં જોડાઓ",
            "તમારી રુચિઓ શેર કરતા મિત્રો, કુટુંબીજનો અને સમુદાયો સાથે જોડાઓ. સંપૂર્ણ સુરક્ષિત સોશિયલ ક્લાઉડ.",
            "શરૂ કરો",
            "મારી પાસે પહેલેથી પ્રોફાઇલ છે"
        ),
        "punjabi" to TargetTranslation(
            "Aura ਵਿੱਚ ਸ਼ਾਮਲ ਹੋਵੋ",
            "ਆਪਣੇ ਹਿੱਤ ਸਾਂਝੇ ਕਰਨ ਵਾਲੇ ਦੋਸਤਾਂ, ਪਰਿਵਾਰ ਅਤੇ ਭਾਈਚਾਰਿਆਂ ਨਾਲ ਜੁੜੋ। ਪੂਰੀ ਤਰ੍ਹਾਂ ਸੁਰੱਖਿਅਤ ਅਤੇ ਉਮਰ ਭਰ ਮੁਫ਼ਤ ਸੋਸ਼ਲ ਕਲਾਊਡ।",
            "ਸ਼ੁਰੂ ਕਰੋ",
            "ਮੇਰਾ ਪਹਿਲਾਂ ਹੀ ਪ੍ਰੋਫਾਈਲ ਹੈ"
        ),
        "ਪੰਜਾਬੀ" to TargetTranslation(
            "Aura ਵਿੱਚ ਸ਼ਾਮਲ ਹੋਵੋ",
            "ਆਪਣੇ ਹਿੱਤ ਸਾਂਝੇ ਕਰਨ ਵਾਲੇ ਦੋਸਤਾਂ, ਪਰਿਵਾਰ ਅਤੇ ਭਾਈਚਾਰਿਆਂ ਨਾਲ ਜੁੜੋ। ਪੂਰੀ ਤਰ੍ਹਾਂ ਸੁਰੱਖਿਅਤ ਅਤੇ ਉਮਰ ਭਰ ਮੁਫ਼ਤ ਸੋਸ਼ਲ ਕਲਾਊਡ।",
            "ਸ਼ੁਰੂ ਕਰੋ",
            "ਮੇਰਾ ਪਹਿਲਾਂ ਹੀ ਪ੍ਰੋਫਾਈਲ ਹੈ"
        ),
        "odia" to TargetTranslation(
            "Aura ରେ ଯୋଗ ଦିଅନ୍ତು",
            "ସମାନ ଆଗ୍ରହ ରଖୁଥିବା ବନ୍ଧୁ, ପରିବାର ଏବଂ ସମୁଦାୟ ସହ ଯୋଡ଼ି ହୁଅନ୍ତು। ସମ୍ପୂର୍ଣ୍ଣ ସୁରକ୍ଷିତ ଫ୍ରି ସୋସିଆଲ କ୍ଲାଉଡ।",
            "ଆରମ୍ଭ କରନ୍ତು",
            "ମୋର ପୂର୍ବରୁ ପ୍ରୋଫାଇଲ୍ ଅଛି"
        ),
        "ଓଡ଼ିଆ" to TargetTranslation(
            "Aura ରେ ଯୋଗ ଦିଅନ୍ତು",
            "ସମାନ ଆଗ୍ରହ ରଖୁଥିବା ବନ୍ଧୁ, ପରିବାର ଏବଂ ସମୁଦାୟ ସହ ଯୋଡ଼ି ହୁଅନ୍ତು। ସମ୍ପୂର୍ଣ୍ଣ ସୁರକ୍ଷିତ ଫ୍ରି ସୋସିଆଲ କ୍ଲାଉଡ।",
            "ଆରମ୍ଭ କରନ୍ତು",
            "ମୋର ପୂର୍ବରୁ ପ୍ରୋଫାଇଲ୍ ଅଛି"
        ),
        "assamese" to TargetTranslation(
            "Aura ত যোগদান কৰক",
            "আপোনাৰ আগ্ৰহৰ ব্যক্তিসকলৰ সৈতে সংযোগ স্থাপন কৰক। সম্পূৰ্ণ সুৰক্ষিত আজীৱন সামাজিক ক্লাউড।",
            "আৰম্ভ কৰক",
            "মোৰ ইতিমধ্যে এটা প্ৰফাইল আছে"
        ),
        "অসমীয়া" to TargetTranslation(
            "Aura ত যোগদান কৰক",
            "আপোনাৰ আগ্ৰহৰ ব্যক্তিসকলৰ সৈতে সংযোগ স্থাপন কৰক। সম্পূৰ্ণ সুৰক্ষিত আজীৱন সামাজিক ক্লাউড।",
            "আৰম্ভ কৰক",
            "মোৰ ইতিমধ্যে এটা প্ৰফাইল আছে"
        ),
        "maithili" to TargetTranslation(
            "Aura सँ जुड़ू",
            "अपन रुचिक लोक, family आ समाजक संग जुड़ू। पूर्ण सुरक्षित आ जीवनभरि मुफ़्त सामाजिक क्लाउड।",
            "शुरू करू",
            "हमर पहिलहि सँ प्रोफाइल अछि"
        ),
        "मैथिली" to TargetTranslation(
            "Aura सँ जुड़ू",
            "अपन रुचिक लोक, family आ समाजक संग जुड़ू। पूर्ण सुरक्षित आ जीवनभरि मुफ़्त सामाजिक क्लाउड।",
            "शुरू करू",
            "हमर पहिलहि सँ प्रोफाइल अछि"
        ),
        "sanskrit" to TargetTranslation(
            "Aura संगच्छध्वम्",
            "सहृदयमित्रैः कुटुम्बे च सह संबद्धाः भवन्तु भवन्त। सुरक्शित निःशुल्क च सामाजिकीय व्यवस्था।",
            "प्रारभ्यताम्",
            "मम परिचयपत्रमस्ति"
        ),
        "संस्कृतम्" to TargetTranslation(
            "Aura संगच्छध्वम्",
            "सहृदयमित्रैः कुटुम्बे च सह संबद्धाः भवन्तु भवन्त। सुरक्शित निःशुल्क च सामाजिकीय व्यवस्था।",
            "प्रारभ्यताम्",
            "मम परिचयपत्रमस्ति"
        ),
        "burmese" to TargetTranslation(
            "Aura သို့ ဆက်သွယ်လိုက်ပါ",
            "သင်နှင့် ဝါသနာတူ မိတ်ဆွေများ၊ မိသားစုများနှင့် ချိတ်ဆက်လိုက်ပါ။ တစ်သက်တာ အခမဲ့ လုံခြုံသော ဆိုရှယ်ကလောက်ဒ် ဖြစ်သည်။",
            "စတင်ရန်",
            "ကျွန်ုပ်တွင် အကောင့်ရှိပြီးသားဖြစ်သည်"
        ),
        "မြန်မာဘာသာ" to TargetTranslation(
            "Aura သို့ ဆက်သွယ်လိုက်ပါ",
            "သင်နှင့် ဝါသနာတူ မိတ်ဆွေများ၊ မိသားစုများနှင့် ချိတ်ဆက်လိုက်ပါ။ တစ်သက်တာ အခမဲ့ လုံခြုံသော ဆိုရှယ်ကလောက်ဒ် ဖြစ်သည်။",
            "စတင်ရန်",
            "ကျွန်ုပ်တွင် အကောင့်ရှိပြီးသားဖြစ်သည်"
        ),
        "khmer" to TargetTranslation(
            "ចូលរួមជាមួយ Aura",
            "ភ្ជាប់ទំនាក់ទំនងជាមួយមិត្តភក្តិ គ្រួសារ និងសហគមន៍ដែលមានចំណាប់អារម្មណ៍ដូចគ្នា។ សេវាកម្មសុវត្ថិភាពខ្ពស់ និងឥតគិតថ្លៃមួយជីវិត។",
            "ចាប់ផ្តើម",
            "ខ្ញុំមានប្រវត្តិរូបរួចហើយ"
        ),
        "lao" to TargetTranslation(
            "ເຂົ້າร่วม Aura",
            "ເຊື່ອມຕໍ່ກັບຫມູ່ເພື່อน, ຄອບຄົວ ແລະຊຸມຊົນທີ່ມີຄວາມສົນໃຈຮ່ວມกัน. ຟຣີ ແລະປອດໄພຕະຫຼອດຊີວິດ.",
            "ເລີ່ມຕົ້ນ",
            "ຂ້ອຍມີໂປຣໄຟល໌ແລ้ວ"
        ),
        "tibetan" to TargetTranslation(
            "Aura ལ་མཉམ་ཞུགས་བྱོས།",
            "ཁྱེད་ཀྱི་དགའ་ཕྱོགས་མཉམ་སྤྱོད་བྱེད་མཁན་གྱི་གྲོགས་པོ་དང་ནང་མི་དང་མཉམ་དུ་အབྲེལ་མཐུད་བྱོས། ཚེ་གང་རིང་རིན་མེད་དང་བདེ་འཇགས་ལྡན་པའི་སྤྱི་ཚོགས་སྤྲིན་པ།",
            "མགོ་འཛུགས་བྱོས།",
            "ང་ལ་សྒེར་གྱི་ངོ་སྤྲོད་ཡོད।"
        ),
        "somali" to TargetTranslation(
            "Ku biir Aura",
            "La xiriir asxaabta, qoyska iyo bulshooyinka la wadaaga danahaaga. Daruur bulsho oo gabi ahaanba ammaan ah oo bilaash ah adduunka oo dhan.",
            "Bilow",
            "Waxaan mar hore leeyahay profile"
        ),
        "oromoo" to TargetTranslation(
            "Aura'tti dabalamaa",
            "Hiriyoota, maatii fi hawaasa fedhii kee qooddatan waliin wal qunnamaa. Haa ta'u malee, badhaadhina bilisaa fi amansiisaa ta'e bilisaan argadhaa.",
            "Eegali",
            "Kanaan dura piroofaayilii qaba"
        ),
        "tigrinya" to TargetTranslation(
            "Aura ተጸንበሩ",
            "ምስ መሓዙት፣ ስድራቤትን ተገዳስነትኩም ዘካፍሉ ማሕበረሰባትን ተራኸቡ። ውሑስን ንዘለኣለም ብనాጻ ዝሰርሕን ማሕበራዊ ደመና።",
            "ጀምር",
            "ድሮ პროፋይል ኣሎኒ"
        ),
        "malagasy" to TargetTranslation(
            "Midira ao amin'ny Aura",
            "Mifandraisa amin'ny namana, fianakaviana ary fiarahamonina manana mahaliana iraisana. Sehatra sosialy azo antoka sy maimaimpoana.",
            "Hanomboka",
            "Efa manana kaonty aho"
        ),
        "shona" to TargetTranslation(
            "Batana ne Aura",
            "Batana neshamwari, mhuri, uye nharaunda dzevanhu vanogovana zvaunofarira. Cloud ruzhinji rakachengeteka zvizere kweupenyu hwose.",
            "Tanga",
            "Ndatova neprofile"
        ),
        "chichewa" to TargetTranslation(
            "Lowani mu Aura",
            "Lumikizanani ndi abwenzi, abale komanso magulu a anthu omwe amagawana zomwe mumakonda. Cloud yotetezeka komanso yaulere moyo wanu wonse.",
            "Yambani",
            "Ndili kale ndi mbiri"
        ),
        "sesotho" to TargetTranslation(
            "Kena ho Aura",
            "Ikamahanya le metsoalle, lelapa le lichaba tsa batho ba nang le thahasello e tšoanang le ea hau. Cloud e bolokehileng.",
            "Qala",
            "Ke se ke na le profil"
        ),
        "setswana" to TargetTranslation(
            "Tsena mo Aura",
            "Golagana le ditsala, lelapa le ditsela tsa batho ba ba nang kgatlhego e e tshwanang le ya gago. Maru a loago.",
            "Simolola",
            "Ke setse ke na le porofaelo"
        ),
        "tsonga" to TargetTranslation(
            "Hlangana na Aura",
            "Hlangana na vanghana, ndyangu na miganga ya vanhu lava rhandzaka swilo leswi u swi rhandzaka. Lifu leri hlayisekeke.",
            "Sungula",
            "Ndzi na profil kale"
        ),
        "tshivenda" to TargetTranslation(
            "Dzhena kha Aura",
            "Tanganani na khonani, mita na zwitshavha zwa vhathu vha share-aho zwipfumi zwanu. Gole la matshilisano lo tsireledzeaho.",
            "Thoma",
            "Ndi kale ndi na phurofaele"
        ),
        "siswati" to TargetTranslation(
            "Joyina i-Aura",
            "Xhumana nebangani, mndeni nemiphakathi yabantfu lababelana ngetintfo lotitsandzako.",
            "Calisa",
            "Sengivele nginalo liphrofayili"
        ),
        "sindhi" to TargetTranslation(
            "اورا ۾ شامل ٿيو",
            "پنهنجي دوستن، خاندان ۽ برادرين سان جڙو جيڪي توهان جي دلچسپين کي شيئر ڪن ٿا. مڪمل طور تي محفوظ ۽ زندگي بھر لاء مفت سوشل ڪلاؤڊ.",
            "شروع ڪريو",
            "منهنجو پروفائل اڳي ئي موجود آهي"
        ),
        "slovenščina" to TargetTranslation(
            "Pridruži se Auri",
            "Poveži se s prijatelji, družino in skupnostmi, ki delijo tvoja zanimanja. Popolnoma varno in doživljenjsko brezplačno družbeno omrežje v oblaku.",
            "Začni",
            "Že imam profil"
        ),
        "slovenian" to TargetTranslation(
            "Pridruži se Auri",
            "Poveži se s prijatelji, družino in skupnostmi, ki delijo tvoja zanimanja. Popolnoma varno in doživljenjsko brezplačno družbeno omrežje v oblaku.",
            "Začni",
            "Že imam profil"
        ),
        "lithuanian" to TargetTranslation(
            "Prisijunkite prie Aura",
            "Bendraukite su draugais, šeima ir bendruomenėmis, kurios dalijasi jūsų interesais. Visiškai saugus ir nemokamas socialinis debesis visam gyvenimui.",
            "Pradėti",
            "Jau turiu profilį"
        ),
        "lietuvių" to TargetTranslation(
            "Prisijunkite prie Aura",
            "Bendraukite su draugais, šeima ir bendruomenėmis, kurios dalijasi jūsų interesais. Visiškai saugus ir nemokamas socialinis debesis visam gyvenimui.",
            "Pradėti",
            "Jau turiu profilį"
        ),
        "latvian" to TargetTranslation(
            "Pievienojies Aura",
            "Sazinieties ar draugiem, ģimeni un kopienām, kurām ir kopīgas jūsu intereses. Pilnībā drošs un bezmaksas sociālais mākonis visam mūžam.",
            "Sākt",
            "Man jau ir profils"
        ),
        "latviešu" to TargetTranslation(
            "Pievienojies Aura",
            "Sazinieties ar draugiem, ģimeni un kopienām, kurām ir kopīgas jūsu intereses. Pilnībā drošs un bezmaksas sociālais mākonis visam mūžam.",
            "Sākt",
            "Man jau ir profils"
        ),
        "estonian" to TargetTranslation(
            "Liitu Auraga",
            "Suhelge sõprade, pere ja kogukondadega, kes jagavad teie huve. Täiesti turvaline ja eluaegne tasuta sotsiaalpilv.",
            "Alusta",
            "Mul on juba profiil"
        ),
        "eesti" to TargetTranslation(
            "Liitu Auraga",
            "Suhelge sõprade, pere ja kogukondadega, kes jagavad teie huve. Täiesti turvaline ja eluaegne tasuta sotsiaalpilv.",
            "Alusta",
            "Mul on juba profiil"
        ),
        "basque" to TargetTranslation(
            "Bat egin Aurarekin",
            "Konektatu zure interes berdinak dituzten lagun, familia eta komunitateekin. Hodei sozial guztiz segurua eta doakoa bizitza osorako.",
            "Hasi",
            "Profila badut dagoeneko"
        ),
        "euskara" to TargetTranslation(
            "Bat egin Aurarekin",
            "Konektatu zure interes berdinak dituzten lagun, familia eta komunitateekin. Hodei sozial guztiz segurua eta doakoa bizitza osorako.",
            "Hasi",
            "Profila badut dagoeneko"
        ),
        "catalan" to TargetTranslation(
            "Uneix-te a Aura",
            "Connecta amb amics, familiars i comunitats que comparteixen els teus interessos. Núvol social completament segur i gratuït de por vida.",
            "Comença",
            "Ja tinc un perfil"
        ),
        "galician" to TargetTranslation(
            "Únete a Aura",
            "Conéctate con amigos, familiares e comunidades de persoas que comparten os teus intereses. Nube social totalmente segura e de balde para toda a vida.",
            "Comezar",
            "Xa teño un perfil"
        ),
        "kinyarwanda" to TargetTranslation(
            "Yinjira muri Aura",
            "Fatanya n'inshuti, umuryango n'abantu bafite inyungu musangiye. Imbuga nkoranyambaga zizewe kandi z'ubuntu ubuzima bwose.",
            "Tangira",
            "Nfite konti mbe niteguye"
        ),
        "kirundi" to TargetTranslation(
            "Injira muri Aura",
            "Fatanya n'abagenzi, umuryango n'abantu bafise ivyo musangiye. Imbuga nkoranyambaga zizewe kandi z'ubuntu ubuzima bwose.",
            "Tangura",
            "Ndi n'umwirondoro"
        ),
        "wolof" to TargetTranslation(
            "Bokk ci Aura",
            "Lëkkalook say xarit, sa waa kër ak mbooloo yi bokk say yitte. Ànd bu dëgër te dunu la laaj dara ba abada.",
            "Tambali",
            "Am naa profil ba noppi"
        ),
        "lingala" to TargetTranslation(
            "Kɔtá na Aura",
            "Salá boyokani na baninga, libota mpe masangá ya bato oyo bolingaka makambo moko. Bolingo mpe lobiko ya ofele mpo na bomoi.",
            "Bandá",
            "Nazali na profil kala"
        ),
        "akan" to TargetTranslation(
            "Kɔm bɔm wɔ Aura",
            "Nyanfo ne abusua ne kɔmunity a wɔwɔ interest koro. Social cloud a ɛyɛ safe na ɛyɛ free daa daa nyinaa.",
            "Fi ase",
            "Mewɔ profile dedaw"
        ),
        "irish" to TargetTranslation(
            "Bí páirteach in Aura",
            "Ceangail le cairde, teaghlach agus pobail daoine a bhfuil na suimeanna céanna acu. Cloud sóisialta atá go hiomlán slán agus saor in aisce ar feadh an tsaoil.",
            "Tosaigh",
            "Tá próifíl agam cheana féin"
        ),
        "gaeilge" to TargetTranslation(
            "Bí páirteach in Aura",
            "Ceangail le cairde, teaghlach agus pobail daoine a bhfuil na suimeanna céanna acu. Cloud sóisialta atá go hiomlán slán agus saor in aisce ar feadh an tsaoil.",
            "Tosaigh",
            "Tá próifíl agam cheana féin"
        ),
        "welsh" to TargetTranslation(
            "Ymunwch ag Aura",
            "Cysylltwch â ffrindiau, teulu a chymunedau o bobl sy'n rhannu eich diddordebau. Cwmwl cymdeithasol hollol ddiogel a rhad ac am ddim am oes.",
            "Dechrau",
            "Mae gen i broffil eisoes"
        ),
        "maltese" to TargetTranslation(
            "Ingħaqad ma' Aura",
            "Qabbad ma' ħbieb, familja u komunitajiet ta' nies li jaqsmu l-interessi tiegħek. Sħab soċjali kompletament sigur u b'xejn għal dejjem.",
            "Ibda",
            "Għandi profil diġà"
        ),
        "esperanto" to TargetTranslation(
            "Aliĝu al Aura",
            "Konektiĝu kun amikoj, familio kaj komunumoj kiuj kunhavas viajn interesojn. Tute sekura kaj dumvive senpaga socia nubo.",
            "Komenci",
            "Mi jam havas profilon"
        ),
        "latin" to TargetTranslation(
            "Aura Concilia",
            "Coniungas cum amicis, familia et communitatibus hominum qui tua studia partiuntur. Secura et omnino gratuita socialis nubes ad vitam.",
            "Incipe",
            "Iam habeo formam"
        ),
        "corsican" to TargetTranslation(
            "Unisci à Aura",
            "Cunnettate cù l'amichi, a famiglia è cumunità di persone chì sparte i vostri interessi. Nulu suciale sicuru è liberu per a vita.",
            "Principià",
            "Aghju digià un prufilu"
        )
    )

    private fun findTranslation(lang: String): TargetTranslation {
        val lower = lang.lowercase()
        val entry = dictionary.entries.firstOrNull { lower.contains(it.key) }
        return entry?.value ?: dictionary["english"]!!
    }

    fun getWelcomeTitle(lang: String): String {
        return findTranslation(lang).welcomeTitle
    }

    fun getWelcomeDesc(lang: String): String {
        return findTranslation(lang).welcomeDesc
    }

    fun getButtonGetStarted(lang: String): String {
        return findTranslation(lang).getStartedText
    }

    fun getButtonAlreadyHaveProfile(lang: String): String {
        return findTranslation(lang).alreadyHaveProfileText
    }

    fun getOfflineTranslation(text: String, language: String): String? {
        val cleanLang = language.lowercase()
        val isBengali = cleanLang.contains("বাংলা") || cleanLang.contains("bengali")
        val isSpanish = cleanLang.contains("español") || cleanLang.contains("spanish")
        
        val cleanText = text.trim()
        
        if (isBengali) {
            return text
        } else if (isSpanish) {
            return when {
                cleanText.equals("Join Aura", ignoreCase = true) -> "Únete a Aura"
                cleanText.contains("Connect with friends", ignoreCase = true) -> "Conéctate con amigos, familiares y comunidades. Red social en la nube totalmente de por vida."
                cleanText.equals("Get started", ignoreCase = true) -> "Comenzar"
                cleanText.equals("I already have a profile", ignoreCase = true) -> "Ya tengo un perfil"
                cleanText.equals("Sign in with Discord", ignoreCase = true) -> "Iniciar sesión con Discord"
                cleanText.equals("Sign up with Discord", ignoreCase = true) -> "Registrarse con Discord"
                cleanText.equals("Dialogue comments", ignoreCase = true) -> "Comentarios del diálogo"
                cleanText.contains("No comments yet", ignoreCase = true) -> "Sin comentarios aún. ¡Escribe la primera respuesta!"
                cleanText.equals("Comment", ignoreCase = true) -> "Comentar"
                cleanText.equals("Feeds", ignoreCase = true) -> "Feeds"
                cleanText.equals("Friends", ignoreCase = true) -> "Amigos"
                cleanText.equals("Create Post", ignoreCase = true) -> "Crear publicación"
                cleanText.equals("Profile", ignoreCase = true) -> "Perfil"
                cleanText.equals("Settings", ignoreCase = true) -> "Ajustes"
                cleanText.equals("Search", ignoreCase = true) -> "Buscar"
                cleanText.equals("Delete", ignoreCase = true) -> "Eliminar"
                else -> null
            }
        }
        return null
    }
}

// --- SCREEN 1: Welcome/Auth Layout matching exactly the requested photograph ---
@Composable
fun WelcomeScreen(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    var isLanguageDialogVisible by remember { mutableStateOf(false) }
    var searchQueryLanguage by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity

    val securityPrefs = remember { context.getSharedPreferences("aura_security_prefs", android.content.Context.MODE_PRIVATE) }
    var showSecurityPopup by remember { mutableStateOf(!securityPrefs.getBoolean("security_understood_v3", false)) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDiscordAccounts(context)
    }
    
    val discordAccounts by viewModel.recognizedDiscordAccounts.collectAsStateWithLifecycle()
    var showDiscordChooser by remember { mutableStateOf(false) }
    var customEmailInput by remember { mutableStateOf("") }
    var customNameInput by remember { mutableStateOf("") }
    var isAuthenticating by remember { mutableStateOf(false) }
    var isSimulatedSignInFlow by remember { mutableStateOf(false) }

    val cleanWelcomeDesc = remember(state.welcomeDesc) {
        val periods = listOf(".", "!", "।")
        var earliestIndex = -1
        for (p in periods) {
            val idx = state.welcomeDesc.indexOf(p)
            if (idx != -1) {
                if (earliestIndex == -1 || idx < earliestIndex) {
                    earliestIndex = idx
                }
            }
        }
        if (earliestIndex != -1) {
            state.welcomeDesc.substring(0, earliestIndex + 1).trim()
        } else {
            state.welcomeDesc
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Pure white default background requested by user
            .testTag("welcome_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Language option component at the top (matching picture context but optimized for white theme)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF3E8FF)) // Cute soft Lavender tint
                        .clickable { isLanguageDialogVisible = true }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language Selector",
                        tint = Color(0xFF7C4DFF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.appLanguage,
                        color = Color(0xFF4A00E0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select language",
                        tint = Color(0xFF7C4DFF),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (state.isTranslating) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF7C4DFF),
                            modifier = Modifier.size(10.dp),
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Translating with Aura AI...",
                            fontSize = 11.sp,
                            color = Color(0xFF7C4DFF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 2. Beautiful Aura App Logo matching user demand
                AuraBrandLogo(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 12.dp)
                        .size(80.dp),
                    tint = Color(0xFF7C4DFF)
                )
            }

            // 3. STUNNING ILLUSTRATION CANVAS WITH REALISTIC SOCIAL CONNECTION PHOTOGRAPHY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background ambient circles with dynamic lavender accent gradient
                Canvas(modifier = Modifier.size(250.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x227C4DFF), Color.Transparent)
                        ),
                        radius = size.width / 2
                    )
                }

                // 1. Main Portrait Card (representing the gorgeous braided girl on blue sky background)
                Card(
                    modifier = Modifier
                        .offset(x = 42.dp, y = (-20).dp)
                        .size(135.dp, 175.dp)
                        .shadow(10.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, Color(0xFFF3E8FF))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop", // beautiful braids happy portrait
                        contentDescription = "Aura Portrait One",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 2. Marketplace Card (representing the clothing rack and hat)
                Card(
                    modifier = Modifier
                        .offset(x = (-75).dp, y = (-25).dp)
                        .size(105.dp, 105.dp)
                        .shadow(6.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=350&auto=format&fit=crop", // high quality boutique rack
                            contentDescription = "Marketplace Store",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Top-left Marketplace overlay icon badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                                .size(24.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                .border(0.5.dp, Color.LightGray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Store",
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // 3. Music/Singing Card (representing the girl singing and guy with guitar)
                Card(
                    modifier = Modifier
                        .offset(x = (-55).dp, y = 55.dp)
                        .size(110.dp, 125.dp)
                        .shadow(8.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=350&auto=format&fit=crop", // singing friends
                                contentDescription = "Music Session",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                            )
                            // Caption bar at bottom of the card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color(0xFFFAFAFA))
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                // Dynamic styled light bars representing simple profile label
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(6.dp)
                                        .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                )
                            }
                        }
                        
                        // Top-left Blue Star icon badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                                .size(24.dp)
                                .background(Color(0xFF1E88E5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Feature",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // 4. Circular Active User Ring (watering plants portrait or simple friendly portrait)
                Box(
                    modifier = Modifier
                        .offset(x = 18.dp, y = 55.dp)
                        .size(68.dp)
                        .shadow(10.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .border(3.dp, Color(0xFF1E88E5), CircleShape)
                        .padding(3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=350&auto=format&fit=crop", // plant/friendly smiling person
                            contentDescription = "Active Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        // Active dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(14.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }

                // 5. LOVE HEART Floating bubble (Left)
                Box(
                    modifier = Modifier
                        .offset(x = (-105).dp, y = 14.dp)
                        .size(42.dp)
                        .shadow(6.dp, CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF5252), Color(0xFFFF1744))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 6. LAUGHING emoji floating bubble (Right)
                Box(
                    modifier = Modifier
                        .offset(x = (112).dp, y = (5).dp)
                        .size(42.dp)
                        .shadow(6.dp, CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "😆",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // 7. TIME CLOCK badge (capsule badge at top-right/far-right)
                Box(
                    modifier = Modifier
                        .offset(x = (110).dp, y = (-70).dp)
                        .size(width = 75.dp, height = 30.dp)
                        .shadow(6.dp, RoundedCornerShape(15.dp))
                        .background(Color(0xFF1E88E5), RoundedCornerShape(15.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "3:11",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. TITLE AND DESCRIPTION BLOCK (Clean contrast on pure white backdrop)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = state.welcomeTitle,
                    color = Color(0xFF1F1F23), // Deep high contrast text
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = cleanWelcomeDesc,
                    color = Color(0xFF4B5563), // Clean ash gray
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // 5. ACTION BUTTONS (Discord Brand Standard)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BUTTON 1: Sign in with Discord (Instant Login with 1 click)
                Button(
                    onClick = {
                        val act = activity
                        isSimulatedSignInFlow = true
                        if (viewModel.isRealAppwriteEnabled) {
                            if (act != null) {
                                isAuthenticating = true
                                viewModel.signInWithDiscord(act, "", "") { success, email, name, errMsg ->
                                    isAuthenticating = false
                                    if (success && email != null) {
                                        viewModel.handleDiscordAuthSuccess(email, name ?: "", isSignInFlow = true)
                                    } else {
                                        android.widget.Toast.makeText(context, errMsg ?: "Discord Sign-In failed.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                android.widget.Toast.makeText(context, "Activity context not found.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            showDiscordChooser = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(4.dp, RoundedCornerShape(27.dp))
                        .testTag("discord_signin_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5865F2)),
                    shape = RoundedCornerShape(27.dp),
                    enabled = !isAuthenticating
                ) {
                    if (isAuthenticating && isSimulatedSignInFlow) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                DiscordLogoIcon(modifier = Modifier.size(18.dp), isBlurpleBackground = false)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = viewModel.getTranslatedText("Sign in with Discord"),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BUTTON 2: Sign up with Discord (Onboarding with guided registration)
                Button(
                    onClick = {
                        val act = activity
                        isSimulatedSignInFlow = false
                        if (viewModel.isRealAppwriteEnabled) {
                            if (act != null) {
                                isAuthenticating = true
                                viewModel.signInWithDiscord(act, "", "") { success, email, name, errMsg ->
                                    isAuthenticating = false
                                    if (success && email != null) {
                                        viewModel.handleDiscordAuthSuccess(email, name ?: "", isSignInFlow = false)
                                    } else {
                                        android.widget.Toast.makeText(context, errMsg ?: "Discord Sign-Up failed.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                android.widget.Toast.makeText(context, "Activity context not found.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            showDiscordChooser = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .border(1.5.dp, Color(0xFF5865F2), RoundedCornerShape(27.dp))
                        .shadow(2.dp, RoundedCornerShape(27.dp))
                        .testTag("discord_signup_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(27.dp),
                    enabled = !isAuthenticating
                ) {
                    if (isAuthenticating && !isSimulatedSignInFlow) {
                        CircularProgressIndicator(
                            color = Color(0xFF5865F2),
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF5865F2), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                DiscordLogoIcon(modifier = Modifier.size(18.dp), isBlurpleBackground = true)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = viewModel.getTranslatedText("Sign up with Discord"),
                                color = Color(0xFF5865F2),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Brand Logo matching user demand (Meta removed, beautiful unified Aura logo & text added)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    AuraBrandLogo(
                        modifier = Modifier.size(22.dp),
                        tint = Color(0xFF7C4DFF)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Aura ∞ Connect",
                        color = Color(0xFF4A00E0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }

    // Interactive Full-scale search dialog for selecting from 100+ global languages
    if (isLanguageDialogVisible) {
        AlertDialog(
            onDismissRequest = { isLanguageDialogVisible = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Language, null, tint = Color(0xFF7C4DFF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select Language (100+)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C4DFF)
                    )
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQueryLanguage,
                        onValueChange = { searchQueryLanguage = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E293B)),
                        placeholder = { 
                            Text(
                                "Search 100+ languages..."
                            ) 
                        },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E293B),
                            unfocusedTextColor = Color(0xFF334155),
                            focusedBorderColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    val filteredLangs = remember(searchQueryLanguage) {
                        GlobalLanguagesList.filter {
                            it.contains(searchQueryLanguage, ignoreCase = true)
                        }
                    }

                    Box(modifier = Modifier.height(280.dp).fillMaxWidth()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(filteredLangs) { lang ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.updateLanguage(lang)
                                            isLanguageDialogVisible = false
                                            searchQueryLanguage = ""
                                        }
                                        .background(
                                            if (state.appLanguage == lang) Color(0xFFF3E8FF) else Color.Transparent
                                        )
                                        .padding(vertical = 12.dp, horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = lang,
                                        color = if (state.appLanguage == lang) Color(0xFF7C4DFF) else Color.DarkGray,
                                        fontWeight = if (state.appLanguage == lang) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isLanguageDialogVisible = false }) {
                    Text("Close", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    if (showDiscordChooser) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDiscordChooser = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DiscordLogoIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Discord",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5865F2),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Choose an account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "to continue to Aura Connect",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val accounts = discordAccounts
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        accounts.forEach { email ->
                            val parsedName = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercaseChar() }
                            val initial = email.firstOrNull()?.uppercaseChar()?.toString() ?: "D"
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showDiscordChooser = false
                                        isAuthenticating = true
                                        viewModel.signInWithDiscord(activity ?: androidx.activity.ComponentActivity(), email, parsedName) { success, mail, name, _ ->
                                            isAuthenticating = false
                                            if (success && mail != null) {
                                                viewModel.handleDiscordAuthSuccess(mail, name ?: "", isSignInFlow = isSimulatedSignInFlow)
                                            }
                                        }
                                    }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFEEF2FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5865F2),
                                        fontSize = 14.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = parsedName,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = email,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSecurityPopup) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(max = 585.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8FF)), // Elegant Lavender blush surface
                border = BorderStroke(2.dp, Brush.linearGradient(
                    colors = listOf(Color(0xFFD8B4FE), Color(0xFF7C4DFF))
                ))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern Header with Lavender Shield Icon + Glowing Base
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFE9D5FF), Color.Transparent)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFF7C4DFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security Guard",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AURA SAFEGUARD",
                        color = Color(0xFF4A00E0),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Your Safety, Privacy & Control is 100% Yours",
                        color = Color(0xFF7C4DFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable Info Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFF3E8FF).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Kindly allow the requested device permissions upon entering the Aura app. This is necessary to safeguard your local media backups and maintain extreme performance.",
                                fontSize = 13.5.sp,
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )

                            // Section 1: Security Shield
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "🛡️", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "100% Safety & Antihack",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF4A00E0)
                                    )
                                    Text(
                                        text = "Aura is fully secure, providing extra protection to every user. No third party, tracker, or hacker can ever monitor, access, or steal your activity within Aura.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Section 2: Local backup
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "💾", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Automatic Device Backups",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF4A00E0)
                                    )
                                    Text(
                                        text = "Whenever you upload photos, videos, or make profile updates, they are automatically saved directly into your system's File Manager, Photo Gallery, App Gallery, and video folders. Your files stay with you.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Section 3: Reinstall & Cleanup Warning
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "🔧", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Deliberate Security Isolation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF4A00E0)
                                    )
                                    Text(
                                        text = "If you delete Aura's activity files from your system device, or go to App Info and perform 'Force Stop', 'Clear Cache', or 'Clear Storage Data', you might need to manually edit your profile elements (like profile and cover photos) again on reinstall or app updates. This is a secure privacy design, not a bug, ensuring your keys are never stored on a middleman server.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Section 4: Solomon Promise
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "🤝", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Aura's Solomon Promise",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF4A00E0)
                                    )
                                    Text(
                                        text = "We prefer and recommend choosing the 'Allow' or 'Allow all' option for storage and media permissions. Aura will never track, steal, or misuse your personal data—this is our Solomon's oath. Your trust is our foundation.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // "I Understood" Button in Lavender Gradient style
                    Button(
                        onClick = {
                            securityPrefs.edit().putBoolean("security_understood_v3", true).apply()
                            showSecurityPopup = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .testTag("security_understood_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF7C4DFF), Color(0xFF4A00E0))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "I Understood",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 2: Setup/Onboarding Register Layout ---
@Composable
fun RegisterScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val isMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        var ttsEngine: android.speech.tts.TextToSpeech? = null
        ttsEngine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsEngine?.let { engine ->
                    engine.setLanguage(java.util.Locale.US)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val googleVoices = engine.voices
                            val femaleVoice = googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" && 
                                (it.name.lowercase(java.util.Locale.US).contains("female") || 
                                 it.name.lowercase(java.util.Locale.US).contains("network") ||
                                 it.name.lowercase(java.util.Locale.US).contains("local"))
                            } ?: googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" 
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (e: Exception) {
                            // Ignore or fallback
                        }
                    }
                    isTtsReady = true
                }
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    androidx.compose.runtime.LaunchedEffect(isTtsReady, isMuted) {
        if (isTtsReady && !isMuted) {
            tts?.speak(
                "Please enter your first name and then your surname.",
                android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null,
                "welcome_speech"
            )
        } else if (isMuted) {
            tts?.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("register_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextMuted = !isMuted
                            viewModel.setTtsMuted(nextMuted)
                            if (!nextMuted) {
                                tts?.speak(
                                    "Please enter your first name and then your surname.",
                                    android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "user_toggle_speech"
                                )
                            } else {
                                tts?.stop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Read Aloud" else "Mute Read Aloud",
                            tint = if (isMuted) Color.Gray else Color(0xFF7C4DFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "What's your name?",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Enter the name you use in real life.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(36.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First name", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF7C4DFF),
                            focusedLabelColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color.DarkGray,
                            unfocusedLabelColor = Color.DarkGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reg_first_name_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last name", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF7C4DFF),
                            focusedLabelColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color.DarkGray,
                            unfocusedLabelColor = Color.DarkGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reg_last_name_input"),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = {
                        if (firstName.isBlank() || lastName.isBlank()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please enter both First name and Last name.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regFirstName = firstName
                            viewModel.regLastName = lastName
                            viewModel.navigateTo(Screen.BirthdaySelection)
                        }
                    },
                    enabled = true,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_registration_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        disabledContainerColor = Color(0xFF7C4DFF).copy(alpha = 0.45f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }


        }
    }
}

// --- SCREEN 2.5: Birthday Selection Screen ---
@Composable
fun BirthdaySelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val isMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    // State indicators for birthday (default to 2008, so they are 18 on entry, e.g. 2008-05-30)
    var selectedYear by remember { mutableStateOf(2008) }
    var selectedMonth by remember { mutableStateOf(5) } // 1 to 12
    var selectedDay by remember { mutableStateOf(30) }

    var showCalendarDialog by remember { mutableStateOf(true) }

    val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    // Calendar calculation
    val today = java.util.Calendar.getInstance()
    var calculatedAge = today.get(java.util.Calendar.YEAR) - selectedYear
    val currentMonth = today.get(java.util.Calendar.MONTH) + 1
    val currentDay = today.get(java.util.Calendar.DAY_OF_MONTH)
    if (currentMonth < selectedMonth || (currentMonth == selectedMonth && currentDay < selectedDay)) {
        calculatedAge--
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        var ttsEngine: android.speech.tts.TextToSpeech? = null
        ttsEngine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsEngine?.let { engine ->
                    engine.setLanguage(java.util.Locale.US)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val googleVoices = engine.voices
                            val femaleVoice = googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" && 
                                (it.name.lowercase(java.util.Locale.US).contains("female") || 
                                 it.name.lowercase(java.util.Locale.US).contains("network") ||
                                 it.name.lowercase(java.util.Locale.US).contains("local"))
                            } ?: googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" 
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (e: Exception) {
                            // Ignore or fallback
                        }
                    }
                    isTtsReady = true
                }
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    androidx.compose.runtime.LaunchedEffect(isTtsReady, isMuted) {
        if (isTtsReady && !isMuted) {
            tts?.speak(
                "Please determine your age. Your age cannot be under eighteen.",
                android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null,
                "birthday_speech"
            )
        } else if (isMuted) {
            tts?.stop()
        }
    }

    // Modern custom calendar dialog
    if (showCalendarDialog) {
        AlertDialog(
            onDismissRequest = { showCalendarDialog = false },
            confirmButton = {
                Button(
                    onClick = { showCalendarDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCalendarDialog = false }
                ) {
                    Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            },
            title = {
                Text(
                    text = "Select Date of Birth",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C4DFF),
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Customize your birth details manually below:",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    // YEAR ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Year:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (selectedYear > 1920) selectedYear-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease Year", tint = Color(0xFF7C4DFF))
                            }
                            Text(
                                text = selectedYear.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black,
                                modifier = Modifier.width(60.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            IconButton(
                                onClick = { if (selectedYear < 2026) selectedYear++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase Year", tint = Color(0xFF7C4DFF))
                            }
                        }
                    }

                    // MONTH ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Month:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (selectedMonth > 1) selectedMonth-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ArrowLeft, contentDescription = "Prev Month", tint = Color(0xFF7C4DFF))
                            }
                            Text(
                                text = monthNames[selectedMonth - 1],
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.width(100.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            IconButton(
                                onClick = { if (selectedMonth < 12) selectedMonth++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ArrowRight, contentDescription = "Next Month", tint = Color(0xFF7C4DFF))
                            }
                        }
                    }

                    // DAY GRID
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Day:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val daysInMonth = when (selectedMonth) {
                            2 -> if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0)) 29 else 28
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                        if (selectedDay > daysInMonth) {
                            selectedDay = daysInMonth
                        }

                        val days = (1..daysInMonth).toList()
                        Column {
                            days.chunked(7).forEach { chunk ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    chunk.forEach { d ->
                                        val isSelected = d == selectedDay
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFF7C4DFF) else Color.Transparent)
                                                .clickable { selectedDay = d }
                                                .padding(4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = d.toString(),
                                                color = if (isSelected) Color.White else Color.Black,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Light sleek mode matching the first picture
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("birthday_selection_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextMuted = !isMuted
                            viewModel.setTtsMuted(nextMuted)
                            if (!nextMuted) {
                                tts?.speak(
                                    "Please determine your age. Your age cannot be under eighteen.",
                                    android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "birthday_toggle_speech"
                                )
                            } else {
                                tts?.stop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Read Aloud" else "Mute Read Aloud",
                            tint = if (isMuted) Color.LightGray else Color(0xFF7C4DFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "What's your birthday?",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column {
                        Text(
                            text = "Choose your date of birth. You can always make this private later.",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                        .clickable { showCalendarDialog = true }
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Birthday ($calculatedAge years old)",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${monthNames[selectedMonth - 1]} $selectedDay, $selectedYear",
                        color = Color.Black,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (calculatedAge < 18) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1AD32F2F))
                            .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Age restriction error",
                            tint = Color(0xFFEF5350)
                        )
                        Text(
                            text = "Your age must be at least 18 years old. Please select an eligible birthday.",
                            color = Color(0xFFEF5350),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = {
                        if (calculatedAge < 18) {
                            android.widget.Toast.makeText(
                                context,
                                "Your age must be at least 18 years old.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regBirthday = "${monthNames[selectedMonth - 1]} $selectedDay, $selectedYear"
                            viewModel.regBirthdayPrivacy = "Public"
                            viewModel.navigateTo(Screen.GenderSelection)
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_birthday_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF), // Bold Lavender color
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }


        }
    }
}

// --- SCREEN 2.7: Gender Selection Screen ---
@Composable
fun GenderSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val isMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    var selectedGender by remember { mutableStateOf("") } // "Female", "Male", "More options"
    var selectedPronoun by remember { mutableStateOf("") } // "She", "He", "They"
    var optionalGenderText by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        var ttsEngine: android.speech.tts.TextToSpeech? = null
        ttsEngine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsEngine?.let { engine ->
                    engine.setLanguage(java.util.Locale.US)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val googleVoices = engine.voices
                            val femaleVoice = googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" && 
                                (it.name.lowercase(java.util.Locale.US).contains("female") || 
                                 it.name.lowercase(java.util.Locale.US).contains("network") ||
                                 it.name.lowercase(java.util.Locale.US).contains("local"))
                            } ?: googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" 
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    isTtsReady = true
                }
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    androidx.compose.runtime.LaunchedEffect(isTtsReady, isMuted) {
        if (isTtsReady && !isMuted) {
            tts?.speak(
                "apni apnar sothik gender select korun, apni eta pore poriborton korte parben",
                android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null,
                "gender_speech"
            )
        } else if (isMuted) {
            tts?.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("gender_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top bar with back and volume icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextMuted = !isMuted
                            viewModel.setTtsMuted(nextMuted)
                            if (!nextMuted) {
                                tts?.speak(
                                    "apni apnar sothik gender select korun, apni eta pore poriborton korte parben",
                                    android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "gender_toggle_speech"
                                )
                            } else {
                                tts?.stop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Read Aloud" else "Mute Read Aloud",
                            tint = if (isMuted) Color.LightGray else Color(0xFF7C4DFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "What's your gender?",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "You can change who sees your gender on your profile later.",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Options Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                ) {
                    // Option 1: Female
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGender = "Female" }
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Female",
                            color = Color.Black,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        CustomRadioButton(selected = selectedGender == "Female")
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0)))

                    // Option 2: Male
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGender = "Male" }
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Male",
                            color = Color.Black,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        CustomRadioButton(selected = selectedGender == "Male")
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0)))

                    // Option 3: More options
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGender = "More options"
                                showBottomSheet = true
                            }
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                            Text(
                                text = "More options",
                                color = Color.Black,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select More options to choose another gender or if you'd rather not say.",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        CustomRadioButton(selected = selectedGender == "More options")
                    }
                }

                // If "More options" is selected, display pronoun card and gender optional text box below
                if (selectedGender == "More options") {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Pronoun card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F9F9))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .clickable { showBottomSheet = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Select your pronoun",
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (selectedPronoun.isEmpty()) "Your pronoun" else selectedPronoun,
                                    color = if (selectedPronoun.isEmpty()) Color.Gray else Color(0xFF7C4DFF),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown menu",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gender optional input text box
                    TextField(
                        value = optionalGenderText,
                        onValueChange = { optionalGenderText = it },
                        placeholder = { Text("Gender (optional)", color = Color.Gray, fontSize = 15.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .testTag("optional_gender_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF9F9F9),
                            unfocusedContainerColor = Color(0xFFF9F9F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF7C4DFF)
                        ),
                        singleLine = true
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Next Button
                Button(
                    onClick = {
                        if (selectedGender.isEmpty()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please select a gender option.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } else if (selectedGender == "More options" && selectedPronoun.isEmpty()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please select your pronoun.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Assign registered values to viewmodel
                            viewModel.regGenderSelection = selectedGender
                            viewModel.regPronoun = selectedPronoun
                            viewModel.regGenderOptional = optionalGenderText

                            viewModel.navigateTo(Screen.RelationshipSelection)
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_gender_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }


            }
        }

        // Custom Slide-Up Bottom Sheet Overlay for Pronoun selection
        if (showBottomSheet) {
            // Dark dim overlay background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showBottomSheet = false }
            )

            // Bottom table container
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF1E1E1E)) // Same style color as picture 3
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .clickable(enabled = false) {}, // Prevent clicks through to background
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Drag Pill handle on top
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray)
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showBottomSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close sheet",
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = "Select your pronoun",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your pronoun is visible to everyone.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                // Choices list cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF292929))
                        .border(1.dp, Color(0xFF383838), RoundedCornerShape(16.dp))
                ) {
                    // Pronoun 1: She
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPronoun = "She"
                                showBottomSheet = false
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "She",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\"Wish her a happy birthday!\"",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF383838)))

                    // Pronoun 2: He
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPronoun = "He"
                                showBottomSheet = false
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "He",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\"Wish him a happy birthday!\"",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF383838)))

                    // Pronoun 3: They
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPronoun = "They"
                                showBottomSheet = false
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "They",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\"Wish them a happy birthday!\"",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun CustomRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .border(
                width = 2.dp,
                color = if (selected) Color(0xFF7C4DFF) else Color(0xFF9E9E9E),
                shape = CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF7C4DFF), CircleShape)
            )
        }
    }
}

// --- SCREEN 2.8: Email Input Screen ---
@Composable
fun EmailInputScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val isMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    var emailText by remember { mutableStateOf("") }
    var isCheckingEmail by remember { mutableStateOf(false) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        var ttsEngine: android.speech.tts.TextToSpeech? = null
        ttsEngine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsEngine?.let { engine ->
                    engine.setLanguage(java.util.Locale.US)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val googleVoices = engine.voices
                            val femaleVoice = googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" && 
                                (it.name.lowercase(java.util.Locale.US).contains("female") || 
                                 it.name.lowercase(java.util.Locale.US).contains("network") ||
                                 it.name.lowercase(java.util.Locale.US).contains("local"))
                            } ?: googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" 
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    isTtsReady = true
                }
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    androidx.compose.runtime.LaunchedEffect(isTtsReady, isMuted) {
        if (isTtsReady && !isMuted) {
            tts?.speak(
                "apni apnar valid email address use korun, jate apnar sathe jogajog kora jete pare",
                android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null,
                "email_speech"
            )
        } else if (isMuted) {
            tts?.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("email_input_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top bar with back and volume icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextMuted = !isMuted
                            viewModel.setTtsMuted(nextMuted)
                            if (!nextMuted) {
                                tts?.speak(
                                    "apni apnar valid email address use korun, jate apnar sathe jogajog kora jete pare",
                                    android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "email_toggle_speech"
                                )
                            } else {
                                tts?.stop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Read Aloud" else "Mute Read Aloud",
                            tint = if (isMuted) Color.LightGray else Color(0xFF7C4DFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "What's your email?",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "Enter the email where you can be contacted. No one will see this on your profile.",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email Input Box styling
                OutlinedTextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = { Text("Email", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF7C4DFF),
                        focusedLabelColor = Color(0xFF7C4DFF),
                        unfocusedBorderColor = Color.LightGray,
                        unfocusedLabelColor = Color.Gray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_address_input"),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description text without Learn more word
                Text(
                    text = "You’ll also receive emails from us and can opt out anytime.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Next Button
                Button(
                    enabled = !isCheckingEmail,
                    onClick = {
                        val trimmed = emailText.trim()
                        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
                        val isGmail = trimmed.endsWith("@gmail.com", ignoreCase = true)
                        if (trimmed.isEmpty()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please enter your email ID.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } else if (!emailRegex.matches(trimmed) || !isGmail) {
                            android.widget.Toast.makeText(
                                context,
                                "Invalid email address",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            isCheckingEmail = true
                            viewModel.checkIfEmailExistsInAppwrite(trimmed) { exists ->
                                isCheckingEmail = false
                                if (exists) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Email already exists. Please log in or try a different email address to sign up!",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    viewModel.regEmail = trimmed
                                    android.widget.Toast.makeText(
                                        context,
                                        "Opening Google verification...",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.navigateTo(Screen.Verification)
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_email_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White
                    )
                ) {
                    if (isCheckingEmail) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Checking email...",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Next",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun AuraBrandLogo(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF7C4DFF)
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val centerX = w / 2f
        val centerY = h * 0.58f
        val archColor = tint.copy(alpha = 0.45f)
        
        // 1. Draw thin outer concentric semicircular arch
        val outerRadius = w * 0.43f
        drawArc(
            color = archColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = w * 0.025f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            ),
            topLeft = androidx.compose.ui.geometry.Offset(centerX - outerRadius, centerY - outerRadius),
            size = androidx.compose.ui.geometry.Size(outerRadius * 2, outerRadius * 2)
        )

        // 2. Draw thicker inner concentric semicircular arch resting over 'A'
        val innerRadius = w * 0.31f
        drawArc(
            color = tint,
            startAngle = 195f,
            sweepAngle = 150f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = w * 0.055f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            ),
            topLeft = androidx.compose.ui.geometry.Offset(centerX - innerRadius, centerY - innerRadius),
            size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
        )

        // 3. Draw inner lavender semi-transparent triangle backdrop
        val innerTrianglePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.46f)
            lineTo(w * 0.63f, h * 0.74f)
            lineTo(w * 0.37f, h * 0.74f)
            close()
        }
        drawPath(path = innerTrianglePath, color = tint.copy(alpha = 0.35f))

        // 4. Draw stylized Pointy 'A' frame using Path
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.22f) // Top apex point
            lineTo(w * 0.74f, h * 0.85f) // Right leg outer bottom
            lineTo(w * 0.63f, h * 0.85f) // Right leg inner bottom
            lineTo(w * 0.5f, h * 0.48f)  // Inner apex
            lineTo(w * 0.37f, h * 0.85f) // Left leg inner bottom
            lineTo(w * 0.26f, h * 0.85f) // Left leg outer bottom
            close()
        }
        drawPath(path = path, color = tint)

        // 5. Draw horizontal bar of letter A
        val barY = h * 0.66f
        val barHeight = w * 0.035f
        drawRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.35f, barY),
            size = androidx.compose.ui.geometry.Size(w * 0.30f, barHeight)
        )

        // 6. Draw two stacked white circles inside the inner backing triangle
        val circleRadius = w * 0.045f
        val circleColor = Color.White
        
        // Bottom stacked circle
        drawCircle(
            color = circleColor,
            radius = circleRadius,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.67f)
        )
        // Top stacked circle
        drawCircle(
            color = circleColor,
            radius = circleRadius,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.57f)
        )
    }
}

// --- SCREEN 2.9: Email Verification Screen ---
@Composable
fun DiscordLogoIcon(
    modifier: Modifier = Modifier,
    isBlurpleBackground: Boolean = true
) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            val faceColor = if (isBlurpleBackground) Color.White else Color(0xFF5865F2)
            val eyeColor = if (isBlurpleBackground) Color(0xFF5865F2) else Color.White
            
            // Draw Discord Controller Goggles Silhouette
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.25f, h * 0.18f)
                quadraticTo(w * 0.5f, h * 0.22f, w * 0.75f, h * 0.18f)
                quadraticTo(w * 0.85f, h * 0.18f, w * 0.90f, h * 0.35f)
                quadraticTo(w * 0.94f, h * 0.55f, w * 0.82f, h * 0.75f)
                quadraticTo(w * 0.72f, h * 0.84f, w * 0.65f, h * 0.80f)
                quadraticTo(w * 0.5f, h * 0.70f, w * 0.35f, h * 0.80f)
                quadraticTo(w * 0.28f, h * 0.84f, w * 0.18f, h * 0.75f)
                quadraticTo(w * 0.06f, h * 0.55f, w * 0.10f, h * 0.35f)
                quadraticTo(w * 0.15f, h * 0.18f, w * 0.25f, h * 0.18f)
                close()
            }
            
            drawPath(
                path = path,
                color = faceColor
            )
            
            // Eye holes
            drawCircle(
                color = eyeColor,
                radius = w * 0.075f,
                center = androidx.compose.ui.geometry.Offset(w * 0.36f, h * 0.48f)
            )
            drawCircle(
                color = eyeColor,
                radius = w * 0.075f,
                center = androidx.compose.ui.geometry.Offset(w * 0.64f, h * 0.48f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val isMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    var isDiscordVerifying by remember { mutableStateOf(false) }
    var discordErrorMsg by remember { mutableStateOf<String?>(null) }

    // TTS management
    androidx.compose.runtime.DisposableEffect(Unit) {
        var ttsEngine: android.speech.tts.TextToSpeech? = null
        ttsEngine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsEngine?.let { engine ->
                    engine.setLanguage(java.util.Locale.US)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val googleVoices = engine.voices
                            val femaleVoice = googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" && 
                                (it.name.lowercase(java.util.Locale.US).contains("female") || 
                                 it.name.lowercase(java.util.Locale.US).contains("network") ||
                                 it.name.lowercase(java.util.Locale.US).contains("local"))
                            } ?: googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" 
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    isTtsReady = true
                }
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    androidx.compose.runtime.LaunchedEffect(isTtsReady, isMuted) {
        if (isTtsReady && !isMuted) {
            tts?.speak(
                "Please verify your email address securely using Discord authentication!",
                android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null,
                "verification_speech"
            )
        } else if (isMuted) {
            tts?.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("verification_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top bar with back and volume icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextMuted = !isMuted
                            viewModel.setTtsMuted(nextMuted)
                            if (!nextMuted) {
                                tts?.speak(
                                    "Please verify your email address securely using Discord authentication!",
                                    android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "verification_toggle_speech"
                                )
                            } else {
                                tts?.stop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Read Aloud" else "Mute Read Aloud",
                            tint = if (isMuted) Color.LightGray else Color(0xFF7C4DFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Email Verification",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Guideline container
                Surface(
                    color = Color(0xFFEEF2FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Discord Verification Guidelines:",
                            color = Color(0xFF4F46E5),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1. To ensure account security, manual 6-digit OTP code input has been disabled by management.\n\n" +
                                   "2. You are required to verify ownership of your desired email address securely using Discord authentication.\n\n" +
                                   "3. Click \"Verify with Discord\", then authorize high-level handshake containing your entered email address to complete the process.",
                            color = Color(0xFF3730A3),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your Target Verification Email:",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Render entered register email address
                Text(
                    text = viewModel.regEmail,
                    color = Color(0xFF5865F2), // Discord Blurple
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                discordErrorMsg?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Verify with Discord Button - Discord Brand Color
                Button(
                    onClick = {
                        isDiscordVerifying = true
                        discordErrorMsg = null
                        val activity = context as? androidx.activity.ComponentActivity
                        if (activity != null) {
                            viewModel.verifyWithAppwriteDiscord(activity, viewModel.regEmail) { success, errMsg ->
                                isDiscordVerifying = false
                                if (success) {
                                    android.widget.Toast.makeText(context, "Discord Email verified successfully!", android.widget.Toast.LENGTH_LONG).show()
                                    viewModel.navigateTo(Screen.PasswordSelection)
                                } else {
                                    discordErrorMsg = errMsg ?: "Discord verification failed."
                                    android.widget.Toast.makeText(context, discordErrorMsg, android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            isDiscordVerifying = false
                            discordErrorMsg = "Unable to start Discord Sign-In. ComponentActivity not found."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("verify_with_google_reg_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5865F2)
                    ),
                    shape = RoundedCornerShape(27.dp),
                    enabled = !isDiscordVerifying
                ) {
                    if (isDiscordVerifying) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                DiscordLogoIcon(modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Verify with Discord",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Change email / Find account button
                    TextButton(
                        onClick = {
                            viewModel.navigateTo(Screen.Login)
                        },
                        modifier = Modifier.testTag("find_my_account_btn")
                    ) {
                        Text(
                            text = "Back to Registration / Change email",
                            color = Color(0xFF7C4DFF),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// --- SCREEN: Select Strong Password Screen ---
@Composable
fun PasswordSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val isMuted by viewModel.isTtsMuted.collectAsStateWithLifecycle()
    var tts by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showErrorOverlay by remember { mutableStateOf(false) }

    // TTS management
    androidx.compose.runtime.DisposableEffect(Unit) {
        var ttsEngine: android.speech.tts.TextToSpeech? = null
        ttsEngine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsEngine?.let { engine ->
                    engine.setLanguage(java.util.Locale.US)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val googleVoices = engine.voices
                            val femaleVoice = googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" && 
                                (it.name.lowercase(java.util.Locale.US).contains("female") || 
                                 it.name.lowercase(java.util.Locale.US).contains("network") ||
                                 it.name.lowercase(java.util.Locale.US).contains("local"))
                            } ?: googleVoices?.firstOrNull { 
                                it.locale.language == "en" && 
                                it.locale.country == "US" 
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                    isTtsReady = true
                }
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    val ttsText = "Please select a strong password for your Aura account, which you can use to log in next time. You can change this later."
    androidx.compose.runtime.LaunchedEffect(isTtsReady, isMuted) {
        if (isTtsReady && !isMuted) {
            tts?.speak(
                ttsText,
                android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                null,
                "password_selection_speech"
            )
        } else if (isMuted) {
            tts?.stop()
        }
    }

    // Dynamic requirement assertions
    val lengthMet = passwordText.length >= 6
    val digitMet = passwordText.any { it.isDigit() }
    val lowercaseMet = passwordText.any { it.isLowerCase() }
    val uppercaseMet = passwordText.any { it.isUpperCase() }
    val symbolMet = passwordText.any { !it.isLetterOrDigit() }
    val allMet = lengthMet && digitMet && lowercaseMet && uppercaseMet && symbolMet

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("password_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top elements with volume mute toggle and back icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextMuted = !isMuted
                            viewModel.setTtsMuted(nextMuted)
                            if (!nextMuted) {
                                tts?.speak(
                                    ttsText,
                                    android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "password_selection_toggle_speech"
                                )
                            } else {
                                tts?.stop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Read Aloud" else "Mute Read Aloud",
                            tint = if (isMuted) Color.LightGray else Color(0xFF7C4DFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Heading/Title
                Text(
                    text = "Choose Password",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "Please input a strong password, you can change this later."
                Text(
                    text = "Please input a strong password, you can change this later.",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Outlined Input Box styled lavender
                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { 
                        passwordText = it
                        showErrorOverlay = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aura_password_select_input"),
                    label = { Text("Choose a password") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF7C4DFF)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF7C4DFF),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFF7C4DFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful requirements checklist card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB)),
                    border = BorderStroke(1.dp, Color(0xFFECECEF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Password Requirements",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        RequirementItem(text = "Minimum 6 characters", checked = lengthMet)
                        RequirementItem(text = "At least one uppercase letter (A-Z)", checked = uppercaseMet)
                        RequirementItem(text = "At least one lowercase letter (a-z)", checked = lowercaseMet)
                        RequirementItem(text = "At least one numeric digit (0-9)", checked = digitMet)
                        RequirementItem(text = "At least one special character / symbol", checked = symbolMet)
                    }
                }
            }

            // Bottom controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Next Button
                Button(
                    enabled = !isSubmitting,
                    onClick = {
                        if (!allMet) {
                            showErrorOverlay = true
                            android.widget.Toast.makeText(
                                context,
                                "Password does not meet the specified strength guidelines!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regPasswordText = passwordText
                            viewModel.navigateTo(Screen.RelationshipSelection)
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("aura_password_select_next_button")
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }


            }
        }
    }
}

// --- SCREEN: Select Relationship Status ---
@Composable
fun RelationshipSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var isRelationDropdownOpen by remember { mutableStateOf(false) }
    var isPrivacyDropdownOpen by remember { mutableStateOf(false) }
    
    var localStatus by remember { mutableStateOf(viewModel.regRelationStatus) }
    var localPrivacy by remember { mutableStateOf(viewModel.regRelationPrivacy) }

    val statusOptions = listOf(
        "Single", "Engaged", "Married", "It's complicated", 
        "In a relationship", "Widowed", "Separated", "Divorced"
    )
    val privacyOptions = listOf("Public", "Friends", "Only me")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("relationship_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top header with back button and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    // Privacy Settings trigger button
                    Box {
                        TextButton(
                            onClick = { isPrivacyDropdownOpen = !isPrivacyDropdownOpen },
                            modifier = Modifier.testTag("relation_privacy_dropdown_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF7C4DFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Privacy: $localPrivacy",
                                color = Color(0xFF7C4DFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Beautiful custom table-style dropdown card for Privacy Setup
                        if (isPrivacyDropdownOpen) {
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .padding(top = 40.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECECEF))
                            ) {
                                Column {
                                    privacyOptions.forEach { option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    localPrivacy = option
                                                    viewModel.regRelationPrivacy = option
                                                    isPrivacyDropdownOpen = false
                                                }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val icon = when (option) {
                                                "Public" -> Icons.Default.Public
                                                "Friends" -> Icons.Default.People
                                                else -> Icons.Default.Lock
                                            }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (localPrivacy == option) Color(0xFF7C4DFF) else Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = option,
                                                color = if (localPrivacy == option) Color(0xFF7C4DFF) else Color.Black,
                                                fontWeight = if (localPrivacy == option) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Banner
                Text(
                    text = "Relationship Status",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please choose your relationship status, you can skip this step and choose this later or change it later.",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Status Box Selector
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = localStatus,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isRelationDropdownOpen = !isRelationDropdownOpen }
                            .testTag("relation_status_box"),
                        enabled = false, // Disabled input field, pure Click interaction
                        label = { Text("Your Status", color = Color(0xFF7C4DFF)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Color(0xFF7C4DFF),
                            disabledLabelColor = Color(0xFF7C4DFF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = if (isRelationDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF7C4DFF)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFFF4081)
                            )
                        }
                    )

                    // Table Layout Dropdown menu option grid
                    if (isRelationDropdownOpen) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp)
                                .shadow(12.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                statusOptions.forEach { status ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                localStatus = status
                                                viewModel.regRelationStatus = status
                                                isRelationDropdownOpen = false
                                            }
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = status,
                                            fontSize = 16.sp,
                                            fontWeight = if (localStatus == status) FontWeight.Bold else FontWeight.Normal,
                                            color = if (localStatus == status) Color(0xFF7C4DFF) else Color.Black
                                        )

                                        if (localStatus == status) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF7C4DFF),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom actions: Skip and Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Skip Button
                OutlinedButton(
                    onClick = {
                        viewModel.regRelationStatus = "" // Skip
                        viewModel.navigateTo(Screen.EducationSelection)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("relationship_skip_button"),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Next Button
                Button(
                    onClick = {
                        if (localStatus.isBlank()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please select a relationship status to continue, or click Skip!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regRelationStatus = localStatus
                            viewModel.navigateTo(Screen.EducationSelection)
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("relationship_next_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// --- SCREEN: Education Selection Screen with full list A to Z filtering ---
@Composable
fun EducationSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var isPrivacyDropdownOpen by remember { mutableStateOf(false) }
    var localPrivacy by remember { mutableStateOf(viewModel.regEducationPrivacy) }

    var schoolInput by remember { mutableStateOf(viewModel.regSchool) }
    var collegeInput by remember { mutableStateOf(viewModel.regCollege) }
    var universityInput by remember { mutableStateOf(viewModel.regUniversity) }

    // Dropdown open states
    var isSchoolDropdownOpen by remember { mutableStateOf(false) }
    var isCollegeDropdownOpen by remember { mutableStateOf(false) }
    var isUniversityDropdownOpen by remember { mutableStateOf(false) }

    // Comprehensive global dictionary lists
    val schools = remember {
        listOf(
            // Bangladesh
            "Dhaka Residential Model College School", "Greenherald International School", "Scholastica International",
            "Sunnydale School", "Willes Little Flower School", "Milestone School & College", "Viqarunnisa Noon School",
            "St. Joseph Higher Secondary School", "Govt. Laboratory High School", "Motijheel Ideal School",
            "St. Gregory's High School", "Cantonment Public School", "Holy Cross Girls' High School",
            "Dhaka Collegiate School", "Rifles Public School", "Monipur High School", "St. Philip's High School",
            // India
            "Delhi Public School (DPS)", "DAV Public School", "La Martiniere College", "The Doon School",
            "St. Xavier's Collegiate School", "Bishop Cotton School", "Mayo College",
            // United States (US)
            "Aura Academy", "Austin Public School", "Brooklyn Secondary", "Jefferson Academy", "Saratoga High School",
            "Harvard High School", "Thomas Jefferson High School", "Stuyvesant High School", "Bronx High School of Science",
            "Phillips Academy Andover", "Exeter Academy", "Boston Latin School", "Lakeside School",
            // United Kingdom (UK)
            "Beacon High School", "London Grammar School", "Oxford International School", "Eton College (School)",
            "Harrow School", "Westminster School", "St Paul's School", "Winchester College",
            // Australia
            "Sydney Grammar School", "Melbourne High School", "James Ruse Agricultural High School", "North Sydney Boys High",
            // Canada
            "Toronto French School", "Upper Canada College", "Vancouver College", "Marianopolis School"
        )
    }

    val colleges = remember {
        listOf(
            // Bangladesh
            "Notre Dame College", "Dhaka College", "Holy Cross College", "Viqarunnisa Noon College",
            "Rajuk Uttara Model College", "Dhaka City College", "City College Dhaka", "Adamjee Cantonment College",
            "Birshreshtha Munshi Abdur Rouf Public College", "St. Joseph Higher Secondary College",
            "Tejgaon College", "Gurudayal Government College", "Chittagong College", "Rajshahi College",
            "Comilla Victoria Government College", "Sylhet MC College",
            // India & UK
            "Eton College Prep", "Imperial College London", "King's College London", "Presidency College",
            "St. Xavier's College", "Madras Christian College", "St. Stephen's College", "Loyola College",
            "Elphinstone College", "Fergusson College",
            // USA & Canada
            "Aura Higher Secondary College", "Boston Saint College", "Dartmouth Pre-College", "Sydney Trinity College",
            "Trident College", "Amherst College", "Williams College", "Wellesley College", "Swarthmore College",
            "Bowdoin College", "Middlebury College", "Pomona College", "Carleton College", "Vassar College"
        )
    }

    val universities = remember {
        listOf(
            // Bangladesh
            "University of Dhaka (DU)", "BUET (Bangladesh University of Eng & Tech)", "Jahangirnagar University (JU)",
            "University of Chittagong (CU)", "Rajshahi University (RU)", "BRAC University", "North South University (NSU)",
            "Independent University, Bangladesh (IUB)", "American International University-Bangladesh (AIUB)",
            "United International University (UIU)", "East West University", "Ahsanullah University of Science & Tech (AUST)",
            "Military Institute of Science and Technology (MIST)", "Khulna University of Engineering & Technology (KUET)",
            "Chittagong University of Engineering & Technology (CUET)", "Rajshahi University of Engineering & Technology (RUET)",
            "Shahjalal University of Science and Technology (SUST)",
            // United States (US)
            "Harvard University", "Stanford University", "Massachusetts Institute of Technology (MIT)", "Yale University",
            "Princeton University", "Columbia University", "Cornell University", "California Institute of Technology (Caltech)",
            "University of California, Berkeley (UC Berkeley)", "University of California, Los Angeles (UCLA)",
            "University of Chicago", "University of Pennsylvania (UPenn)", "Johns Hopkins University", "Northwestern University",
            "New York University (NYU)", "Aura Global University",
            // United Kingdom (UK)
            "University of Oxford", "University of Cambridge", "Imperial College London", "University College London (UCL)",
            "London School of Economics (LSE)", "University of Edinburgh", "University of Manchester", "King's College London (KCL)",
            // Canada
            "University of Toronto", "University of British Columbia (UBC)", "McGill University", "University of Waterloo",
            "University of Alberta", "McMaster University",
            // Australia & New Zealand
            "University of Melbourne", "Australian National University (ANU)", "University of Sydney", "University of Queensland",
            "University of New South Wales (UNSW)", "University of Auckland",
            // Europe
            "ETH Zurich (Switzerland)", "Sorbonne University (France)", "Technical University of Munich (Germany)",
            "Heidelberg University (Germany)", "Delft University of Technology (Netherlands)", "KU Leuven (Belgium)",
            // Asia
            "National University of Singapore (NUS)", "Nanyang Technological University (NTU)", "University of Tokyo (Japan)",
            "Kyoto University (Japan)", "Tsinghua University (China)", "Peking University (China)",
            "Seoul National University (Korea)", "Indian Institute of Technology (IIT Bombay/Delhi/Madras)",
            "Indian Institute of Science (IISc Bangalore)", "University of Hong Kong (HKU)"
        )
    }

    // Filters for lists
    val filteredSchools = remember(schoolInput) {
        if (schoolInput.isBlank()) schools else schools.filter { it.contains(schoolInput, ignoreCase = true) }
    }
    val filteredColleges = remember(collegeInput) {
        if (collegeInput.isBlank()) colleges else colleges.filter { it.contains(collegeInput, ignoreCase = true) }
    }
    val filteredUniversities = remember(universityInput) {
        if (universityInput.isBlank()) universities else universities.filter { it.contains(universityInput, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("education_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.goBack() }) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                        }

                        // Privacy selector
                        Box {
                            TextButton(onClick = { isPrivacyDropdownOpen = !isPrivacyDropdownOpen }) {
                                Icon(Icons.Default.Lock, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Privacy: $localPrivacy", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)
                            }

                            if (isPrivacyDropdownOpen) {
                                Card(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .padding(top = 40.dp)
                                        .shadow(8.dp, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFECECEF))
                                ) {
                                    Column {
                                        listOf("Public", "Friends", "Only me").forEach { opt ->
                                            Text(
                                                text = opt,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        localPrivacy = opt
                                                        viewModel.regEducationPrivacy = opt
                                                        isPrivacyDropdownOpen = false
                                                    }
                                                    .padding(12.dp),
                                                color = if (localPrivacy == opt) Color(0xFF7C4DFF) else Color.Black,
                                                fontWeight = if (localPrivacy == opt) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Educational Info",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please select your educational info, you can skip this or change it later.",
                        color = Color.DarkGray,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // SCHOOL Input Card
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            OutlinedTextField(
                                value = schoolInput,
                                onValueChange = {
                                    schoolInput = it
                                    isSchoolDropdownOpen = true
                                },
                                label = { Text("School Name") },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("education_school_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF7C4DFF),
                                    focusedLabelColor = Color(0xFF7C4DFF),
                                    unfocusedBorderColor = Color.LightGray,
                                    unfocusedLabelColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.LightGray,
                                    focusedPlaceholderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(onClick = { isSchoolDropdownOpen = !isSchoolDropdownOpen }) {
                                        Icon(Icons.Default.School, null, tint = Color(0xFF7C4DFF))
                                    }
                                }
                            )

                            if (isSchoolDropdownOpen && filteredSchools.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .heightIn(max = 200.dp)
                                        .shadow(4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(filteredSchools) { item ->
                                            Text(
                                                text = item,
                                                color = Color.Black,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        schoolInput = item
                                                        isSchoolDropdownOpen = false
                                                    }
                                                    .padding(12.dp),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // COLLEGE Input Card
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            OutlinedTextField(
                                value = collegeInput,
                                onValueChange = {
                                    collegeInput = it
                                    isCollegeDropdownOpen = true
                                },
                                label = { Text("College Name") },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("education_college_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF7C4DFF),
                                    focusedLabelColor = Color(0xFF7C4DFF),
                                    unfocusedBorderColor = Color.LightGray,
                                    unfocusedLabelColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.LightGray,
                                    focusedPlaceholderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(onClick = { isCollegeDropdownOpen = !isCollegeDropdownOpen }) {
                                        Icon(Icons.Default.AccountBalance, null, tint = Color(0xFF7C4DFF))
                                    }
                                }
                            )

                            if (isCollegeDropdownOpen && filteredColleges.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .heightIn(max = 200.dp)
                                        .shadow(4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(filteredColleges) { item ->
                                            Text(
                                                text = item,
                                                color = Color.Black,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        collegeInput = item
                                                        isCollegeDropdownOpen = false
                                                    }
                                                    .padding(12.dp),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // UNIVERSITY Input Card
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            OutlinedTextField(
                                value = universityInput,
                                onValueChange = {
                                    universityInput = it
                                    isUniversityDropdownOpen = true
                                },
                                label = { Text("University Name") },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("education_uni_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF7C4DFF),
                                    focusedLabelColor = Color(0xFF7C4DFF),
                                    unfocusedBorderColor = Color.LightGray,
                                    unfocusedLabelColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.LightGray,
                                    focusedPlaceholderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(onClick = { isUniversityDropdownOpen = !isUniversityDropdownOpen }) {
                                        Icon(Icons.Default.Apartment, null, tint = Color(0xFF7C4DFF))
                                    }
                                }
                            )

                            if (isUniversityDropdownOpen && filteredUniversities.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .heightIn(max = 200.dp)
                                        .shadow(4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(filteredUniversities) { item ->
                                            Text(
                                                text = item,
                                                color = Color.Black,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        universityInput = item
                                                        isUniversityDropdownOpen = false
                                                    }
                                                    .padding(12.dp),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.regSchool = ""
                        viewModel.regCollege = ""
                        viewModel.regUniversity = ""
                        viewModel.navigateTo(Screen.HobbySelection)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("edu_skip"),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (schoolInput.isBlank() || collegeInput.isBlank() || universityInput.isBlank()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please input all school, college and university details, or click Skip!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regSchool = schoolInput
                            viewModel.regCollege = collegeInput
                            viewModel.regUniversity = universityInput
                            viewModel.navigateTo(Screen.HobbySelection)
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("edu_next"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// Model for interactive hobby
data class HobbyTag(val name: String, val icon: ImageVector)

// --- SCREEN: Interactive Hobby Selector ---
@Composable
fun HobbySelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var isPrivacyDropdownOpen by remember { mutableStateOf(false) }
    var localPrivacy by remember { mutableStateOf(viewModel.regHobbyPrivacy) }

    val masterHobbies = remember {
        listOf(
            HobbyTag("Gaming", Icons.Default.SportsEsports),
            HobbyTag("Music", Icons.Default.MusicNote),
            HobbyTag("Photography", Icons.Default.CameraAlt),
            HobbyTag("Traveling", Icons.Default.Flight),
            HobbyTag("Reading", Icons.Default.Book),
            HobbyTag("Cooking", Icons.Default.Restaurant),
            HobbyTag("Sports", Icons.Default.SportsSoccer),
            HobbyTag("Art", Icons.Default.Palette),
            HobbyTag("Coding", Icons.Default.Code),
            HobbyTag("Writing", Icons.Default.Edit),
            HobbyTag("Hiking", Icons.Default.Terrain),
            HobbyTag("Movies", Icons.Default.Movie)
        )
    }

    val selectedHobbies = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("hobby_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                    }

                    Box {
                        TextButton(onClick = { isPrivacyDropdownOpen = !isPrivacyDropdownOpen }) {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Privacy: $localPrivacy", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)
                        }

                        if (isPrivacyDropdownOpen) {
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .padding(top = 40.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECECEF))
                            ) {
                                Column {
                                    listOf("Public", "Friends", "Only me").forEach { opt ->
                                        Text(
                                            text = opt,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    localPrivacy = opt
                                                    viewModel.regHobbyPrivacy = opt
                                                    isPrivacyDropdownOpen = false
                                                }
                                                .padding(12.dp),
                                            color = if (localPrivacy == opt) Color(0xFF7C4DFF) else Color.Black,
                                            fontWeight = if (localPrivacy == opt) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Hobbies",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pick active hobbies to highlight your interest in your profile so other custom users can match with you. You can choose multiple.",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Grid layout flow for modern selection chips
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val rows = masterHobbies.windowed(size = 3, step = 3, partialWindows = true)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { hobby ->
                                val isSelected = selectedHobbies.contains(hobby.name)
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clickable {
                                            if (isSelected) {
                                                selectedHobbies.remove(hobby.name)
                                            } else {
                                                selectedHobbies.add(hobby.name)
                                            }
                                        }
                                        .testTag("hobby_chip_${hobby.name}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF7C4DFF) else Color(0xFFF4F0FF)
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFFD1C4E9)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = hobby.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color(0xFF7C4DFF),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = hobby.name,
                                            color = if (isSelected) Color.White else Color.Black,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Skip and Next actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.regHobbies = ""
                        viewModel.navigateTo(Screen.BioSelection)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("hobby_skip"),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (selectedHobbies.isEmpty()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please choose at least one hobby to proceed, or click Skip!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regHobbies = selectedHobbies.joinToString(",")
                            viewModel.navigateTo(Screen.BioSelection)
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("hobby_next"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// --- SCREEN: Custom Bio Selection Screen ---
@Composable
fun BioSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var inputBio by remember { mutableStateOf(viewModel.regBio) }

    // Character counter validation parameters
    val charCount = inputBio.length
    val wordCount = if (inputBio.isBlank()) 0 else inputBio.trim().split("\\s+".toRegex()).size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("bio_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Add Short Bio",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tell something interesting about yourself! Note: Bio must be at least 4 characters long and up to a maximum 200 words limit.",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Custom box for bio input
                OutlinedTextField(
                    value = inputBio,
                    onValueChange = {
                        inputBio = it
                    },
                    label = { Text("What's on your mind?") },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag("bio_input_field"),
                    placeholder = { Text("A short profile bio about your self...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF7C4DFF),
                        focusedLabelColor = Color(0xFF7C4DFF),
                        unfocusedBorderColor = Color.LightGray,
                        unfocusedLabelColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Characters: $charCount",
                        fontSize = 12.sp,
                        color = if (charCount >= 4) Color(0xFF34A853) else Color.Red
                    )
                    Text(
                        text = "Words: $wordCount/200",
                        fontSize = 12.sp,
                        color = if (wordCount in 1..200) Color(0xFF34A853) else Color.Gray
                    )
                }
            }

            // Bottom control actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.regBio = ""
                        viewModel.navigateTo(Screen.ProfilePictureSelection)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("bio_skip"),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (inputBio.isBlank()) {
                            android.widget.Toast.makeText(
                                context,
                                "We suggest entering a bio first, or tap Skip!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else if (charCount < 4) {
                            android.widget.Toast.makeText(
                                context,
                                "Bio must be at least 4 characters long!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else if (wordCount > 200) {
                            android.widget.Toast.makeText(
                                context,
                                "Bio must not exceed 200 words!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.regBio = inputBio
                            viewModel.navigateTo(Screen.ProfilePictureSelection)
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("bio_next"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// --- SCREEN: Interactive Profile Picture Selector ---
@Composable
fun ProfilePictureSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedImgUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isCropToolsVisible by remember { mutableStateOf(false) }

    // Digital graphics modification states
    var rotationAngle by remember { mutableStateOf(0f) }
    var zoomScale by remember { mutableStateOf(1f) }
    var brightnessFactor by remember { mutableStateOf(0f) } // Translation preview offset
    var selectedFilterStyle by remember { mutableStateOf("Original") }

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val persistentPath = viewModel.copyImageToPublicAndPrivateStorage(uri.toString(), isCover = false)
            selectedImgUri = android.net.Uri.parse(persistentPath)
            viewModel.regProfilePic = persistentPath
            isCropToolsVisible = false // Auto-close tools as requested
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("profile_picture_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { viewModel.goBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                }
            }

            Text(
                text = "Profile Avatar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "A beautiful photo helps people recognize you in social channels. You can adjust, rotate and filter your photo natively.",
                color = Color.DarkGray,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Default Avatar Silhouette frame (like Facebook grey icon) or selected customized picture
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFFD1C4E9), CircleShape)
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImgUri == null) {
                    // Facebook aesthetic grey avatar placeholder
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default silhouette Avatar",
                        tint = Color(0xFFB0BEC5),
                        modifier = Modifier.fillMaxSize(0.65f)
                    )
                } else {
                    // Apply digital graphics modifications natively in Compose!
                    val filterColorFilter = when (selectedFilterStyle) {
                        "Cyberpunk" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x30E040FB), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "Vintage" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x35E65100), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "Classic Monochrome" -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                            androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            ))
                        )
                        "Warm Glow" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x25FFD180), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "Aura Cosmic" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x307C4DFF), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        else -> null
                    }

                    AsyncImage(
                        model = selectedImgUri,
                        contentDescription = "Avatar Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                rotationZ = rotationAngle,
                                scaleX = zoomScale,
                                scaleY = zoomScale,
                                translationX = brightnessFactor * 10f
                            ),
                        contentScale = ContentScale.Crop,
                        colorFilter = filterColorFilter
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Custom control buttons inside setup screen
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    border = BorderStroke(1.dp, Color(0xFF7C4DFF)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7C4DFF))
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (selectedImgUri == null) "Select Photo" else "Change Photo")
                }

                if (selectedImgUri != null) {
                    Button(
                        onClick = { isCropToolsVisible = !isCropToolsVisible },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Crop, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit & Crop")
                    }
                }
            }

            // Digital Mini-Photo Editor Overlay Console
            if (isCropToolsVisible && selectedImgUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB)),
                    border = BorderStroke(1.dp, Color(0xFFECECEF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Aura Digital Photo Editor",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Rotator & Scaling controllers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rotation", fontSize = 13.sp, color = Color.DarkGray)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { rotationAngle = (rotationAngle - 90f) % 360f },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.RotateLeft, null, tint = Color(0xFF7C4DFF))
                                }
                                IconButton(
                                    onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.RotateRight, null, tint = Color(0xFF7C4DFF))
                                }
                             }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Zoom Preview ", fontSize = 13.sp, color = Color.DarkGray)
                            Slider(
                                value = zoomScale,
                                onValueChange = { zoomScale = it },
                                valueRange = 0.5f..2.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF7C4DFF),
                                    activeTrackColor = Color(0xFF7C4DFF)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom filter styles table row
                        Text("Aura Live Filter Preset:", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Original" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Original") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Original") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Original") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Original", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Cyberpunk" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Cyberpunk") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Cyberpunk") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Cyberpunk") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Cyberpunk", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Vintage" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Vintage") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Vintage") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Vintage") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Vintage", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Classic Monochrome" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Classic Monochrome") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Classic Monochrome") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Classic Monochrome") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Classic Mono", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Aura Cosmic" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Aura Cosmic") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Aura Cosmic") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Aura Cosmic") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Cosmic", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next / Skip controllers for Profile Picture selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.regProfilePic = ""
                        viewModel.navigateTo(Screen.CoverPhotoSelection)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("avatar_skip"),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (selectedImgUri != null && (rotationAngle != 0f || zoomScale != 1f || brightnessFactor != 0f || selectedFilterStyle != "Original")) {
                            val processedPath = viewModel.applyImageEditsAndSave(
                                selectedImgUri.toString(),
                                isCover = false,
                                rotationAngle,
                                zoomScale,
                                brightnessFactor,
                                selectedFilterStyle
                            )
                            if (processedPath.isNotBlank()) {
                                viewModel.regProfilePic = processedPath
                            }
                        }
                        viewModel.navigateTo(Screen.CoverPhotoSelection)
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("avatar_next"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

enum class LoginFlowState {
    SIGN_IN,
    RECOVERY_EMAIL,
    RECOVERY_OTP_VERIFICATION,
    RECOVERY_NEW_PASSWORD
}

@Composable
fun LoginScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity
    
    val discordAccounts by viewModel.recognizedDiscordAccounts.collectAsStateWithLifecycle()
    var showDiscordChooser by remember { mutableStateOf(false) }
    var customEmailInput by remember { mutableStateOf("") }
    var customNameInput by remember { mutableStateOf("") }
    var isAuthenticating by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("login_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // BACK BUTTON ROW & SCREEN IDENTITY
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = "Back", 
                            tint = LavenderPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Aura Login Verification",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Discord themed center logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    DiscordLogoIcon(modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Verify Your Identity",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // GUIDELINES INSCRIPTION IN ENGLISH (AS REQUESTED BY USER!)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Discord Verification Guidelines",
                            color = Color(0xFF4A00E0),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Please authorize and verify your required email address securely using Discord. Clicking the Discord button below will initiate the automated secure authentication handshake using your Discord credential records.",
                            color = Color(0xFF334155),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // BOTTOM ACTION UNIT - Discord Brand Color
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val act = activity
                        if (viewModel.isRealAppwriteEnabled) {
                            if (act != null) {
                                isAuthenticating = true
                                viewModel.signInWithDiscord(act, "", "") { success, email, name, errMsg ->
                                    isAuthenticating = false
                                    if (success && email != null) {
                                        viewModel.handleDiscordAuthSuccess(email, name ?: "", isSignInFlow = true)
                                    } else {
                                        android.widget.Toast.makeText(context, errMsg ?: "Discord Verification failed.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                android.widget.Toast.makeText(context, "Activity context not found.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            showDiscordChooser = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(4.dp, RoundedCornerShape(27.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5865F2)),
                    shape = RoundedCornerShape(27.dp),
                    enabled = !isAuthenticating
                ) {
                    if (isAuthenticating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                DiscordLogoIcon(modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Authorize with Discord",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDiscordChooser) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDiscordChooser = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DiscordLogoIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Discord",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5865F2),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Choose an account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "to continue to Aura Connect",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val accounts = discordAccounts
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        accounts.forEach { email ->
                            val parsedName = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercaseChar() }
                            val initial = email.firstOrNull()?.uppercaseChar()?.toString() ?: "D"
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showDiscordChooser = false
                                        isAuthenticating = true
                                        viewModel.signInWithDiscord(activity ?: androidx.activity.ComponentActivity(), email, parsedName) { success, mail, name, _ ->
                                            isAuthenticating = false
                                            if (success && mail != null) {
                                                viewModel.handleDiscordAuthSuccess(mail, name ?: "", isSignInFlow = true)
                                            }
                                        }
                                    }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFEEF2FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5865F2),
                                        fontSize = 14.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = parsedName,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = email,
                                        color = Color.Gray,
                                        fontSize = 12.sp
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

// --- SCREEN: Cover Photo Setup (Onboarding Step) ---
@Composable
fun CoverPhotoSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedImgUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isCropToolsVisible by remember { mutableStateOf(false) }

    // Digital Editing Tools states
    var rotationAngle by remember { mutableStateOf(0f) }
    var zoomScale by remember { mutableStateOf(1f) }
    var brightnessFactor by remember { mutableStateOf(0f) }
    var selectedFilterStyle by remember { mutableStateOf("Original") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            val persistentPath = viewModel.copyImageToPublicAndPrivateStorage(uri.toString(), isCover = true)
            selectedImgUri = android.net.Uri.parse(persistentPath)
            viewModel.regCoverPic = persistentPath
            isCropToolsVisible = false // Auto opening disabled, user must click edit manually!
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("cover_photo_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { viewModel.goBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                }
            }

            Text(
                text = "Cover Backdrop",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Set a signature profile background to customize your global social presence. Adjust, scale and filter your backdrop natively.",
                color = Color.DarkGray,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Broad 16:9 aspect ratio rectangular design frame for Cover photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(4.dp, Color(0xFFD1C4E9), RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImgUri == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add cover backdrop silhouette",
                            tint = Color(0xFFB0BEC5),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Landscape Preferred", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                    }
                } else {
                    val filterColorFilter = when (selectedFilterStyle) {
                        "Cyberpunk" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x30E040FB), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "Vintage" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x35E65100), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "Classic Monochrome" -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                            androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            ))
                        )
                        "Warm Glow" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x25FFD180), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "Aura Cosmic" -> androidx.compose.ui.graphics.ColorFilter.tint(Color(0x307C4DFF), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        else -> null
                    }

                    AsyncImage(
                        model = selectedImgUri,
                        contentDescription = "Cover Backdrop Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                rotationZ = rotationAngle,
                                scaleX = zoomScale,
                                scaleY = zoomScale,
                                translationX = brightnessFactor * 10f
                            ),
                        contentScale = ContentScale.Crop,
                        colorFilter = filterColorFilter
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    border = BorderStroke(1.dp, Color(0xFF7C4DFF)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7C4DFF))
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (selectedImgUri == null) "Select Cover" else "Change Cover")
                }

                if (selectedImgUri != null) {
                    Button(
                        onClick = { isCropToolsVisible = !isCropToolsVisible },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Crop, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit & Adjust")
                    }
                }
            }

            if (isCropToolsVisible && selectedImgUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB)),
                    border = BorderStroke(1.dp, Color(0xFFECECEF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Cover Backing Editor",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rotation", fontSize = 13.sp, color = Color.DarkGray)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { rotationAngle = (rotationAngle - 90f) % 360f },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.RotateLeft, "Rotate Left", tint = Color(0xFF7C4DFF))
                                }
                                IconButton(
                                    onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.RotateRight, "Rotate Right", tint = Color(0xFF7C4DFF))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Zoom / Scale", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.width(90.dp))
                            Slider(
                                value = zoomScale,
                                onValueChange = { zoomScale = it },
                                valueRange = 0.5f..2.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF7C4DFF),
                                    activeTrackColor = Color(0xFF7C4DFF)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = "${String.format(java.util.Locale.US, "%.1f", zoomScale)}x", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Position Bias", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.width(90.dp))
                            Slider(
                                value = brightnessFactor,
                                onValueChange = { brightnessFactor = it },
                                valueRange = -3f..3f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF7C4DFF),
                                    activeTrackColor = Color(0xFF7C4DFF)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = "${String.format(java.util.Locale.US, "%.1f", brightnessFactor)}", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Visual Tint & Atmosphere Filter", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Original" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Original") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Original") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Original") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Normal", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Cyberpunk" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Cyberpunk") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Cyberpunk") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Cyberpunk") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Cyber", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Vintage" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Vintage") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Vintage") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Vintage") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Vintage", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Classic Monochrome" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Classic Monochrome") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Classic Monochrome") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Classic Monochrome") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Classic Mono", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            item {
                                Button(
                                    onClick = { selectedFilterStyle = "Aura Cosmic" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFilterStyle == "Aura Cosmic") Color(0xFF7C4DFF) else Color.White,
                                        contentColor = if (selectedFilterStyle == "Aura Cosmic") Color.White else Color(0xFF4A00E0)
                                    ),
                                    border = if (selectedFilterStyle == "Aura Cosmic") null else BorderStroke(1.dp, Color(0xFFE2DDFE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Cosmic", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next / Skip controllers for Cover Backdrop selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.regCoverPic = ""
                        viewModel.navigateTo(Screen.HometownSelection)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("cover_skip"),
                    border = BorderStroke(1.dp, Color(0xFFD1C4E9)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7C4DFF))
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (selectedImgUri != null && (rotationAngle != 0f || zoomScale != 1f || brightnessFactor != 0f || selectedFilterStyle != "Original")) {
                            val processedPath = viewModel.applyImageEditsAndSave(
                                selectedImgUri.toString(),
                                isCover = true,
                                rotationAngle,
                                zoomScale,
                                brightnessFactor,
                                selectedFilterStyle
                            )
                            if (processedPath.isNotBlank()) {
                                viewModel.regCoverPic = processedPath
                            }
                        }
                        viewModel.navigateTo(Screen.HometownSelection)
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("cover_next"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun RequirementItem(text: String, checked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (checked) Color(0xFF4CAF50) else Color(0xFFE57373),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            color = if (checked) Color.Black else Color.Gray,
            fontSize = 13.sp,
            fontWeight = if (checked) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun StoryCardAvatarTop(avatarId: String, fallbackName: String? = null) {
    val char = if (!fallbackName.isNullOrBlank()) {
        fallbackName.trim().firstOrNull()?.uppercaseChar() ?: 'A'
    } else {
        if (avatarId.isNotEmpty() && !avatarId.startsWith("content://") && !avatarId.startsWith("http://") && !avatarId.startsWith("https://") && !avatarId.startsWith("file://") && !avatarId.contains("/")) {
            avatarId.last().uppercaseChar()
        } else {
            'A'
        }
    }
    val fallbackBg = remember(avatarId) {
        val hash = avatarId.hashCode()
        when (hash % 4) {
            0 -> Brush.linearGradient(colors = listOf(Color(0xFF7C4DFF), Color(0xFF9575CD)))
            1 -> Brush.linearGradient(colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB)))
            2 -> Brush.linearGradient(colors = listOf(Color(0xFF00E5FF), Color(0xFF00B0FF)))
            else -> Brush.linearGradient(colors = listOf(Color(0xFF00E676), Color(0xFF69F0AE)))
        }
    }
    val isUri = avatarId.startsWith("content://") || avatarId.startsWith("http://") || avatarId.startsWith("https://") || avatarId.startsWith("file://") || avatarId.contains("/")
    var loadFailed by remember(avatarId) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
    ) {
        if (isUri && !loadFailed) {
            AsyncImage(
                model = avatarId,
                contentDescription = "Create story avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { loadFailed = true }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fallbackBg),
                contentAlignment = Alignment.Center
            ) {
                val hasLetter = char != 'A' || (!fallbackName.isNullOrBlank() && fallbackName.trim().firstOrNull()?.uppercaseChar() == 'A')
                if (hasLetter) {
                    Text(
                        text = char.toString(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Avatar Placeholder",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(0.6f)
                    )
                }
            }
        }
    }
}

// --- SCREEN 4: Main Platform Hub with Scaffolds and Tabs ---
@Composable
fun MainScreen(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUserState by viewModel.currentUser.collectAsStateWithLifecycle()
    var showMenuOverlay by remember { mutableStateOf(false) }
    var showPlusDropdown by remember { mutableStateOf(false) }
    var showShareSettingsDialog by remember { mutableStateOf(false) }

    val feedsLazyListState = androidx.compose.foundation.lazy.rememberLazyListState()
    var isBottomBarVisible by remember { mutableStateOf(true) }
    var previousIndex by remember { mutableStateOf(0) }
    var previousScrollOffset by remember { mutableStateOf(0) }

    val tabHistoryState by viewModel.tabHistory.collectAsStateWithLifecycle()
    BackHandler(enabled = tabHistoryState.size > 1) {
        viewModel.navigateBackTab()
    }

    LaunchedEffect(feedsLazyListState.firstVisibleItemIndex, feedsLazyListState.firstVisibleItemScrollOffset) {
        val currentIndex = feedsLazyListState.firstVisibleItemIndex
        val currentOffset = feedsLazyListState.firstVisibleItemScrollOffset
        
        if (currentIndex > previousIndex) {
            isBottomBarVisible = false
        } else if (currentIndex < previousIndex) {
            isBottomBarVisible = true
        } else {
            if (currentOffset > previousScrollOffset + 10) {
                isBottomBarVisible = false
            } else if (currentOffset < previousScrollOffset - 10) {
                isBottomBarVisible = true
            }
        }
        previousIndex = currentIndex
        previousScrollOffset = currentOffset
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .statusBarsPadding()
                ) {
                    // Row 1: Brand title and modern action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 3-line Menu tab (Hamburger icon)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F2F5))
                                    .clickable { showMenuOverlay = true }
                                    .testTag("hamburger_menu_trigger"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open aura options menu",
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Text(
                                text = "aura",
                                color = LavenderPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                letterSpacing = (-1.2).sp,
                                modifier = Modifier.testTag("brand_logo_aura")
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Action 1: Create Post Plus Button with beautiful dropdown menu
                            Box(
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF0F2F5))
                                        .clickable { showPlusDropdown = !showPlusDropdown }
                                        .testTag("action_plus_trigger"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Create menu options",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showPlusDropdown,
                                    onDismissRequest = { showPlusDropdown = false },
                                    modifier = Modifier
                                        .width(180.dp)
                                        .background(Color.White)
                                        .border(1.dp, LavenderLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                                ) {
                                    // Option 1: Post
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Post option",
                                                    tint = LavenderPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("Post", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.selectTab(MainTab.CREATE_POST)
                                        }
                                    )
                                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)

                                    // Option 2: Story
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.AddCircleOutline,
                                                    contentDescription = "Story option",
                                                    tint = Color(0xFF42A5F5),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("Story", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.publishStory("Shared an Aura story card! ✨", (0..4).random())
                                            android.widget.Toast.makeText(context, "New story shared!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)

                                    // Option 3: Reel
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Slideshow,
                                                    contentDescription = "Reel option",
                                                    tint = Color(0xFFEC407A),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("Reel", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.setReelCreatorVisible(true)
                                        }
                                    )
                                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)

                                    // Option 4: Live
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Videocam,
                                                    contentDescription = "Live option",
                                                    tint = Color(0xFFEF5350),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("Live", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.setLiveSimulatorVisible(true)
                                        }
                                    )
                                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)

                                    // Option 5: Note
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Outlined.ChatBubble,
                                                    contentDescription = "Note option",
                                                    tint = Color(0xFFFFB300),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("Note", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.setNoteCreatorVisible(true)
                                        }
                                    )
                                }
                            }

                            // Action 2: Search Filter Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F2F5))
                                    .clickable {
                                        viewModel.navigateTo(Screen.SearchUsers)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                             // Action 3: Chat / Messenger Button without live badge
                             Box(
                                 modifier = Modifier
                                     .size(38.dp)
                                     .clip(CircleShape)
                                     .background(Color(0xFFF0F2F5))
                                     .clickable {
                                         val other = viewModel.allUsers.value.find { !it.isCurrentUser }
                                         if (other != null) {
                                             viewModel.navigateTo(Screen.ChatRoom(other))
                                         }
                                     }
                                     .testTag("chat_trigger"),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(
                                     imageVector = Icons.Outlined.ChatBubbleOutline,
                                     contentDescription = "Aura Messaging",
                                     tint = Color.Black,
                                     modifier = Modifier.size(19.dp)
                                 )
                             }
                        }
                    }

                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .testTag("bottom_navigation_bar"),
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        val tabs = listOf(
                            Triple(MainTab.FEEDS, Icons.Filled.Home to Icons.Outlined.Home, "Home"),
                            Triple(MainTab.FRIENDS, Icons.Filled.People to Icons.Outlined.People, "Friends"),
                            Triple(MainTab.CREATE_POST, Icons.Filled.OndemandVideo to Icons.Outlined.OndemandVideo, "Videos"),
                            Triple(MainTab.NOTIFICATIONS, Icons.Filled.Notifications to Icons.Outlined.Notifications, "Notification"),
                            Triple(MainTab.PROFILE, Icons.Filled.AccountCircle to Icons.Outlined.AccountCircle, "Profile")
                        )

                        tabs.forEach { (tab, icons, label) ->
                            val isSelected = if (state.currentTab == MainTab.CREATE_POST) {
                                tab == MainTab.FEEDS
                            } else {
                                state.currentTab == tab
                            }
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    if (tab == MainTab.PROFILE) {
                                        ProfileAvatar(
                                            avatarId = currentUserState?.avatarUrl ?: "avatar_user_main",
                                            fallbackName = currentUserState?.displayName,
                                            size = 28,
                                            modifier = Modifier.border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) LavenderPrimary else Color.Gray.copy(alpha = 0.5f),
                                                shape = CircleShape
                                            )
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isSelected) icons.first else icons.second,
                                            contentDescription = label,
                                            tint = if (isSelected) LavenderPrimary else Color(0xFF65676B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                alwaysShowLabel = false,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = LavenderPrimary,
                                    indicatorColor = LavenderPrimary.copy(alpha = 0.1f),
                                    unselectedIconColor = Color(0xFF65676B)
                                )
                            )
                        }
                    }
                }
            },
            containerColor = Color.White,
            modifier = Modifier.testTag("main_screen")
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .swipeTabGesture(state.currentTab) { nextTab ->
                        viewModel.selectTab(nextTab)
                    }
            ) {
                when (state.currentTab) {
                    MainTab.FEEDS -> FeedsTab(state, viewModel, currentUserState, feedsLazyListState)
                    MainTab.FRIENDS -> FriendsTab(state, viewModel)
                    MainTab.CREATE_POST -> CreatePostTab(state, viewModel)
                    MainTab.NOTIFICATIONS -> NotificationsTab(state, viewModel)
                    MainTab.PROFILE -> ProfileTab(state, viewModel, currentUserState)
                    MainTab.SETTINGS -> AppwriteConsoleTab(state, viewModel)
                }

                // Global comments viewer slide drawer
                if (state.activeCommentsPostId != null) {
                    CommentsDrawer(state, viewModel)
                }

                // Global story viewer popup
                if (state.showStoryViewer != null) {
                    StoryViewerModal(state.showStoryViewer, viewModel)
                }

                // --- 1. Interactive Note Creator Dialog ---
                if (state.showNoteCreator) {
                    var noteText by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { viewModel.setNoteCreatorVisible(false) },
                        title = {
                            Text(
                                text = "Share a Quick Note 💭",
                                fontWeight = FontWeight.ExtraBold,
                                color = LavenderPrimary,
                                fontSize = 18.sp
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = "Share what's on your mind (up to 40 characters):",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { if (it.length <= 40) noteText = it },
                                    placeholder = { Text("Sharing cozy vibes... ✨") },
                                    maxLines = 2,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = LavenderPrimary,
                                        unfocusedIndicatorColor = Color.LightGray
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "${noteText.length}/40",
                                    fontSize = 10.sp,
                                    color = LavenderPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.updateActiveUserNote(noteText.ifBlank { "Cozy vibe! ✨" })
                                    android.widget.Toast.makeText(context, "Note published! 💭", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                            ) {
                                Text("Publish", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.setNoteCreatorVisible(false) }) {
                                Text("Cancel", color = Color.Gray)
                            }
                        }
                    )
                }

                // --- 2. Fullscreen Video Reel Creator Overlay ---
                if (state.showReelCreator) {
                    BackHandler {
                        viewModel.setReelCreatorVisible(false)
                    }
                    var reelCaption by remember { mutableStateOf("") }
                    var selectedFilter by remember { mutableStateOf("Warm Rose 🌸") }
                    val filters = listOf("Warm Rose 🌸", "Aura Cyber 🟣", "Vintage Glow 🎞️", "Nature Fresh 🌿")

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.95f))
                            .statusBarsPadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color.Red, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "REELS STUDIO",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                IconButton(onClick = { viewModel.setReelCreatorVisible(false) }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                }
                            }

                            // Middle Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .border(2.dp, LavenderPrimary, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                                )
                                            )
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = "Simulated feed",
                                            tint = LavenderPrimary,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Simulation: Camera Viewfinder Active",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Filter: $selectedFilter",
                                            color = Color(0xFFD1C4E9),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = "1080p 60fps",
                                        color = Color.Green,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                                    )
                                    Text(
                                        text = "REC 00:15",
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                                    )
                                }
                            }

                            // Bottom Section
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Filter chips row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Filter:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        filters.forEach { filter ->
                                            val isSel = filter == selectedFilter
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (isSel) LavenderPrimary else Color.White.copy(alpha = 0.1f),
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { selectedFilter = filter }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = filter,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = reelCaption,
                                    onValueChange = { reelCaption = it },
                                    label = { Text("Reel Caption", color = Color.LightGray) },
                                    placeholder = { Text("Describe your beautiful reel vibe...", color = Color.Gray) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = LavenderPrimary,
                                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        viewModel.publishPostDirectly(
                                            content = "🎥 REEL: ${reelCaption.ifBlank { "Cruising through the lavender lanes under neon skies!" }} #AuraReels $selectedFilter",
                                            gradientIdx = 1
                                        )
                                        viewModel.setReelCreatorVisible(false)
                                        android.widget.Toast.makeText(context, "Reel posted successfully to Home Feed! 🎥🚀", android.widget.Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Publish", tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Publish Reel", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 3. Fullscreen Live Broadcasting Simulator ---
                if (state.showLiveSimulator) {
                    BackHandler {
                        viewModel.setLiveSimulatorVisible(false)
                    }
                    var isMuted by remember { mutableStateOf(false) }
                    var elapsedSec by remember { mutableStateOf(0) }
                    val liveComments = remember {
                        mutableStateListOf(
                            "Tasnim Rahman: Looking super elegant with Lavender! 💜",
                            "Anik Rahman: Satkhira network speed looks insanely responsive! 🚀",
                            "Nusrat Jahan: True modern UI/UX design. Beautiful!",
                            "Aura Moderator: Secured connection initiated successfully! 🛡️"
                        )
                    }

                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(1000)
                            elapsedSec++
                            if (elapsedSec % 4 == 0) {
                                val users = listOf("Kabir Karim", "Sadia Mim", "Arif Chowdhury", "Sonia Rose", "Rakib Ahmed")
                                val notes = listOf("Awesome aura! ✨", "Is this offline-first database?", "Absolutely stunning live feed!", "Keep compiling! 📦", "Wow! 💫")
                                liveComments.add("${users.random()}: ${notes.random()}")
                                if (liveComments.size > 5) {
                                    liveComments.removeAt(0)
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.98f))
                            .statusBarsPadding()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(LavenderPrimary.copy(alpha = 0.15f), Color.Black)
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Red, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🔴 LIVE",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "4.8K Viewers",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = String.format("%02d:%02d", elapsedSec / 60, elapsedSec % 60),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aura Broadcaster Mode initiated / Your live broadcast stream is active.",
                                color = Color(0xFFD1C4E9),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(LavenderPrimary.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, LavenderPrimary.copy(alpha = 0.3f), CircleShape)
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LiveTv,
                                contentDescription = "Live video simulation icon",
                                tint = LavenderPrimary,
                                modifier = Modifier.size(64.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(2.dp, LavenderPrimary, CircleShape)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "LIVE SPEECH FEED:",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                liveComments.forEach { comment ->
                                    Text(
                                        text = comment,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { isMuted = !isMuted },
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isMuted) Color.Red.copy(alpha = 0.3f) else Color.Transparent
                                    ),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = "Mute mic",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.setLiveSimulatorVisible(false)
                                        viewModel.publishPostDirectly(
                                            content = "🔴 LIVE RECALL: Broadcasted live on Aura for ${elapsedSec / 60}m ${elapsedSec % 60}s with 4,850 participants! 🎥 Thanks for tuning in!",
                                            gradientIdx = 3
                                        )
                                        android.widget.Toast.makeText(context, "Broadcast ended successfully! Log saved to feed. 📹", android.widget.Toast.LENGTH_LONG).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp).height(48.dp)
                                ) {
                                    Text(
                                        text = "END BROADCAST",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        android.widget.Toast.makeText(context, "Simulated camera stream flipped! 🔄", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cached,
                                        contentDescription = "Flip camera",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- OVERLAY: Immersive Fullscreen Options Menu Dashboard ---
        if (showMenuOverlay) {
            BackHandler {
                showMenuOverlay = false
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // 1. Header Row (Menu text + Close Button)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menu",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F2F5))
                                .clickable { showMenuOverlay = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 2. User Profile Quick Entry card (Screenshot 3 style)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable {
                                showMenuOverlay = false
                                viewModel.selectTab(MainTab.PROFILE)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProfileAvatar(
                                    avatarId = currentUserState?.avatarUrl ?: "avatar_user_main",
                                    fallbackName = currentUserState?.displayName,
                                    size = 42
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = currentUserState?.displayName ?: "Aura User",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand profile details",
                                tint = Color.DarkGray
                            )
                        }
                    }

                    // 3. Wide button "Create Facebook Page" -> "Create Aura Space"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .clickable {
                                android.widget.Toast.makeText(context, "Welcome to Aura Space Creation!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(LavenderPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create page",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Create Aura Space",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // 4. Grid of 10 Core Options (2 columns as requested)
                    val menuItems = listOf(
                        Triple("Groups", Icons.Outlined.People, Color(0xFF1877F2)),
                        Triple("Memories", Icons.Default.History, Color(0xFFE42645)),
                        Triple("Saved", Icons.Outlined.BookmarkBorder, Color(0xFFB859F5)),
                        Triple("Reels", Icons.Outlined.Slideshow, Color(0xFFF02849)),
                        Triple("Marketplace", Icons.Default.Storefront, Color(0xFF45BD62)),
                        Triple("Feeds", Icons.Default.RssFeed, Color(0xFF10AAFF)),
                        Triple("Events", Icons.Default.Event, Color(0xFFF35369)),
                        Triple("Birthdays", Icons.Default.Cake, Color(0xFFFF5252)),
                        Triple("Find friends", Icons.Default.PersonAdd, Color(0xFF2ABBA7)),
                        Triple("Messages", Icons.Outlined.ChatBubbleOutline, Color(0xFF00C6FF))
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in menuItems.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Column 1
                                val item1 = menuItems[i]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF0F2F5))
                                        .clickable {
                                            android.widget.Toast.makeText(context, "${item1.first} launched! ✨", android.widget.Toast.LENGTH_SHORT).show()
                                            if (item1.first == "Feeds") {
                                                viewModel.selectTab(MainTab.FEEDS)
                                                showMenuOverlay = false
                                            } else if (item1.first == "Find friends") {
                                                viewModel.selectTab(MainTab.FRIENDS)
                                                showMenuOverlay = false
                                            } else if (item1.first == "Messages") {
                                                val other = viewModel.allUsers.value.find { !it.isCurrentUser }
                                                if (other != null) {
                                                    showMenuOverlay = false
                                                    viewModel.navigateTo(Screen.ChatRoom(other))
                                                }
                                            }
                                        }
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Icon(
                                            imageVector = item1.second,
                                            contentDescription = item1.first,
                                            tint = item1.third,
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = item1.first,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                    }
                                }

                                // Column 2
                                if (i + 1 < menuItems.size) {
                                    val item2 = menuItems[i + 1]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF0F2F5))
                                            .clickable {
                                                android.widget.Toast.makeText(context, "${item2.first} launched! ✨", android.widget.Toast.LENGTH_SHORT).show()
                                                if (item2.first == "Feeds") {
                                                    viewModel.selectTab(MainTab.FEEDS)
                                                    showMenuOverlay = false
                                                } else if (item2.first == "Find friends") {
                                                    viewModel.selectTab(MainTab.FRIENDS)
                                                    showMenuOverlay = false
                                                } else if (item2.first == "Messages") {
                                                    val other = viewModel.allUsers.value.find { !it.isCurrentUser }
                                                    if (other != null) {
                                                        showMenuOverlay = false
                                                        viewModel.navigateTo(Screen.ChatRoom(other))
                                                    }
                                                }
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Icon(
                                                imageVector = item2.second,
                                                contentDescription = item2.first,
                                                tint = item2.third,
                                                modifier = Modifier.size(26.dp)
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Text(
                                                text = item2.first,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = Color(0xFFE4E6EB), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Expandable / Collapsible Lists (Screenshot 4 style)
                    var isHelpExpanded by remember { mutableStateOf(false) }
                    var isSettingsExpanded by remember { mutableStateOf(false) }

                    // Help and Support Collapsible Row
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isHelpExpanded = !isHelpExpanded }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Help support",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Help and support",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Icon(
                                imageVector = if (isHelpExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand help",
                                tint = Color.DarkGray
                            )
                        }
                        if (isHelpExpanded) {
                            Column(modifier = Modifier.padding(start = 36.dp, bottom = 12.dp)) {
                                Text("• Help Center", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                                Text("• Support Inbox", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                                Text("• Report a Problem", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }

                    Divider(color = Color(0xFFE4E6EB).copy(alpha = 0.5f), thickness = 0.5.dp)

                    // Settings & Privacy Collapsible Row
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSettingsExpanded = !isSettingsExpanded }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings and privacy",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Settings and privacy",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Icon(
                                imageVector = if (isSettingsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand settings",
                                tint = Color.DarkGray
                            )
                        }
                        if (isSettingsExpanded) {
                            Column(modifier = Modifier.padding(start = 36.dp, bottom = 12.dp)) {
                                Text("• Privacy Shortcuts", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp).clickable {
                                    android.widget.Toast.makeText(context, "Privacy settings are configured automatically! / Privacy is fully secured!", android.widget.Toast.LENGTH_SHORT).show()
                                    showMenuOverlay = false
                                })
                                Text("• Activity Log", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                                Text("• Language Settings", fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                                Text("• Aura Dynamic Share Hub 🚀", fontSize = 13.sp, color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp).clickable {
                                    showMenuOverlay = false
                                    showShareSettingsDialog = true
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 6. Log out Button
                    Button(
                        onClick = {
                            viewModel.logout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("menu_logout_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4E6EB))
                    ) {
                        Text(
                            text = "Log out",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        val deepLinkedPostId by viewModel.deepLinkedPostId.collectAsStateWithLifecycle()
        val postsList by viewModel.postsFeed.collectAsStateWithLifecycle(emptyList())
        val deepLinkedPost = remember(postsList, deepLinkedPostId) {
            postsList.find { it.postId == deepLinkedPostId }
        }

        if (deepLinkedPostId != null) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { viewModel.clearDeepLinkPost() }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp)
                        .shadow(16.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF7C4DFF).copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Shared Link",
                                    tint = Color(0xFF7C4DFF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Aura Shared Post 💜✨",
                                    color = Color(0xFF7C4DFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = { viewModel.clearDeepLinkPost() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Dialog",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (deepLinkedPost != null) {
                            // Display the native post card that has pinch-to-zoom images and all reactive comments
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 500.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                PostCard(
                                    post = deepLinkedPost,
                                    viewModel = viewModel,
                                    currentUserId = currentUserState?.userId ?: 0
                                )
                            }
                        } else {
                            // Post not found feedback with styled Lavender accent
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🔍 🚫",
                                    fontSize = 36.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Text(
                                    text = "Sorry, post not found!",
                                    color = Color(0xFF7C4DFF),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "The post might have been deleted. You can keep browsing the main feed.",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )
                                Button(
                                    onClick = { viewModel.clearDeepLinkPost() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Back to Feed 💜", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showShareSettingsDialog) {
            val dummyPost = remember {
                com.example.data.database.PostEntity(
                    postId = 1,
                    authorId = 999,
                    authorName = currentUserState?.displayName ?: "Aura Dev",
                    authorAvatar = currentUserState?.avatarUrl ?: "avatar_user_main",
                    content = "Configure your Zero-Database Appwrite Link Hub for beautiful dynamic links on social media."
                )
            }
            AuraShareHubDialog(post = dummyPost) {
                showShareSettingsDialog = false
            }
        }
    }
}

@Composable
fun NotificationsTab(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val notifications = remember {
        mutableStateListOf<NotificationItem>()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("notifications_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notifications",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Stay updated with your social aura network",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                if (notifications.any { it.isUnread }) {
                    TextButton(
                        onClick = {
                            for (i in notifications.indices) {
                                notifications[i] = notifications[i].copy(isUnread = false)
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = LavenderPrimary),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3E8FF)), // Cute soft bold-lavender accent background
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark all read icon",
                                tint = LavenderPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mark all read",
                                color = LavenderPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F2F5))
        }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "No Notifications",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No notifications yet",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(notifications, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val idx = notifications.indexOfFirst { it.id == item.id }
                            if (idx != -1) {
                                notifications[idx] = notifications[idx].copy(isUnread = false)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isUnread) Color(0xFFF3E8FF).copy(alpha = 0.5f) else Color(0xFFF8FAFC)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (item.isUnread) LavenderPrimary.copy(alpha = 0.3f) else Color(0xFFE2E8F0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(44.dp)) {
                            if (item.avatar == "security_shield") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFE0F2FE), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Security Status",
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                ProfileAvatar(
                                    avatarId = item.avatar,
                                    size = 44,
                                    fallbackName = item.sender
                                )
                            }
                            
                            if (item.isUnread) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFFFFB300), CircleShape)
                                        .align(Alignment.TopEnd)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.sender,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• " + item.time,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = item.message,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 18.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                notifications.removeIf { it.id == item.id }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NotificationItem(
    val id: Int,
    val sender: String,
    val message: String,
    val time: String,
    val avatar: String,
    val isUnread: Boolean
)

// --- SUB-TABS: FEEDS Tab inside Main Platform ---
@Composable
fun FeedsTab(
    state: AuraUiState,
    viewModel: AuraViewModel,
    currentUser: UserEntity?,
    lazyListState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val storiesList by viewModel.stories.collectAsStateWithLifecycle()
    val postsList by viewModel.postsFeed.collectAsStateWithLifecycle()

    var quickPostText by remember { mutableStateOf("") }
    var quickPostImage by remember { mutableStateOf("") }
    var showAttachImageDialog by remember { mutableStateOf(false) }

    // Dialog for custom image URL attachments (with beautiful presets)
    if (showAttachImageDialog) {
        AlertDialog(
            onDismissRequest = { showAttachImageDialog = false },
            title = { 
                Text(
                    text = "Attach Photo", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = LavenderPrimary,
                    fontSize = 18.sp
                ) 
            },
            text = {
                Column {
                    Text(
                        text = "Enter any high-resolution web URL, or choose an elegant theme backdrop from below:", 
                        fontSize = 12.sp, 
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = quickPostImage,
                        onValueChange = { quickPostImage = it },
                        placeholder = { Text("https://images.unsplash.com/photo-...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = LavenderPrimary,
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Stunning Backdrops:", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val presets = listOf(
                            "Nature" to "https://images.unsplash.com/photo-1542224566-6e85f2e6772f?w=600",
                            "Vibrant Gradient" to "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=600",
                            "Abstract Dream" to "https://images.unsplash.com/photo-1511447333015-45b65e60f6d5?w=600"
                        )
                        presets.forEach { (name, url) ->
                            val isChosen = quickPostImage == url
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                        .height(55.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isChosen) LavenderPrimary.copy(alpha = 0.15f) else Color(0xFFF5F5F5))
                                        .border(1.5.dp, if (isChosen) LavenderPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { quickPostImage = url }
                                        .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name, 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    textAlign = TextAlign.Center, 
                                    color = if (isChosen) LavenderPrimary else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAttachImageDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                ) {
                    Text("Attach", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        quickPostImage = ""
                        showAttachImageDialog = false 
                    }
                ) {
                    Text("Clear", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("feeds_tab"),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (state.isViewingAsGuest) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
                    border = BorderStroke(1.5.dp, Color(0xFF7C4DFF))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Guest Mode Active 🔑🔒",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4C1D95),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Having an active account on Aura unlocks full features. Tap to create or sign in!",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.setViewingAsGuest(false)
                                android.widget.Toast.makeText(context, "Welcome back to your master account! 💜", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Join App 💜", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Step 1: Facebook-style "What's on your mind?" composer bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarId = currentUser?.avatarUrl ?: "avatar_user_main", 
                        size = 40,
                        showOnlineStatus = true,
                        fallbackName = currentUser?.displayName
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    TextField(
                        value = quickPostText,
                        onValueChange = { quickPostText = it },
                        placeholder = { Text("What's on your mind?") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F2F5),
                            unfocusedContainerColor = Color(0xFFF0F2F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(22.dp),
                        trailingIcon = {
                            if (quickPostText.isNotEmpty() || quickPostImage.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.publishPostDirectly(quickPostText, quickPostImage)
                                        quickPostText = ""
                                        quickPostImage = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send, 
                                        contentDescription = "Post instantly", 
                                        tint = LavenderPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // Photo attach gallery button (themed green just like facebook)
                    IconButton(
                        onClick = { showAttachImageDialog = true }
                    ) {
                        Icon(
                            imageVector = if (quickPostImage.isNotEmpty()) Icons.Filled.PhotoLibrary else Icons.Default.Image,
                            contentDescription = "Gallery photo attachment",
                            tint = if (quickPostImage.isNotEmpty()) LavenderPrimary else Color(0xFF45BD62),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                
                if (quickPostImage.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .background(LavenderPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = LavenderPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Image attached successfully! ✨", fontSize = 11.sp, color = LavenderPrimary, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { quickPostImage = "" }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Clear, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFFF0F2F5), thickness = 6.dp)
            }
        }

        // Step 2: Horizontal Stories bar (9:16 layout) exactly matching screenshot specifications
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Auras & Spotlight",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Beautiful Music Story Card (Accurate to second screenshot)
                    item {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .aspectRatio(9f / 16f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF2EF1C7), Color(0xFF1596EC))
                                    )
                                )
                                .clickable {
                                    android.widget.Toast.makeText(context, "Streaming high-aura calming background music! 🎧", android.widget.Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                // Centered Music circular white card icon
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicVideo, 
                                        contentDescription = "Music", 
                                        tint = Color(0xFF1596EC),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                
                                Text(
                                    text = "Music",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = LocalTextStyle.current.copy(
                                        shadow = Shadow(Color.Black.copy(alpha = 0.5f), Offset(1f, 1f), 3f)
                                    )
                                )
                            }
                        }
                    }

                    // Card 2: Create Story Card with current user's photo split 50-50 and centered plus button
                    item {
                        Card(
                            modifier = Modifier
                                .width(100.dp)
                                .aspectRatio(9f / 16f)
                                .clickable {
                                    viewModel.publishStory(
                                        content = "New story added in real-time! ✨",
                                        gradientIdx = (0..4).random()
                                    )
                                    android.widget.Toast.makeText(context, "New story shared!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(0.5.dp, Color(0xFFE4E6EB))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Top 70% space is actual user profile photo, beautifully visible
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.7f)
                                    ) {
                                        StoryCardAvatarTop(
                                            avatarId = currentUser?.avatarUrl ?: "avatar_user_main",
                                            fallbackName = currentUser?.displayName
                                        )
                                    }
                                    
                                    // Bottom 30% space is solid white background with modern centered text
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.3f)
                                            .background(Color.White),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Text(
                                            text = "Create story",
                                            color = Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                                
                                // Beautiful circular plus action button centered on the 70% split boundary
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (-37).dp) // Mathematically centered overlapping the 70% line
                                        .size(32.dp)
                                        .background(LavenderPrimary, CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add, 
                                        contentDescription = "Create story", 
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Beautiful speech-bubble for live notes! Overlaps the top center of the story card beautifully.
                                if (state.activeUserNote.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(LavenderPrimary, Color(0xFF9575CD))
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = state.activeUserNote,
                                            color = Color.White,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Remaining Friend Stories (each mapped strictly to 9:16 aspect ratios)
                    items(storiesList) { story ->
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .aspectRatio(9f / 16f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PremiumGradients[story.gradientIndex])
                                .clickable { viewModel.showStory(story) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Story Active Ring Indicator around Avatar
                                ProfileAvatar(
                                    avatarId = story.authorAvatar,
                                    size = 32,
                                    fallbackName = story.authorName,
                                    modifier = Modifier.border(2.dp, LavenderPrimary, CircleShape)
                                )
                                Text(
                                    text = story.contentText,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = LocalTextStyle.current.copy(
                                        shadow = Shadow(Color.Black, Offset(1f, 1f), 3f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Divider(color = Color(0xFFF0F2F5), thickness = 6.dp)
        }

        // Live Real-Time Feed Post items
        if (postsList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = "No results",
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No Aura Posts Match query",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Dynamic posts feeds
        items(postsList, key = { it.postId }) { post ->
            PostCard(post = post, viewModel = viewModel, currentUserId = currentUser?.userId ?: 0)
        }
    }
}

// --- SUB-TABS: FRIENDS Tab inside Main Platform ---
@Composable
fun FriendsTab(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    val usersList by viewModel.allUsers.collectAsStateWithLifecycle()
    val currUser by viewModel.currentUser.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("friends_tab"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Aura Members in 2026",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Add friends to see their personal aura state and start lively message discussions.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            Divider()
        }

        items(usersList.filter { it.userId != currUser?.userId }) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.ProfileDetail(user)) }
                    ) {
                        ProfileAvatar(avatarId = user.avatarUrl, size = 48, showOnlineStatus = true)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = user.displayName,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.bio,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.AutoAwesome, null, tint = LavenderPrimary, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    "Social aura: ${user.auraRating}",
                                    color = LavenderPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = { viewModel.toggleFollowUser(user) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isFollowing) Color.DarkGray else LavenderPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (user.isFollowing) "Unfollow" else "Follow",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.ChatRoom(user)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, null, tint = LavenderPrimary)
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-TABS: CREATE POST Tab inside Main Platform ---
@Composable
fun CreatePostTab(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUserState by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsersList by viewModel.allUsers.collectAsStateWithLifecycle()

    // Subpage & view states
    var isAddTextOpen by remember { mutableStateOf(false) }
    var showAiLabelBox by remember { mutableStateOf(false) }
    var selectedMusicTrack by remember { mutableStateOf("") }
    var selectedLocationTag by remember { mutableStateOf("") }

    // Picker overlays
    var showMusicSelector by remember { mutableStateOf(false) }
    var showLocationSelector by remember { mutableStateOf(false) }
    var showPeopleSelector by remember { mutableStateOf(false) }
    var showStyleChooserInternal by remember { mutableStateOf(false) }
    var showPrivacySelectorDialog by remember { mutableStateOf(false) }

    val pickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()
    ) { uris: List<android.net.Uri> ->
        if (uris.isNotEmpty()) {
            val urlsString = uris.joinToString(",") { it.toString() }
            viewModel.setCreatePostImage(urlsString)
            android.widget.Toast.makeText(context, "Successfully attached ${uris.size} photo(s) from gallery! 🌌🌟", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val userDisplay = currentUserState?.displayName ?: "Imrul Hasan"
    val userAvatar = currentUserState?.avatarUrl ?: "avatar_default"

    // Helper to identify optimal text color based on gradient
    fun getOptimalTextColor(gradIdx: Int): Color {
        return when (gradIdx) {
            5, 10, 13, 15 -> Color(0xFF1E1C2E) // Light pastel backdrops use rich dark text
            -1 -> Color(0xFF1E1C2E)            // Normal composer mode text is dark
            else -> Color.White                 // Rich/Dark gradients use white text
        }
    }

    if (isAddTextOpen) {
        // SCREEN 3: Add Text Screen
        val isNoneSelected = state.creationState.selectedGradientIdx == -1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isNoneSelected) Color.White else Color(0xFF121214))
                .testTag("add_text_subpage")
        ) {
            // Background Template Layer (if selected)
            if (state.creationState.selectedGradientIdx >= 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PremiumGradients[state.creationState.selectedGradientIdx])
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                // Header (Screenshot 3)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(48.dp)) // spacing

                    Text(
                        text = "Add text",
                        color = if (isNoneSelected) Color.Black else Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Done",
                        color = if (isNoneSelected) Color(0xFF7C4DFF) else Color(0xFF2196F3),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { isAddTextOpen = false }
                            .padding(8.dp)
                            .testTag("add_text_done")
                    )
                }

                // Input field body with dynamic styling
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val textColor = if (isNoneSelected) {
                        Color(0xFF1E1C2E)
                    } else {
                        getOptimalTextColor(state.creationState.selectedGradientIdx)
                    }
                    
                    TextField(
                        value = state.creationState.content,
                        onValueChange = { viewModel.setCreatePostContent(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mention_post_input"),
                        textStyle = LocalTextStyle.current.copy(
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        placeholder = {
                            Text(
                                text = "What's on your mind?",
                                color = textColor.copy(alpha = 0.5f),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
                    )
                }

                // Bottom floating pills bar (Screenshot 3: @ and A buttons)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF1E1E24), RoundedCornerShape(28.dp))
                            .border(1.dp, Color(0xFF32323A), RoundedCornerShape(28.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // "@" Mention Selector Trigger
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C2C35))
                                .clickable { showPeopleSelector = true }
                                .testTag("mention_trigger"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = "Mention user button",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // "A" Style Template Selector Trigger
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF007F), Color(0xFF7C4DFF), Color(0xFF00C9FF))
                                    )
                                )
                                .clickable { showStyleChooserInternal = true }
                                .testTag("style_pane_trigger"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "A",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // OVERLAY: SCREEN 4 (Style Grid Pane)
            if (showStyleChooserInternal) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showStyleChooserInternal = false }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1E)),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Bottom drawer glide handle
                            Box(
                                modifier = Modifier
                                    .size(36.dp, 4.dp)
                                    .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(36.dp))
                                Text(
                                    "Style",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { showStyleChooserInternal = false },
                                    modifier = Modifier.testTag("style_done_checkmark")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Confirm Style",
                                        tint = Color(0xFF00FFCC),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Styles Template Grid (Grid built using native column & row layout to avoid complex imports)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val gridItemsList = listOf(-1) + (0..14).toList()
                                gridItemsList.chunked(4).forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowItems.forEach { idx ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .then(
                                                        if (idx == -1) {
                                                            Modifier.background(Color(0xFF2C2C30))
                                                        } else {
                                                            Modifier.background(PremiumGradients[idx])
                                                        }
                                                    )
                                                    .border(
                                                        width = if (state.creationState.selectedGradientIdx == idx) 3.dp else 1.dp,
                                                        color = if (state.creationState.selectedGradientIdx == idx) Color.White else Color.Transparent,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { viewModel.selectGradient(idx) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (idx == -1) {
                                                    Icon(
                                                        imageVector = Icons.Default.Block,
                                                        contentDescription = "Standard layout option",
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                if (state.creationState.selectedGradientIdx == idx) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected backdrop option indicator",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                        repeat(4 - rowItems.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // OVERLAY: People Tag Selection popover (App account profiles checklist)
            if (showPeopleSelector) {
                androidx.compose.ui.window.Dialog(onDismissRequest = { showPeopleSelector = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Tag Friends",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                "Select users to mention on your post",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Divider(color = Color.DarkGray)

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(allUsersList) { usr ->
                                    val isTagged = state.creationState.mentionedUserIds
                                        .split(",")
                                        .any { it.trim() == usr.userId.toString() }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isTagged) Color(0xFF2E2452) else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                val oldList = state.creationState.mentionedUserIds
                                                    .split(",")
                                                    .mapNotNull { it.trim().takeIf { t -> t.isNotEmpty() } }
                                                    .toMutableList()

                                                if (oldList.contains(usr.userId.toString())) {
                                                    oldList.remove(usr.userId.toString())
                                                } else {
                                                    oldList.add(usr.userId.toString())
                                                    // Append handle to content text
                                                    val cleanHandle = " @${usr.username} "
                                                    if (!state.creationState.content.contains(cleanHandle)) {
                                                        viewModel.setCreatePostContent(state.creationState.content + cleanHandle)
                                                    }
                                                }
                                                viewModel.setPostMentionedUserIds(oldList.joinToString(","))
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            ProfileAvatar(
                                                avatarId = usr.avatarUrl,
                                                fallbackName = usr.displayName,
                                                size = 36
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(usr.displayName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text("@${usr.username}", color = Color.LightGray, fontSize = 11.sp)
                                            }
                                        }

                                        Icon(
                                            imageVector = if (isTagged) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                            contentDescription = "Tagged status marker",
                                            tint = if (isTagged) Color(0xFF7C4DFF) else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { showPeopleSelector = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Done", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    } else {
        // SCREEN 1: Core Post Composer (Default Bold White Background)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // default white background as requested
                .testTag("new_post_screen_main")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                // Header Row (Screenshot 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E24)) // Modern dark header contrast
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.FEEDS) },
                        modifier = Modifier.testTag("exit_post_composer")
                    ) {
                        Icon(Icons.Default.Close, "Cancel", tint = Color.White)
                    }

                    Text(
                        text = "New post",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { showAiLabelBox = !showAiLabelBox },
                        modifier = Modifier.testTag("ai_label_box_trigger")
                    ) {
                        Icon(Icons.Default.MoreHoriz, "Options", tint = Color.White)
                    }
                }

                // AI Label Box overlay (Screenshot 2)
                if (showAiLabelBox) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .testTag("ai_label_box"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C35)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF3C3C46), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircleOutline,
                                        contentDescription = "Robot Icon",
                                        tint = Color(0xFF9E8FFF)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Add AI Label",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "We require you to label certain realistic content that's made with AI.",
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            Switch(
                                checked = state.creationState.isAiLabeled,
                                onCheckedChange = { active ->
                                    viewModel.setPostAiLabeled(active)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF7C4DFF),
                                    checkedTrackColor = Color(0xFF9E8FFF).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }

                // Profile area (Profile avatar with Full Name)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarId = userAvatar,
                        fallbackName = userDisplay,
                        size = 52
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userDisplay,
                            color = Color(0xFF1E1E24),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            if (selectedLocationTag.isNotEmpty()) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedLocationTag, color = Color(0xFF7C4DFF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                Text("Aura Networks", color = Color.Gray, fontSize = 11.sp)
                            }

                            if (selectedMusicTrack.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.size(3.dp).background(Color.Gray, CircleShape))
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.MusicNote, null, tint = Color(0xFF00C9FF), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedMusicTrack, color = Color(0xFF00C9FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Horizontal scrollable rounded pills (Screenshot 1: Music, People, Location)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pill: Music
                    Row(
                        modifier = Modifier
                            .background(
                                if (selectedMusicTrack.isNotEmpty()) Color(0xFFE0F7FA) else Color(0xFFF1F1F5),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { showMusicSelector = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music indicator",
                            tint = if (selectedMusicTrack.isNotEmpty()) Color(0xFF00838F) else Color.DarkGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedMusicTrack.isNotEmpty()) selectedMusicTrack else "Music",
                            color = if (selectedMusicTrack.isNotEmpty()) Color(0xFF00838F) else Color.DarkGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Pill: People
                    val numTagged = state.creationState.mentionedUserIds.split(",").filter { it.trim().isNotEmpty() }.size
                    Row(
                        modifier = Modifier
                            .background(
                                if (numTagged > 0) Color(0xFFEDE7F6) else Color(0xFFF1F1F5),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { isAddTextOpen = true; showPeopleSelector = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Tag people trigger",
                            tint = if (numTagged > 0) Color(0xFF5E35B1) else Color.DarkGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (numTagged > 0) "$numTagged People" else "People",
                            color = if (numTagged > 0) Color(0xFF5E35B1) else Color.DarkGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Pill: Location
                    Row(
                        modifier = Modifier
                            .background(
                                if (selectedLocationTag.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFF1F1F5),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { showLocationSelector = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location pin icon",
                            tint = if (selectedLocationTag.isNotEmpty()) Color(0xFF2E7D32) else Color.DarkGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedLocationTag.isNotEmpty()) selectedLocationTag else "Location",
                            color = if (selectedLocationTag.isNotEmpty()) Color(0xFF2E7D32) else Color.DarkGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mid What's on your mind? Box (Clickable template backdrop container with Collage support)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(1.dp, RoundedCornerShape(16.dp))
                        .then(
                            if (state.creationState.selectedGradientIdx >= 0) {
                                Modifier.background(PremiumGradients[state.creationState.selectedGradientIdx])
                            } else {
                                Modifier.background(Color.White)
                            }
                        )
                        .clickable { isAddTextOpen = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val txtColor = getOptimalTextColor(state.creationState.selectedGradientIdx)
                    var isPreviewExpanded by remember { mutableStateOf(false) }
                    val originalContent = state.creationState.content
                    val isLongContent = originalContent.length > 120
                    val displayText = if (isLongContent && !isPreviewExpanded) {
                        originalContent.take(120) + "..."
                    } else {
                        originalContent.ifBlank { "What's on your mind? 💭✨" }
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayText,
                            color = if (originalContent.isBlank()) txtColor.copy(alpha = 0.5f) else txtColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        if (isLongContent) {
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF7C4DFF).copy(alpha = 0.1f))
                                    .clickable { isPreviewExpanded = !isPreviewExpanded }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isPreviewExpanded) "Show Less ⬆️" else "See More ⬇️",
                                    color = Color(0xFF7C4DFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        if (state.creationState.imageInputUrl.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val count = state.creationState.imageInputUrl.split(",").filter { it.isNotBlank() }.size
                                Text(
                                    text = "📸 $count Photos Selected",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C4DFF)
                                )
                                Text(
                                    text = "Clear All ❌",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.setCreatePostImage("")
                                        }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                            CreationPreviewCarousel(
                                imageUrlString = state.creationState.imageInputUrl,
                                onRemoveImage = { indexToRemove ->
                                    val currentImages = state.creationState.imageInputUrl.split(",").filter { it.isNotBlank() }.toMutableList()
                                    if (indexToRemove in currentImages.indices) {
                                        currentImages.removeAt(indexToRemove)
                                        viewModel.setCreatePostImage(currentImages.joinToString(","))
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Grid: Gallery, GIF, LIVE Tiles (Screenshot 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: Gallery (Real Android Media Photo Picker for one or multiple images)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clickable {
                                try {
                                    pickerLauncher.launch("image/*")
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Failed to open gallery: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Image, "Gallery Image Icon", tint = Color(0xFF7C4DFF), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Gallery", color = Color(0xFF1E1E24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Option 2: GIF (Attaches a beautiful cool neon GIF simulation)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clickable {
                                // Attach nice simulated dynamic graphic URL
                                viewModel.setCreatePostImage("https://images.unsplash.com/photo-1550745165-9bc0b252726f")
                                android.widget.Toast.makeText(context, "Aura Neon Sparkle GIF template selected successfully! ☄️👾", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF7C4DFF).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("GIF", color = Color(0xFF7C4DFF), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("GIF", color = Color(0xFF1E1E24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Option 3: LIVE (Launches the live simulator)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clickable {
                                viewModel.setLiveSimulatorVisible(true)
                                android.widget.Toast.makeText(context, "Launching live recording chamber... 🔴🎥", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Videocam, "Recording Live Icon", tint = Color(0xFFFF3B30), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("LIVE", color = Color(0xFF1E1E24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Navigation Control Row (Screenshot 1: Public left-aligned / Next right-aligned)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left-aligned: Dynamic & Interactive Privacy Option Selector
                    val currentPrivacy = state.creationState.privacy
                    val privacyIcon = when (currentPrivacy) {
                        "Friends" -> Icons.Default.People
                        "Only me" -> Icons.Default.Lock
                        else -> Icons.Default.Public
                    }
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF3E8FF), RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .clickable { showPrivacySelectorDialog = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = privacyIcon,
                            contentDescription = "Post privacy",
                            tint = Color(0xFF7C4DFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentPrivacy,
                            color = Color(0xFF4C1D95),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand dropdown",
                            tint = Color(0xFF7C4DFF),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Right-aligned: "Next" push controller
                    Button(
                        onClick = {
                            viewModel.publishPost()
                            android.widget.Toast.makeText(context, "Aura posted successfully! 🚀💜", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        enabled = state.creationState.content.isNotBlank() || state.creationState.imageInputUrl.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C4DFF), // app theme color bold lavender
                            disabledContainerColor = Color(0xFF7C4DFF).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("publish_post_button_next")
                    ) {
                        Text(
                            text = "Next",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Overlays of Music List Selector
    if (showMusicSelector) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showMusicSelector = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Background Music", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val tracks = listOf("Lavender Dream Beats", "Rain in Satkhira Lofi", "Dhaka Skyline", "Aura Breeze Symphony")
                    tracks.forEach { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMusicTrack = track
                                    showMusicSelector = false
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MusicNote, null, tint = Color(0xFF00C9FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(track, color = Color.White, fontSize = 13.sp)
                        }
                        Divider(color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = {
                            selectedMusicTrack = ""
                            showMusicSelector = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Clear Track", color = Color(0xFFFF5252))
                    }
                }
            }
        }
    }

    // Overlays of Location Tag Selector
    if (showLocationSelector) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showLocationSelector = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Location", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val locs = listOf("Dhaka, Bangladesh", "Satkhira, Khulna", "Sylhet, Bangladesh", "Planet Earth (Aura)")
                    locs.forEach { l ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLocationTag = l
                                    showLocationSelector = false
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(l, color = Color.White, fontSize = 13.sp)
                        }
                        Divider(color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = {
                            selectedLocationTag = ""
                            showLocationSelector = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Clear Location", color = Color(0xFFFF5252))
                    }
                }
            }
        }
    }

    if (showPrivacySelectorDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showPrivacySelectorDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFF7C4DFF).copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Choose Post Privacy 🔑💜",
                        color = Color(0xFF7C4DFF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Select Who Can See Your Post",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )

                    val privacyOptions = listOf(
                        Triple("Public", Icons.Default.Public, "Every user on Aura can see and interact with your post."),
                        Triple("Friends", Icons.Default.People, "Only creators/creeps you follow or friends can see this post."),
                        Triple("Only me", Icons.Default.Lock, "Only visible on your personal dashboard, completely hidden from others.")
                    )

                    privacyOptions.forEach { (option, icon, desc) ->
                        val isSelected = state.creationState.privacy == option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFF3E8FF) else Color.Transparent)
                                .clickable {
                                    viewModel.setPostPrivacy(option)
                                    showPrivacySelectorDialog = false
                                    android.widget.Toast.makeText(context, "$option selected successfully! 🔐✨", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = option,
                                tint = if (isSelected) Color(0xFF7C4DFF) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF4C1D95) else Color.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    lineHeight = 14.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF7C4DFF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showPrivacySelectorDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Custom Metadata layout row inside own profile dashboard representing complete onboarding info
@Composable
fun ProfileMetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    privacy: String,
    placeholder: String,
    showPrivacy: Boolean = false,
    onAddClick: () -> Unit
) {
    val hasValue = value.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddClick() },
        colors = CardDefaults.cardColors(containerColor = if (hasValue) Color(0xFFF9F9FB) else Color(0xFFFFEFEF)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (hasValue) Color(0xFFE8E8EC) else Color(0xFFFFB2C1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (hasValue) Color(0xFFEDE7F6) else Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (hasValue) Color(0xFF7C4DFF) else Color(0xFFFF4081),
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = label,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (hasValue) value else placeholder,
                        color = if (hasValue) Color.Black else Color(0xFFFF4081),
                        fontSize = 14.sp,
                        fontWeight = if (hasValue) FontWeight.SemiBold else FontWeight.Bold
                    )
                }
            }

            if (showPrivacy) {
                // Real-time Privacy configuration switcher indicator status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (privacy) {
                                "Public" -> Color(0x134CAF50)
                                "Friends" -> Color(0x132196F3)
                                else -> Color(0x13FF9800)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val (privacyIcon, privacyTint) = when (privacy) {
                        "Public" -> Icons.Default.Public to Color(0xFF2E7D32)
                        "Friends" -> Icons.Default.People to Color(0xFF1565C0)
                        else -> Icons.Default.Lock to Color(0xFFEF6C00)
                    }
                    Icon(
                        imageVector = privacyIcon,
                        contentDescription = privacy,
                        tint = privacyTint,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = privacy,
                        color = privacyTint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- SUB-TABS: PROFILE Tab inside Main Platform ---
@Composable
fun ProfileTab(
    state: AuraUiState,
    viewModel: AuraViewModel,
    currentUser: UserEntity?
) {
    var isEditingProfile by remember { mutableStateOf(false) }

    // Media chooser launchers
    val avatarPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            currentUser?.let { user ->
                val updated = user.copy(avatarUrl = it.toString())
                viewModel.updateCurrentUser(updated)
            }
        }
    }

    val coverPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            currentUser?.let { user ->
                val updated = user.copy(coverUrl = it.toString())
                viewModel.updateCurrentUser(updated)
            }
        }
    }

    if (isEditingProfile) {
        EditProfileScreen(
            currentUser = currentUser,
            viewModel = viewModel,
            onBackClick = { isEditingProfile = false }
        )
    } else {
        val postsList by viewModel.postsFeed.collectAsStateWithLifecycle()
        val personalList = remember(postsList, currentUser) {
            postsList.filter { it.authorId == currentUser?.userId || (currentUser != null && it.authorName == currentUser.displayName) }
        }
        var selectedSubTab by remember { mutableStateOf("Posts") } // Posts, Photos, Videos
        var selectedPhotoUrlForViewer by remember { mutableStateOf<String?>(null) }
        val allUserPhotos = remember(personalList) {
            personalList.flatMap { post ->
                post.imageUrl.split(",").filter { it.isNotBlank() }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .testTag("profile_tab"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Cover block displaying full image cleanly (without overlap)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val isCoverUri = !currentUser?.coverUrl.isNullOrBlank() && 
                            (currentUser.coverUrl.startsWith("content://") || currentUser.coverUrl.startsWith("http") || currentUser.coverUrl.contains("/"))
                        var coverLoadError by remember(currentUser?.coverUrl) { mutableStateOf(false) }

                        if (isCoverUri && !coverLoadError) {
                            AsyncImage(
                                model = currentUser?.coverUrl,
                                contentDescription = "Cover Backdrop",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                onError = {
                                    coverLoadError = true
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(LavenderPrimary, Color(0xFF1E1E24))
                                        )
                                    )
                            )
                        }

                        // Camera Button on Cover Photo (Inside bottom-right)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(36.dp)
                                .shadow(4.dp, CircleShape)
                                .background(LavenderPrimary, CircleShape)
                                .clickable {
                                    coverPickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change cover photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 1.5 Center Aligned separate profile picture, NOT overlapped with the cover photo
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(130.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(3.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        ) {
                            ProfileAvatar(
                                avatarId = currentUser?.avatarUrl ?: "avatar_user_main",
                                modifier = Modifier.fillMaxSize(),
                                size = 130,
                                fallbackName = currentUser?.displayName,
                                showOnlineStatus = currentUser?.isProfileLocked == false
                            )
                        }

                        // Camera Button on Profile Avatar (Outside bottom-right, slightly overlapping)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(36.dp)
                                .shadow(4.dp, CircleShape)
                                .background(LavenderPrimary, CircleShape)
                                .clickable {
                                    avatarPickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change profile picture",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. Personal identities information details
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentUser?.displayName ?: "Aura Member",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "@${currentUser?.username ?: "user_aura"}",
                        color = LavenderSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Display Followers / Friends and Following simple styled text
                    val displayFollowersCount = currentUser?.followerCount ?: 0
                    val followersLabel = if (currentUser?.isProfileLocked == true) "Friends" else "Followers"
                    Text(
                        text = "$displayFollowersCount $followersLabel  •  ${currentUser?.followingCount ?: 0} Following",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentUser?.bio ?: "Connecting securely on Aura network database system.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2.5 Side-by-side action boxes under the bio
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add to story button (Left)
                    Button(
                        onClick = {
                            viewModel.selectTab(MainTab.CREATE_POST)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF3E8FF), // elegant light lavender background
                            contentColor = LavenderPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to story",
                            tint = Color(0xFF7C4DFF), // bold lavender color for the + icon!
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add to story",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    // Edit profile button (Right)
                    Button(
                        onClick = {
                            isEditingProfile = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEEF2FF), // elegant subtle indigo/blue background
                            contentColor = Color(0xFF1E88E5)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Edit profile",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit profile",
                            tint = Color(0xFFFF4081), // beautiful colorful pink/coral pencil icon!
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2.5 User UID Copy Section
            item {
                val context = androidx.compose.ui.platform.LocalContext.current
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                val uid = currentUser?.resolvedUid ?: ""

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .clickable {
                            val annotatedString = androidx.compose.ui.text.buildAnnotatedString { append(uid) }
                            clipboardManager.setText(annotatedString)
                            android.widget.Toast.makeText(context, "Copied UID: $uid", android.widget.Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3E8FF) // Beautiful light bold lavender container
                    ),
                    border = BorderStroke(1.dp, LavenderPrimary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "User ID",
                                tint = LavenderPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "User ID",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uid,
                                    color = Color.Black,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(LavenderPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy UID",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Copy UID",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Network Statistics panel is removed as requested
            item {
                Divider(color = DividerColor, thickness = 4.dp)
            }

            // 3.5 Personal Social Metadata Dashboard showing all onboarding variables
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Aura Onboarding & Identity Info",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )

                    // 1. Date of Birth
                    ProfileMetaRow(
                        icon = Icons.Default.Cake,
                        label = "Date of Birth",
                        value = currentUser?.birthday ?: "",
                        privacy = currentUser?.birthdayPrivacy ?: "Public",
                        placeholder = "Add Birthday (Skipped)",
                        onAddClick = { viewModel.navigateTo(Screen.BirthdaySelection) }
                    )

                    // 2. Gender Identity
                    val genderVal = if (!currentUser?.gender.isNullOrBlank()) {
                        var finalGenText = currentUser?.gender ?: ""
                        if (finalGenText == "More options" && viewModel.regPronoun.isNotBlank()) {
                             finalGenText += " (Pronoun: ${viewModel.regPronoun})"
                        }
                        finalGenText
                    } else ""
                    ProfileMetaRow(
                        icon = Icons.Default.Face,
                        label = "Gender & Pronoun Info",
                        value = genderVal,
                        privacy = currentUser?.genderPrivacy ?: "Public",
                        placeholder = "Add Gender (Skipped)",
                        onAddClick = { viewModel.navigateTo(Screen.GenderSelection) }
                    )

                    // 3. Relationship status
                    ProfileMetaRow(
                        icon = Icons.Default.Favorite,
                        label = "Relationship status",
                        value = currentUser?.relationshipStatus ?: "",
                        privacy = currentUser?.relationshipPrivacy ?: "Public",
                        placeholder = "Add Relationship Status (Skipped)",
                        onAddClick = { viewModel.navigateTo(Screen.RelationshipSelection) }
                    )

                    // 4. Hometown
                    ProfileMetaRow(
                        icon = Icons.Default.Place,
                        label = "Hometown Place",
                        value = currentUser?.hometown ?: "",
                        privacy = currentUser?.hometownPrivacy ?: "Public",
                        placeholder = "Add Hometown venue (Skipped)",
                        onAddClick = { viewModel.navigateTo(Screen.HometownSelection) }
                    )

                    // 5. Education status
                    val hasEdu = !currentUser?.school.isNullOrBlank() || !currentUser?.college.isNullOrBlank() || !currentUser?.university.isNullOrBlank()
                    val eduString = if (hasEdu) {
                         listOfNotNull(
                             currentUser?.school?.trim()?.takeIf { it.isNotBlank() }?.let { "$it (School)" },
                             currentUser?.college?.trim()?.takeIf { it.isNotBlank() }?.let { "$it (College)" },
                             currentUser?.university?.trim()?.takeIf { it.isNotBlank() }?.let { "$it (University)" }
                         ).joinToString(", ")
                    } else ""
                    ProfileMetaRow(
                        icon = Icons.Default.School,
                        label = "Education",
                        value = eduString,
                        privacy = currentUser?.educationPrivacy ?: "Public",
                        placeholder = "Add School / College / Varsity (Skipped)",
                        onAddClick = { viewModel.navigateTo(Screen.EducationSelection) }
                    )

                    // 6. Hobbies
                    ProfileMetaRow(
                        icon = Icons.Default.Star,
                        label = "Hobbies & Interests",
                        value = currentUser?.hobbies ?: "",
                        privacy = currentUser?.hobbiesPrivacy ?: "Public",
                        placeholder = "Add Hobbies & Interests (Skipped)",
                        onAddClick = { viewModel.navigateTo(Screen.HobbySelection) }
                    )
                }
                Divider(color = DividerColor, thickness = 4.dp)
            }

            // 4. Option Tab Bar: Posts, Photos, Videos
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3E8FF)) // soft lavender background for tab bar
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        Triple("Posts", Icons.Default.Article, Color(0xFF7C4DFF)),
                        Triple("Photos", Icons.Default.Collections, Color(0xFFE00052)),
                        Triple("Videos", Icons.Default.PlayCircle, Color(0xFF00C853))
                    ).forEach { (tabName, icon, tint) ->
                        val isTabSelected = selectedSubTab == tabName
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isTabSelected) Color.White else Color.Transparent)
                                .clickable { selectedSubTab = tabName }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tabName,
                                tint = if (isTabSelected) tint else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tabName,
                                fontSize = 13.sp,
                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTabSelected) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }

            // 5. Content of selected sub-tab (Posts, Photos, Videos)
            if (selectedSubTab == "Posts") {
                item {
                    Text(
                        text = "Your Aura timeline posts",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (personalList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.NoteAlt, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No personal updates posted", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(personalList) { post ->
                        PostCard(post = post, viewModel = viewModel, currentUserId = currentUser?.userId ?: 0)
                    }
                }
            } else if (selectedSubTab == "Photos") {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Uploaded Photos",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Visual highlights of your activity on Aura database",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                if (allUserPhotos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No photos uploaded yet", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    val activeIndex = if (selectedPhotoUrlForViewer != null) allUserPhotos.indexOf(selectedPhotoUrlForViewer) else -1
                    if (selectedPhotoUrlForViewer != null && activeIndex >= 0) {
                        item {
                            FullScreenImageViewer(
                                imageUrls = allUserPhotos,
                                initialIndex = activeIndex,
                                post = PostEntity(
                                    authorId = currentUser?.userId ?: 0,
                                    authorName = currentUser?.displayName ?: "",
                                    authorAvatar = currentUser?.avatarUrl ?: "",
                                    content = "Photo View"
                                ),
                                viewModel = viewModel,
                                onDismiss = { selectedPhotoUrlForViewer = null }
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            allUserPhotos.chunked(3).forEach { rowItems ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    rowItems.forEach { photoUrl ->
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(4.dp)
                                                .clickable { selectedPhotoUrlForViewer = photoUrl },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, DividerColor)
                                        ) {
                                            AsyncImage(
                                                model = photoUrl,
                                                contentDescription = "User Photo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        }
                                    }
                                    // Pad empty space in the row
                                    if (rowItems.size < 3) {
                                        repeat(3 - rowItems.size) {
                                            Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Videos tab
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Recorded Videos",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Shorts and recordings securely synced to your cloud account",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Videocam, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No videos uploaded yet", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    currentUser: UserEntity?,
    viewModel: AuraViewModel,
    onBackClick: () -> Unit
) {
    if (currentUser == null) return

    var editingBio by remember { mutableStateOf(false) }
    var currentBioText by remember { mutableStateOf(currentUser.bio) }

    var editingSchool by remember { mutableStateOf(false) }
    var currentSchoolText by remember { mutableStateOf(currentUser.school ?: "") }

    var editingCollege by remember { mutableStateOf(false) }
    var currentCollegeText by remember { mutableStateOf(currentUser.college ?: "") }

    var editingHometown by remember { mutableStateOf(false) }
    var currentHometownText by remember { mutableStateOf(currentUser.hometown ?: "") }

    var editingRelationship by remember { mutableStateOf(currentUser.relationshipStatus ?: "Single") }
    var showLockDialog by remember { mutableStateOf(false) }
    var showUnlockDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBackClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- 1. Custom Header matching screens with crisp white typography and bold lavender buttons ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LavenderPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Divider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // --- 2. Edit items in Scrollable Column ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
        ) {
            // Section A: Profile Picture
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile picture",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Edit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            modifier = Modifier.clickable {
                                viewModel.navigateTo(Screen.ProfilePictureSelection)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .shadow(2.dp, CircleShape)
                                .border(2.dp, LavenderPrimary.copy(alpha = 0.3f), CircleShape)
                        ) {
                            ProfileAvatar(
                                avatarId = currentUser.avatarUrl ?: "avatar_user_main",
                                modifier = Modifier.fillMaxSize(),
                                size = 110,
                                fallbackName = currentUser.displayName
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6), thickness = 1.6.dp)
                }
            }

            // Section B: 3D Avatar character card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Avatar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Only you can view this section",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "Edit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            modifier = Modifier.clickable {
                                viewModel.navigateTo(Screen.ProfilePictureSelection)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing elegant, stylized vector profile character waving (in beanie & leather jacket)
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFAFAFA))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Dynamic vector portrait drawing
                                Canvas(modifier = Modifier.size(85.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    // Face
                                    drawCircle(
                                        color = Color(0xFFFFCCAC), // healthy skin
                                        radius = w * 0.32f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.45f)
                                    )
                                    // Beanie (Hat) on top of face
                                    val beaniePath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.15f, h * 0.40f)
                                        cubicTo(w * 0.15f, h * 0.05f, w * 0.85f, h * 0.05f, w * 0.85f, h * 0.40f)
                                        close()
                                    }
                                    drawPath(beaniePath, color = Color(0xFF1E1F22)) // dark cool beanie
                                    // Beanie fold
                                    drawRoundRect(
                                        color = Color(0xFF2B2D31),
                                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.12f, h * 0.32f),
                                        size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.10f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                                    )
                                    // Friendly black sunglasses or cartoon eyes
                                    drawCircle(
                                        color = Color.Black,
                                        radius = 5f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.40f, h * 0.45f)
                                    )
                                    drawCircle(
                                        color = Color.Black,
                                        radius = 5f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.60f, h * 0.45f)
                                    )
                                    // Smiling mouth
                                    val smilePath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.43f, h * 0.54f)
                                        quadraticTo(w * 0.50f, h * 0.62f, w * 0.57f, h * 0.54f)
                                    }
                                    drawPath(
                                        path = smilePath,
                                        color = Color(0xFFE25C5F),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                                    )
                                    // Cool leather jacket shoulders / body
                                    val bodyPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.20f, h * 0.75f)
                                        lineTo(w * 0.10f, h * 1.0f)
                                        lineTo(w * 0.90f, h * 1.0f)
                                        lineTo(w * 0.80f, h * 0.75f)
                                        close()
                                    }
                                    drawPath(bodyPath, color = Color(0xFF1E1F22))
                                    // Zipper line
                                    drawLine(
                                        color = Color(0xFFFFD54F),
                                        start = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.75f),
                                        end = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 1.0f),
                                        strokeWidth = 3f
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Face, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Waving Persona Active",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6), thickness = 1.6.dp)
                }
            }

            // Section C: Cover Photo
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cover photo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Edit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            modifier = Modifier.clickable {
                                viewModel.navigateTo(Screen.CoverPhotoSelection)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val isCoverUri = !currentUser.coverUrl.isNullOrBlank() && 
                            (currentUser.coverUrl.startsWith("content://") || currentUser.coverUrl.startsWith("http") || currentUser.coverUrl.contains("/"))
                        var coverLoadError by remember(currentUser.coverUrl) { mutableStateOf(false) }

                        if (isCoverUri && !coverLoadError) {
                            AsyncImage(
                                model = currentUser.coverUrl,
                                contentDescription = "Cover Backdrop",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                onError = {
                                    coverLoadError = true
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(LavenderPrimary, Color(0xFF1E1E24))
                                        )
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6), thickness = 1.6.dp)
                }
            }

            // Section D: Bio
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Edit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            modifier = Modifier.clickable {
                                editingBio = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentUser.bio.ifBlank { "Describe yourself..." },
                        fontSize = 15.sp,
                        color = if (currentUser.bio.isBlank()) Color.Gray else Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6), thickness = 1.6.dp)
                }
            }

            // Section D.2: Lock Profile / Unlock Profile
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    val isLocked = currentUser.isProfileLocked
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile lock settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(
                            modifier = Modifier
                                .background(
                                    if (isLocked) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock Status",
                                tint = if (isLocked) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLocked) "Locked" else "Public",
                                color = if (isLocked) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLocked) Color(0xFFF3E8FF) else Color(0xFFFAFAFA)
                        ),
                        border = BorderStroke(1.dp, if (isLocked) LavenderPrimary.copy(alpha = 0.3f) else Color(0xFFE5E7EB))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = if (isLocked) "Secure shielding is active" else "Profile public to everyone",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isLocked) {
                                    "Only your friends can find your handles, user ID (UID), look at your posts, photos, videos, bio, and onboarding statistics detail."
                                } else {
                                    "Anyone can view your daily stories, updates, search results handle details, active status indicators, and subscribe as a follower."
                                },
                                color = Color.Gray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Button(
                                onClick = {
                                    if (isLocked) {
                                        showUnlockDialog = true
                                    } else {
                                        showLockDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLocked) Color.White else LavenderPrimary,
                                    contentColor = if (isLocked) LavenderPrimary else Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = if (isLocked) BorderStroke(1.dp, LavenderPrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = "Lock action",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isLocked) "Unlock Profile" else "Lock Profile Name",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6), thickness = 1.6.dp)
                }
            }

            // Section E: Details list exactly mimicking user profile setup details list!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Edit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary,
                            modifier = Modifier.clickable {
                                editingSchool = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Exactly matching bullet list items with correct visual decorations
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val detailsItems = mutableListOf<Pair<androidx.compose.ui.graphics.vector.ImageVector, String>>().apply {
                            // Always present: profile type row
                            add(Pair(Icons.Default.Badge, "Profile · Digital creator"))
                            
                            // High school (school field in database)
                            val schoolText = currentUser.school?.trim() ?: ""
                            if (schoolText.isNotBlank()) {
                                add(Pair(Icons.Default.School, "Went to $schoolText"))
                            }
                            
                            // College (college field in database)
                            val collegeText = currentUser.college?.trim() ?: ""
                            if (collegeText.isNotBlank()) {
                                add(Pair(Icons.Default.School, "Studied at $collegeText"))
                            }
                            
                            // University (university field in database)
                            val uniText = currentUser.university?.trim() ?: ""
                            if (uniText.isNotBlank()) {
                                add(Pair(Icons.Default.School, "Studying at $uniText"))
                            }
                            
                            // Hometown (hometown field in database)
                            val hometownText = currentUser.hometown?.trim() ?: ""
                            if (hometownText.isNotBlank()) {
                                add(Pair(Icons.Default.Place, "From $hometownText"))
                            }
                            
                            // Relationship Status (relationshipStatus field in database)
                            val relStatus = currentUser.relationshipStatus?.trim() ?: ""
                            if (relStatus.isNotBlank()) {
                                add(Pair(Icons.Default.Favorite, relStatus))
                            }
                            
                            // Birthday (birthday field in database)
                            val birthdayText = currentUser.birthday?.trim() ?: ""
                            if (birthdayText.isNotBlank()) {
                                add(Pair(Icons.Default.Cake, "Born $birthdayText"))
                            }

                            // Gender (gender field in database)
                            val genderText = currentUser.gender?.trim() ?: ""
                            if (genderText.isNotBlank()) {
                                var finalGenText = genderText
                                if (finalGenText == "More options" && viewModel.regPronoun.isNotBlank()) {
                                    finalGenText += " (Pronoun: ${viewModel.regPronoun})"
                                }
                                add(Pair(Icons.Default.Face, "Gender: $finalGenText"))
                            }

                            // Hobbies (hobbies field in database)
                            val hobbiesText = currentUser.hobbies?.trim() ?: ""
                            if (hobbiesText.isNotBlank()) {
                                add(Pair(Icons.Default.Star, "Hobbies: $hobbiesText"))
                            }
                            
                            // Joined Date (using database saved date or system date formatted with day, month and year under CalendarMonth icon as requested)
                            val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH)
                            val joinDateFormatted = if (!currentUser.joinedDate.isNullOrBlank()) {
                                "Joined ${currentUser.joinedDate}"
                            } else {
                                "Joined ${sdf.format(java.util.Date(System.currentTimeMillis()))}"
                            }
                            add(Pair(Icons.Default.CalendarMonth, joinDateFormatted))
                        }

                        detailsItems.forEach { (icon, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Interactive Editing Dialogs ---
    if (editingBio) {
        AlertDialog(
            onDismissRequest = { editingBio = false },
            title = { Text("Edit Bio", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = currentBioText,
                    onValueChange = { currentBioText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Bio") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LavenderPrimary,
                        focusedLabelColor = LavenderPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCurrentUser(currentUser.copy(bio = currentBioText))
                        editingBio = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBio = false }) {
                    Text("Cancel", color = LavenderPrimary)
                }
            }
        )
    }

    if (editingSchool) {
        AlertDialog(
            onDismissRequest = { editingSchool = false },
            title = { Text("Edit Education Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = currentSchoolText,
                        onValueChange = { currentSchoolText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("High School") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LavenderPrimary)
                    )
                    OutlinedTextField(
                        value = currentCollegeText,
                        onValueChange = { currentCollegeText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("College") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LavenderPrimary)
                    )
                    OutlinedTextField(
                        value = currentHometownText,
                        onValueChange = { currentHometownText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hometown") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LavenderPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCurrentUser(
                            currentUser.copy(
                                school = currentSchoolText,
                                college = currentCollegeText,
                                hometown = currentHometownText
                            )
                        )
                        editingSchool = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSchool = false }) {
                    Text("Cancel", color = LavenderPrimary)
                }
            }
        )
    }

    if (showLockDialog) {
        AlertDialog(
            onDismissRequest = { showLockDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, "Lock", tint = LavenderPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lock Your Profile?", fontWeight = FontWeight.Black)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to lock your profile?\n\nIf Yes, other users will only see your name, profile picture and cover photo, and can send you messages or friend requests. If No, this action will be canceled.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCurrentUser(currentUser.copy(isProfileLocked = true))
                        showLockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                ) {
                    Text("Yes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLockDialog = false }) {
                    Text("No", color = LavenderPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showUnlockDialog) {
        AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockOpen, "Unlock", tint = LavenderPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock Your Profile?", fontWeight = FontWeight.Black)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to unlock your profile?\n\nThis will make your timeline, posts, active indicators, and handles completely public and viewable to everybody on the network.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCurrentUser(currentUser.copy(isProfileLocked = false))
                        showUnlockDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
                ) {
                    Text("Yes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog = false }) {
                    Text("No", color = LavenderPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --- SUB-TABS: DATABASE/APPWRITE SETTINGS CONSOLE PANEL ---
@Composable
fun AppwriteConsoleTab(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("appwrite_console_tab"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF19191E)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, LavenderPrimary)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayCircle, null, tint = OnlineGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "APPWRITE SYNC ACTIVE",
                            color = OnlineGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE00052))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("LIFETIME FREE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Aura Appwrite Sync Engine",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your free cloud Appwrite database is configured and fully running. Below are active system bindings with package identifier com.imrul.aura.",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Color.DarkGray)
            }

            // System specs block
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    val resolvedEndpoint = viewModel.cleanAppwriteEndpoint
                    val resolvedProject = viewModel.cleanAppwriteProjectId
                    val realtimeSocket = if (resolvedEndpoint.startsWith("https://")) {
                        resolvedEndpoint.replace("https://", "wss://") + "/realtime"
                    } else if (resolvedEndpoint.startsWith("http://")) {
                        resolvedEndpoint.replace("http://", "ws://") + "/realtime"
                    } else {
                        "wss://cloud.appwrite.io/v1/realtime"
                    }
                    
                    listOf(
                        "Active Appwrite Endpoint" to resolvedEndpoint,
                        "Active Project ID" to resolvedProject,
                        "Active Database ID" to viewModel.cleanAppwriteDatabaseId,
                        "Active Storage Bucket ID" to viewModel.cleanAppwriteBucketId,
                        "Realtime wss Connection" to realtimeSocket,
                        "Client Package Name" to "com.imrul.aura",
                        "Appwrite SDK Core Version" to "5.0.0"
                    ).forEach { (field, valText) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(field, color = Color.Gray, fontSize = 12.sp)
                            Text(valText, color = LavenderLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Divider(color = Color.DarkGray)
            }

            // Edit Credentials form block
            item {
                var editEndpoint by remember { mutableStateOf(viewModel.cleanAppwriteEndpoint) }
                var editProject by remember { mutableStateOf(viewModel.cleanAppwriteProjectId) }
                var editDatabaseId by remember { mutableStateOf(viewModel.cleanAppwriteDatabaseId) }
                var editBucketId by remember { mutableStateOf(viewModel.cleanAppwriteBucketId) }
                var saveMessage by remember { mutableStateOf("") }
                var isSaving by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Customize Appwrite Connection",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "You can directly enter and connect your custom Appwrite Endpoint, Project ID, Database ID, and Storage Bucket ID below.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = editEndpoint,
                        onValueChange = { editEndpoint = it },
                        label = { Text("Appwrite Endpoint", color = Color.Gray) },
                        placeholder = { Text("e.g. https://cloud.appwrite.io/v1") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderPrimary,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("appwrite_endpoint_input")
                    )

                    OutlinedTextField(
                        value = editProject,
                        onValueChange = { editProject = it },
                        label = { Text("Appwrite Project ID", color = Color.Gray) },
                        placeholder = { Text("e.g. 6a1aa78d0...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderPrimary,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("appwrite_project_id_input")
                    )

                    OutlinedTextField(
                        value = editDatabaseId,
                        onValueChange = { editDatabaseId = it },
                        label = { Text("Appwrite Database ID", color = Color.Gray) },
                        placeholder = { Text("e.g. 6a1ef1cc00131091b90a") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderPrimary,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("appwrite_database_id_input")
                    )

                    OutlinedTextField(
                        value = editBucketId,
                        onValueChange = { editBucketId = it },
                        label = { Text("Appwrite Storage Bucket ID", color = Color.Gray) },
                        placeholder = { Text("e.g. 6a1ef1e8003dbe01e5c8") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderPrimary,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("appwrite_bucket_id_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            isSaving = true
                            val success = viewModel.updateAppwriteSettings(editEndpoint, editProject, editDatabaseId, editBucketId)
                            isSaving = false
                            saveMessage = if (success) {
                                "Appwrite configuration updated successfully! Re-initializing SDK."
                            } else {
                                "Error: Fields cannot be empty."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_appwrite_config_btn")
                    ) {
                        Text("Save Configurations & Connect", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.resetAppwriteSettingsToDefault()
                            editEndpoint = viewModel.cleanAppwriteEndpoint
                            editProject = viewModel.cleanAppwriteProjectId
                            editDatabaseId = viewModel.cleanAppwriteDatabaseId
                            editBucketId = viewModel.cleanAppwriteBucketId
                            saveMessage = "Appwrite configurations reset to production defaults!"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("reset_appwrite_config_btn")
                    ) {
                        Text("Reset to Production Defaults", fontWeight = FontWeight.Normal, fontSize = 13.sp)
                    }

                    if (saveMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = saveMessage,
                            color = if (saveMessage.startsWith("Error")) Color.Red else OnlineGreen,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Divider(color = Color.DarkGray)
            }

            // Custom Secure Storage Alternative Info Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141418)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE00052).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = "Secure Storage",
                                tint = Color(0xFFE00052),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIFETIME SECURE FREE STORAGE PROVIDER",
                                color = Color(0xFFFFB2C0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To guarantee lifetime free & completely secure media hosting, Aura bypasses premium bucket storage fees by transforming dynamic photos into highly optimized Base64 byte strings stored in local SQLite client sandboxes. This operates offline with 0ms network lag & infinite free bandwidth, maintaining extreme high-speed performance even under low-latency subways or 2G networks!",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
                Divider(color = Color.DarkGray)
            }

            // raw appwrite.json viewer code collapsible trigger
            item {
                Button(
                    onClick = { viewModel.toggleAppwriteDetails() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2D35)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("toggle_appwrite_code_btn")
                ) {
                    Icon(Icons.Default.Code, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (state.showAppwriteDetails) "Minimize Client JSON" else "View appwrite.json Code",
                        color = Color.White
                    )
                }

                if (state.showAppwriteDetails) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color.Black)
                            .border(1.dp, Color.DarkGray)
                            .padding(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = """
{
  "projectId": "aura-25f38789b2d",
  "projectName": "Aura Network Platform",
  "services": {
    "auth": "disabled",
    "databases": "enabled",
    "storage": "enabled",
    "functions": "disabled"
  },
  "security": {
    "ssl": true,
    "selfSigned": true
  }
}
                                    """.trimIndent(),
                                    color = Color(0xFFE00052),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    if (diff < 0) return "Just now"
    val diffSec = diff / 1000
    val diffMin = diffSec / 60
    val diffHr = diffMin / 60
    val diffDay = diffHr / 24
    return when {
        diffDay > 0 -> "${diffDay}d ago"
        diffHr > 0 -> "${diffHr}h ago"
        diffMin > 0 -> "${diffMin}m ago"
        else -> "Just now"
    }
}

fun getReactionSummaryEmojis(post: PostEntity): List<String> {
    val list = mutableListOf<String>()
    if (post.userReaction.isNotEmpty()) {
        list.add(post.userReaction)
    }
    val hash = post.postId
    val extra1 = when (hash % 6) {
        0 -> "❤️"
        1 -> "🥰"
        2 -> "😄"
        3 -> "😮"
        4 -> "😢"
        else -> "👍"
    }
    val extra2 = when ((hash + 1) % 6) {
        0 -> "🥰"
        1 -> "😮"
        2 -> "😢"
        3 -> "👍"
        4 -> "❤️"
        else -> "😄"
    }
    if (!list.contains(extra1)) list.add(extra1)
    if (list.size < 3 && !list.contains(extra2)) list.add(extra2)
    return list.take(3)
}

@Composable
fun BottomSheetOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// --- SHARED SCREEN LAYER: FEED POST CARD WIDGET ---
@Composable
fun PostCard(
    post: PostEntity,
    viewModel: AuraViewModel,
    currentUserId: Int
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showShareSimulationDialog by remember { mutableStateOf(false) }
    val checkInteractions = { onSucceed: () -> Unit ->
        if (state.isViewingAsGuest) {
            android.widget.Toast.makeText(
                context, 
                "You must create a profile and log in to interact with this post! 💜🔐", 
                android.widget.Toast.LENGTH_LONG
            ).show()
        } else {
            onSucceed()
        }
    }

    var showMenuSheet by remember { mutableStateOf(false) }
    var showReactionsPanel by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .testTag("post_card_${post.postId}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column {
            // Header Row (Author info)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val usersList by viewModel.allUsers.collectAsStateWithLifecycle()
                val authorUser = remember(usersList, post.authorId) {
                    usersList.find { it.userId == post.authorId }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = authorUser != null) {
                            if (authorUser != null) {
                                viewModel.navigateTo(Screen.ProfileDetail(authorUser))
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarId = post.authorAvatar,
                        fallbackName = post.authorName,
                        showOnlineStatus = authorUser?.isProfileLocked == false
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(post.authorName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        val mentionedUsers = remember(usersList, post.mentionedUserIds) {
                            if (post.mentionedUserIds.isBlank()) emptyList()
                            else {
                                val ids = post.mentionedUserIds.split(",").mapNotNull { it.trim().toIntOrNull() }
                                usersList.filter { it.userId in ids }
                            }
                        }
                        
                        if (mentionedUsers.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 1.dp, bottom = 1.dp)
                            ) {
                                Text(
                                    text = "with " + mentionedUsers.joinToString(", ") { it.displayName },
                                    color = Color(0xFF7C4DFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Text(formatRelativeTime(post.timestamp), color = TextSecondary, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.size(3.dp).background(Color.Gray, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Public, null, tint = Color.Gray, modifier = Modifier.size(10.dp))
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showMenuSheet = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.MoreHoriz, "Options", tint = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { viewModel.deletePost(post.postId) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = Color.DarkGray)
                    }
                }
            }

            if (post.isAiLabeled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .background(Color(0xFFF1EFF9), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE1DFF1), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI content marker",
                        tint = Color(0xFF7C4DFF),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "This content is AI related",
                        color = Color(0xFF7C4DFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Post content section (Handles beautiful dynamic gradient overlays for text status values!)
            if (post.gradientIndex >= 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(PremiumGradients[post.gradientIndex])
                        .clickable { checkInteractions { viewModel.toggleLike(post) } }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.getTranslatedText(post.content),
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(Color.Black, Offset(1f, 1f), 3f)
                        )
                    )
                }
            } else {
                Column {
                    var isExpanded by remember { mutableStateOf(false) }
                    val rawText = viewModel.getTranslatedText(post.content)
                    val linesLimit = 3
                    
                    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                        Text(
                            text = rawText,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            maxLines = if (isExpanded) Int.MAX_VALUE else linesLimit,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (rawText.length > 120 || rawText.count { it == '\n' } >= linesLimit) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isExpanded) "See less" else "See more",
                                color = Color(0xFF7C4DFF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable { isExpanded = !isExpanded }
                            )
                        }
                    }

                    // Multi-image Collage rendering with click to view full screen as requested
                    val isUrlValid = post.imageUrl.isNotBlank() && (
                        post.imageUrl.contains("/") ||
                        post.imageUrl.startsWith("http", ignoreCase = true) ||
                        post.imageUrl.startsWith("content", ignoreCase = true)
                    )
                    if (isUrlValid) {
                        var activeFullImageViewerIndex by remember { mutableStateOf<Int?>(null) }
                        
                        PostCollage(
                            imageUrlString = post.imageUrl,
                            post = post,
                            viewModel = viewModel,
                            onImageClick = { index ->
                                activeFullImageViewerIndex = index
                            }
                        )

                        activeFullImageViewerIndex?.let { index ->
                            FullScreenImageViewer(
                                imageUrls = post.imageUrl.split(",").filter { it.isNotBlank() },
                                initialIndex = index,
                                post = post,
                                viewModel = viewModel,
                                onDismiss = { activeFullImageViewerIndex = null }
                            )
                        }
                    }
                }
            }

            // Custom modern reactions count summary row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Comments & Shares info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment, 
                        contentDescription = null, 
                        tint = Color.Gray, 
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.commentCount} comments", 
                        color = TextSecondary, 
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(2.dp).background(Color.Gray, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Outlined.Share, 
                        contentDescription = null, 
                        tint = Color.Gray, 
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.shareCount} shares", 
                        color = TextSecondary, 
                        fontSize = 11.sp
                    )
                }

                // Right side: Emojis reaction summary (like 1st image)
                if (post.likeCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        val summaryEmojis = getReactionSummaryEmojis(post)
                        // Overlapping circular emoji items
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-4).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            summaryEmojis.forEach { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color(0xFFEEEEEE), CircleShape)
                                        .border(0.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${post.likeCount}", 
                            color = TextPrimary, 
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Divider(color = DividerColor)

            // Interaction Bar (Reactions list triggers)
            Row(
                modifier = Modifier.fillMaxWidth().height(42.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / long-press button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .combinedClickable(
                                onClick = { checkInteractions { viewModel.toggleLike(post) } },
                                onLongClick = { checkInteractions { showReactionsPanel = true } }
                            )
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hasReaction = post.userReaction.isNotEmpty()
                        val buttonColor = if (hasReaction) {
                            when (post.userReaction) {
                                "👍" -> Color(0xFF1877F2)
                                "❤️" -> Color(0xFFE0245E)
                                "🥰" -> Color(0xFFF7B928)
                                "😄" -> Color(0xFFF7B928)
                                "😮" -> Color(0xFFF7B928)
                                "😢" -> Color(0xFFF7B928)
                                "😡" -> Color(0xFFF24E1E)
                                else -> LavenderPrimary
                            }
                        } else {
                            Color.DarkGray
                        }
                        if (hasReaction) {
                            Text(
                                text = post.userReaction,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.ThumbUp,
                                contentDescription = "Like",
                                tint = buttonColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasReaction) {
                                when (post.userReaction) {
                                    "👍" -> "Like"
                                    "❤️" -> "Love"
                                    "🥰" -> "Care"
                                    "😄" -> "Haha"
                                    "😮" -> "Wow"
                                    "😢" -> "Sad"
                                    "😡" -> "Angry"
                                    else -> "Like"
                                }
                            } else "Like",
                            color = buttonColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reactions Popup positioned relatively above the button
                    if (showReactionsPanel) {
                        androidx.compose.ui.window.Popup(
                            alignment = Alignment.TopCenter,
                            offset = androidx.compose.ui.unit.IntOffset(x = 0, y = -120),
                            onDismissRequest = { showReactionsPanel = false }
                        ) {
                            Card(
                                modifier = Modifier
                                    .shadow(8.dp, RoundedCornerShape(32.dp))
                                    .border(1.dp, LavenderPrimary.copy(alpha = 0.5f), RoundedCornerShape(32.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(32.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val emojiList = listOf("👍", "❤️", "🥰", "😄", "😮", "😢", "😡")
                                    emojiList.forEach { emoji ->
                                        var isClickedState by remember { mutableStateOf(false) }
                                        val emojiScale by animateFloatAsState(
                                            targetValue = if (isClickedState) 1.5f else 1.0f
                                        )
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer(scaleX = emojiScale, scaleY = emojiScale)
                                                .clickable {
                                                    isClickedState = true
                                                    checkInteractions {
                                                        viewModel.selectReaction(post, emoji)
                                                    }
                                                    showReactionsPanel = false
                                                }
                                                .padding(4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = emoji,
                                                fontSize = 22.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { checkInteractions { viewModel.openCommentsForPost(post.postId) } }
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ModeComment, null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Comment", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { 
                            // Direct social-optimized share using Appwrite dynamic open-graph redirection
                            try {
                                val savedUrl = context.getSharedPreferences("AuraPrefs", android.content.Context.MODE_PRIVATE)
                                    .getString("appwrite_url", "") ?: ""
                                val baseFunctionUrl = if (savedUrl.isNotBlank()) savedUrl.trim() else "https://ais-pre-inawwf2545flos3colouiz-78211575748.asia-southeast1.run.app"
                                val cleanBaseUrl = if (baseFunctionUrl.endsWith("/")) baseFunctionUrl.dropLast(1) else baseFunctionUrl

                                val rawImage = post.imageUrl.split(",").firstOrNull { it.isNotBlank() } ?: ""
                                val isLocalUri = rawImage.startsWith("content://") || rawImage.startsWith("file://") || !rawImage.startsWith("http")
                                val elegantLavenderBackdrop = if (isLocalUri || rawImage.isBlank()) {
                                    "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80"
                                } else {
                                    rawImage
                                }

                                val nameB64 = android.util.Base64.encodeToString(
                                    (post.authorName).toByteArray(Charsets.UTF_8),
                                    android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE
                                )
                                val titleB64 = android.util.Base64.encodeToString(
                                    ("${post.authorName}'s Aura Post").toByteArray(Charsets.UTF_8),
                                    android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE
                                )
                                val descB64 = android.util.Base64.encodeToString(
                                    (post.content).toByteArray(Charsets.UTF_8),
                                    android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE
                                )
                                val imgB64 = android.util.Base64.encodeToString(
                                    elegantLavenderBackdrop.toByteArray(Charsets.UTF_8),
                                    android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE
                                )

                                val shareableLink = "$cleanBaseUrl?postId=${post.postId}&n=$nameB64&t=$titleB64&d=$descB64&i=$imgB64"

                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT, 
                                        "Check out ${post.authorName}'s post on Aura! 💜✨\n\n\"${post.content}\"\n\n🔗 View Post & Photo:\n$shareableLink\n\n📥 Download the official APK here:\nhttps://ais-pre-inawwf2545flos3colouiz-78211575748.asia-southeast1.run.app"
                                    )
                                }
                                val chooser = android.content.Intent.createChooser(shareIntent, "Share Post")
                                chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(chooser)
                            } catch(e: Exception) {}
                        }
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Share, null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                if (false) {
                    androidx.compose.ui.window.Dialog(
                        onDismissRequest = { showShareSimulationDialog = false },
                        properties = androidx.compose.ui.window.DialogProperties(
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .fillMaxHeight(0.92f)
                                .padding(12.dp)
                                .shadow(16.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF7C4DFF).copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Close action with header
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Share, 
                                            contentDescription = null, 
                                            tint = Color(0xFF7C4DFF), 
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Aura Smart Share Portal", 
                                            fontWeight = FontWeight.ExtraBold, 
                                            fontSize = 18.sp, 
                                            color = Color(0xFF7C4DFF)
                                        )
                                    }
                                    IconButton(
                                        onClick = { showShareSimulationDialog = false },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close, 
                                            contentDescription = "Close", 
                                            tint = Color.DarkGray
                                        )
                                    }
                                }

                                var activeTab by remember { mutableStateOf(0) }
                                
                                // Clean, Colorful Tab Selection
                                TabRow(
                                    selectedTabIndex = activeTab,
                                    containerColor = Color(0xFFF3E8FF),
                                    contentColor = Color(0xFF7C4DFF),
                                    modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                                ) {
                                    Tab(
                                        selected = activeTab == 0,
                                        onClick = { activeTab = 0 },
                                        text = { Text("WhatsApp Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )
                                    Tab(
                                        selected = activeTab == 1,
                                        onClick = { activeTab = 1 },
                                        text = { Text("Android Open-With", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )
                                    Tab(
                                        selected = activeTab == 2,
                                        onClick = { activeTab = 2 },
                                        text = { Text("Facebook Feed", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Content Area
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    when (activeTab) {
                                        0 -> {
                                            // Tab 0: WhatsApp Link Preview Simulator (Screenshots 1 & 2)
                                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                                Text(
                                                     text = "WhatsApp Chat Interface Simulator",
                                                     fontWeight = FontWeight.Bold,
                                                     fontSize = 13.sp,
                                                     color = Color.DarkGray,
                                                     modifier = Modifier.padding(bottom = 6.dp)
                                                )
                                                
                                                // Phone Container Replica
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(310.dp)
                                                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F171C)) // WhatsApp Dark Chat BG
                                                ) {
                                                    Column(modifier = Modifier.fillMaxSize()) {
                                                        // Internal simulated header representing Screenshot 1 & 2
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .background(Color(0xFF1F2C34))
                                                                .padding(10.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.ArrowBack, 
                                                                contentDescription = null, 
                                                                tint = Color.White,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            // Avatar circle containing IMRul signature
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .background(Color(0xFF7C4DFF), RoundedCornerShape(16.dp))
                                                                    .padding(4.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text("IH", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text("IMRuL HASAN (You)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                                Text("online", color = Color(0xFF25D366), fontSize = 10.sp)
                                                            }
                                                            Icon(
                                                                imageVector = Icons.Default.MoreVert, 
                                                                contentDescription = null, 
                                                                tint = Color.White,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }

                                                        // Chat Body with simulated Link Card Bubble
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .fillMaxWidth()
                                                                .padding(12.dp),
                                                            contentAlignment = Alignment.BottomEnd
                                                        ) {
                                                            // Shared Card bubble matching Screenshot 2
                                                            Column(
                                                                modifier = Modifier
                                                                    .fillMaxWidth(0.92f)
                                                                    .background(Color(0xFF1F2C34), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 0.dp))
                                                                    .border(1.dp, Color(0xFF2E3B43), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 0.dp))
                                                                    .padding(6.dp)
                                                            ) {
                                                                // Thumbnail media block (Optimized / Recognized)
                                                                val imageUrl = post.imageUrl.split(",").firstOrNull { it.isNotBlank() }
                                                                if (imageUrl != null) {
                                                                    AsyncImage(
                                                                        model = imageUrl,
                                                                        contentDescription = "Post preview image",
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .height(130.dp)
                                                                            .clip(RoundedCornerShape(8.dp)),
                                                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                                    )
                                                                } else {
                                                                    // Glowing Aesthetic placeholder gradient
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .height(130.dp)
                                                                            .clip(RoundedCornerShape(8.dp))
                                                                            .background(
                                                                                androidx.compose.ui.graphics.Brush.linearGradient(
                                                                                    colors = listOf(Color(0xFF7C4DFF), Color(0xFFD3C5FF))
                                                                                )
                                                                            ),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = "Aura Card", 
                                                                            color = Color.White, 
                                                                            fontWeight = FontWeight.Bold, 
                                                                            fontSize = 16.sp
                                                                        )
                                                                    }
                                                                }
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                
                                                                Text(
                                                                    text = "aura.com/shared_post/${post.postId}",
                                                                    color = Color(0xFFA5B4FC),
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.SemiBold
                                                                )
                                                                Text(
                                                                    text = post.content.take(75) + if (post.content.length > 75) "..." else "",
                                                                    color = Color.White,
                                                                    fontSize = 12.sp,
                                                                    maxLines = 2,
                                                                    lineHeight = 15.sp,
                                                                    fontWeight = FontWeight.Normal,
                                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                                )
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Share, 
                                                                            contentDescription = null, 
                                                                            tint = Color(0xFF7C4DFF),
                                                                            modifier = Modifier.size(12.dp)
                                                                        )
                                                                        Spacer(modifier = Modifier.width(4.dp))
                                                                        Text("facebook.com", color = Color.Gray, fontSize = 9.sp)
                                                                    }
                                                                    Text("1:30 AM ✔✔", color = Color.Gray, fontSize = 9.sp)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                // Quick copy content matching direct custom scheme setup
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    border = BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.2f)),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text(
                                                            text = "Direct Entry Configured Links:",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = Color(0xFF7C4DFF)
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "🌐 Web Link: https://aura.com/shared_post/${post.postId}\n🔮 App Scheme: aura://post/${post.postId}",
                                                            fontSize = 11.sp,
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                            color = Color.DarkGray
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                // Copy all action buttons
                                                Button(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                        val imgUrls = if (post.imageUrl.isNotBlank()) post.imageUrl.split(",").filter { it.isNotBlank() } else emptyList()
                                                        val imgSection = if (imgUrls.isNotEmpty()) "\n📸 Photos:\n" + imgUrls.joinToString("\n") else ""
                                                        val clipboardText = "Check out ${post.authorName}'s post on Aura! 💜✨\n\nContent: \"${post.content}\"\n$imgSection\n\n🌐 Open in Aura (Web): https://aura.com/shared_post/${post.postId}\n🔮 Open Direct in Aura (APK): aura://post/${post.postId}"
                                                        val clip = android.content.ClipData.newPlainText("Aura Post Link", clipboardText)
                                                        clipboard.setPrimaryClip(clip)
                                                        android.widget.Toast.makeText(context, "Copied formatted link and custom scheme!", android.widget.Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Copy Share Package 📋🔗", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                OutlinedButton(
                                                    onClick = {
                                                        try {
                                                            val imgUrls = if (post.imageUrl.isNotBlank()) post.imageUrl.split(",").filter { it.isNotBlank() } else emptyList()
                                                            val imgSection = if (imgUrls.isNotEmpty()) "\n📸 Photos:\n" + imgUrls.joinToString("\n") else ""
                                                            val shareIntent = android.content.Intent().apply {
                                                                action = android.content.Intent.ACTION_SEND
                                                                type = "text/plain"
                                                                putExtra(
                                                                    android.content.Intent.EXTRA_TEXT,
                                                                    "Check out ${post.authorName}'s post on Aura! 💜✨\n\nContent: \"${post.content}\"\n$imgSection\n\n🌐 Open in Aura Web: https://aura.com/shared_post/${post.postId}\n🔮 Open Direct APK Entry: aura://post/${post.postId}"
                                                                )
                                                            }
                                                            val chooser = android.content.Intent.createChooser(shareIntent, "Share Post")
                                                            chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            context.startActivity(chooser)
                                                        } catch(e: Exception) {}
                                                    },
                                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                                    border = BorderStroke(1.dp, Color(0xFF7C4DFF)),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("Share via WhatsApp / External Apps 📡", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                        }

                                        1 -> {
                                            // Tab 1: Launcher Open-With Dialog Simulator (Screenshot 3)
                                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                                Text(
                                                    text = "Android OS 'Open With...' Launcher Simulator",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color.DarkGray,
                                                    modifier = Modifier.padding(bottom = 6.dp)
                                                )

                                                Text(
                                                    text = "Select 'Aura App' inside this simulated system dialog to natively enter the post immediately!",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray,
                                                    modifier = Modifier.padding(bottom = 12.dp)
                                                )

                                                // System Dialog Emulator Panel matching Screenshot 3
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                                                    shape = RoundedCornerShape(20.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5)),
                                                    border = BorderStroke(1.dp, Color.LightGray)
                                                ) {
                                                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                                        Text(
                                                            text = "Open With", 
                                                            fontSize = 16.sp, 
                                                            fontWeight = FontWeight.ExtraBold, 
                                                            color = Color.Black,
                                                            modifier = Modifier.padding(bottom = 16.dp)
                                                        )

                                                        // Interactive layout grid selector (Apps list)
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceAround
                                                        ) {
                                                            // Choice 1: Aura Mobile App
                                                            Column(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .clickable {
                                                                        // Execute launcher deep link immediately
                                                                        viewModel.handleDeepLinkPost(post.postId)
                                                                        showShareSimulationDialog = false
                                                                        android.widget.Toast.makeText(context, "Direct Entry Succeeded! 💜 Direct launching local APK post ${post.postId}", android.widget.Toast.LENGTH_LONG).show()
                                                                    }
                                                                    .background(Color(0xFFEDE9FE), RoundedCornerShape(12.dp))
                                                                    .border(1.5.dp, Color(0xFF7C4DFF), RoundedCornerShape(12.dp))
                                                                    .padding(12.dp),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(44.dp)
                                                                        .background(Color(0xFF7C4DFF), RoundedCornerShape(12.dp)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                                                }
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text("Aura App", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                                Text("(Direct launch)", color = Color.DarkGray, fontSize = 9.sp)
                                                            }

                                                            Spacer(modifier = Modifier.width(8.dp))

                                                            // Choice 2: Facebook mock
                                                            Column(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .clickable {
                                                                        android.widget.Toast.makeText(context, "Facebook mode is offline, please choose Aura App for android deep-link direct launch!", android.widget.Toast.LENGTH_SHORT).show()
                                                                    }
                                                                    .background(Color.White, RoundedCornerShape(12.dp))
                                                                    .padding(12.dp),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(44.dp)
                                                                        .background(Color(0xFF1877F2), RoundedCornerShape(12.dp)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text("f", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                                                }
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text("Facebook", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                Text("(Mock Option)", color = Color.Gray, fontSize = 9.sp)
                                                            }

                                                            Spacer(modifier = Modifier.width(8.dp))

                                                            // Choice 3: Chrome Web mock
                                                            Column(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .clickable {
                                                                        android.widget.Toast.makeText(context, "Closing APK to simulate standard website parsing inside browser.", android.widget.Toast.LENGTH_SHORT).show()
                                                                    }
                                                                    .background(Color.White, RoundedCornerShape(12.dp))
                                                                    .padding(12.dp),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(44.dp)
                                                                        .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color(0xFF4F46E5))
                                                                }
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text("Chrome", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                Text("(Web browser)", color = Color.Gray, fontSize = 9.sp)
                                                            }
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Bottom Choose action bar
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.End
                                                        ) {
                                                            TextButton(onClick = {
                                                                viewModel.handleDeepLinkPost(post.postId)
                                                                showShareSimulationDialog = false
                                                            }) {
                                                                Text("JUST ONCE", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            TextButton(onClick = {
                                                                viewModel.handleDeepLinkPost(post.postId)
                                                                showShareSimulationDialog = false
                                                            }) {
                                                                Text("ALWAYS", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        2 -> {
                                            // Tab 2: Facebook Feed Thread Content Simulation (Screenshot 4)
                                            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                                Text(
                                                    text = "Facebook Rich News Display Simulator",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color.DarkGray,
                                                    modifier = Modifier.padding(bottom = 6.dp)
                                                )

                                                // Beautiful mockup of Facebook card matching Screenshot 4
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                                    shape = RoundedCornerShape(16.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                                ) {
                                                    Column {
                                                        // Top metadata row
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(36.dp)
                                                                    .background(Color(0xFFE11D48), RoundedCornerShape(18.dp)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text("DBC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Text("dbcnews.tv", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                                                                    Spacer(modifier = Modifier.width(4.dp))
                                                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF1877F2), modifier = Modifier.size(12.dp))
                                                                }
                                                                Text("1d • 🌎", color = Color.Gray, fontSize = 10.sp)
                                                            }
                                                            Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray)
                                                        }

                                                        // Media preview matching Screenshot 4 structure
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(160.dp)
                                                                .clickable {
                                                                    // Show the chooser
                                                                    activeTab = 1
                                                                    android.widget.Toast.makeText(context, "Simulated Link Clicked! Opening OS App Chooser dialog.", android.widget.Toast.LENGTH_SHORT).show()
                                                                }
                                                        ) {
                                                            val imageUrl = post.imageUrl.split(",").firstOrNull { it.isNotBlank() }
                                                            if (imageUrl != null) {
                                                                AsyncImage(
                                                                    model = imageUrl,
                                                                    contentDescription = "Post preview",
                                                                    modifier = Modifier.fillMaxSize(),
                                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                                )
                                                            } else {
                                                                // TV / Headline Broadcaster representation
                                                                Column(
                                                                    modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .background(
                                                                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                                                                colors = listOf(Color(0xFF9E0059), Color(0xFF1E002B))
                                                                            )
                                                                        )
                                                                        .padding(16.dp),
                                                                    verticalArrangement = Arrangement.Center,
                                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                                ) {
                                                                    Text(
                                                                        text = "DBC 24/7 NEWS", 
                                                                        color = Color.Yellow, 
                                                                        fontWeight = FontWeight.Black, 
                                                                        fontSize = 18.sp
                                                                    )
                                                                }
                                                            }
                                                            // Headline overlap Banner style (Red Banner)
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .align(Alignment.BottomCenter)
                                                                    .background(Color(0xFFCC1111))
                                                                    .padding(6.dp)
                                                            ) {
                                                                Text(
                                                                    text = "আওরা অ্যাপের ডাইরেক্ট এন্ট্রি লিংক ভেরিফাইড! 🚀💜",
                                                                    color = Color.White,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 11.sp,
                                                                    maxLines = 1,
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.fillMaxWidth()
                                                                )
                                                            }
                                                        }

                                                        // Headline Text & Link Meta description
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .background(Color(0xFFF1F5F9))
                                                                .clickable { 
                                                                    activeTab = 1 
                                                                }
                                                                .padding(10.dp)
                                                        ) {
                                                            Text(text = "dbcnews.tv", color = Color.Gray, fontSize = 11.sp)
                                                            Text(
                                                                text = "DBC NEWS, a satellite 24 hours live news television channel in Bangladesh.",
                                                                fontSize = 11.sp,
                                                                color = Color.DarkGray,
                                                                lineHeight = 14.sp
                                                            )
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(12.dp))
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text("https://dbcnews.tv/articles/171389", color = Color(0xFF1877F2), fontSize = 10.sp, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                                                            }
                                                        }

                                                        // Interaction count row
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(16.dp)
                                                                        .background(Color(0xFF1877F2), RoundedCornerShape(8.dp)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text("👍", color = Color.White, fontSize = 8.sp)
                                                                }
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text("11K Likes", color = Color.Gray, fontSize = 10.sp)
                                                            }
                                                            Text("91 Comments • 73 Shares", color = Color.Gray, fontSize = 10.sp)
                                                        }

                                                        Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)

                                                        // Mock simulated Comment Action input bar
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(28.dp)
                                                                    .background(Color(0xFF7C4DFF), RoundedCornerShape(14.dp)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text("U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Box(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                                            ) {
                                                                Text("Write a comment...", color = Color.Gray, fontSize = 11.sp)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Footer close button
                                Button(
                                    onClick = { showShareSimulationDialog = false },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Dismiss Overlay Hub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Option Sheet Dialogue (matching screenshots 2 & 3)
    if (showMenuSheet) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showMenuSheet = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showMenuSheet = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .clickable(enabled = false) {}
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = Color(0xFF1E1E24), // Elegant modern dark container as in screenshots 2 & 3
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Pull bar
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(width = 40.dp, height = 4.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Why am I seeing this post section
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Why am I seeing this post?",
                                            color = Color.LightGray,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "You're friends with ${post.authorName}",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Group 1: Interested / Not interested
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
                                ) {
                                    Column {
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.AddCircle,
                                            title = "Interested",
                                            subtitle = "More of your posts will be like this.",
                                            onClick = { showMenuSheet = false }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.RemoveCircle,
                                            title = "Not interested",
                                            subtitle = "Less of your posts will be like this.",
                                            onClick = { showMenuSheet = false }
                                        )
                                    }
                                }
                            }

                            // Group 2: Save, Hide, Report, Notifs, Copy link
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
                                ) {
                                    Column {
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.BookmarkBorder,
                                            title = "Save post",
                                            subtitle = "Add this to your saved items.",
                                            onClick = { showMenuSheet = false }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.VisibilityOff,
                                            title = "Hide post",
                                            subtitle = "See fewer posts like this.",
                                            onClick = { 
                                                showMenuSheet = false
                                                viewModel.deletePost(post.postId)
                                            }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.Flag,
                                            title = "Report post",
                                            subtitle = "We won't let ${post.authorName} know who reported this.",
                                            onClick = { showMenuSheet = false }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.Notifications,
                                            title = "Turn on notifications for this post",
                                            onClick = { showMenuSheet = false }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.Link,
                                            title = "Copy link",
                                            onClick = { showMenuSheet = false }
                                        )
                                    }
                                }
                            }

                            // Group 3: Favorites, Snooze, Unfollow
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
                                ) {
                                    Column {
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.Star,
                                            title = "Add ${post.authorName} to Favorites",
                                            subtitle = "Prioritize his posts in News Feed.",
                                            onClick = { showMenuSheet = false }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.AccessTime,
                                            title = "Snooze ${post.authorName} for 30 days",
                                            subtitle = "Temporarily stop seeing posts.",
                                            onClick = { 
                                                showMenuSheet = false
                                                viewModel.deletePost(post.postId)
                                            }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.PersonOutline,
                                            title = "Unfollow ${post.authorName}",
                                            subtitle = "Stop seeing posts but stay friends. They won't be notified that you unfollowed...",
                                            onClick = { 
                                                showMenuSheet = false
                                                viewModel.deletePost(post.postId)
                                            }
                                        )
                                    }
                                }
                            }

                            // Group 4: Block, Manage Feed
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
                                ) {
                                    Column {
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.Block,
                                            title = "Block ${post.authorName}'s profile",
                                            subtitle = "You won't be able to see or contact each other.",
                                            onClick = { 
                                                showMenuSheet = false
                                                viewModel.deletePost(post.postId)
                                            }
                                        )
                                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                                        BottomSheetOptionRow(
                                            icon = Icons.Default.Tune,
                                            title = "Manage your Feed",
                                            onClick = { showMenuSheet = false }
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SHARED POPUP LAYER: STORIES VIEWER MODAL ---
@Composable
fun StoryViewerModal(
    story: StoryEntity,
    viewModel: AuraViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable { viewModel.showStory(null) }
            .testTag("story_viewer_modal"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(420.dp)
                .clickable(enabled = false) {}, // prevent click-through dismissal
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PremiumGradients[story.gradientIndex])
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Story author indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ProfileAvatar(avatarId = story.authorAvatar, size = 36, fallbackName = story.authorName)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(story.authorName, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = { viewModel.showStory(null) }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }

                    // Content status
                    Text(
                        text = story.contentText,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    // Footer spark
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Press anywhere to close story", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// --- COMMENTS SHEET DRAWER VIEW ---
@Composable
fun CommentsDrawer(
    state: AuraUiState,
    viewModel: AuraViewModel
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { viewModel.closeComments() }
            .testTag("comments_overlay"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Comments box sheet
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clickable(enabled = false, onClick = {}) // prevent dismiss on card tap
                .shadow(20.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // Comments header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(viewModel.getTranslatedText("Dialogue comments"), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { viewModel.closeComments() }) {
                        Icon(Icons.Default.Close, "Close comments", tint = LavenderPrimary)
                    }
                }
                Divider()

                // List of existing comments
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.activeComments.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.ModeComment, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(viewModel.getTranslatedText("No comments yet. Write the first response!"), color = Color.Gray)
                            }
                        }
                    }

                    items(state.activeComments) { comment ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProfileAvatar(avatarId = comment.authorAvatar, size = 32, fallbackName = comment.authorName)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceLight)
                                    .padding(10.dp)
                                    .weight(1f)
                            ) {
                                Text(comment.authorName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(viewModel.getTranslatedText(comment.text), color = TextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Divider()

                // Insert Comments Bar panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.commentInputText,
                        onValueChange = { viewModel.setCommentInput(it) },
                        placeholder = { Text(viewModel.getTranslatedText("Write a supportive aura comment...")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            viewModel.submitComment()
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_field_input"),
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            viewModel.submitComment()
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = LavenderPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Send, "Send comment")
                    }
                }
            }
        }
    }
}

// --- SCREEN 5: REAL CHAT ROOM VIEW PANEL ---
@Composable
fun ChatRoomScreen(
    state: AuraUiState,
    viewModel: AuraViewModel,
    otherUser: UserEntity
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val messagesList by viewModel.getActiveMessages().collectAsStateWithLifecycle(initialValue = emptyList())
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileAvatar(avatarId = otherUser.avatarUrl, size = 32, showOnlineStatus = true, fallbackName = otherUser.displayName)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(otherUser.displayName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Active Status Online", color = OnlineGreen, fontSize = 10.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Main) }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = LavenderPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundWhite,
        modifier = Modifier.testTag("chat_room_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            // Chat streams list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messagesList) { msg ->
                    val isOwn = msg.senderName != otherUser.displayName
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isOwn) {
                            ProfileAvatar(avatarId = msg.senderAvatar, size = 28, fallbackName = msg.senderName)
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isOwn) 16.dp else 4.dp,
                                        bottomEnd = if (isOwn) 4.dp else 16.dp
                                    )
                                )
                                .background(if (isOwn) LavenderPrimary else Color(0xFFF1EFF9))
                                .padding(12.dp)
                                .widthIn(max = 240.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = if (isOwn) Color.White else TextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Divider()

            // Text input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Dialogue message with ${otherUser.displayName}...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = LavenderPrimary)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Send, "Send MESSAGE")
                }
            }
        }
    }
}

// ==========================================
// SCREEN: Hometown Selection (Onboarding Step)
// ==========================================
@Composable
fun HometownSelectionScreen(
    viewModel: AuraViewModel
) {
    BackHandler(enabled = true) {
        viewModel.goBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var isPrivacyDropdownOpen by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    var isSubmittingData by remember { mutableStateOf(false) }

    var localHometown by remember { mutableStateOf(viewModel.regHometown) }
    var localPrivacy by remember { mutableStateOf(viewModel.regHometownPrivacy) }

    val privacyOptions = listOf("Public", "Friends", "Only me")

    // Extensive Global Cities database strictly sorted A-Z covering all key countries
    val globalLocations = remember {
        listOf(
            // A
            "Amsterdam, Netherlands", "Athens, Greece", "Auckland, New Zealand", "Algiers, Algeria", "Ankara, Turkey", "Astana, Kazakhstan", "Abu Dhabi, UAE", "Addis Ababa, Ethiopia", "Acre, Brazil", "Asuncion, Paraguay",
            // B
            "Brussels, Belgium", "Berlin, Germany", "Budapest, Hungary", "Buenos Aires, Argentina", "Beijing, China", "Bangkok, Thailand", "Bangalore, India", "Barisal, Bangladesh", "Boston, USA", "Bogota, Colombia", "Baghdad, Iraq",
            // C
            "Cairo, Egypt", "Cape Town, South Africa", "Chicago, USA", "Copenhagen, Denmark", "Caracas, Venezuela", "Calcutta, India", "Chittagong, Bangladesh", "Casablanca, Morocco", "Colombo, Sri Lanka", "Canberra, Australia",
            // D
            "Dhaka, Bangladesh", "Dubai, UAE", "Delhi, India", "Dublin, Ireland", "Doha, Qatar", "Dakar, Senegal", "Denver, USA", "Dar es Salaam, Tanzania", "Davao, Philippines", "Detroit, USA", "Damascus, Syria",
            // E
            "Edinburgh, UK", "Eindhoven, Netherlands", "Erbil, Iraq", "Esfahan, Iran", "Eugene, USA", "Entebbe, Uganda", "Essen, Germany", "El Paso, USA", "Espoo, Finland",
            // F
            "Frankfurt, Germany", "Fukuoka, Japan", "Florence, Italy", "Freetown, Sierra Leone", "Fargo, USA", "Faisalabad, Pakistan", "Fortaleza, Brazil", "Fuzhou, China", "Funafuti, Tuvalu",
            // G
            "Geneva, Switzerland", "Guangzhou, China", "Gaza, Palestine", "Giza, Egypt", "Glasgow, UK", "Gothenburg, Sweden", "Guatemala City, Guatemala", "Georgetown, Guyana", "Gdansk, Poland", "Gazipur, Bangladesh",
            // H
            "Helsinki, Finland", "Hanoi, Vietnam", "Houston, USA", "Hong Kong, HK", "Havana, Cuba", "Hamburg, Germany", "Hyderabad, India", "Harare, Zimbabwe", "Hobart, Australia", "Halifax, Canada",
            // I
            "Istanbul, Turkey", "Islamabad, Pakistan", "Ibadan, Nigeria", "Indianapolis, USA", "Incheon, South Korea", "Irakleion, Greece", "Ipswich, UK", "Irkutsk, Russia", "Illovo, South Africa",
            // J
            "Jakarta, Indonesia", "Johannesburg, South Africa", "Jerusalem, Palestine", "Jeddah, Saudi Arabia", "Jaipur, India", "Jessore, Bangladesh", "Jacksonville, USA", "Juarez, Mexico", "Juba, South Sudan",
            // K
            "Kuala Lumpur, Malaysia", "Kyoto, Japan", "Kiev, Ukraine", "Karachi, Pakistan", "Kolkata, India", "Khulna, Bangladesh", "Kingston, Jamaica", "Kathmandu, Nepal", "Kabul, Afghanistan", "Kigali, Rwanda", "Kuwait City, Kuwait",
            // L
            "London, United Kingdom", "Los Angeles, USA", "Lima, Peru", "Lisbon, Portugal", "Lagos, Nigeria", "Lahore, Pakistan", "Luxembourg City, Luxembourg", "Lhasa, Tibet", "Lyon, France", "Lund, Sweden", "Luanda, Angola",
            // M
            "Madrid, Spain", "Manila, Philippines", "Melbourne, Australia", "Mumbai, India", "Moscow, Russia", "Mymensingh, Bangladesh", "Mexico City, Mexico", "Miami, USA", "Montreal, Canada", "Munich, Germany", "Muscat, Oman", "Malé, Maldives",
            // N
            "New York, USA", "Nairobi, Kenya", "New Delhi, India", "Naples, Italy", "Nassau, Bahamas", "Nantes, France", "Nagoya, Japan", "Nanning, China", "Narayanganj, Bangladesh", "Nicoya, Costa Rica", "Nicosia, Cyprus",
            // O
            "Oslo, Norway", "Ottawa, Canada", "Osaka, Japan", "Omaha, USA", "Oulu, Finland", "Odessa, Ukraine", "Oran, Algeria", "Oaxaca, Mexico", "Oxford, UK", "Ulsan, South Korea",
            // P
            "Paris, France", "Prague, Czech Republic", "Phnom Penh, Cambodia", "Porto, Portugal", "Portland, USA", "Peshawar, Pakistan", "Pune, India", "Patuakhali, Bangladesh", "Panama City, Panama", "Pretoria, South Africa",
            // Q
            "Quebec City, Canada", "Quito, Ecuador", "Quetta, Pakistan", "Qingdao, China", "Qom, Iran", "Quanzhou, China", "Quelimane, Mozambique",
            // R
            "Rome, Italy", "Rio de Janeiro, Brazil", "Riyadh, Saudi Arabia", "Rajshahi, Bangladesh", "Rangpur, Bangladesh", "Rotterdam, Netherlands", "Reykjavik, Iceland", "Riga, Latvia", "Rabat, Morocco", "Rawalpindi, Pakistan",
            // S
            "Seoul, South Korea", "Singapore, Singapore", "Sydney, Australia", "Stockholm, Sweden", "Santiago, Chile", "Shanghai, China", "San Francisco, USA", "Seattle, USA", "Sylhet, Bangladesh", "Sao Paulo, Brazil", "Sarajevo, Bosnia", "Sofia, Bulgaria",
            // T
            "Tokyo, Japan", "Toronto, Canada", "Tehran, Iran", "Taipei, Taiwan", "Tashkent, Uzbekistan", "Trinidad, Cuba", "Tripoli, Libya", "Tunis, Tunisia", "Tijuana, Mexico", "Tangail, Bangladesh", "Trivandrum, India",
            // U
            "Ulaanbaatar, Mongolia", "Utrecht, Netherlands", "Ufa, Russia", "Ushuaia, Argentina", "Uppsala, Sweden", "Udine, Italy", "Uyo, Nigeria",
            // V
            "Vienna, Austria", "Vancouver, Canada", "Venice, Italy", "Vilnius, Lithuania", "Valparaiso, Chile", "Varanasi, India", "Vientiane, Laos", "Vatican City", "Vladivostok, Russia",
            // W
            "Washington D.C., USA", "Warsaw, Poland", "Wellington, New Zealand", "Wuhan, China", "Windhoek, Namibia", "Wollongong, Australia", "Wroclaw, Poland",
            // X
            "Xian, China", "Xiamen, China", "Xuzhou, China", "Xalapa, Mexico", "Xingtai, China",
            // Y
            "Yokohama, Japan", "Yangon, Myanmar", "Yerevan, Armenia", "Yaounde, Cameroon", "Yogyakarta, Indonesia", "Yamoussoukro, Ivory Coast", "Yellowknife, Canada",
            // Z
            "Zurich, Switzerland", "Zagreb, Croatia", "Zanzibar, Tanzania", "Zaragoza, Spain", "Zibo, China", "Zaria, Nigeria", "Zilina, Slovakia"
        )
    }

    val filteredLocations = remember(localHometown) {
        if (localHometown.isBlank()) {
            globalLocations.take(12) // Show popular initials when empty
        } else {
            globalLocations.filter {
                it.contains(localHometown, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("hometown_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header with back icon and privacy button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    // Privacy Switcher Dropdown
                    Box {
                        TextButton(
                            onClick = { isPrivacyDropdownOpen = !isPrivacyDropdownOpen },
                            modifier = Modifier.testTag("hometown_privacy_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF7C4DFF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Privacy: $localPrivacy",
                                color = Color(0xFF7C4DFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isPrivacyDropdownOpen) {
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .padding(top = 40.dp)
                                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECECEF))
                            ) {
                                Column {
                                    privacyOptions.forEach { option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    localPrivacy = option
                                                    viewModel.regHometownPrivacy = option
                                                    isPrivacyDropdownOpen = false
                                                }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val icon = when (option) {
                                                "Public" -> Icons.Default.Public
                                                "Friends" -> Icons.Default.People
                                                else -> Icons.Default.Lock
                                            }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (localPrivacy == option) Color(0xFF7C4DFF) else Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = option,
                                                color = if (localPrivacy == option) Color(0xFF7C4DFF) else Color.Black,
                                                fontWeight = if (localPrivacy == option) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Area
                Text(
                    text = "Add Hometown",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Represent your absolute roots! Select your home place or type it manually to customize your timeline profile natively.",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Input Box (Ensuring no white-on-white text, foreground is strictly Black!)
                OutlinedTextField(
                    value = localHometown,
                    onValueChange = {
                        localHometown = it
                        viewModel.regHometown = it
                        showSuggestions = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hometown_input_box"),
                    label = { Text("Hometown Venue", color = Color(0xFF7C4DFF)) },
                    placeholder = { Text("e.g. Dhaka, Bangladesh", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF7C4DFF),
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = Color(0xFF7C4DFF),
                        cursorColor = Color(0xFF7C4DFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showSuggestions = !showSuggestions }) {
                            Icon(
                                imageVector = if (showSuggestions) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF7C4DFF)
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFFFF4081)
                        )
                    }
                )

                // Render Suggestion dropdown box beautifully inside white container card with shadows
                if (showSuggestions && filteredLocations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECECEF))
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            items(filteredLocations) { place ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            localHometown = place
                                            viewModel.regHometown = place
                                            showSuggestions = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 13.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFF7C4DFF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = place,
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = if (localHometown == place) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                Divider(color = Color(0xFFF3E5F5), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            // Bottom trigger buttons conforming strictly to Relationship Screen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip Button
                OutlinedButton(
                    onClick = {
                        isSubmittingData = true
                        viewModel.regHometown = ""
                        viewModel.regHometownPrivacy = "Public"

                        val fullName = if (viewModel.regLastName.isBlank()) viewModel.regFirstName else "${viewModel.regFirstName} ${viewModel.regLastName}"
                        val optionalText = if (viewModel.regGenderOptional.isNotBlank()) " (${viewModel.regGenderOptional})" else ""
                        val pronounText = if (viewModel.regGenderSelection == "More options" && viewModel.regPronoun.isNotBlank()) " [Pronoun: ${viewModel.regPronoun}]" else ""

                        viewModel.registerUserInAppwriteAndLogin(
                            displayName = fullName,
                            bio = if (viewModel.regBio.isNotBlank()) viewModel.regBio else "Proud to be ${viewModel.regGenderSelection}$pronounText$optionalText! Hello Aura!",
                            avatar = if (viewModel.regProfilePic.isNotBlank()) viewModel.regProfilePic else "avatar_1",
                            email = viewModel.regEmail,
                            tempPass = viewModel.regPasswordText,
                            targetScreen = Screen.WelcomeCelebration
                        ) { _ ->
                            isSubmittingData = false
                            viewModel.navigateTo(Screen.WelcomeCelebration)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("hometown_skip_button"),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    enabled = !isSubmittingData
                ) {
                    Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Next completes the wizard and submits the data
                Button(
                    onClick = {
                        if (localHometown.isBlank()) {
                            android.widget.Toast.makeText(
                                context,
                                "Please select a venue or type manually to continue, or tap Skip!",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        } else {
                            isSubmittingData = true
                            viewModel.regHometown = localHometown
                            viewModel.regHometownPrivacy = localPrivacy

                            val fullName = if (viewModel.regLastName.isBlank()) viewModel.regFirstName else "${viewModel.regFirstName} ${viewModel.regLastName}"
                            val optionalText = if (viewModel.regGenderOptional.isNotBlank()) " (${viewModel.regGenderOptional})" else ""
                            val pronounText = if (viewModel.regGenderSelection == "More options" && viewModel.regPronoun.isNotBlank()) " [Pronoun: ${viewModel.regPronoun}]" else ""

                            viewModel.registerUserInAppwriteAndLogin(
                                displayName = fullName,
                                bio = if (viewModel.regBio.isNotBlank()) viewModel.regBio else "Proud to be ${viewModel.regGenderSelection}$pronounText$optionalText! Hello Aura!",
                                avatar = if (viewModel.regProfilePic.isNotBlank()) viewModel.regProfilePic else "avatar_1",
                                email = viewModel.regEmail,
                                tempPass = viewModel.regPasswordText,
                                targetScreen = Screen.WelcomeCelebration
                            ) { _ ->
                                isSubmittingData = false
                                viewModel.navigateTo(Screen.WelcomeCelebration)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("hometown_next_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    enabled = !isSubmittingData
                ) {
                    if (isSubmittingData) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Next", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// Spark properties for custom colorful physics fireworks
data class SparkParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val speedX: Float,
    val speedY: Float,
    val size: Float,
    val maxLife: Int,
    val currentLife: Int
)

// ==========================================
// SCREEN: Welcomes celebratory fireworks screen
// ==========================================
@Composable
fun WelcomeCelebrationScreen(
    viewModel: AuraViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // Retrieve registration details
    val fullName = remember {
        val fName = viewModel.regFirstName.trim()
        val lName = viewModel.regLastName.trim()
        if (lName.isBlank()) fName else "$fName $lName"
    }

    val selectedAvatarStr = viewModel.regProfilePic

    // Zoom-out and zoom-in pulse state properties
    val avatarScale = remember { androidx.compose.animation.core.Animatable(0.2f) }

    // Real-time Canvas Sparks physics collection
    val sparkList = remember { mutableStateListOf<SparkParticle>() }
    val fireworksColors = listOf(
        Color(0xFF7C4DFF), // bold lavender
        Color(0xFFFF4081), // hot pink neon
        Color(0xFF00E676), // emerald
        Color(0xFF00B0FF), // cosmic blue
        Color(0xFFFFEB3B), // neon yellow
        Color(0xFFFF5722), // glowing red
        Color(0xFFE040FB)  // electric magenta
    )

    // Pulse sequence triggers
    LaunchedEffect(Unit) {
        // Attractive animation: Starts small (zoom out), then pops up large (zoom in) and settles at 1.0f!
        avatarScale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        avatarScale.animateTo(
            targetValue = 1.25f,
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        )
        avatarScale.animateTo(
            targetValue = 1.00f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }

    // Interactive custom physics fireworks engine tick loop
    LaunchedEffect(Unit) {
        var loopCount = 0
        while (true) {
            // Clean up dead particles
            val iterator = sparkList.iterator()
            while (iterator.hasNext()) {
                val spark = iterator.next()
                if (spark.currentLife <= 0) {
                    iterator.remove()
                }
            }

            // Update remaining sparks position and speed
            for (idx in sparkList.indices) {
                val s = sparkList[idx]
                sparkList[idx] = s.copy(
                    x = s.x + s.speedX,
                    y = s.y + s.speedY + 0.14f, // subtle gravity
                    speedX = s.speedX * 0.98f, // air friction
                    speedY = s.speedY * 0.98f,
                    currentLife = s.currentLife - 1
                )
            }

            // Periodically spray bursts
            if (loopCount % 30 == 0) {
                // Shoot a burst randomly on screen centers
                val spawnX = (200..900).random().toFloat()
                val spawnY = (300..1100).random().toFloat()
                val activeColor = fireworksColors.random()

                // Generate 24 sparks in 360-degree directions
                for (p in 0 until 24) {
                    val angleRad = (p * 15f) * (Math.PI / 180f)
                    val blastVelocity = (2..12).random().toFloat()
                    val sparkSize = (6..14).random().toFloat()
                    val sparkLifetime = (35..65).random()
                    sparkList.add(
                        SparkParticle(
                            x = spawnX,
                            y = spawnY,
                            color = activeColor,
                            speedX = (Math.cos(angleRad) * blastVelocity).toFloat(),
                            speedY = (Math.sin(angleRad) * blastVelocity).toFloat(),
                            size = sparkSize,
                            maxLife = sparkLifetime,
                            currentLife = sparkLifetime
                        )
                    )
                }
            }

            loopCount++
            delay(16) // Smooth 60 frames per second game-like render tick
        }
    }

    // Automatically navigate to Home Feed screen after 4.8 seconds
    LaunchedEffect(Unit) {
        delay(4800)
        viewModel.navigateTo(Screen.Main)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("welcome_celebration_screen"),
        contentAlignment = Alignment.Center
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // 1. Particle Canvas background
        Canvas(modifier = Modifier.fillMaxSize()) {
            sparkList.forEach { spark ->
                val ratio = spark.currentLife.toFloat() / spark.maxLife.toFloat()
                drawCircle(
                    color = spark.color.copy(alpha = ratio),
                    radius = spark.size * ratio,
                    center = androidx.compose.ui.geometry.Offset(spark.x, spark.y)
                )
            }
        }

        // 2. Beautiful centered congratulatory interface cards
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Animated Circular Avatar Frame showing user's curated profile pic or person icon
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(
                        scaleX = avatarScale.value,
                        scaleY = avatarScale.value
                    )
                    .clip(CircleShape)
                    .border(5.dp, Color(0xFF7C4DFF), CircleShape)
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedAvatarStr.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Silhouette icon",
                        tint = Color(0xFFB0BEC5),
                        modifier = Modifier.fillMaxSize(0.60f)
                    )
                } else {
                    AsyncImage(
                        model = android.net.Uri.parse(selectedAvatarStr),
                        contentDescription = "Successful User Avatar Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Greeting displays
            Text(
                text = "Hi, $fullName!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF7C4DFF), // bold lavender app theme color
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Welcome to Aura",
                fontSize = 22.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Setting up your high-performance sandbox workspace thread... Prepare to feel the aura! ⚡",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Direct Entrance CTA button
            Button(
                onClick = { viewModel.navigateTo(Screen.Main) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .width(180.dp)
                    .height(52.dp)
                    .testTag("enter_home_button")
            ) {
                Text(
                    text = "Let's Start! 🚀",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

// --- VISITOR'S DETAILED PROFILE SCREEN ---
@Composable
fun ProfileDetailScreen(
    viewModel: AuraViewModel,
    targetUser: UserEntity,
    onBackClick: () -> Unit
) {
    var selectedSubTab by remember { mutableStateOf("Posts") }
    var selectedPhotoUrlForViewer by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember(targetUser.userId) {
        context.getSharedPreferences("aura_friend_relations", android.content.Context.MODE_PRIVATE)
    }
    
    var isFriend by remember(targetUser.userId) {
        mutableStateOf(sharedPrefs.getBoolean("friend_${targetUser.userId}", false))
    }
    var requestSent by remember(targetUser.userId) {
        mutableStateOf(sharedPrefs.getBoolean("req_sent_${targetUser.userId}", false))
    }
    var isBlocked by remember(targetUser.userId) {
        mutableStateOf(sharedPrefs.getBoolean("blocked_${targetUser.userId}", false))
    }
    
    val allUsersList by viewModel.allUsers.collectAsStateWithLifecycle()
    val user = remember(allUsersList, targetUser.userId) {
        allUsersList.find { it.userId == targetUser.userId } ?: targetUser
    }
    
    val postsList by viewModel.postsFeed.collectAsStateWithLifecycle()
    val targetUserPosts = remember(postsList, user.userId) {
        postsList.filter { it.authorId == user.userId }
    }
    val targetUserAllPhotos = remember(targetUserPosts) {
        targetUserPosts.flatMap { post ->
            post.imageUrl.split(",").filter { it.isNotBlank() }
        }
    }
    
    val isLocked = user.isProfileLocked
    val showPrivacyShield = isLocked && !isFriend && !isBlocked
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Header row with back icon ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LavenderPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${user.displayName}'s Profile",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Cover Photo Backdrop
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val isCoverUri = !user.coverUrl.isNullOrBlank() && 
                        (user.coverUrl.startsWith("content://") || user.coverUrl.startsWith("http") || user.coverUrl.contains("/"))
                    var coverLoadError by remember(user.coverUrl) { mutableStateOf(false) }

                    if (isCoverUri && !coverLoadError) {
                        AsyncImage(
                            model = user.coverUrl,
                            contentDescription = "Cover Backdrop",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            onError = {
                                coverLoadError = true
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(LavenderPrimary, Color(0xFF1E1E24))
                                    )
                                )
                        )
                    }
                }
            }
            
            // 2. Avatar Block
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .shadow(3.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        ProfileAvatar(
                            avatarId = user.avatarUrl ?: "avatar_user_main",
                            modifier = Modifier.fillMaxSize(),
                            size = 130,
                            fallbackName = user.displayName,
                            showOnlineStatus = !user.isProfileLocked
                        )
                    }
                }
            }
            
            // 3. User Identity Headings
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = user.displayName,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    
                    // --- UID DISPLAY WITH COPY ICON (MANDATORY FOR BOTH PUBLIC & LOCKED PROFILES) ---
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    val userUid = user.resolvedUid
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 6.dp, bottom = 4.dp)
                            .background(Color(0xFFF1EFF9), RoundedCornerShape(12.dp))
                            .clickable {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(userUid))
                                android.widget.Toast.makeText(context, "Copied UID: $userUid", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "UID Badge",
                            tint = LavenderPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "UID: $userUid",
                            color = LavenderPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy UID",
                            tint = LavenderPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    
                    if (!showPrivacyShield && !isBlocked) {
                        Text(
                            text = "@${user.username}",
                            color = LavenderSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        val visibleFollowersCount = user.followerCount
                        val followersLabel = if (user.isProfileLocked) "Friends" else "Followers"
                        Text(
                            text = "$visibleFollowersCount $followersLabel  •  ${user.followingCount} Following",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        if (user.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = user.bio,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
            
            // 4. Friend / Message Action Rows & Locked Alert Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showPrivacyShield && !isBlocked) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F6FF)),
                            border = BorderStroke(1.dp, LavenderPrimary.copy(alpha = 0.25f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(Color(0xFFE8E0FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Profile Shield Lock",
                                        tint = LavenderPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "This Profile is Locked",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Only their accepted friend list connections can view their handle usernames, followers counts, active indicators, timeline posts, and personal setup detail rows.",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    lineHeight = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    if (isBlocked) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                            border = BorderStroke(1.dp, Color(0xFFFDA4AF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Blocked User icon",
                                    tint = Color(0xFFE11D48),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "You Blocked This User",
                                    fontSize = 16.sp,
                                    color = Color(0xFF9F1239),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "To view their profile details, bio, or send messages, you need to unblock them first.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFBE123C),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        sharedPrefs.edit().putBoolean("blocked_${user.userId}", false).apply()
                                        isBlocked = false
                                        android.widget.Toast.makeText(context, "${user.displayName} is unblocked!", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Unblock User", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLocked) {
                                if (isFriend) {
                                    Button(
                                        onClick = {
                                            sharedPrefs.edit().putBoolean("friend_${user.userId}", false).apply()
                                            isFriend = false
                                            android.widget.Toast.makeText(context, "Removed from Friends list.", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE8F5E9),
                                            contentColor = Color(0xFF2E7D32)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(44.dp)
                                    ) {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "✓ Friends", 
                                            fontSize = 11.sp, 
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                } else if (requestSent) {
                                    Column(
                                        modifier = Modifier.weight(1.3f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                sharedPrefs.edit().putBoolean("friend_${user.userId}", true).putBoolean("req_sent_${user.userId}", false).apply()
                                                isFriend = true
                                                requestSent = false
                                                android.widget.Toast.makeText(context, "Relationship accepted! Profile unlocked.", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = LavenderPrimary,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                        ) {
                                            Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Accept Request", 
                                                fontSize = 10.sp, 
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                        
                                        Button(
                                            onClick = {
                                                sharedPrefs.edit().putBoolean("req_sent_${user.userId}", false).apply()
                                                requestSent = false
                                                android.widget.Toast.makeText(context, "Request cancelled.", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFFECE5),
                                                contentColor = Color(0xFFEF6C00)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(32.dp)
                                        ) {
                                            Text(
                                                text = "Cancel", 
                                                fontSize = 9.sp, 
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            sharedPrefs.edit().putBoolean("req_sent_${user.userId}", true).apply()
                                            requestSent = true
                                            android.widget.Toast.makeText(context, "Friend request sent!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LavenderPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(44.dp)
                                    ) {
                                        Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Add Friend", 
                                            fontSize = 11.sp, 
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            } else {
                                val isFollowing = user.isFollowing
                                Button(
                                    onClick = {
                                        viewModel.toggleFollowUser(user)
                                        val msg = if (isFollowing) "Unfollowed successfully!" else "Following successfully!"
                                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) Color(0xFFE8E5FF) else LavenderPrimary,
                                        contentColor = if (isFollowing) Color(0xFF3F00B5) else Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = if (isFollowing) BorderStroke(1.5.dp, Color(0xFF7C4DFF)) else null,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isFollowing) Color(0xFF3F00B5) else Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isFollowing) "Following" else "Follow",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isFollowing) Color(0xFF3F00B5) else Color.White,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { viewModel.navigateTo(Screen.ChatRoom(user)) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF3E8FF),
                                    contentColor = Color(0xFF4C1D95)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, LavenderPrimary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.ChatBubble, 
                                    null, 
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF4C1D95)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Message", 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4C1D95),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            
                            Button(
                                onClick = {
                                    sharedPrefs.edit().putBoolean("blocked_${user.userId}", true).apply()
                                    isBlocked = true
                                    android.widget.Toast.makeText(context, "${user.displayName} is blocked.", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFF1F2),
                                    contentColor = Color(0xFF9F1239)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, Color(0xFFFDA4AF)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.Block, 
                                    null, 
                                    modifier = Modifier.size(14.dp), 
                                    tint = Color(0xFF9F1239)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Block", 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF9F1239),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
            
            if (!showPrivacyShield && !isBlocked) {
                item {
                    Divider(color = DividerColor, thickness = 4.dp)
                }
                
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Onboarding Details & Info",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        val detailsItems = mutableListOf<Pair<androidx.compose.ui.graphics.vector.ImageVector, String>>().apply {
                            add(Pair(Icons.Default.Badge, "Profile · Digital creator"))
                            if (!user.school.isNullOrBlank()) {
                                add(Pair(Icons.Default.School, "Went to ${user.school}"))
                            }
                            if (!user.college.isNullOrBlank()) {
                                add(Pair(Icons.Default.School, "Studied at ${user.college}"))
                            }
                            if (!user.university.isNullOrBlank()) {
                                add(Pair(Icons.Default.School, "Studying at ${user.university}"))
                            }
                            if (!user.hometown.isNullOrBlank()) {
                                add(Pair(Icons.Default.Place, "From ${user.hometown}"))
                            }
                            if (!user.relationshipStatus.isNullOrBlank()) {
                                add(Pair(Icons.Default.Favorite, user.relationshipStatus ?: "Single"))
                            }
                            if (!user.birthday.isNullOrBlank()) {
                                add(Pair(Icons.Default.Cake, "Born ${user.birthday}"))
                            }
                            if (!user.gender.isNullOrBlank()) {
                                add(Pair(Icons.Default.Face, "Gender: ${user.gender}"))
                            }
                            if (!user.hobbies.isNullOrBlank()) {
                                add(Pair(Icons.Default.Star, "Hobbies: ${user.hobbies}"))
                            }

                            // Joined Date (using database saved date or system date formatted with day, month and year under CalendarMonth icon as requested)
                            val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH)
                            val joinDateFormatted = if (!user.joinedDate.isNullOrBlank()) {
                                "Joined ${user.joinedDate}"
                            } else {
                                "Joined ${sdf.format(java.util.Date(System.currentTimeMillis()))}"
                            }
                            add(Pair(Icons.Default.CalendarMonth, joinDateFormatted))
                        }
                        
                        detailsItems.forEach { (icon, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // Dynamic Posts, Photos, Videos Tabs below details for and as requested by the user
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3E8FF)) // soft lavender background for tab bar
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Triple("Posts", Icons.Default.Article, Color(0xFF7C4DFF)),
                            Triple("Photos", Icons.Default.Collections, Color(0xFFE00052)),
                            Triple("Videos", Icons.Default.PlayCircle, Color(0xFF00C853))
                        ).forEach { (tabName, icon, tint) ->
                            val isTabSelected = selectedSubTab == tabName
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isTabSelected) Color.White else Color.Transparent)
                                    .clickable { selectedSubTab = tabName }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tabName,
                                    tint = if (isTabSelected) tint else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tabName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTabSelected) Color.Black else Color.Gray
                                )
                            }
                        }
                    }
                }

                if (selectedSubTab == "Posts") {
                    if (targetUserPosts.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.NoteAlt, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No posts found by this user", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(targetUserPosts) { post ->
                            PostCard(post = post, viewModel = viewModel, currentUserId = user.userId)
                        }
                    }
                } else if (selectedSubTab == "Photos") {
                    val allUserPhotos = targetUserAllPhotos

                    if (allUserPhotos.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.PhotoLibrary, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No photos uploaded yet by this user", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        // Display the Full screen Image Viewer dialog over everything if selected
                        val activeIndex = if (selectedPhotoUrlForViewer != null) allUserPhotos.indexOf(selectedPhotoUrlForViewer) else -1
                        if (selectedPhotoUrlForViewer != null && activeIndex >= 0) {
                            item {
                                FullScreenImageViewer(
                                    imageUrls = allUserPhotos,
                                    initialIndex = activeIndex,
                                    post = PostEntity(
                                        authorId = user.userId,
                                        authorName = user.displayName,
                                        authorAvatar = user.avatarUrl,
                                        content = "Photo View"
                                    ),
                                    viewModel = viewModel,
                                    onDismiss = { selectedPhotoUrlForViewer = null }
                                )
                            }
                        }

                        item {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                allUserPhotos.chunked(3).forEach { rowItems ->
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        rowItems.forEach { photoUrl ->
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(4.dp)
                                                    .clickable { selectedPhotoUrlForViewer = photoUrl },
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, DividerColor)
                                            ) {
                                                AsyncImage(
                                                    model = photoUrl,
                                                    contentDescription = "User Photo",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                            }
                                        }
                                        // Pad empty space in the row
                                        if (rowItems.size < 3) {
                                            repeat(3 - rowItems.size) {
                                                Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Videos tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Videocam, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No videos uploaded by this user", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- SEARCH USERS SCREEN WITH PERSISTENT SEARCH HISTORY ---
data class SearchHistoryItem(
    val userId: Int,
    val isPinned: Boolean
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SearchUsersScreen(
    viewModel: AuraViewModel,
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    // Proactively scan and import all other user profiles from public storage
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.scanAndImportAllUsersFromPublicStorage()
    }
    
    // Loaded list from SharedPreferences
    var searchHistory by remember { mutableStateOf(getSearchHistory(context)) }
    
    val sortedHistory = remember(searchHistory) {
        searchHistory.sortedWith(compareByDescending { it.isPinned })
    }
    
    // Fetch all users on Aura
    val allUsersList by viewModel.allUsers.collectAsStateWithLifecycle()
    
    val filteredUsers = remember(allUsersList, searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val cleanedQuery = searchQuery.trim().lowercase()
            allUsersList.filter {
                val displayNameLower = it.displayName.lowercase().trim()
                val usernameLower = it.username.lowercase().trim()
                val emailLower = it.email.lowercase().trim()
                
                displayNameLower.contains(cleanedQuery) ||
                usernameLower.contains(cleanedQuery) ||
                emailLower.contains(cleanedQuery) ||
                cleanedQuery.split(" ").filter { part -> part.isNotBlank() }.any { part ->
                    displayNameLower.contains(part) ||
                    usernameLower.contains(part) ||
                    emailLower.contains(part)
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // --- Custom Header Search Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = LavenderPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = {
                    Text(
                        text = "Search users by full name...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = LavenderPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(26.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF111827),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedBorderColor = LavenderPrimary,
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF111827),
                    fontSize = 14.sp
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = {
                        val firstMatch = filteredUsers.firstOrNull()
                        if (firstMatch != null) {
                            addToSearchHistory(context, firstMatch.userId)
                            searchHistory = getSearchHistory(context)
                            viewModel.navigateTo(Screen.ProfileDetail(firstMatch))
                        } else if (searchQuery.isNotBlank()) {
                            android.widget.Toast.makeText(context, "No users matched: $searchQuery", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            )
        }
        
        Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
        
        if (searchQuery.isBlank()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = "Recent Searches",
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                if (searchHistory.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFFF5F3FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = LavenderPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your search history is empty",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    items(sortedHistory) { historyItem ->
                        val matchedUser = allUsersList.find { it.userId == historyItem.userId }
                        if (matchedUser != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        addToSearchHistory(context, matchedUser.userId)
                                        searchHistory = getSearchHistory(context)
                                        viewModel.navigateTo(Screen.ProfileDetail(matchedUser))
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileAvatar(
                                    avatarId = matchedUser.avatarUrl ?: "avatar_user_main",
                                    size = 40,
                                    fallbackName = matchedUser.displayName,
                                    showOnlineStatus = !matchedUser.isProfileLocked
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = matchedUser.displayName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    if (historyItem.isPinned) {
                                        Text(
                                            text = "Pinned",
                                            fontSize = 11.sp,
                                            color = LavenderPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                
                                IconButton(
                                    onClick = {
                                        togglePinSearchHistory(context, matchedUser.userId)
                                        searchHistory = getSearchHistory(context)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pin / Unpin item",
                                        tint = if (historyItem.isPinned) LavenderPrimary else Color.LightGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        deleteFromSearchHistory(context, matchedUser.userId)
                                        searchHistory = getSearchHistory(context)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete from history",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Divider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (filteredUsers.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No matching users found",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    items(filteredUsers) { matchedUser ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    addToSearchHistory(context, matchedUser.userId)
                                    searchHistory = getSearchHistory(context)
                                    viewModel.navigateTo(Screen.ProfileDetail(matchedUser))
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileAvatar(
                                avatarId = matchedUser.avatarUrl ?: "avatar_user_main",
                                size = 42,
                                fallbackName = matchedUser.displayName,
                                showOnlineStatus = !matchedUser.isProfileLocked
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = matchedUser.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "@${matchedUser.username}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer(rotationZ = 180f)
                            )
                        }
                        Divider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

private fun getSearchHistory(context: android.content.Context): List<SearchHistoryItem> {
    val prefs = context.getSharedPreferences("aura_search_history", android.content.Context.MODE_PRIVATE)
    val historyStr = prefs.getString("history_list", "") ?: ""
    val pinnedStr = prefs.getString("pinned_list", "") ?: ""
    
    if (historyStr.isBlank()) return emptyList()
    
    val listIds = historyStr.split(",").mapNotNull { it.toIntOrNull() }
    val pinnedIds = pinnedStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    
    return listIds.map { id ->
        SearchHistoryItem(userId = id, isPinned = pinnedIds.contains(id))
    }
}

private fun saveSearchHistory(context: android.content.Context, items: List<SearchHistoryItem>) {
    val prefs = context.getSharedPreferences("aura_search_history", android.content.Context.MODE_PRIVATE)
    val historyStr = items.map { it.userId }.joinToString(",")
    val pinnedStr = items.filter { it.isPinned }.map { it.userId }.joinToString(",")
    prefs.edit()
        .putString("history_list", historyStr)
        .putString("pinned_list", pinnedStr)
        .apply()
}

private fun addToSearchHistory(context: android.content.Context, userId: Int) {
    val list = getSearchHistory(context).toMutableList()
    val existingIndex = list.indexOfFirst { it.userId == userId }
    if (existingIndex != -1) {
        val existingItem = list.removeAt(existingIndex)
        list.add(0, existingItem)
    } else {
        list.add(0, SearchHistoryItem(userId, isPinned = false))
    }
    if (list.size > 20) {
        saveSearchHistory(context, list.take(20))
    } else {
        saveSearchHistory(context, list)
    }
}

private fun togglePinSearchHistory(context: android.content.Context, userId: Int) {
    val list = getSearchHistory(context).map {
        if (it.userId == userId) {
            it.copy(isPinned = !it.isPinned)
        } else {
            it
        }
    }
    saveSearchHistory(context, list)
}

private fun deleteFromSearchHistory(context: android.content.Context, userId: Int) {
    val list = getSearchHistory(context).filter { it.userId != userId }
    saveSearchHistory(context, list)
}

// --- GESTURE TABS SWIPE ENABLER EXTRA COMFORT ---
fun Modifier.swipeTabGesture(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
): Modifier {
    val tabList = listOf(
        MainTab.FEEDS,
        MainTab.FRIENDS,
        MainTab.CREATE_POST,
        MainTab.NOTIFICATIONS,
        MainTab.PROFILE
    )
    val currentIndex = tabList.indexOf(currentTab)
    if (currentIndex == -1) return this

    return this.pointerInput(currentTab) {
        var dragAmountAccumulated = 0f
        detectHorizontalDragGestures(
            onDragEnd = {
                val threshold = 180f
                if (dragAmountAccumulated > threshold) {
                    if (currentIndex > 0) {
                        onTabSelected(tabList[currentIndex - 1])
                    }
                } else if (dragAmountAccumulated < -threshold) {
                    if (currentIndex < tabList.lastIndex) {
                        onTabSelected(tabList[currentIndex + 1])
                    }
                }
                dragAmountAccumulated = 0f
            },
            onDragCancel = {
                dragAmountAccumulated = 0f
            },
            onHorizontalDrag = { change, dragAmount ->
                change.consume()
                dragAmountAccumulated += dragAmount
            }
        )
    }
}

@Composable
fun PostCollage(
    imageUrlString: String,
    post: PostEntity,
    viewModel: AuraViewModel,
    onImageClick: (Int) -> Unit
) {
    val urls = remember(imageUrlString) {
        if (imageUrlString.isBlank()) emptyList() else imageUrlString.split(",").filter { it.isNotBlank() }
    }
    if (urls.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        if (urls.size == 1) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clickable { onImageClick(0) },
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = urls[0],
                    contentDescription = "Post image 1",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        } else if (urls.size == 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                urls.forEachIndexed { index, url ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onImageClick(index) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Post image ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
            }
        } else if (urls.size == 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .clickable { onImageClick(0) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = urls[0],
                        contentDescription = "Post image 1",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..2) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clickable { onImageClick(i) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = urls[i],
                                contentDescription = "Post image ${i + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }
            }
        } else if (urls.size == 4) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0..1) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onImageClick(i) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = urls[i],
                                contentDescription = "Post image ${i + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 2..3) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onImageClick(i) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = urls[i],
                                contentDescription = "Post image ${i + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }
            }
        } else {
            // 5 or more images collage with hidden count indicators as requested
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxWidth()
                        .clickable { onImageClick(0) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = urls[0],
                        contentDescription = "Post image 1",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier.weight(1.0f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..4) {
                        val isLast = i == 4
                        val hiddenCount = urls.size - 5
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onImageClick(i) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = urls[i],
                                    contentDescription = "Post image ${i + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                if (isLast && hiddenCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.65f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+$hiddenCount",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
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
}

@Composable
fun FullScreenImageViewer(
    imageUrls: List<String>,
    initialIndex: Int,
    post: com.example.data.database.PostEntity,
    viewModel: AuraViewModel,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Close Action
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Viewer",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Download Trigger Action with full storage download capability
            IconButton(
                onClick = {
                    val modelValue = imageUrls[currentIndex]
                    coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            val resolver = context.contentResolver
                            val bytes = if (modelValue.startsWith("content://")) {
                                resolver.openInputStream(android.net.Uri.parse(modelValue))?.use { it.readBytes() }
                            } else if (modelValue.startsWith("http")) {
                                java.net.URL(modelValue).openStream()?.use { it.readBytes() }
                            } else {
                                null
                            }

                            if (bytes != null) {
                                val contentValues = android.content.ContentValues().apply {
                                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "Aura_Download_${System.currentTimeMillis()}.jpg")
                                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/Aura")
                                    }
                                }
                                val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                if (uri != null) {
                                    resolver.openOutputStream(uri)?.use { outStream ->
                                        outStream.write(bytes)
                                    }
                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Successfully saved to your Gallery! 📥💾✨", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Download failed (Storage insertion failed)", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    android.widget.Toast.makeText(context, "Failed to download image (No bytes read)", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                android.widget.Toast.makeText(context, "Error: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download Photo",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Centered Main Image with Pinch-To-Zoom and Pan controls
            if (currentIndex in imageUrls.indices) {
                var scale by remember(currentIndex) { mutableStateOf(1f) }
                var offset by remember(currentIndex) { mutableStateOf(Offset.Zero) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp, bottom = 80.dp)
                        .clipToBounds()
                        .pointerInput(currentIndex) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                if (scale > 1f) {
                                    offset = Offset(
                                        x = offset.x + pan.x,
                                        y = offset.y + pan.y
                                    )
                                } else {
                                    offset = Offset.Zero
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrls[currentIndex],
                        contentDescription = "Full Screen Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            },
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }

            // Bottom Navigation Dots / Pager controls for extra comfort
            if (imageUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Image",
                            tint = if (currentIndex > 0) Color.White else Color.DarkGray
                        )
                    }

                    Text(
                        text = "${currentIndex + 1} / ${imageUrls.size}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            if (currentIndex < imageUrls.size - 1) currentIndex++
                        },
                        enabled = currentIndex < imageUrls.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Image",
                            tint = if (currentIndex < imageUrls.size - 1) Color.White else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreationPreviewCarousel(
    imageUrlString: String,
    onRemoveImage: (Int) -> Unit
) {
    val urls = remember(imageUrlString) {
        if (imageUrlString.isBlank()) emptyList() else imageUrlString.split(",").filter { it.isNotBlank() }
    }
    if (urls.isEmpty()) return

    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF3E8FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(urls) { index, url ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Preview Image",
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                // Remove individual image button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { onRemoveImage(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

fun getWebShareableUrl(uri: String): String {
    if (uri.startsWith("http://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)) {
        return uri
    }
    // High-resolution premium landscape/nature Unsplash collections that generate beautiful card preview layouts instantly
    val unsplashPool = listOf(
        "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800",
        "https://images.unsplash.com/photo-1447752875215-b2761acb3c5d?w=800",
        "https://images.unsplash.com/photo-1472214222541-d510753a49fa?w=800",
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
        "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=800",
        "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800",
        "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800",
        "https://images.unsplash.com/photo-1475924156734-496f6cac6ec1?w=800",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
        "https://images.unsplash.com/photo-1470240731273-7821a6eeb6bd?w=800",
        "https://images.unsplash.com/photo-1502082553048-f009c37129b9?w=800"
    )
    val index = kotlin.math.abs(uri.hashCode()) % unsplashPool.size
    return unsplashPool[index]
}

@Composable
fun AuraShareHubDialog(
    post: com.example.data.database.PostEntity,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    val savedUrl = remember {
        context.getSharedPreferences("AuraPrefs", android.content.Context.MODE_PRIVATE)
            .getString("appwrite_url", "") ?: ""
    }
    var functionUrlInput by remember { mutableStateOf(savedUrl) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(12.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFF7C4DFF).copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top decorative Lavender gradient bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(Color(0xFF7C4DFF), Color(0xFFD1C4E9), Color(0xFFFF4081))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF3E8FF), CircleShape)
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color(0xFF7C4DFF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Aura Unlimited Share Portal",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF7C4DFF)
                                )
                                Text(
                                    text = "Zero-Database Dynamic Linking Hub",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF3F4F6), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    var activeTab by remember { mutableStateOf(0) }

                    val tabTitles = listOf("Netlify Deploy ⚡", "Hub Config ⚙️", "Manifest XML 📦", "Why Netlify? 👑")

                    ScrollableTabRow(
                        selectedTabIndex = activeTab,
                        containerColor = Color(0xFFF3E8FF),
                        contentColor = Color(0xFF7C4DFF),
                        edgePadding = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .shadow(1.dp)
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = activeTab == index,
                                onClick = { activeTab = index },
                                text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (activeTab) {
                            0 -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "ধাপ ১: Netlify সার্ভারলেস ডিপ্লয়মেন্ট 🚀",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF7C4DFF)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "১. প্রথমে Netlify তে একটি সম্পূর্ণ ফ্রি অ্যাকাউন্ট তৈরি করুন।\n" +
                                                    "২. ড্যাশবোর্ডে গিয়ে 'Add new site' -> 'Import from existing project' সিলেক্ট করে আপনার গিটহাব কানেক্ট করুন।\n" +
                                                    "৩. 'Base directory' হিসেবে '/Netlify-Portal' দিন এবং 'Publish directory' ফিল্ডে 'public' সেট করুন।\n" +
                                                    "৪. 'Deploy site' বাটনে চাপ দিলেই কয়েক সেকেন্ডে আপনার লিঙ্ক পোর্টাল লাইভ হয়ে যাবে!",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "💡 বিস্তারিত নির্দেশিকা এই প্রজেক্টের রুট ডিরেক্টরির '/Netlify-Portal/README.md' ফাইলে দেওয়া হয়েছে!",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7C4DFF)
                                        )
                                    }
                                }
                            }
                            1 -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "পোর্টাল লিঙ্ক কনফিগারেশন ⚙️",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF7C4DFF)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "আপনার Netlify ডিপ্লয়মেন্ট লিঙ্কটি নিচে পেস্ট করুন। এর ফলে প্রতিবার শেয়ার বাটনে চাপ দিলে অটোমেটিক সঠিক লিঙ্ক তৈরি হবে:",
                                            fontSize = 10.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        androidx.compose.material3.OutlinedTextField(
                                            value = functionUrlInput,
                                            onValueChange = { newValue ->
                                                functionUrlInput = newValue
                                                context.getSharedPreferences("AuraPrefs", android.content.Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("appwrite_url", newValue)
                                                    .apply()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = {
                                                Text(
                                                    text = "e.g. https://my-aura-portal.netlify.app",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Link,
                                                    contentDescription = "URL link icon",
                                                    tint = Color(0xFF7C4DFF),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            trailingIcon = {
                                                if (functionUrlInput.isNotEmpty()) {
                                                    IconButton(
                                                        onClick = {
                                                            functionUrlInput = ""
                                                            context.getSharedPreferences("AuraPrefs", android.content.Context.MODE_PRIVATE)
                                                                .edit()
                                                                .putString("appwrite_url", "")
                                                                .apply()
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Clear",
                                                            tint = Color.Gray,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color(0xFF111827),
                                                unfocusedTextColor = Color(0xFF1E293B),
                                                focusedBorderColor = Color(0xFF7C4DFF),
                                                unfocusedBorderColor = Color(0xFFE9D5FF),
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White
                                            ),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                            2 -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Deep Linking Interceptor 🌐",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF7C4DFF)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "AndroidManifest.xml এ এই ফিল্টারটি অলরেডি সেট আছে, যাতে শেয়ার লিঙ্কে ক্লিক করলে অটোমেটিক অ্যাপ চালু হয়:",
                                            fontSize = 10.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        val manifestCode = """
                                        <intent-filter android:label="Aura Deep Link Gate">
                                            <action android:name="android.intent.action.VIEW" />
                                            <category android:name="android.intent.category.DEFAULT" />
                                            <category android:name="android.intent.category.BROWSABLE" />
                                            <data android:scheme="aura" android:host="post" />
                                            <data android:scheme="aura" />
                                        </intent-filter>
                                        """.trimIndent()

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF1E1E2E), RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = manifestCode,
                                                color = Color(0xFFCDD6F4),
                                                fontSize = 9.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                            3 -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "কেন Netlify Serverless সবচেয়ে সেরা? 💎",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF7C4DFF)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "• কোনো ডেটাবেস কানেকশন প্রয়োজন নেই, তাই ১ সেকেন্ডেরও কম সময়ে অত্যন্ত দ্রুত মেটা ট্যাগ জেনারেট করে কার্ড প্রিভিউ শো করে।\n" +
                                                    "• Netlify-র গ্লোবাল সার্ভারলেস ফাংশন সম্পূর্ণ ফ্রী এবং প্রতিদিন হাজার হাজার ট্রাফিক সহজে হ্যান্ডেল করতে পারে।\n" +
                                                    "• ক্রলাররা (WhatsApp, FB, Telegram) রিকোয়েস্ট পাঠালে এটি মেটা ট্যাগ রেসপন্স দেয় আর মোবাইল ইউজার সরাসরি লিঙ্কে ক্লিক করলে ফোনে থাকা Aura অ্যাপটি বুস্ট করে ওপেন করে!",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            try {
                                val savedBaseUrl = context.getSharedPreferences("AuraPrefs", android.content.Context.MODE_PRIVATE)
                                    .getString("appwrite_url", "") ?: ""

                                val baseDomain = if (savedBaseUrl.trim().isNotEmpty()) savedBaseUrl.trim() else "https://ais-pre-inawwf2545flos3colouiz-78211575748.asia-southeast1.run.app"
                                val cleanBaseUrl = baseDomain.trimEnd('/')

                                // Encode parameters dynamically to URL safe Base64
                                val nameB64 = android.util.Base64.encodeToString(
                                    post.authorName.toByteArray(Charsets.UTF_8),
                                    android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
                                )
                                val descB64 = android.util.Base64.encodeToString(
                                    post.content.toByteArray(Charsets.UTF_8),
                                    android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
                                )
                                val imgB64 = if (post.imageUrl.startsWith("http")) {
                                    android.util.Base64.encodeToString(
                                        post.imageUrl.toByteArray(Charsets.UTF_8),
                                        android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
                                    )
                                } else ""

                                val shareableLink = "$cleanBaseUrl/shared_post/?postId=${post.postId}&n=$nameB64&d=$descB64&i=$imgB64"

                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "Check out ${post.authorName}'s post on Aura! 💜✨\n\n\"${post.content}\"\n\n🔗 View Post & Photo:\n$shareableLink\n\n📥 Download the official APK here:\n$cleanBaseUrl"
                                    )
                                }
                                val chooser = android.content.Intent.createChooser(shareIntent, "Share Post")
                                chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(chooser)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Dynamic Post Link 📡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CA3AF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Dismiss", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

fun generateAppwriteFunctionTarGz(): ByteArray {
    val d = "$"
    val mainJsBytes = """
// 🚀 100% DATABASE-FREE APPRWRITE CLOUD FUNCTION (main.js)
// Since you reached your Appwrite Database Limit, we encode post metadata directly in the URL!
// This function requires ZERO Appwrite Database operations and is incredibly fast!

module.exports = async function (context) {
    const req = context.req;
    const res = context.res;

    // 1. Extract the encoded parameters
    const postId = req.query.postId || '1';
    const rawTitle = req.query.t || '';
    const rawDesc = req.query.d || '';
    const rawImage = req.query.i || '';

    // 2. Decode Base64 URL-safe parameters to show original details in Rich Previews
    let title = "Aura Shared Post";
    let description = "Click to view this complete post inside the Aura App!";
    let imageUrl = "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=800"; // default fallbacks

    try {
        if (rawTitle) {
            title = Buffer.from(rawTitle, 'base64').toString('utf8');
        }
        if (rawDesc) {
            description = Buffer.from(rawDesc, 'base64').toString('utf8');
        }
        if (rawImage) {
            imageUrl = Buffer.from(rawImage, 'base64').toString('utf8');
        }
    } catch (e) {
        context.error("Base64 decoding failed: " + e.message);
    }

    // 3. Render dynamic HTML document containing standard Open Graph (OG) meta tags
    const html = `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${d}{title}</title>
    
    <!-- Open Graph OG tags for WhastApp, Facebook & Telegram previews -->
    <meta property="og:title" content="${d}{title}" />
    <meta property="og:description" content="${d}{description}" />
    <meta property="og:image" content="${d}{imageUrl}" />
    <meta property="og:image:width" content="1200" />
    <meta property="og:image:height" content="630" />
    <meta property="og:type" content="article" />
    <meta property="og:site_name" content="Aura" />

    <!-- Twitter Previews -->
    <meta name="twitter:card" content="summary_large_image" />
    <meta name="twitter:title" content="${d}{title}" />
    <meta name="twitter:description" content="${d}{description}" />
    <meta name="twitter:image" content="${d}{imageUrl}" />

    <!-- JavaScript deep-linking engine redirecting directly to your Applet -->
    <script>
        window.onload = function() {
            // Trigger Android Deep Link Interceptor
            window.location.href = 'aura://post/' + '${d}{postId}';
            
            // Redirect fallback for users who don't have the app yet
            setTimeout(function() {
                window.location.href = 'https://ais-pre-inawwf2545flos3colouiz-78211575748.asia-southeast1.run.app';
            }, 2500);
        };
    </script>
</head>
<body style="font-family: -apple-system, sans-serif; background: #FAF5FF; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; text-align: center; color: #7C4DFF;">
    <div style="padding: 24px; background: white; border-radius: 20px; box-shadow: 0 10px 30px rgba(124, 77, 255, 0.15); max-width: 400px; margin: 16px; border: 2px solid #F3E8FF;">
        <h2 style="margin: 0 0 12px 0;">Opening inside Aura...</h2>
        <p style="color: #666; font-size: 14px; line-height: 1.5; margin-bottom: 20px;">Attempting to launch Aura App on your Android device. If you don't have the app installed, please download the APK!</p>
        <div style="width: 32px; height: 32px; border: 3px solid #F3E8FF; border-top-color: #7C4DFF; border-radius: 50%; animation: spin 1s infinite linear; display: inline-block;"></div>
        <style>@keyframes spin {100% {transform: rotate(360deg);}}</style>
    </div>
</body>
</html>`;

    return res.html(html);
};
""".trimIndent().toByteArray(Charsets.UTF_8)

    val packageJsonBytes = """
{
  "name": "aura-share-hub",
  "version": "1.0.0",
  "description": "Infinite database-free Aura share previews",
  "main": "main.js",
  "dependencies": {}
}
""".trimIndent().toByteArray(Charsets.UTF_8)

    val bos = java.io.ByteArrayOutputStream()
    val gzos = java.util.zip.GZIPOutputStream(bos)

    fun writeTarEntry(name: String, content: ByteArray) {
        val header = createTarHeader(name, content.size.toLong())
        gzos.write(header)
        gzos.write(content)
        val remainder = (content.size % 512)
        if (remainder > 0) {
            gzos.write(ByteArray(512 - remainder))
        }
    }

    writeTarEntry("main.js", mainJsBytes)
    writeTarEntry("package.json", packageJsonBytes)

    gzos.write(ByteArray(1024))
    gzos.finish()
    gzos.close()

    return bos.toByteArray()
}

fun createNullTerminatedBytes(str: String): ByteArray {
    val stringBytes = str.toByteArray(Charsets.UTF_8)
    val result = ByteArray(stringBytes.size + 1)
    System.arraycopy(stringBytes, 0, result, 0, stringBytes.size)
    result[stringBytes.size] = 0.toByte()
    return result
}

fun createTarHeader(name: String, size: Long): ByteArray {
    val header = ByteArray(512)
    val nameBytes = name.toByteArray(Charsets.UTF_8)
    System.arraycopy(nameBytes, 0, header, 0, minOf(nameBytes.size, 99))
    
    val modeBytes = createNullTerminatedBytes("0000644")
    System.arraycopy(modeBytes, 0, header, 100, minOf(modeBytes.size, 8))
    
    val uidBytes = createNullTerminatedBytes("0000000")
    System.arraycopy(uidBytes, 0, header, 108, minOf(uidBytes.size, 8))
    
    val gidBytes = createNullTerminatedBytes("0000000")
    System.arraycopy(gidBytes, 0, header, 116, minOf(gidBytes.size, 8))
    
    val octalSize = String.format(java.util.Locale.US, "%011o", size)
    val sizeBytes = createNullTerminatedBytes(octalSize)
    System.arraycopy(sizeBytes, 0, header, 124, minOf(sizeBytes.size, 12))
    
    val mtimeBytes = createNullTerminatedBytes("14141414141")
    System.arraycopy(mtimeBytes, 0, header, 136, minOf(mtimeBytes.size, 12))
    
    for (i in 148 until 156) {
        header[i] = ' '.toByte()
    }
    header[156] = '0'.toByte()
    
    val magicBytes = createNullTerminatedBytes("ustar")
    System.arraycopy(magicBytes, 0, header, 257, minOf(magicBytes.size, 6))
    
    header[263] = '0'.toByte()
    header[264] = '0'.toByte()
    
    var checksum = 0L
    for (b in header) {
        checksum += (b.toInt() and 0xFF)
    }
    
    val checksumStr = String.format(java.util.Locale.US, "%06o", checksum)
    val checksumBytes = ByteArray(8)
    val chBytes = checksumStr.toByteArray()
    System.arraycopy(chBytes, 0, checksumBytes, 0, minOf(chBytes.size, 6))
    checksumBytes[6] = 0.toByte()
    checksumBytes[7] = ' '.toByte()
    System.arraycopy(checksumBytes, 0, header, 148, 8)
    
    return header
}

