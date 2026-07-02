# Keep our app classes and database entities intact to prevent reflection/database issues
-keep class com.example.** { *; }
-keep class com.imrul.aura.** { *; }

# Speed up compilation dramatically by skipping optimization and obfuscation passes
-dontoptimize
-dontobfuscate

# Preserve signatures and annotations for reflection-based libraries (Moshi, Retrofit, Room)
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Keep Room database and its DAO/Entity classes
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class * {
    @androidx.room.Database *;
    @androidx.room.Dao *;
    @androidx.room.Entity *;
}

