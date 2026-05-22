# Fidelitas App - Sistema de Fidelidade e Recompensas

Este projeto representa o Trabalho de Conclusão de Curso (TCC) focado no desenvolvimento de uma solução mobile nativa para gestão de programas de fidelidade, permitindo o acúmulo, resgate e transferência de pontos entre usuários.

## 👥 Integrantes do Grupo
*   **Couto**, Alexsander Montenegro de
*   **Dias**, William Jose
*   **Filho**, Rudge Santos Machado
*   **Mathias**, Sandra Coutinho
*   **Souza**, Anderson de
*   **Vasconcelos**, Anderson Feitosa de

---

## 🎯 Objetivo do Projeto
O **Fidelitas App** visa modernizar a interação entre empresas e clientes através de uma interface intuitiva e de alto desempenho. O sistema permite que o usuário acompanhe seu saldo em tempo real, visualize um catálogo de prêmios, realize resgates e envie pontos para outros participantes de forma segura.

## 🚀 Principais Funcionalidades
*   **Dashboard Inteligente**: Visualização do saldo acumulado com sincronização em tempo real entre todas as telas do aplicativo.
*   **Catálogo de Prêmios**: Lista dinâmica de recompensas com suporte a cache local.
*   **Transferência Peer-to-Peer**: Envio de pontos entre usuários com validação instantânea de destinatário e saldo.
*   **Extrato Detalhado**: Histórico completo de transações (entradas e saídas) armazenado localmente para consulta offline.
*   **Sincronização Global**: Botão de atualização manual que sincroniza todos os módulos do app simultaneamente.
*   **Segurança Avançada**: Interceptor de rede para gestão de tokens JWT e limpeza automática de dados sensíveis em caso de expiração de sessão.

---

## 🛠 Arquitetura e Tecnologias
O projeto foi construído seguindo as **Modern Android Development (MAD)** practices, garantindo um código limpo, testável e escalável.

*   **Linguagem**: Kotlin (100%)
*   **UI Framework**: Jetpack Compose (Interface declarativa e reativa)
*   **Arquitetura**: MVVM (Model-View-ViewModel) + Repository Pattern
*   **Injeção de Dependência**: Hilt (Dagger)
*   **Banco de Dados Local**: Room Database (SQLite) para persistência de transações.
*   **Gestão de Sessão**: Jetpack DataStore (Preferences) para armazenamento seguro de tokens.
*   **Networking**: Retrofit 2 + OkHttp 3 com interceptores de autenticação.
*   **Assincronismo**: Kotlin Coroutines & Flow (StateFlow para estado global).
*   **Carregamento de Imagens**: Coil (Image loading otimizado).

---

## 🏗️ Destaques de Engenharia
Durante o desenvolvimento, foram aplicadas soluções para desafios comuns em aplicações corporativas:

1.  **Fonte Única da Verdade (SSOT)**: Implementação de um `StateFlow` centralizado no repositório para garantir que o saldo do usuário seja idêntico em todas as camadas da aplicação sem necessidade de múltiplas requisições.
2.  **Resiliência Offline**: Uso de Room para permitir que o usuário consulte seu extrato e prêmios mesmo sem conexão com a internet.
3.  **Tratamento de Erros Silencioso**: Sistema de interceptação 401 que limpa o banco de dados e redireciona o usuário para o login de forma fluida, sem travamentos ou telas de erro técnicas.
4.  **Feedback Reativo**: Sistema de notificações via `Toast` integrado ao estado da ViewModel, separando a lógica de negócio da exibição de mensagens.

---

## ⚙️ Como Executar o Projeto

1.  **Pré-requisitos**:
    *   Android Studio Ladybug ou superior.
    *   SDK Android 34+.
    *   API Backend rodando localmente (porta 3000) ou via URL configurada em `NetworkModule.kt`.

2.  **Passos**:
    *   Clone o repositório.
    *   Sincronize o Gradle.
    *   Execute o app em um emulador ou dispositivo físico.

---

## 📄 Licença
Este projeto foi desenvolvido para fins estritamente acadêmicos como parte do currículo de graduação/especialização.
