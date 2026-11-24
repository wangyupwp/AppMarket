-dontwarn org.slf4j.helpers.SubstituteLogger

# Release 已开启 R8 全量优化与资源裁剪；只保留明确需要反射或由系统调用的入口。
-allowaccessmodification
-repackageclasses
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# 删除开发调试日志；保留 warning/error 以便定位线上故障。
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}

-keep class com.app.market.data.install.backend.root.RootBridgeMain {
    public static void main(java.lang.String[]);
}

-keep class com.app.market.install.InstallResultReceiver { *; }
-keep class com.app.market.install.InstallForegroundService { *; }
-keep class com.app.market.install.InstallNotificationActionReceiver { *; }
