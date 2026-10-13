# Requirements: Classificador de Comprovantes on-device

## 1. Contexto & Motivação
O aplicativo precisa categorizar despesas de forma automática através de comprovantes de pagamento (fotos ou PDFs), operando de forma 100% offline (on-device) para garantir privacidade, segurança de dados financeiros e economia de dados do usuário.

## 2. Requisitos Funcionais (RF)
*   **RF-01:** O sistema deve extrair texto legível a partir de uma imagem/bitmap de comprovante de pagamento.
*   **RF-02:** O sistema deve classificar o texto extraído em uma das três categorias: `PIX`, `BOLETO` ou `CARTAO`.
*   **RF-03:** Se o modelo não atingir uma margem mínima de certeza, o documento deve ser classificado como `UNKNOWN`.

## 3. Requisitos Não-Funcionais (RNF)
*   **RNF-01:** O processamento completo (OCR + Classificação) deve rodar de forma síncrona/assíncrona localmente no dispositivo, sem chamadas de rede.
*   **RNF-02:** O tempo de inferência total do modelo de classificação não deve ultrapassar 200ms em hardware intermediário.
*   **RNF-03:** O tamanho do arquivo do modelo de IA embarcado não deve poluir o tamanho do APK, limitando-se a no máximo 5MB.

## 4. Critérios de Aceitação
*   Dado um comprovante com a string "Comprovante de PIX recebido", a label retornada deve ser `PIX`.
*   Dado um comprovante com a string "Autenticação mecânica" ou "Linha digitável", a label retornada deve ser `BOLETO`.
*   Margem de confiança mínima para validação de classificação positiva: 65% (`score > 0.65`).
