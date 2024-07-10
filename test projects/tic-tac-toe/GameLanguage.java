/**
 * <pre>
 * Die GameLanguage-Klasse verwaltet die Sprache des Spiels.
 * 
 * Alle Befehle, welche während des Spiels ausgegeben werden, sind in dieser Klasse definiert.
 * </pre>
 */
public class GameLanguage {
    private boolean isEnglish;

    /**
     * Erstellt ein neues Sprach Objekt.
     * 
     * @param isEnglish Definiert bei true, dass die Texte auf Englisch ausgegeben werden. Wenn auf false werden diese auf Deutsch ausgegeben.
     */
    public GameLanguage(boolean isEnglish) {
        this.isEnglish = isEnglish;
    }

    /**
     * Standard Konstruktor definiert isEnglish als true.
     */
    public GameLanguage() {
        isEnglish = true;
    }

    /**
     * Gibt den Setzen Befehl.
     * 
     * @return Den Setzen Befehl in der jeweiligen Sprache.
     */
    public String getSetCommand() {
        if (isEnglish) {
            return "set";
        } else {
            return "setzen";
        }
    }

    /**
     * Gibt den Sprach Befehl.
     * 
     * @return Den Sprach Befehl in der jeweiligen Sprache.
     */
    public String getLanguageCommand() {
        if (isEnglish) {
            return "language";
        } else {
            return "sprache";
        }
    }

    /**
     * Ändert die Sprache.
     */
    public void changeLanguage() {
        isEnglish = !isEnglish;
    }

    /**
     * Gibt eine Willkommens Nachricht in die Konsole aus.
     */
    public void printWelcomeMessage() {
        if (isEnglish) {
            System.out.println("The Tired Cowboy's welcomes you to Tic-Tac-Toe");
        } else {
            System.out.println("Die Müden Cowboy's heissen Sie Herzlich Willkommen zu Tic-Tac-Toe");
        }
    }

    /**
     * Gibt eine Hilfe Nachricht in die Konsole aus.
     */
    public void printHelpMessage() {
        if (isEnglish) {
            System.out.println("Enter 'set' to make your next move, or 'language' to change language");
        } else {
            System.out.println(
                    "Geben sie 'setzen' ein um den nächsten Zug zu machen, oder 'sprache' um die Sprache zu ändern");
        }
    }

    /**
     * Gibt die momentane Runde in die Konsole aus.
     * 
     * @param round Momentane Spielrunde.
     */
    public void printRoundInfo(int round) {
        System.out.println();
        System.out.println();
        System.out.println("===========================");

        if (isEnglish) {
            System.out.println("Round " + round);
        } else {
            System.out.println("Runde " + round);
        }

        System.out.println("===========================");
    }

    /**
     * Gibt eine Spieler Nachricht in die Konsole aus.
     * 
     * @param currentPlayer Der Name des momentanen Spielers.
     */
    public void printCurrentPlayerMessage(String currentPlayer) {
        if (isEnglish) {
            System.out.println(currentPlayer + " please make your next move");
        } else {
            System.out.println(currentPlayer + " bitte machen Sie Ihren nächsten Zug");
        }
    }

    /**
     * Gibt eine Befehls Nachricht in die Konsole aus.
     */
    public void printCommandText() {
        if (isEnglish) {
            System.out.println("Please set your next command");
        } else {
            System.out.println("Bitte geben Sie Ihren nächsten Befehl ein");
        }
    }

    /**
     * Gibt eine Zuginfo Nachricht in die Konsole aus.
     */
    public void printSelectFieldText() {
        if (isEnglish) {
            System.out.println("Please choose your next move");
        } else {
            System.out.println("Bitte machen Sie Ihren nächsten Zug");
        }
    }

    /**
     * Gibt eine Zug Nachricht in die Konsole aus.
     * 
     * @param currentPlayer Der Name des momentanen Spielers.
     * @param field         Das Spielfeld, welches der Spieler belegen möchte.
     */
    public void printCurrentPlayersMoveMessage(String currentPlayer, int field) {
        if (isEnglish) {
            System.out.println(currentPlayer + " makes the move " + field);
        } else {
            System.out.println(currentPlayer + " macht den Zug " + field);
        }
    }

    /**
     * Gibt eine Spielername Nachricht in die Konsole aus.
     * 
     * @param player Der Name des momentanen Spielers.
     */
    public void printGetNewPlayerMessage(String player) {
        if (isEnglish) {
            System.out.println(player + " please enter your name");
        } else {
            System.out.println(player + " bitte geben sie ihren namen ein");
        }
    }

    /**
     * Gibt den Gewinner in die Konsole aus.
     * 
     * @param winner
     */
    public void printWinnerMessage(String winner) {
        System.out.println();
        System.out.println();
        System.out.println("****************************");

        if (isEnglish) {
            System.out.println("The winner is: " + winner);
        } else {
            System.out.println("Der Gewinner ist: " + winner);
        }

        System.out.println("****************************");
    }

    /**
     * Gibt eine Unentschieden Nachricht in die Konsole aus.
     */
    public void printDrawMessage() {
        if (isEnglish) {
            System.out.println("Its a draw");
        } else {
            System.out.println("Unentschieden");
        }
    }

    /**
     * Gibt die Spielstatistiken in die Konsole aus.
     * 
     * @param player1_name Name von Spieler 1.
     * @param player2_name Name von Spieler 2.
     * @param player1_wins Gewinne von Spieler 1.
     * @param player2_wins Gewinne von Spieler 2.
     */
    public void printStats(String player1_name, String player2_name, int player1_wins, int player2_wins) {
        System.out.println();

        if (isEnglish) {
            System.out.println("Gamestats: " + player1_name + ": " + player1_wins + " - " + player2_name
                    + ": " + player2_wins);
        } else {
            System.out.println("Spielstatistik: " + player1_name + ": " + player1_wins + " - "
                    + player2_name + ": " + player2_wins);
        }

        System.out.println();
        System.out.println();
    }

    /**
     * Gibt eine Restart Nachricht in die Konsole aus.
     */
    public void printRestartMessage() {
        if (isEnglish) {
            System.out.println("Press 'y' for a restart, or 'n' to exit");
        } else {
            System.out.println("Drücken Sie 'j' für einen Neustart, oder 'n' um das Spiel zu beenden");
        }
    }

    /**
     * Gibt eine Schliess Nachricht in die Konsole aus.
     */
    public void printExitMessage() {
        if (isEnglish) {
            System.out.println("Thanks for playing");
        } else {
            System.out.println("Danke fürs spielen");
        }
    }

    /**
     * Gibt eine Drehbuch Nachricht in die Konsole aus.
     */
    public void printScreenplayDone() {
        if (isEnglish) {
            System.out.println("Information: No more turns left in screenplay, switching to interactive mode");
        } else {
            System.out.println("Information: Es sind keine Züge mehr im Drehbuch, wechsel auf interaktiven Modus");
        }
    }

    /// *********** ERROR MESSAGES ***********///

    /**
     * Gibt eine allgemeine Fehlermeldung in die Konsole aus.
     */
    public void printErrorMessage() {
        if (isEnglish) {
            System.out.println("This input is invalid");
        } else {
            System.out.println("Diese Eingabe ist ungültig");
        }

        System.out.println();
    }

    /**
     * Gibt eine syntaktische Fehlermeldung in die Konsole aus.
     */
    public void printSyntaxFieldErrorText() {
        if (isEnglish) {
            System.out.println("Please enter a valid input between 1-9");
        } else {
            System.out.println("Bitte geben Sie einen gültigen Input zwischen 1-9 an");
        }

        System.out.println();
    }

    /**
     * Gibt eine Feldbelegung Fehlermeldung in die Konsole aus.
     */
    public void printOccupiedFieldErrorMessage() {
        if (isEnglish) {
            System.out.println("This Field is already used");
        } else {
            System.out.println("Dieses Feld ist schon besetzt");
        }

        System.out.println();
    }
}
