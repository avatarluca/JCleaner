/**
 * <pre>
 * Tic-Tac-Toe Hauptspielklasse für 2 Spieler mit einem 3x3 Spielbrett.
 * Sie erzeugt, initialisiert und verwaltet die benötigten TicTacToe Klassen: GameInput, GameLanguage, GameBoard, GameLogic, Player.
 * 
 * Das Spiel wird mit einer Hauptschleife verwaltet, welche sich pro Zug wiederholt bis das Spiel vorbei ist.
 * Zusätzlich besitzt die Hauptschleife eine weitere Schleife, welche den Input managed. In dieser wird eine Kommunikation mit dem GameInput Objekt aufgebaut.
 * Wenn der Input falsch ist (nach den Kriterien von GameInput), wird der Loop wiederholt. Wenn der Input wahr ist, wird eine Kommunikation, je nach Befehl ("sprache" oder "setzen"), zwischen GameLanguage oder GameLogic aufgebaut.
 * 
 * Wenn das Spiel vorbei gibt es die Möglichkeit das Spiel zu beenden oder zu restarten. Dabei werden pro Runde die Gewinner aufgezeichnet und die Statistiken jeweils ausgegeben.
 * </pre>
 */
public class TicTacToe {
    private final int PLAYER_1_ID = -1, PLAYER_2_ID = 1, MAX_ROUNDS = 9;
    private final String PLAYER_1_NAME = "Player 1", PLAYER_2_NAME = "Player 2";

    private final Player player1, player2;
    private final GameBoard board;
    private final GameLanguage language;
    private final GameInput input;
    private final GameLogic logic;

    private boolean gameOver;
    private int round;
    private Player currentPlayer;

    /**
     * Konstruktor, um bei Start Player, GameBoard, GameLanguage, GameInput und GameLogic zu initialisieren.
     * 
     * @param isInteractive Wenn true, dann "Interaktiver Modus", wenn falsch dann
     *                      "Screenplay Modus".
     */
    public TicTacToe(boolean isInteractive) {
        this.round = 0;

        this.player1 = new Player(PLAYER_1_NAME, PLAYER_1_ID);
        this.player2 = new Player(PLAYER_2_NAME, PLAYER_2_ID);
        this.currentPlayer = this.player2;

        this.board = new GameBoard();
        this.language = new GameLanguage();
        this.input = new GameInput(isInteractive, this.language);
        this.logic = new GameLogic(this.board);
    }

    /**
     * <pre>
     * Hauptmethode, um eine neue TicTacToe Instanz zu erzeugen.
     * Wenn die TicTacToe Klasse von aussen erstellt wird, muss diese Methode ausgeklammert oder entfernt werden.
     * </pre>
     * 
     * @param args Kommandozeilen Argumente
     */
    public static void main(String... args) {
        TicTacToe game = new TicTacToe(true);
        game.run();
    }

    /**
     * <pre>
     * Definiert den Spielablauf einer Runde.
     * Pro Zug wird der Spieler geändert und mit Hilfe von GameLogic überprüft, ob jemand gewonnen hat oder das Spiel vorbei ist.
     * 
     * Wenn das Spiel vorbei ist werden die Statistiken aufgerufen. Die Spieler werden dann gefragt, ob sie das Spiel beenden oder restarten möchten.
     * </pre>
     */
    public void run() {
        language.printWelcomeMessage();

        if (player1.getName().equals(PLAYER_1_NAME) && player2.getName().equals(PLAYER_2_NAME)) {
            String name_player1 = input.requestName(player1.getName());
            String name_player2 = input.requestName(player2.getName());

            player1.setName(name_player1);
            player2.setName(name_player2);
        }

        language.printRoundInfo(round++);
        board.print();

        while (isRoundRunning()) {
            switchPlayer();

            language.printCurrentPlayerMessage(currentPlayer.getName());

            int field = manageInput();

            board.makeMove(field, currentPlayer.getID());

            language.printRoundInfo(round++);
            board.print();
        }

        gameOver = true;

        int winner = logic.getWinner(player1.getID(), player2.getID());

        switch (winner) {
            case PLAYER_1_ID -> {
                language.printWinnerMessage(player1.getName());
                addWonCounter(player1);
            }
            case PLAYER_2_ID -> {
                language.printWinnerMessage(player2.getName());
                addWonCounter(player2);
            }
            default -> language.printDrawMessage();

        }

        language.printStats(player1.getName(), player2.getName(), player1.getWins(), player2.getWins());

        if (manageInput() < 1) { // manageInput() wird 0 zurückgeben, wenn kein Zug Input definiert wurde.
            restart();
        } else {
            language.printExitMessage();
        }
    }

    /**
     * <pre>
     * Bestimmt, ob die Runde noch läuft oder nicht. 
     * 
     * Die Runde ist vorbei wenn Unentschieden ist oder ein Spieler gewonnen hat.
     * </pre>
     * 
     * @return Ob die Runde immer noch läuft.
     */
    private boolean isRoundRunning() {
        return !(win() || (round > MAX_ROUNDS));
    }

    /**
     * Wechselt den momentanen Spieler.
     */
    private void switchPlayer() {
        if (currentPlayer.getID() == PLAYER_1_ID) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    /**
     * Erhöht die Anzahl der Gewinne eines Spielers um 1.
     *
     * @param player Die Anzahl der Gewinne wird bei diesem Spieler geändert
     */
    private void addWonCounter(Player player) {
        if (player.getID() == PLAYER_1_ID)
            player1.setWins(player1.getWins() + 1);
        else if (player.getID() == PLAYER_2_ID)
            player2.setWins(player2.getWins() + 1);
    }

    /**
     * Überprüft, ob der momentane Spieler gewonnen hat.
     * 
     * @return True wenn der momentane Spieler gewonnen hat.
     */
    private boolean win() {
        return logic.checkWin(currentPlayer.getID());
    }

    /**
     * <pre>
     * Koordiniert den Input mit einer Schleife.
     * 
     * Es gibt eine Kommunikation zur "GameInput" Klasse, welche den Syntax validiert.
     * Es gibt eine Kommunikation zur "GameLogic" Klasse, welche die Semantik validiert.
     * 
     * Falls der Input von beiden Seiten korrekt ist, wird dieser als Integer zurückgegeben.
     * Wenn der Befehl das Ändern der Sprache gewesen ist, wird der Wert 0 zurückgegeben.
     * </pre> 
     * 
     * @return Die Feldzahl, welche der momentane Spieler belegen möchte.
     */
    private int manageInput() {
        boolean validInput = true;
        int fieldValue = 0;

        do {
            if (!gameOver) {
                if (input.readInput()) {
                    if (input.getCommandText().equals(language.getSetCommand())) {
                        fieldValue = input.getPosition();
                        validInput = logic.checkSemantic(fieldValue);

                        if(!validInput) language.printOccupiedFieldErrorMessage();
                    } else {
                        language.changeLanguage();
                        validInput = false;
                    }
                } else {
                    validInput = false;
                }
            } else {
                validInput = input.requestRestart();
            }
        } while (!validInput);

        if (!gameOver) {
            language.printCurrentPlayersMoveMessage(currentPlayer.getName(), fieldValue);
        }

        return fieldValue;
    }

    /**
     * <pre>
     * Wenn das Spiel fertig ist und die Spieler das Spiel erneut spielen möchten, wird das Spiel gesäubert.
     * 
     * - Game Over Flag wird auf false zurückgesetzt.
     * - Die Anzahl Runden wird auf 0 zurückgesetzt.
     * - Das Spielbrett wird gesäubert.
     * 
     * Die Spieler werden automatisch in jeder neuen Runde gewechselt. switchPlayer() muss nicht mehr aufgerufen werden.
     * </pre>
     */
    private void restart() {
        gameOver = false;
        round = 0;

        board.clear();

        run();
    }
}
