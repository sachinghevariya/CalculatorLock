# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
-keep,allowobfuscation,allowshrinking class retrofit2.**
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-flattenpackagehierarchy
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Keep the names of classes/members used in the AndroidManifest.xml files
-keep class **.R$* {
    public static <fields>;
}

# Keep all classes that might be used as fragment classes
-keep class * extends androidx.fragment.app.Fragment
-keep class * extends androidx.fragment.app.DialogFragment

# Keep classes with a default constructor that might be instantiated by the system
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep the default constructor for fragments
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    <init>(...);
}

# Keep the default constructor for activities and services
-keepclassmembers class * extends android.app.Activity {
    <init>(...);
}

-keepclassmembers class * extends android.app.Service {
    <init>(...);
}

# Preserve annotations
-keepattributes *Annotation*


-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.AlbumMediaFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.AllAudioFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.AllDocFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.AllPhotosFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.CameraFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HideAudioFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HideDocFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HidePhotoAlbumFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HidePhotosFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.ImageViewerFragment { *; }
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.TrashFragment { *; }


# keep the class and specified members from being removed or renamed
-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem { *; }
-keepclassmembers class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem { *; }
-keepnames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem { *; }
-keepclassmembernames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem { *; }

-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems { *; }
-keepclassmembers class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems { *; }
-keepnames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems { *; }
-keepclassmembernames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems { *; }

-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem { *; }
-keepclassmembers class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem { *; }
-keepnames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem { *; }
-keepclassmembernames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem { *; }


-keep class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType { *; }
-keepclassmembers class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType { *; }
-keepnames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType { *; }
-keepclassmembernames class calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType { *; }

