package com.example.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeTrackItem(
    val id: String,
    val name: String,
    val artistName: String,
    val imageUrl: String?,
    val previewUrl: String?
)

object YouTubeClient {
    private val presetTracks = listOf(
        YouTubeTrackItem(
            id = "p1",
            name = "Rainy Day Chill Lo-Fi Beats",
            artistName = "Lofi Study Session",
            imageUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=300&q=80",
            previewUrl = "Rainy Day Chill Lo-Fi Beats"
        ),
        YouTubeTrackItem(
            id = "p2",
            name = "Acoustic Cafe Guitar Covers",
            artistName = "Acoustic Vibe",
            imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300&q=80",
            previewUrl = "Acoustic Cafe Guitar Covers"
        ),
        YouTubeTrackItem(
            id = "p3",
            name = "Synthwave Retro Highway Drive",
            artistName = "Neon Rider",
            imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300&q=80",
            previewUrl = "Synthwave Retro Highway Drive"
        ),
        YouTubeTrackItem(
            id = "p4",
            name = "Classical Piano Focus Masterpieces",
            artistName = "Nirvana Symphony",
            imageUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=300&q=80",
            previewUrl = "Classical Piano Focus Masterpieces"
        ),
        YouTubeTrackItem(
            id = "p5",
            name = "Peaceful Nature Meditation Soundscape",
            artistName = "Forest Ambient",
            imageUrl = "https://images.unsplash.com/photo-1445743432342-eac500cf72b7?w=300&q=80",
            previewUrl = "Peaceful Nature Meditation Soundscape"
        ),
        YouTubeTrackItem(
            id = "p6",
            name = "Bollywood Soft Acoustic Melodies",
            artistName = "Desi Acoustic Sessions",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&q=80",
            previewUrl = "Bollywood Soft Acoustic Melodies"
        )
    )

    suspend fun searchTracks(query: String, order: String? = null): List<YouTubeTrackItem> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return presetTracks
        
        // Let's dynamically create a search response item matching the query + some nice context
        val isLink = trimmed.startsWith("http://") || trimmed.startsWith("https://")
        val trackTitle = if (isLink) {
            if (trimmed.contains("youtu")) "Pasted YouTube Track" else "Pasted Custom Web Track"
        } else {
            trimmed
        }
        
        return listOf(
            YouTubeTrackItem(
                id = "custom_" + System.currentTimeMillis(),
                name = trackTitle,
                artistName = if (isLink) "Direct Embed Web Link" else "Selected via Smart Search",
                imageUrl = "https://images.unsplash.com/photo-1487180142328-0c4e37023af5?w=300&q=80",
                previewUrl = trimmed
            )
        ) + presetTracks.filter { 
            it.name.contains(trimmed, ignoreCase = true) || it.artistName.contains(trimmed, ignoreCase = true)
        }
    }

    suspend fun getTrendingTracks(): List<YouTubeTrackItem> {
        return presetTracks
    }

    suspend fun getNewestTracks(): List<YouTubeTrackItem> {
        return presetTracks.reversed()
    }
}

fun extractYouTubeVideoId(url: String): String? {
    val clean = url.trim()
    return if (clean.contains("youtu.be/")) {
        clean.substringAfter("youtu.be/").substringBefore("?").substringBefore("&").trim()
    } else if (clean.contains("v=")) {
        clean.substringAfter("v=").substringBefore("&").substringBefore("?").trim()
    } else if (clean.contains("embed/")) {
        clean.substringAfter("embed/").substringBefore("?").substringBefore("&").trim()
    } else {
        null
    }
}

fun YouTubeTrackItem.toSerializedString(): String {
    return "$name|||$artistName|||${imageUrl ?: ""}|||${previewUrl ?: ""}"
}

fun String.toYouTubeTrackItem(): YouTubeTrackItem {
    if (this.isBlank()) {
        return YouTubeTrackItem("none", "No Track Selected", "Aura Music System", null, null)
    }
    val parts = this.split("|||")
    if (parts.size >= 4) {
        val nameVal = parts[0]
        val artistVal = parts[1]
        val imageVal = parts[2]
        val urlVal = parts[3]
        
        var finalName = nameVal
        var finalArtist = artistVal
        var finalImage = imageVal.ifBlank { null }
        
        val videoId = extractYouTubeVideoId(urlVal)
        if (videoId != null) {
            if (finalImage.isNullOrBlank() || finalImage.contains("photo-1470225620780") || finalImage.contains("photo-1487180142328")) {
                finalImage = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
            }
            if (finalName.isBlank() || finalName == "Imported Video/Music Track" || finalName == "Custom YouTube Link" || finalName == "Imported Music Track") {
                finalName = "YouTube Music Track (#$videoId)"
                finalArtist = "YouTube Music App Sync"
            }
        }
        
        return YouTubeTrackItem(
            id = "custom_" + parts[0].hashCode(),
            name = finalName.trim(),
            artistName = finalArtist.trim(),
            imageUrl = finalImage,
            previewUrl = urlVal.trim()
        )
    }
    
    val videoId = extractYouTubeVideoId(this)
    val finalImage = if (videoId != null) "https://img.youtube.com/vi/$videoId/hqdefault.jpg" else null
    val finalName = if (videoId != null) "YouTube Music Track (#$videoId)" else this
    return YouTubeTrackItem(
        id = "legacy_" + this.hashCode(),
        name = finalName,
        artistName = "Aura Music Player",
        imageUrl = finalImage,
        previewUrl = this
    )
}

