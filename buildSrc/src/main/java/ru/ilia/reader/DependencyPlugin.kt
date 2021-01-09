package ru.ilia.reader

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

class DependencyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project.dependencies) {
            implementation(Deps.kotlin)
            implementation(Deps.androidx)
            implementation(Deps.material)
            implementation(Deps.constraint)
            implementation(Deps.nearby)
            implementation(Deps.qr)

            testImplementation(Deps.junit)

            androidTestImplementation(Deps.junitAndroid)
            androidTestImplementation(Deps.espresso)
        }
    }

    private fun DependencyHandler.implementation(dependency: Any) {
        add("implementation", dependency)
    }

    private fun DependencyHandler.testImplementation(dependency: Any) {
        add("testImplementation", dependency)
    }

    private fun DependencyHandler.androidTestImplementation(dependency: Any) {
        add("androidTestImplementation", dependency)
    }
}