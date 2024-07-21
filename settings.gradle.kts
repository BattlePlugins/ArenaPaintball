rootProject.name = "ArenaPaintball"

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        // Paper, Velocity
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}