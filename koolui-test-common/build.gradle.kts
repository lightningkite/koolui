import com.lightningkite.konvenience.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.*

plugins {
    kotlin("multiplatform") version "1.3.21"
    `maven-publish`
    id("com.android.library")// version "3.3.1"
}

buildscript {
    repositories {
        mavenLocal()
        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
    }
    dependencies {
        classpath("com.lightningkite:konvenience:+")
    }
}
apply(plugin = "com.lightningkite.konvenience")

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

android {
    compileSdkVersion(27)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(27)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
        }
    }
}


kotlin {

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

    val tryTargets = KTarget.run {
        setOf(
                android,
                javafx,
                jvmVirtual,
                js
        )
    }
    sources(tryTargets = tryTargets) {
        main {
            dependency(standardLibrary)
            dependency(coroutines(versions.getProperty("kotlinx_coroutines")).type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon")) {
                isJvm uses maven("com.lightningkite", "kommon-jvm", versions.getProperty("kommon"))
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "lokalize", versions.getProperty("lokalize")) {
                isJvm uses maven("com.lightningkite", "lokalize-jvm", versions.getProperty("lokalize"))
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "reacktive", versions.getProperty("reacktive")) {
                isJvm uses maven("com.lightningkite", "reacktive-jvm", versions.getProperty("reacktive"))
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "recktangle", versions.getProperty("recktangle")) {
                isJvm uses maven("com.lightningkite", "recktangle-jvm", versions.getProperty("recktangle"))
            }.type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "koolui", versions.getProperty("koolui")).type(KDependencyType.Api))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
        }
    }
    android {
        publishLibraryVariants("release")
    }
}