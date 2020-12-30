# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lirui/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


####################################################################################################
#一般以下情况都会不混淆：
#1.使用了自定义控件那么要保证它们不参与混淆
#2.使用了枚举要保证枚举不被混淆
#3.对第三方库中的类不进行混淆
#4.运用了反射的类也不进行混淆
#5.使用了 Gson 之类的工具要使 JavaBean 类即实体类不被混淆
#6.在引用第三方库的时候，一般会标明库的混淆规则的，建议在使用的时候就把混淆规则添加上去，免得到最后才去找
#7.有用到 WebView 的 JS 调用也需要保证写的接口方法不混淆，原因和第一条一样
#8.Parcelable 的子类和 Creator 静态成员变量不混淆，否则会产生 Android.os.BadParcelableException 异常
#9.Android四大组件和Application最好也不要混淆

#---------------------------------基本指令区----------------------------------
-optimizationpasses 5       # 指定代码的压缩级别
-dontusemixedcaseclassnames     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses        # 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers       # 指定不去忽略包可见的库类的成员
-dontpreverify      # 混淆时是否做预校验
-verbose        # 混淆时是否记录日志
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*      # 混淆时所采用的算法
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------
-ignorewarnings     # 是否忽略检测，（是）
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
#-ignorewarnings -keep class * { public private *; }

#如果有引用v4包可以添加下面这行
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#表示不混淆R文件中的所有静态字段
-keep class **.R$* {
    public static <fields>;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}


#################### FaceUnity #######################
-keep class com.faceunity.wrapper.faceunity {*;}
-keep class com.faceunity.wrapper.faceunity$* {*;}

#################### 本 demo 用到的第三方库 ######################

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder