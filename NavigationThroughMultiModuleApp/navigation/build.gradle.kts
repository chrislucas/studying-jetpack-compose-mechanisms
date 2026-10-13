plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
}

android {
    namespace = "com.br.navigation"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Runtime do Navigation 3: traz NavKey e o backstack saveable.
    implementation(libs.androidx.navigation3.runtime)
    // Keys de navegação são @Serializable para sobreviver a mudanças de configuração.
    implementation(libs.kotlinx.serialization.core)

    // Testes de unidade puros na JVM.
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
