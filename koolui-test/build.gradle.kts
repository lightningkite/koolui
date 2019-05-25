import com.lightningkite.konvenience.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.*

plugins {
    id("com.android.application") version "3.3.1"
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

val jvmServer = KTarget.jvmVirtual.copy(name = "jvmServer")

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
            dependency(ktorClient(versions.getProperty("ktor")).type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "lokalize", versions.getProperty("lokalize"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "reacktive", versions.getProperty("reacktive"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "recktangle", versions.getProperty("recktangle"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "koolui", versions.getProperty("koolui"), groupings = KTargetPredicates.ui).type(KDependencyType.Api))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
        }

        KTarget.android.sources {
            main {
                apiSingle(maven("com.android.support", "multidex", "1.0.3"))
            }
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
        multiDexEnabled = true
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

javaApp(forTarget = KTarget.javafx, mainClassName = "com.lightningkite.koolui.test.MainKt")
jsApp(forTarget = KTarget.js)