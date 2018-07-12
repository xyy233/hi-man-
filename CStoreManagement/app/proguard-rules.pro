# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留R下面的资源
-keep class **.R$* {*;}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留在Activity中的方法参数是view的方法
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 保留Serializable序列化的类不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#保留bean
-keep class com.cstore.zhiyazhang.cstoremanagement.bean.**{*;}
#动态权限
-keep class pub.devrel.easypermissions.**{*;}
-dontwarn pub.devrel.easypermissions.**
#Glide图形加载
-keep class com.bumptech.glide.**{*;}
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#图片view,随意拖动放大缩小
-keep class com.github.chrisbanes.photoview.**{*;}
-dontwarn com.github.chrisbanes.photoview.**

##http请求
#-dontwarn com.squareup.okhttp3.**
#-keep class com.squareup.okhttp3.** { *;}
#-dontwarn okio.**
#-keep class com.zhy.http.okhttp.**{*;}
#-keep class com.zhy.http.okhttp.**

#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}

#Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
#zxing
-keep class com.uuzuche.lib_zxing.**{*;}
-keep class com.google.zxing.**{*;}
-dontwarn com.uuzuche.lib_zxing.**
-dontwarn com.google.zxing.**
#wxpay
-keep class com.github.wxpay.sdk.**{*;}
-dontwarn com.github.wxpay.sdk.**
#alipay
-keep class com.alipay.api.**{*;}
-dontwarn com.alipay.api.**
#Google pano
#-keep class com.google.**{*;}
#-dontwarn com.google.**
#tray
-keep class net.grandcentrix.tray.**{*;}
-dontwarn net.grandcentrix.tray.**

-ignorewarnings

-keep class com.gprinter.**{*;}
-dontwarn com.gprinter.**

-keep class taobe.tec.jcc.**{*;}
-dontwarn taobe.tec.jcc.**

-keep class org.kobjects.**{*;}
-dontwarn org.kobjects.**

-keep class org.ksoap2.**{*;}
-dontwarn org.ksoap2.**

-keep class org.kxml2.**{*;}
-dontwarn org.kxml2.**

-keep class org.xmlpull.**{*;}
-dontwarn org.xmlpull.**

-keep class com.lidroid.xutils.**{*;}
-dontwarn com.lidroid.xutils.**

