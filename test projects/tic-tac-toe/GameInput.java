import java.util.Scanner;

/**
 * <pre>
 * GameInput behandelt alle Fälle, in denen ein Spieler eine Eingabe macht.
 * 
 * Es gibt grundsätzlich 2 Spielvarianten: Interaktiv und Screenplay, welche die GameInput Klasse verwaltet.
 * Wenn isInteractive falsch ist, werden alle Aufrufe von readInput() automatisch von einem Screenplay (Drehbuch) zurückgegeben.
 * </pre>
 */
public class GameInput {
    private final GameLanguage language;
    private boolean isInteractive;
    private int screenplayActionIndex;
    private String command;
    private int position;
    private final Scanner scanner;

    /**
     * Erstellt eine neues Input Objekt.
     * 
     * @param isInteractive Gibt an ob der User selbst eine Eingabe macht.
     * @param language      Spracheobjekt mit welchem das Input Objekt arbeitet.
     */
    public GameInput(boolean isInteractive, GameLanguage language) {
        this.isInteractive = isInteractive;
        this.language = language;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Gibt den abgespeicherten Befehl.
     * 
     * @return Den momentanen Befehl.
     */
    public String getCommandText() {
        return this.command;
    }

    /**
     * Gibt die abgespeicherte Feldposition.
     * 
     * @return Die momentane Position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Verlange Eingaben basierend auf isInteractive.
     * 
     * @return Ob der Input valid oder invalid ist.
     */
    public boolean readInput() {
        if (this.isInteractive) {
            return this.readScanner();
        } else {
            return this.readScreenplay();
        }
    }

    /**
     * <pre>
     * Lass den Spieler seinen Namen durch eine Eingabe ändern.
     * Wenn nichts eingegeben wird, wird der alte Name zurückgegeben
     * </pre>
     * 
     * @param oldName Der alte Name des Spielers.
     * @return Gibt den neuen Namen des Spielers zurück.
     */
    public String requestName(String oldName) {
        String newName;

        language.printGetNewPlayerMessage(oldName);

        newName = scanner.nextLine();

        if (newName.equals("")) {
            return oldName;
        } else {
            return newName;
        }
    }

    /**
     * Frage die Spieler durch Eingabe, ob sie noch eine Runde spielen möchten.
     */
    public boolean requestRestart() {
        String answer;

        language.printRestartMessage();

        answer = scanner.nextLine();
        answer = answer.substring(0, 1);

        // (y/n) oder (j/n) für yes/no oder ja/nein
        if (answer.equals("n")) {
            language.printExitMessage();
            System.exit(0);
        } else if (answer.equals("y") || answer.equals("j")) {
            return true;
        }

        language.printErrorMessage();

        return false;
    }

    /**
     * <pre>
     * Verlangt interaktive Eingaben des Spielers, um den nächsten Zug auszuführen oder die Sprache zu ändern.
     *  
     * Je nach aktueller Sprache sind die Befehle unterschiedlich:
     * Deutsch:<br>
     * "setzen" oder "sprache"
     * Englisch:
     * "set" oder "language"
     * 
     * Ein gültiger Zug kann eine Nummer von 1 (inklusiv) bis 9 (inklusiv) sein.
     * Dabei überprüft readScanner bereits, ob die Eingabe ein gültiger Zug in Form des Syntax ist. Sie macht keine Semantik Überprüfung (diese wird in der Spielelogik durchgeführt).
     * </pre>
     * 
     * @return Ist die Eingabe syntaktisch korrekt.
     */
    private boolean readScanner() {
        language.printCommandText();
        language.printHelpMessage();

        command = scanner.nextLine();

        char charPosition;
        String strPosition;

        if (command.equals(language.getSetCommand())) {
            language.printSelectFieldText();

            strPosition = scanner.nextLine();

            if (strPosition.length() != 1) { // Erlaube nur Eingaben mit einer Länge von einem Zeichen.
                language.printSyntaxFieldErrorText();

                command = "";
                position = 0;

                return false;
            } else {
                charPosition = strPosition.charAt(0);

                if ((charPosition > 48) && (charPosition < 58)) { // 49-57 = Ascii 1-9
                    position = charPosition - '0';

                    return true;
                } else {
                    language.printSyntaxFieldErrorText();

                    command = "";
                    position = 0;

                    return false;
                }
            }
        } else if (command.equals(language.getLanguageCommand())) {
            return true;
        } else {
            language.printErrorMessage();

            return false;
        }
    }

    /**
     * Gibt automatisch Züge zurück basierend auf dem Drehbuch.
     * 
     * @return Gibt den naechsten Zug zurück.
     */
    private boolean readScreenplay() {
        switch (++screenplayActionIndex) {

            /*
             * Vorlage, um das Drehbuch zu ergänzen:
             * 
             * command = "set";
             * position = 3;
             * 
             * return true;
             */

            case 1 -> {
                command = "set";
                position = 3;

                return true;
            }
            case 2 -> {
                command = "set";
                position = 5;

                return true;
            }
            case 3 -> {
                command = "set";
                position = 1;

                return true;
            }
            case 4 -> {
                command = "set";
                position = 7;

                return true;
            }
            case 5 -> {
                System.out.println("readScreenplay: the next turn is invalid by design");
                command = "set";
                position = 1;

                return true;
            }
            case 6 -> {
                command = "set";
                position = 4;

                return true;
            }
            case 7 -> {
                command = "set";
                position = 2;

                return true;
            }
            case 8 -> {
                command = "set";
                position = 9;

                return true;
            }
            case 9 -> {
                command = "set";
                position = 6;

                return true;
            }
            case 10 -> {
                command = "set";
                position = 8;

                return true;
            }
            default -> {
                language.printScreenplayDone();
                isInteractive = true;

                return false;
            }
        }
    }
}