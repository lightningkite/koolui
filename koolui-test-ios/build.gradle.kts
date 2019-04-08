import com.lightningkite.konvenience.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import java.util.*

plugins {
    kotlin("multiplatform") version "1.3.21"
    `maven-publish`
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


kotlin {
    val tryTargets = KTarget.run {
        setOf(
                iosArm64,
                iosX64
        )
    }
    sources(tryTargets = tryTargets) {
        main {
            dependency(standardLibrary)
            apiSet(coroutines(versions.getProperty("kotlinx_coroutines")))
            apiSet(project(":koolui-test-common"))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
        }
    }
//    iosArm64 {
//        binaries {
//            framework(listOf(DEBUG, RELEASE)) {
//                transitiveExport = true
//                export(project(":koolui-test-common"))
//            }
//        }
////        compilations.getByName("main").outputKinds("framework")
//    }
//    iosX64 {
//        binaries {
//            framework(listOf(DEBUG, RELEASE)) {
//                transitiveExport = true
//                export(project(":koolui-test-common"))
//            }
//        }
////        compilations.getByName("main").outputKinds("framework")
//    }
}

iosApp("FromKotlin")

//tasks.create("copyFramework") {
//    val buildType = (project.findProperty("kotlin.build.type") as? String) ?: "DEBUG"
//    val target = (project.findProperty("kotlin.build.target") as? String) ?: "iosArm64"
////    dependsOn(kotlin.targets.getByName(target)!!.compilations.getByName("main").let{ it as KotlinNativeCompilation }.linkTaskName("FRAMEWORK", buildType))
//    val dependsOnTaskName = "link${buildType}Framework$target"
//    dependsOn(tasks.find { it.name.equals(dependsOnTaskName, true) }!!)
//
//    doLast {
//        //        val srcFile = kotlin.targets.getByName(target)!!.compilations.getByName("main").let{ it as KotlinNativeCompilation }.getBinary("FRAMEWORK", buildType)
//        val srcFile = project.file("build/bin/$target/${buildType}Framework")
//        val targetDir = System.getProperty("configuration.build.dir") ?: project.file("build/output").path
//        println("Copying from $srcFile to $targetDir")
//        copy {
//            from(srcFile)
//            into(targetDir)
//        }
//    }
//}
