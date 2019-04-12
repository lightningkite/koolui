pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        jcenter()
        mavenLocal()
        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

include("koolui")
include("koolui-test")
include("koolui-test-common")
include("koolui-test-android")
include("koolui-test-javafx")
include("koolui-test-common-indirect")
include("koolui-test-js")

enableFeaturePreview("GRADLE_METADATA")
