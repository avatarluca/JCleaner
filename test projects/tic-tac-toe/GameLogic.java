/**
 * Repräsentiert die Spiele Logik.
 */
public class GameLogic {
    GameBoard board;

    /**
     * Erstellt eine neue Spiellogik.
     *
     * @param board Spielbrett mit welchem die Spiele Logik arbeitet.
     */
    public GameLogic(GameBoard board) {
        this.board = board;
    }

    /**
     * Überprüft, ob ein Zug gültig ist.
     *
     * @param fieldName Zahl von 1 bis 9 welche das Feld angibt.
     * @return Ob dieser Zug gültig ist.
     */
    public boolean checkSemantic(int fieldName) {
        return board.getField(fieldName) == 0;
    }

    /**
     * <pre>
     * Überprüft ob es einen Gewinner gibt.
     * Wenn ein Spieler gewonnen hat, wird die jeweilige ID zurückgegeben. Bei Unentschieden wird 0 zurückgegeben.
     * </pre>
     * 
     * @param player1Id ID des ersten Spielers.
     * @param player2Id ID des zweiten Spielers.
     * @return Gibt die ID des Gewinners zurück.
     */
    public int getWinner(int player1Id, int player2Id) {
        if (checkWin(player1Id)) {
            return player1Id;
        } else if (checkWin(player2Id)) {
            return player2Id;
        } else {
            return 0;
        }
    }

    /**
     * <pre>
     * Überprüft ob der Spieler bereits gewonnen hat.
     * 
     * Dadurch dass die Spieler jeweils die ID -1 und 1 haben, kann man beispielsweise jedes Spielfeld in einer Zeile addieren. 
     * Wenn dann die Addition 3 bzw. -3 ergibt, weiss man ob ein Spieler (und welcher) gewonnen hat.
     * </pre>
     * 
     * @param playerId ID des zu überprüfenden Spielers.
     * @return Gibt zurück ob der Spieler bereits gewonnen hat.
     */
    public boolean checkWin(int playerId) {
        int winNumber = playerId * 3;

        int sumRow1 = board.getField(7) + board.getField(8) + board.getField(9);
        int sumRow2 = board.getField(4) + board.getField(5) + board.getField(6);
        int sumRow3 = board.getField(1) + board.getField(2) + board.getField(3);

        int sumColumn1 = board.getField(7) + board.getField(4) + board.getField(1);
        int sumColumn2 = board.getField(8) + board.getField(5) + board.getField(2);
        int sumColumn3 = board.getField(9) + board.getField(6) + board.getField(3);

        int sumDiagonal1 = board.getField(7) + board.getField(5) + board.getField(3);
        int sumDiagonal2 = board.getField(9) + board.getField(5) + board.getField(1);

        boolean rowWin = sumRow1 == winNumber || sumRow2 == winNumber || sumRow3 == winNumber;
        boolean columnWin = sumColumn1 == winNumber || sumColumn2 == winNumber || sumColumn3 == winNumber;
        boolean diagonalWin = sumDiagonal1 == winNumber || sumDiagonal2 == winNumber;

        return rowWin || columnWin || diagonalWin;
    }
}