import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.example.dndcombatmanager.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DnDCombatManager"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.file("packaging/icon.icns"))
            }
            windows {
                iconFile.set(project.file("packaging/icon.ico"))
            }
            linux {
                iconFile.set(project.file("packaging/icon.png"))
            }
        }
    }
}