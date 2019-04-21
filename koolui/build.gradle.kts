import com.lightningkite.konvenience.gradle.*
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication
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
version = versions.getProperty(project.name)

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
            dependency(coroutines(versions.getProperty("kotlinx_coroutines")))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "lokalize", versions.getProperty("lokalize"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "reacktive", versions.getProperty("reacktive"), groupings = KTargetPredicates.binary))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "recktangle", versions.getProperty("recktangle"), groupings = KTargetPredicates.binary))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
        }
        KTarget.android.sources {
            main {
                dependencies {

                    val compatVersion = "27.1.1"
                    api("com.android.support:appcompat-v7:$compatVersion")
                    api("com.android.support:cardview-v7:$compatVersion")
                    api("com.android.support:gridlayout-v7:$compatVersion")
                    api("com.android.support:recyclerview-v7:$compatVersion")
                    api("com.android.support:design:$compatVersion")
                }
            }
        }
        KTarget.javafx.sources {
            main {
                dependencies {
                    api("com.jfoenix:jfoenix:8.0.8")
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
