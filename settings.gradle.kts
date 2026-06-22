pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "gpr-vbwd-android-core"
            url = uri("https://maven.pkg.github.com/vbwd-platform/vbwd-android-core")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: (providers.gradleProperty("gpr.user").orNull ?: "")
                password = System.getenv("GITHUB_TOKEN") ?: (providers.gradleProperty("gpr.key").orNull ?: "")
            }
        }
    }
}
rootProject.name = "vbwd-android-tarot"
include(":tarot")
