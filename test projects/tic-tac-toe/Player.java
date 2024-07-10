/**
 * Player Klasse, welche die Namen, Gewinne und IDs der jeweiligen Spieler verwaltet.
 */
public class Player {
    private String name;
    private final int ID_Player;
    private int wins;

    /**
     * Erstellt einen neuen Spieler.
     * 
     * @param name      Name vom neuen Spieler.
     * @param ID_Player ID vom neuen Spieler.
     */
    public Player(String name, int ID_Player) {
        this.name = name;
        this.ID_Player = ID_Player;
    }

    /**
     * Setzt einen neuen Namen.
     * 
     * @param name Neuer Name, welcher für den Spieler gesetzt wird.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setzt eine neue Anzahl Gewinne. Dabei wird die gesamte Anzahl ersetzt und darum nicht mit dem momentanen Gewinnstand addiert.
     * 
     * @param wins Neue Anzahl Gewinne.
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * Gibt den Namen des Spielers.
     * 
     * @return Den momentanen Namen des Spielers.
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt die ID des Spielers.
     * 
     * @return Die ID des Spielers.
     */
    public int getID() {
        return this.ID_Player;
    }

    /**
     * Gibt die Anzahl der Gewinne des Spielers.
     * 
     * @return Die Anzahl der Gewinne des Spielers.
     */
    public int getWins() {
        return this.wins;
    }
}