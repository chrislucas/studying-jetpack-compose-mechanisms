# Robolectric + Compose: "Unable to resolve activity for ComponentActivity"

## Contexto

Teste de UI de Compose rodando como **unit test** (JVM) com Robolectric,
usando `createComposeRule()` para hospedar um composable isolado:

```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `given the state of switch is disabled, when checked then its value should be false`() {
        composeTestRule.setContent {
            SettingsScreen(false) { }
        }
        composeTestRule.onNodeWithRole(Role.Switch).assertIsOff()
    }
}
```

## O problema

Ao executar `./gradlew :app:testDebugUnitTest`, o teste falha em runtime:

```
java.lang.RuntimeException: Unable to resolve activity for Intent {
  act=android.intent.action.MAIN
  cat=[android.intent.category.LAUNCHER]
  cmp=org.robolectric.default/androidx.activity.ComponentActivity
} -- see https://github.com/robolectric/robolectric/pull/4736 for details
    at org.robolectric.android.internal.RoboMonitoringInstrumentation.startActivitySyncInternal(...)
    at androidx.test.core.app.ActivityScenario.launch(...)
    at androidx.test.ext.junit.rules.ActivityScenarioRule.before(...)
    at androidx.compose.ui.test.junit4.AndroidComposeTestRule...
```

### Causa raiz

- `createComposeRule()` (sem activity específica) hospeda o conteúdo do teste
  dentro de uma `androidx.activity.ComponentActivity` genérica. Internamente ele
  usa `ActivityScenarioRule` + `ActivityScenario.launch(ComponentActivity)`, que
  **lança uma Activity de verdade** via `Intent`.
- Essa `ComponentActivity` **não pertence ao app** — ela é fornecida pelo
  artefato `androidx.compose.ui:ui-test-manifest`, cujo `AndroidManifest.xml`
  contém:

  ```xml
  <manifest package="androidx.compose.ui.test.manifest">
      <application>
          <activity
              android:name="androidx.activity.ComponentActivity"
              android:exported="true"
              android:theme="@android:style/Theme.Material.Light.NoActionBar" />
      </application>
  </manifest>
  ```

- No ambiente Robolectric (pacote `org.robolectric.default`), a Activity só é
  resolvível se estiver no **AndroidManifest mesclado** que os unit tests
  enxergam. Por padrão, o AGP **não** mescla o manifest dos AARs no manifest de
  unit test, então a `ComponentActivity` fica ausente e o lançamento falha.

> Observação: declarar `ui-test-manifest` apenas em `debugImplementation`
> (padrão dos templates) resolve o caso de **instrumented tests**
> (`androidTest`), mas **não** o caso de **unit tests** (`test`) com Robolectric.

## A solução

Duas mudanças no `app/build.gradle.kts`:

### 1. Habilitar o manifest/recursos Android mesclados nos unit tests

Essa é a correção decisiva. Faz o AGP gerar e mesclar o manifest de unit test
(tasks `mergeDebugUnitTestManifest` / `processDebugUnitTestManifest`), incluindo
o `AndroidManifest.xml` do `ui-test-manifest` com a `ComponentActivity`:

```kotlin
android {
    // ...
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
```

### 2. Garantir o artefato no classpath de unit test

O `ui-test-manifest` precisa estar disponível para o conjunto de teste JVM:

```kotlin
dependencies {
    // ...
    testImplementation(libs.androidx.compose.ui.test.manifest)
}
```

> Depois dessa mudança, o build passa a executar as tasks
> `mergeDebugUnitTestManifest` e `processDebugUnitTestManifest`, sinal de que o
> manifest mesclado (com a `ComponentActivity`) está sendo usado pelo Robolectric.

## Verificação

```bash
./gradlew :app:testDebugUnitTest --tests "com.br.shortcutmenu.SettingsScreenTest"
# BUILD SUCCESSFUL
```

## Resumo

| Item | Valor |
| --- | --- |
| Sintoma | `RuntimeException: Unable to resolve activity ... androidx.activity.ComponentActivity` |
| Quem lança | `createComposeRule()` ao fazer `ActivityScenario.launch(ComponentActivity)` |
| Quem declara a Activity | AAR `androidx.compose.ui:ui-test-manifest` |
| Por que falha no Robolectric | O manifest do AAR não é mesclado no manifest de unit test por padrão |
| Correção principal | `testOptions.unitTests.isIncludeAndroidResources = true` |
| Correção de apoio | `testImplementation(libs.androidx.compose.ui.test.manifest)` |

## Referências

- https://github.com/robolectric/robolectric/pull/4736
- Artefato `androidx.compose.ui:ui-test-manifest` (declara `ComponentActivity`)
