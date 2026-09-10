pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // 阿里云的 jcenter 镜像，用来下载 DanmakuFlameMaster 这种老库
        maven("https://maven.aliyun.com/repository/jcenter")

        maven("https://jitpack.io")
    }
}

rootProject.name = "bilibili"
include(":app")
