package com.br.shortcutmenu.core

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SettingRepositoryTest {

    /*
        O DataStore de Preferences persiste os dados em um ARQUIVO real no disco
        (um arquivo .preferences_pb). Mesmo em teste, o DataStore precisa de um
        caminho de arquivo válido para ler e gravar — ele não funciona apenas em memória.

        A rule TemporaryFolder resolve isso de duas formas:

        1. Cria um diretório temporário exclusivo ANTES de cada teste e o remove
           automaticamente DEPOIS que o teste termina. Isso garante isolamento:
           cada teste começa com um arquivo limpo, sem vazamento de estado entre
           os testes (evita que o valor salvo em um teste afete outro).

        2. Evita colisões e lixo no sistema de arquivos: não precisamos escolher
           um caminho fixo nem fazer limpeza manual, pois a rule cuida do ciclo
           de vida (setup/teardown) do diretório.

        Sem essa rule, teríamos que gerenciar manualmente a criação e exclusão do
        arquivo do DataStore, o que seria repetitivo e propenso a testes "sujos".
     */
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var settingRepository: SettingRepository

    /*
        @Before faz este método rodar ANTES de CADA teste (@Test). Como ele é
        reexecutado a cada teste, garantimos uma instância nova e isolada do
        SettingManager — sem estado compartilhado entre os casos de teste.

        Aqui montamos manualmente um DataStore de teste em vez de usar o
        Context.dataStore do app:

        - PreferenceDataStoreFactory.create: cria uma instância de DataStore
          controlada por nós, sem depender de um Context do Android. Por isso
          este teste roda como teste unitário puro na JVM (src/test), rápido e
          sem necessidade de Robolectric ou emulador.

        - scope = testScope: o DataStore faz leituras/gravações de forma assíncrona
          (coroutines). Passar o TestScope deixa essas operações sob o controle do
          dispatcher de teste, tornando-as determinísticas e sincronizadas com o
          runTest dos casos de teste.

        - produceFile: define ONDE o arquivo .preferences_pb será criado. Usamos a
          TemporaryFolder para gerar um arquivo novo e descartável a cada teste,
          garantindo o isolamento descrito na rule acima.

        Por fim, injetamos esse DataStore de teste no SettingManager, exercitando
        exatamente a mesma lógica de produção, mas com um backend controlado.
     */
    @Before
    fun setUp() {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = {
                temporaryFolder.newFile("test_preferences.preferences_pb")
            },
        )
        settingRepository = SettingRepository(testDataStore)
    }

    @Test
    fun initialValue_isFalse() = testScope.runTest {
        val value = settingRepository.configEnabled.first()
        assertEquals(false, value)
    }

    @Test
    fun saveConfig_updatesValue() = testScope.runTest {
        settingRepository.saveConfig(newValue = true)
        var value = settingRepository.configEnabled.first()
        assertEquals(true, value)

        settingRepository.saveConfig(newValue = false)
        value = settingRepository.configEnabled.first()
        assertEquals(false, value)
    }
}
