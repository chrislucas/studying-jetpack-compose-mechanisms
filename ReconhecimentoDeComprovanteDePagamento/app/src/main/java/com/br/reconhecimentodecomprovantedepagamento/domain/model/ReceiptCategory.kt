package com.br.reconhecimentodecomprovantedepagamento.domain.model

/**
 * Categorias de comprovante suportadas pelo classificador on-device.
 *
 * As labels [PIX], [BOLETO] e [CARTAO] correspondem às labels de saída do
 * modelo TFLite. [UNKNOWN] é um valor sintético atribuído pela camada de
 * domínio quando o maior score fica abaixo do limiar de confiança (RF-03).
 */
enum class ReceiptCategory {
    PIX,
    BOLETO,
    CARTAO,
    UNKNOWN;

    companion object {
        /**
         * Converte uma label textual retornada pelo modelo em [ReceiptCategory].
         * Labels desconhecidas são mapeadas para [UNKNOWN].
         */
        fun fromLabel(label: String): ReceiptCategory =
            entries.firstOrNull { it.name.equals(label.trim(), ignoreCase = true) } ?: UNKNOWN
    }
}
