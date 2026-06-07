# Multiplayer Tic-Tac-Toe — OCSF + EventBus

Software Engineering, Spring 2025-2026 — Lab 7.
Authors: **Waseem Sheety (205733322)**, **Adam (318407889)**.

A two-player networked Tic-Tac-Toe game. The **server** (built on OCSF's
`AbstractServer`) coordinates the match; the **client** is a JavaFX GUI built on
OCSF's `AbstractClient` and uses **GreenRobot EventBus** (the mediator /
publisher-subscriber pattern) to push server updates into the UI.

## How it works
- The first player to connect waits until a second player joins.
- When two players are connected the server randomly assigns **X and O** and
  randomly chooses **who starts**, then notifies both clients.
- The server is **authoritative**: every click is sent to the server, which
  validates the move, updates the board, and broadcasts the result to both
  players, so the two boards can never disagree.
- The server detects a **win** (and highlights the winning line) or a **draw**,
  supports **Play again**, and if a player disconnects the other is returned to
  the waiting room.

## Structure (three Maven modules)
1. **entities** — shared serializable data + protocol: `Message`, `Commands`,
   `Board` (pure game logic), `StartInfo`, `MoveInfo`, `GameResult`.
2. **server** — `App` (entry point), `SimpleServer` (extends OCSF `AbstractServer`)
   and `GameSession` (pairing, move validation, win/draw, rematch). OCSF lives in
   `server/.../server/ocsf` and is unchanged.
3. **client** — JavaFX: `App`, `PrimaryController` (connect screen),
   `GameController` (the board), `SimpleClient` (extends OCSF `AbstractClient`,
   turns server messages into EventBus events), and the `events` package. OCSF
   lives in `client/.../client/ocsf` and is unchanged.

## Build
Requires **JDK 17** (or newer) and **Maven**. The bundled JavaFX 21 (LTS) runs
on JDK 17 through 24. From the project root:

```
mvn clean install
```

## Run (same computer)
Three terminals:

```
cd server && mvn exec:java        # server (listens on port 3000)
cd client && mvn javafx:run       # player 1
cd client && mvn javafx:run       # player 2
```

In each client press **Connect** (host `127.0.0.1`, port `3000`).

## Run (two computers)
1. Both computers on the same network.
2. On the **server** computer, start the server and find its IP
   (`ipconfig getifaddr en0` on macOS, `ipconfig` on Windows).
3. On the **other** computer, run the client jar
   (`java -jar client-0.0.1-SNAPSHOT.jar`) and in the Connect screen enter the
   server computer's IP and port `3000`.

## Built JARs
The runnable JARs are committed under `jars/`:
`jars/tictactoe-server.jar` and `jars/tictactoe-client.jar`.
