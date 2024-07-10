/**
 * Repräsentiert das Spielfeld.
 * 
 * <pre>
 * Layout des Spielfeldes:
 *      |     |
 *   7  |  8  |  9
 * _____|_____|_____
 *      |     |
 *   4  |  5  |  6
 * _____|_____|_____
 *      |     |
 *   1  |  2  |  3
 *      |     |
 * </pre>
 */
public class GameBoard {
    private int field1;
    private int field2;
    private int field3;
    private int field4;
    private int field5;
    private int field6;
    private int field7;
    private int field8;
    private int field9;

    /**
     * Gibt den im Feld gespeicherten Wert aus.
     *
     * @param fieldName Zahl von 1 bis 9 welche das Feld angibt.
     * @return Denn im Feld gespeicherten Zahlen Wert.
     */
    public int getField(int fieldName) {
        return switch (fieldName) {
            case 1 -> field1;
            case 2 -> field2;
            case 3 -> field3;
            case 4 -> field4;
            case 5 -> field5;
            case 6 -> field6;
            case 7 -> field7;
            case 8 -> field8;
            case 9 -> field9;
            default -> throw new IllegalArgumentException("Unexpected value: " + fieldName);
        };
    }

    /**
     * Gib das Symbol des Feldes aus.
     *
     * @param fieldName Zahl von 1 bis 9 welche das Feld angibt.
     * @return Symbol des Feldes.
     */
    private String getFieldSymbol(int fieldName) {
        return switch (getField(fieldName)) {
            case 0 -> " ";
            case 1 -> "X";
            case -1 -> "O";
            default -> throw new IllegalArgumentException("Unexpected value: " + getField(fieldName));
        };
    }

    /**
     * Setzt den Wert des Feldes neu.
     *
     * @param fieldName  Zahl von 1 bis 9 welche das Feld angibt.
     * @param fieldValue Zahl des neuen Feldwertes.
     */
    public void makeMove(int fieldName, int fieldValue) {
        switch (fieldName) {
            case 1 -> field1 = fieldValue;
            case 2 -> field2 = fieldValue;
            case 3 -> field3 = fieldValue;
            case 4 -> field4 = fieldValue;
            case 5 -> field5 = fieldValue;
            case 6 -> field6 = fieldValue;
            case 7 -> field7 = fieldValue;
            case 8 -> field8 = fieldValue;
            case 9 -> field9 = fieldValue;
            default -> throw new IllegalArgumentException("Unexpected value: " + fieldName);
        }
    }

    /**
     * Gibt das Spielfeld in der Konsole aus.
     */
    public void print() {
        System.out.println();
        System.out.println("          |     |     ");
        System.out.println(
                "       " + getFieldSymbol(7) + "  |  " + getFieldSymbol(8) + "  |  " + getFieldSymbol(9) + "  ");
        System.out.println("     _____|_____|_____");
        System.out.println("          |     |     ");
        System.out.println(
                "       " + getFieldSymbol(4) + "  |  " + getFieldSymbol(5) + "  |  " + getFieldSymbol(6) + "  ");
        System.out.println("     _____|_____|_____");
        System.out.println("          |     |     ");
        System.out.println(
                "       " + getFieldSymbol(1) + "  |  " + getFieldSymbol(2) + "  |  " + getFieldSymbol(3) + "  ");
        System.out.println("          |     |     ");
        System.out.println();
    }

    /**
     * Das Spielfeld wird gesäubert.
     */
    public void clear() {
        field1 = 0;
        field2 = 0;
        field3 = 0;
        field4 = 0;
        field5 = 0;
        field6 = 0;
        field7 = 0;
        field8 = 0;
        field9 = 0;
    }
}