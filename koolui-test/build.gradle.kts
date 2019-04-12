import com.lightningkite.konvenience.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.*

plugins {
    id("com.android.application")// version "3.3.1"
    kotlin("multiplatform") version "1.3.21"
//    kotlin("dce-js") version "1.3.21"
//    id("org.jetbrains.kotlin.frontend") version "0.0.45"
    `maven-publish`
}

buildscript {
    repositories {
        mavenLocal()
        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    dependencies {
        classpath("com.lightningkite:konvenience:+")
    }
}
apply(plugin = "com.lightningkite.konvenience")
apply(plugin = "kotlin-dce-js")


repositories {
    mavenLocal()
    google()
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
    maven("https://kotlin.bintray.com/kotlinx")
}

val versions = Properties().apply {
    load(project.file("versions.properties").inputStream())
}

group = "com.lightningkite"
version = versions.getProperty("koolui")

val jvmVirtual = KTarget(
        name = "jvmVirtual",
        platformType = KotlinPlatformType.jvm,
        konanTarget = null,
        worksOnMyPlatform = { true },
        configure = {
            jvm("jvmVirtual") {
                attributes {
                    attribute(KTarget.attributeUI, "jvmVirtual")
                }
            }
        }
)

val jvmServer = KTarget(
        name = "jvmServer",
        platformType = KotlinPlatformType.jvm,
        konanTarget = null,
        worksOnMyPlatform = { true },
        configure = {
            jvm("jvmServer") {
                attributes {
                    attribute(KTarget.attributeUI, "jvmVirtual")
                }
            }
        }
)

kotlin {
    val tryTargets = KTarget.run {
        setOf(
                android,
                javafx,
                jvmVirtual,
                jvmServer,
                js,
                iosArm64,
                iosX64
        )
    }
    sources(tryTargets = tryTargets) {
        main {
            dependency(standardLibrary)
            dependency(coroutines(versions.getProperty("kotlinx_coroutines")).type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon")) {
                isJvm uses projectOrMaven("com.lightningkite", "kommon-jvm", versions.getProperty("kommon"), ":kommon")
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "lokalize", versions.getProperty("lokalize")) {
                isJvm uses projectOrMaven("com.lightningkite", "lokalize-jvm", versions.getProperty("lokalize"), ":lokalize")
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "reacktive", versions.getProperty("reacktive")) {
                isJvm uses projectOrMaven("com.lightningkite", "reacktive-jvm", versions.getProperty("reacktive"), ":reacktive")
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "recktangle", versions.getProperty("recktangle")) {
                isJvm uses projectOrMaven("com.lightningkite", "recktangle-jvm", versions.getProperty("recktangle"), ":recktangle")
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "koolui", versions.getProperty("koolui")).type(KDependencyType.Api))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
        }
    }
}

android {
    compileSdkVersion(27)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(27)

        applicationId = "com.lightningkite.kotlinx.test"

        versionCode = 1
        versionName = "1.0"
    }

//    lintOptions {
//        tasks.lint.enabled = false
//    }

//    compileOptions {
//        targetCompatibility("8")
//        sourceCompatibility("8")
//    }

    packagingOptions {
        exclude("META-INF/**.kotlin_module")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
        }
    }
}

//javaApp(forTarget = KTarget.javafx, mainClassName = "com.lightningkite.koolui.test.MainKt")
jsApp(forTarget = KTarget.js)