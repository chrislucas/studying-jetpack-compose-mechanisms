plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.br.composablesstatelessandstateful"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.br.composablesstatelessandstateful"
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

    testOptions {
        unitTests {
            // Faz os unit tests (Robolectric) usarem os recursos Android e o
            // AndroidManifest MESCLADO do app, que inclui o manifest do artefato
            // androidx.compose.ui:ui-test-manifest. Esse manifest declara a
            // androidx.activity.ComponentActivity usada por createComposeRule().
            // Sem isso, o Robolectric lança:
            // "Unable to resolve activity for Intent ... androidx.activity.ComponentActivity".
            isIncludeAndroidResources = true
        }
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
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.ui.test.junit4)

    testImplementation(libs.junit)
    // Compose UI Test API nos unit tests (Robolectric). O BOM alinha a versão do
    // ui-test-junit4 (createComposeRule, onNodeWithText, performTextInput, etc.)
    // com o restante do Compose.
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Robolectric + Compose UI Test
    testImplementation("org.robolectric:robolectric:4.17")
    // Necessário para que o createComposeRule() consiga resolver a ComponentActivity
    // host nos testes Robolectric (unit test). Sem isso, o manifest de teste
    // (org.robolectric.default) não declara a ComponentActivity e lança
    // "Unable to resolve activity for Intent ... androidx.activity.ComponentActivity".
    testImplementation(libs.androidx.compose.ui.test.manifest)
}