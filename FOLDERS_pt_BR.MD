FOLDERS_pt_BR.MD

Este documento explica o propósito e a estrutura típica das pastas encontradas neste repositório. Destina-se a ajudar contribuintes a localizar rapidamente onde estão o código-fonte, recursos, saídas de build e configurações.

Estrutura de nível superior

- `androidApp/`
  - Propósito: Módulo do aplicativo Android. Contém código específico do Android e recursos.
  - Arquivos e pastas comuns:
    - `build.gradle.kts` - script de build do módulo (Kotlin DSL).
    - `src/` - conjuntos de código do aplicativo Android.
      - `main/` - código principal do aplicativo Android (Java/Kotlin, AndroidManifest.xml, recursos em `res/`).
    - `build/` - saídas do Gradle (classes compiladas, intermediários, recursos gerados).
    - `kotlin/`, `outputs/`, `generated/` (subpastas geradas pelo processo de build).

- `desktopApp/`
  - Propósito: Módulo do aplicativo Desktop (JVM).
  - Arquivos e pastas comuns:
    - `build.gradle.kts` - script de build do módulo.
    - `src/main/` - código principal da aplicação JVM.
    - `build/` - artefatos de build e arquivos de classe do desktop.

- `iosApp/`
  - Propósito: Módulo do aplicativo iOS (projeto Xcode + código SwiftUI).
  - Arquivos e pastas comuns:
    - `iosApp.xcodeproj/` - arquivos do projeto Xcode.
    - `iosApp/` - arquivos fonte Swift, assets, Info.plist e recursos de suporte.
    - `Configuration/` - arquivos de configuração do Xcode (por exemplo, `Config.xcconfig`).

- `sharedLogic/`
  - Propósito: Módulo Kotlin Multiplatform contendo lógica de negócio compartilhada e adaptadores específicos de plataforma.
  - Arquivos e pastas comuns:
    - `build.gradle.kts` - script de build do módulo.
    - `src/` - conjuntos de código multiplataforma:
      - `commonMain/` - código compartilhado entre plataformas (Kotlin).
      - `commonTest/` - testes compartilhados.
      - `androidMain/`, `jvmMain/`, `iosMain/` - implementações específicas por plataforma.
    - `build/` - saídas de compilação e artefatos (por exemplo, artefatos do Kotlin/Native).

- `sharedUI/`
  - Propósito: Módulo Kotlin Multiplatform de UI (componentes de UI compartilhados, theming).
  - Arquivos e pastas comuns:
    - `build.gradle.kts` - script de build do módulo.
    - `src/commonMain/` - código de UI compartilhado.
    - `src/commonTest/` - testes para componentes de UI compartilhados.
    - `build/` - saídas de build.

- `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/`, `gradlew`, `gradlew.bat`
  - Propósito: Configuração de build do Gradle em nível de projeto e wrappers.
  - `gradle/` contém configurações locais e do wrapper como `libs.versions.toml`.
  - `gradlew`/`gradlew.bat` são os scripts do Gradle Wrapper para Unix/Windows.

- `build/` (raiz do projeto)
  - Propósito: Relatórios de build de nível superior e saídas geradas pelo projeto.
  - Contém relatórios e caches criados pelo Gradle.

- `docs/`
  - Propósito: Documentação do projeto.
  - Subpastas (exemplo): `en_US/` e `pt_BR/` contendo docs de arquitetura e imagens.

- `local.properties`
  - Propósito: Configuração local de desenvolvimento (normalmente inclui caminhos do SDK). Geralmente não é versionado.

- `README.md`
  - Propósito: Introdução ao projeto e instruções básicas.

Padrões comuns dentro de cada módulo

- `build.gradle.kts` (ou `build.gradle`) - Cada módulo normalmente tem um script de build em sua raiz.
- `src/` - Árvore de código. Para módulos Kotlin Multiplatform, isso normalmente contém pastas aninhadas para conjuntos de código específicos por plataforma e comuns: `commonMain`, `androidMain`, `jvmMain`, `iosMain`, além das pastas `*Test` correspondentes.
- `src/main/` - Para módulos de plataforma única (Android, JVM), localização típica para código da aplicação, recursos e manifests.
- `build/` - Gerado pelo Gradle; contém classes compiladas, recursos gerados, arquivos intermediários, pacotes e relatórios do módulo. Esta pasta é gerada pelo build e geralmente ignorada no controle de versão.
- `generated/` - Subpasta para fontes geradas (codegen, kapt, saídas do compilador do Compose, etc.).

Notas sobre artefatos e saídas de build

- Muitas pastas dentro de `build/` são gerenciadas pelo Gradle e usadas durante builds incrementais (por exemplo, `intermediates/`, `outputs/`, `kotlin/`). Não edite manualmente arquivos dentro de `build/`.
- Se precisar inspecionar fontes ou recursos gerados, verifique `build/generated/` e `build/intermediates/` no módulo relevante.

Onde adicionar novo código

- Código de feature/UI que deve ser reutilizado entre plataformas deve ir para `sharedLogic/src/commonMain` ou `sharedUI/src/commonMain`, assim o código fica visível para todos os targets.
- Implementações específicas de plataforma devem viver nos source sets da plataforma (por exemplo, `androidMain`, `iosMain`, `jvmMain`).
- A configuração específica do app (activities, manifests, pontos de entrada) pertence a `androidApp/src/main`, `iosApp/iosApp` (Swift) e `desktopApp/src/main`, respectivamente.

Controle de versão e manutenção

- Mantenha as saídas de build fora do controle de versão. As pastas `build/` são geradas e devem estar ignoradas.
- Mantenha a configuração de plataforma (por exemplo, `Config.xcconfig`) nos respectivos módulos de plataforma.

Referência rápida (caminhos mostrados relativos à raiz do repositório)

- `androidApp/` - fontes e recursos do app Android
- `desktopApp/` - app Desktop (JVM)
- `iosApp/` - app iOS e projeto Xcode
- `sharedLogic/` - lógica de negócio cross-platform (Kotlin Multiplatform)
- `sharedUI/` - componentes de UI cross-platform
- `gradle/`, `gradlew`, `gradlew.bat` - wrapper do Gradle e configuração
- `docs/` - documentação

Se desejar, posso:
- Expandir este arquivo com um diagrama ou árvore ASCII da estrutura atual do repositório.
- Gerar um README de módulo para qualquer módulo específico (por exemplo `sharedLogic/README.md`) que explique os source sets em mais detalhe.

— Fim de FOLDERS_pt_BR.MD

