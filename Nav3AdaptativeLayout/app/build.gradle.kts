plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Necessário se você usar @Serializable nas suas NavKeys (opcional, mas recomendado)
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.br.nav3adaptativelayout"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.br.nav3adaptativelayout"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    val nav3_version = "1.2.0" // Versão estável mais recente (ou use 1.3.0-alpha02 para testar novos recursos)

    // 1. Núcleo (Obrigatórios)
    // Contém a API central: NavEntry, EntryProvider e DSLs
    implementation("androidx.navigation3:navigation3-runtime:$nav3_version")
    // Contém as classes de renderização UI: NavDisplay e Scene
    implementation("androidx.navigation3:navigation3-ui:$nav3_version")

    // 2. Integrações Recomendadas (Opcionais)
    // Permite que ViewModels sobrevivam apenas enquanto a tela existir na back stack
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.8.0")

    // Fornece SceneStrategies e metadados para telas adaptáveis (útil para o exemplo Mestre-Detalhe)
    implementation("androidx.compose.material3.adaptive:adaptive-navigation3:1.0.0")

    // Permite serialização segura das suas chaves de navegação
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")

    implementation("androidx.compose.material3:material3-window-size-class")

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Robolectric + Compose UI Test
    testImplementation("org.robolectric:robolectric:4.14.1")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}