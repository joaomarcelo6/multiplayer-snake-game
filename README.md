# 🐍 Multiplayer Snake Game

> Um jogo Snake para dois jogadores no mesmo teclado, construído com Java e LibGDX.

![Badge Concluído](http://img.shields.io/static/v1?label=STATUS&message=CONCLUÍDO&color=GREEN&style=for-the-badge)
![Badge Java](http://img.shields.io/static/v1?label=LANGUAGE&message=JAVA&color=orange&style=for-the-badge)
![Badge LibGDX](http://img.shields.io/static/v1?label=FRAMEWORK&message=LIBGDX&color=red&style=for-the-badge)

---

## 📋 Descrição

Este projeto é um **jogo Snake multiplayer local** desenvolvido como trabalho da disciplina **SCC0204 — Programação Orientada a Objetos (2026)**. Dois jogadores competem no mesmo teclado, controlando cobras de cores distintas numa grade 2D em tempo real.

O projeto foi construído sobre o framework **LibGDX**, aplicando princípios de POO como herança, encapsulamento, composição e polimorfismo para organizar as entidades do jogo, as telas e os subsistemas de áudio e persistência.

---

## 🚀 Funcionalidades Técnicas

* **Arquitetura de Telas (Screen Lifecycle):** Cada estado da aplicação — menu, configurações, jogo, game over, ranking, instruções — é uma tela isolada que implementa a interface `Screen` do LibGDX. A troca entre elas é feita via `game.setScreen()`, sem acoplamento entre os estados.
* **Loop de Jogo Baseado em Ticks:** O movimento das cobras não ocorre a cada frame, mas a cada intervalo de tempo configurável (`tickInterval`). O acumulador de delta garante velocidade consistente independentemente do FPS.
* **Aceleração Progressiva:** A cada alimento consumido, o `tickInterval` é reduzido em 0,003 s até o mínimo de 0,065 s, acelerando o jogo gradualmente.
* **Detecção de Colisão por Posição:** A classe `GridCell` é imutável e sobrescreve `equals()` e `hashCode()` baseados em coluna e linha. Isso permite detectar colisões comparando posições — não referências de objeto — de forma correta com as coleções Java.
* **Corpo da Cobra com `LinkedList`:** Adição de cabeça no início e remoção de cauda no fim ocorrem em O(1), operações que `ArrayList` faria em O(n).
* **Degradação Graciosa de Áudio:** O `SoundManager` carrega os arquivos de som com tratamento de falha — arquivos ausentes retornam `null` e o jogo continua funcionando normalmente sem som.
* **Persistência de High Scores:** O `HighScoreManager` lê e grava o top-5 em arquivo local (`highscores.txt`) usando `Gdx.files.local()`. A lista é mantida ordenada pelo `Comparable` implementado em `ScoreEntry`.

---

## 🎮 Como Jogar

### Objetivo
Coma o máximo de comida possível para crescer e pontuar. O jogo termina quando uma cobra morre — ganha quem tiver mais pontos.

### Controles

| Tecla | Ação |
| :---: | :--- |
| **↑ ↓ ← →** | Mover Jogador 1 (cobra verde) |
| **W A S D** | Mover Jogador 2 (cobra azul) |
| **ESC** ou **P** | Pausar / Retomar o jogo |

### Regras
| Evento | Consequência |
| :--- | :--- |
| Cobra come a comida vermelha | +1 ponto, cobra cresce, velocidade aumenta |
| Cobra sai de um lado da tela | Reaparece no lado oposto (wrap-around) |
| Cobra colide com o próprio corpo | Morre |
| Cobra colide com o corpo da outra | Morre |
| Uma cobra morre | Jogo encerra; ganha quem tem mais pontos |
| Ambas morrem com mesma pontuação | Empate |

---

## 🗂️ Estrutura do Projeto

```
multiplayer-snake-game/
├── core/src/com/snakegame/
│   ├── SnakeGame.java               ← Classe principal (Game)
│   ├── entities/
│   │   ├── Snake.java               ← Corpo, movimento, colisão, render
│   │   ├── Food.java                ← Spawn em posição aleatória válida
│   │   └── GridCell.java            ← Posição imutável no grid (col, row)
│   ├── screens/
│   │   ├── MenuScreen.java          ← Menu com cobra decorativa animada
│   │   ├── SettingsScreen.java      ← Configuração de grid e velocidade
│   │   ├── GameScreen.java          ← Loop principal do jogo
│   │   ├── GameOverScreen.java      ← Resultado e opções pós-partida
│   │   ├── HighScoreScreen.java     ← Ranking top-5
│   │   └── InstructionsScreen.java  ← Regras e controles
│   └── utils/
│       ├── HighScoreManager.java    ← Leitura e gravação do ranking
│       └── SoundManager.java        ← Carregamento e reprodução de sons
├── desktop/src/com/snakegame/desktop/
│   └── DesktopLauncher.java         ← Ponto de entrada (LWJGL3)
├── assets/sounds/
│   ├── eat.wav
│   ├── death.wav
│   ├── navigate.wav
│   └── select.wav
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
└── README.md
```

---

## 💻 Como Executar

Este projeto usa **Gradle** para gerenciamento de dependências. O *Gradle wrapper* já está incluído — não é necessário instalar o Gradle separadamente.

### Pré-requisitos

* [JDK 11+](https://adoptium.net)
* Git

### 1. Clonar o repositório

```bash
git clone https://github.com/[usuario]/multiplayer-snake-game.git
cd multiplayer-snake-game
```

### 2. Rodar via terminal (recomendado)

**Linux / macOS:**
```bash
./gradlew desktop:run
```

**Windows:**
```bash
gradlew.bat desktop:run
```

### 3. Rodar via IDE (IntelliJ / Eclipse)

1. Importe o projeto apontando para o `build.gradle` na raiz.
2. Aguarde a sincronização das dependências.
3. Localize `DesktopLauncher.java` no módulo `lwjgl3` e execute.

### Gerar JAR distribuível

```bash
./gradlew desktop:run
```
O executável será gerado em `lwjgl3/build/libs/`.

### Comandos úteis

| Comando | Descrição |
| :--- | :--- |
| `./gradlew clean` | Remove as pastas `build` com classes antigas |
| `./gradlew --refresh-dependencies` | Força atualização das bibliotecas do LibGDX |

---

## ✒️ Autores

* **João Marcelo Abreu dos Santos** — Número USP: 16876021
* **Lucas Waldrighi Lima** — Numero USP: 16885286
