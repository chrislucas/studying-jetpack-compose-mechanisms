#!/usr/bin/env bash
#
# Testa manualmente o fluxo de navegação da FixingSettingsNavigationActivity,
# incluindo o caminho do onNewIntent (app em segundo plano recebendo o atalho).
#
# Pré-requisitos:
#   - adb no PATH
#   - um emulador ou dispositivo conectado (confira com: adb devices)
#   - a Activity precisa estar com android:launchMode="singleTop" no manifest,
#     caso contrário o passo 4 recria a Activity (onCreate) em vez de reentregar
#     a intent (onNewIntent).
#
# Dica: para observar qual callback é chamado, rode em outro terminal:
#   adb logcat -s ShortcutNav
#
# Uso:
#   ./test-shortcut-navigation.sh            # execução normal
#   DEBUG=1 ./test-shortcut-navigation.sh    # modo debug (am start -D)
#
# No modo debug (DEBUG=1), o passo 4 usa "am start -D": a Activity fica
# CONGELADA aguardando um debugger ser anexado. Nesse momento, abra o Android
# Studio em: Run -> Attach Debugger to Android Process -> com.br.shortcutmenu,
# com breakpoints em onNewIntent / handlerShortcutIntent.
#
set -euo pipefail

PKG="com.br.shortcutmenu"
ACTIVITY=".FixingSettingsNavigationActivity"
COMPONENT="${PKG}/${ACTIVITY}"

# DEBUG=1 habilita o lançamento em modo debug (-D) no passo 4.
DEBUG="${DEBUG:-0}"

# Diretório do script -> raiz do módulo Gradle (ShortcutMenu/).
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

echo "==> 1. Instalando o app (installDebug)"
./gradlew :app:installDebug

echo "==> 2. Simulando o clique no atalho (ACTION_VIEW -> ${COMPONENT})"
adb shell am start -a android.intent.action.VIEW -n "${COMPONENT}"
sleep 2

echo "==> 3. Mandando o app para segundo plano (KEYCODE_HOME)"
adb shell input keyevent KEYCODE_HOME
sleep 2

if [ "${DEBUG}" = "1" ]; then
    echo "==> 4. [MODO DEBUG] Disparando a MESMA intent com -D (esperado: onNewIntent)"
    adb shell am start -D -a android.intent.action.VIEW -n "${COMPONENT}"
    echo ""
    echo "**************************************************************************"
    echo "*  ATENÇÃO: a Activity está CONGELADA aguardando o debugger.             *"
    echo "*  ACIONE AGORA o Attach Debugger:                                       *"
    echo "*    Android Studio -> Run -> Attach Debugger to Android Process         *"
    echo "*    -> selecione o processo 'com.br.shortcutmenu'                       *"
    echo "*  Coloque breakpoints em onNewIntent / handlerShortcutIntent antes.     *"
    echo "*  A execução só continua DEPOIS que o debugger for anexado.             *"
    echo "**************************************************************************"
else
    echo "==> 4. Disparando a MESMA intent de novo (esperado: onNewIntent)"
    adb shell am start -a android.intent.action.VIEW -n "${COMPONENT}"
fi

echo ""
echo "Pronto. Verifique no dispositivo se o app abriu DIRETO na tela de Settings."
echo "Para confirmar o callback usado, acompanhe os logs com:"
echo "  adb logcat -s ShortcutNav"
