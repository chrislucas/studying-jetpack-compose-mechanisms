# Erro no `ProductDetailScreenTest`: `NoSuchMethodException`

Durante a investigação, foram encontrados **dois problemas distintos** que, juntos,
impediam os testes de UI de `ProductDetailScreenTest` de passar. O sintoma superficial
(`NoSuchMethodException`) aparecia em ambos, mas a causa era diferente.

---

## Problema 1 — Faltava `@RunWith(AndroidJUnit4::class)`

### Sintoma
Os testes nem chegavam a rodar; falhavam na inicialização do runner.

### Causa
A classe de teste instrumentado não tinha a anotação `@RunWith(AndroidJUnit4::class)`.
O import de `AndroidJUnit4` existia, mas a anotação não estava aplicada.

Sem ela, o JUnit usava o runner JVM padrão (`BlockJUnit4ClassRunner`), que não
inicializa o ambiente instrumentado do Android exigido por `createComposeRule()`.

### Solução
```kotlin
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // ✅
class ProductDetailScreenTest { ... }
```

---

## Problema 2 — Espresso incompatível com a API do Android do emulador

> Este foi o erro que **continuou** aparecendo mesmo após corrigir o Problema 1,
> quando os testes passaram a de fato executar no emulador (API 36.1).

### Sintoma (mensagem real do relatório)
```
java.lang.RuntimeException: java.util.concurrent.ExecutionException:
java.lang.RuntimeException:
java.lang.NoSuchMethodException: android.hardware.input.InputManager.getInstance []
    at androidx.test.espresso.base.InputManagerEventInjectionStrategy.initialize(...)
    at androidx.test.espresso.Espresso.onIdle(...)
    at androidx.compose.ui.test.EspressoLink.runUntilIdle(...)
```

### Causa raiz
O `createComposeRule()` do Compose usa o Espresso internamente para sincronizar a UI
(`Espresso.onIdle()`). Ao inicializar, o Espresso cria o
`InputManagerEventInjectionStrategy`, que acessa **por reflexão** o método oculto
`android.hardware.input.InputManager.getInstance()`.

Esse método oculto foi **removido/alterado nas versões recentes do Android**. O
emulador em uso é **API 36.1**, onde `InputManager.getInstance()` não existe mais com
essa assinatura. A versão do Espresso do projeto (`espresso-core 3.5.1`) ainda procura
esse método, resultando no `NoSuchMethodException`.

Em resumo: **versão antiga do Espresso + API do Android muito nova = método oculto
esperado pelo Espresso não existe mais no device.**

Referência: issue oficial do Google
[android/android-test #2507](https://github.com/android/android-test/issues/2507).

### Solução aplicada
Atualizar as bibliotecas de teste instrumentado em `gradle/libs.versions.toml`:

```toml
# Antes
junitVersion = "1.1.5"
espressoCore = "3.5.1"

# Depois
junitVersion = "1.3.0"
espressoCore = "3.7.0"
```

Essas versões são compatíveis com as APIs recentes do Android e corrigem a estratégia
de injeção de eventos do Espresso.

### Alternativa (caso a atualização não resolva na sua API)
Se o ambiente exigir uma API ainda mais nova do que a suportada pela versão disponível
do Espresso, rode os testes em um emulador/dispositivo de uma **API mais antiga** (por
exemplo, API 34), onde `InputManager.getInstance()` ainda existe. Isso é um contorno,
não a correção definitiva.

---

## Como verificar

Compilação dos testes instrumentados:
```bash
./gradlew :app:compileDebugAndroidTestKotlin
```

Execução no emulador/dispositivo conectado:
```bash
./gradlew :app:connectedDebugAndroidTest
```

### Resultado após as correções (verificado)
```
com.br.composenav3.feature.navbydeeplink.ProductDetailScreenTest
tests="3" failures="0" errors="0" skipped="0"
BUILD SUCCESSFUL
```

---

## Resumo

| # | Problema | Causa | Correção |
|---|----------|-------|----------|
| 1 | Testes não rodavam | Faltava `@RunWith(AndroidJUnit4::class)` | Anotar a classe de teste |
| 2 | `NoSuchMethodException: InputManager.getInstance` | Espresso 3.5.1 incompatível com API 36 (método oculto removido) | Atualizar `espresso-core` para 3.7.0 e `androidx.test.ext:junit` para 1.3.0 |

Os dois precisam estar corretos para os testes de UI passarem no emulador atual.
