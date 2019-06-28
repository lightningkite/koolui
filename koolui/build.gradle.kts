import com.lightningkite.konvenience.gradle.*
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.*

plugins {
    kotlin("multiplatform") version "1.3.21"
    `maven-publish`
    id("com.android.library") version "3.3.1"
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
    maven("https://dl.bintray.com/pixplicity/android")
}

val versions = Properties().apply {
    load(project.file("versions.properties").inputStream())
}

group = "com.lightningkite"
version = versions.getProperty(project.name)

android {
    project.ext.set("android.useAndroidX", true)
    project.ext.set("android.enableJetifier", true)

    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
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
            implementationSet(standardLibrary)
            implementationSet(coroutines(versions.getProperty("kotlinx_coroutines")))
            apiSet(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon"), groupings = KTargetPredicates.binary))
            apiSet(projectOrMavenDashPlatform("com.lightningkite", "lokalize", versions.getProperty("lokalize"), groupings = KTargetPredicates.binary))
            apiSet(projectOrMavenDashPlatform("com.lightningkite", "reacktive", versions.getProperty("reacktive"), groupings = KTargetPredicates.binary))
            apiSet(projectOrMavenDashPlatform("com.lightningkite", "recktangle", versions.getProperty("recktangle"), groupings = KTargetPredicates.binary))
        }
        test {
            implementationSet(testing)
            implementationSet(testingAnnotations)
        }
        KTarget.android.sources {
            main {
                dependencies {

                    api("androidx.appcompat:appcompat:1.0.2")
                    api("androidx.cardview:cardview:1.0.0")
                    api("androidx.gridlayout:gridlayout:1.0.0")
                    api("androidx.recyclerview:recyclerview:1.0.0")
                    api("com.google.android.material:material:1.1.0-alpha07")
                    api("com.pixplicity.sharp:library:1.1.0")
                    api("com.squareup.picasso:picasso:2.71828")

//                    api("com.lightningkite:lokalize-jvm:${versions.getProperty("lokalize")}")
//                    api("com.lightningkite:reacktive-jvm:${versions.getProperty("reacktive")}")
//                    api("com.lightningkite:recktangle-jvm:${versions.getProperty("recktangle")}")

                    api("com.google.firebase:firebase-messaging:18.0.0")
                }
            }
        }
        KTarget.javafx.sources {
            main {
                dependencies {
                    sequenceOf("win", "mac", "linux").forEach {
                        api("org.openjfx:javafx-base:12:$it")
                        api("org.openjfx:javafx-graphics:12:$it")
                        api("org.openjfx:javafx-controls:12:$it")
                        api("org.openjfx:javafx-media:12:$it")
                        api("org.openjfx:javafx-web:12:$it")
                        api("org.openjfx:javafx-swing:12:$it")
                    }

                    api("com.jfoenix:jfoenix:9.0.3")
                    api("org.apache.xmlgraphics:batik-dom:1.10")
                    api("org.apache.xmlgraphics:batik-anim:1.10")
                    api("org.apache.xmlgraphics:batik-bridge:1.10")
                    api("org.apache.xmlgraphics:batik-transcoder:1.10")
                    api("xalan:xalan:2.7.2")
                    api("org.apache.logging.log4j:log4j-api:2.5")

                    api("org.apache.xmlgraphics:xmlgraphics-commons:2.2")
                    api("org.apache.logging.log4j:log4j-core:2.5")
                }
            }
        }
        KTarget.jvmVirtual.sources {}
        isJs.sources {}
    }
}

publishing {
    doNotPublishMetadata()
    repositories {
        bintray(
                project = project,
                organization = "lightningkite",
                repository = "com.lightningkite.krosslin"
        )
    }

    appendToPoms {
        github("lightningkite", project.name)
        licenseMIT()
        developers {
            developer {
                id.set("UnknownJoe796")
                name.set("Joseph Ivie")
                email.set("joseph@lightningkite.com")
                timezone.set("America/Denver")
                roles.set(listOf("architect", "developer"))
                organization.set("Lightning Kite")
                organizationUrl.set("http://www.lightningkite.com")
            }
        }
    }
}
