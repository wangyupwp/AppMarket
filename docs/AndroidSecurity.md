# Android 发布签名与混淆

正式签名证书的 SHA-256 固定在 `buildSrc/src/main/kotlin/ProjectConfig.kt`：

```text
4A:5C:8F:13:13:26:A4:A3:1A:72:4C:09:46:EA:16:8F:D9:D1:7D:E3:21:DB:47:B8:EA:48:CD:5A:24:F6:CA:08
```

该指纹属于公开证书信息，APK 中不包含密钥库密码或私钥密码。

## 校验流程

- `verifyReleaseSigningCertificate` 在 `preReleaseBuild` 前读取配置的签名证书并核对指纹。缺少正式签名配置、别名不是密钥项或证书不匹配时，发布构建失败；不再回退使用 debug 签名。
- 正式版在 `MarketApplication.attachBaseContext()` 中校验已安装 APK 的签名，早于 ContentProvider、Koin 和后台任务初始化。
- Android 9 及以上读取 `SigningInfo.apkContentsSigners`；Android 8 使用兼容接口。所有当前签名者都必须受信任。读取失败、签名为空或包含其他证书时拒绝启动。
- 不使用历史签名证书放宽当前签名者校验。正式密钥轮换时，需要验证新证书并同步更新固定指纹，同时处理 Android 的签名轮换及应用更新兼容性。
- Debug 构建不强制此校验，便于使用开发证书调试。Release 开关由构建常量控制，不依赖 Manifest 中的 debuggable 标记。

## R8

Release 使用 `proguard-android-optimize.txt`，启用代码压缩、优化、名称混淆和资源裁剪，关闭 Java / JNI 调试。自定义规则明确启用类重打包、使用通用源文件名并移除 `Log.d` / `Log.v`；系统组件和 root bridge 的必要入口继续保留。

签名校验类不添加 `-keep`，由 R8 正常优化和混淆。崩溃还原映射位于 `app/android/build/outputs/mapping/release/mapping.txt`，应单独保存，不随 APK 分发。

R8 提高阅读和修改代码的成本，不加密所有字符串，也不能保证代码无法被反编译。本地校验仍可能被修改或 Hook；它主要用于识别未获授权的重新签名版本。

## 构建与验证

通过未提交的 `local.properties` 或环境变量配置 `KEYSTORE_PATH`、`KEYSTORE_PASS`、`KEY_ALIAS`、`KEY_PASSWORD`。CI 签名证书也必须与固定指纹匹配。

```text
gradlew :app:android:testDebugUnitTest
gradlew :app:android:assembleRelease --no-configuration-cache
apksigner verify --verbose --print-certs <release.apk>
```

测试覆盖合法证书、重签名、证书字节变化、空数据、无效指纹及混合签名者。构建后还需核对 APK 证书、合并后的 Manifest、R8 配置与映射，并在设备上启动正式版。

参考：[SigningInfo](https://developer.android.com/reference/android/content/pm/SigningInfo)、[R8 优化](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization)。
