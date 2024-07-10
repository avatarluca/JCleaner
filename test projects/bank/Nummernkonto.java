/**
 * Diese Klasse verwaltet ein Nummernkonto.
 * @author Marc Rennhard
 */
public class Nummernkonto extends Bankkonto {
  private int kontonummer;
  private static int naechsteKontonummer = 1000;

  /**
   * Erzeugt ein Nummernkonto mit dem spezifizierten Inhaber.
   * @param inhaber Der Inhaber des Kontos
   */
  public Nummernkonto(String inhaber) {
    super(inhaber);
    setzeKontonummer();
  }

  /**
   * Erzeugt ein Nummernkonto mit dem spezifizierten Inhaber und einer Ersteinlage.
   * @param inhaber Der Inhaber des Kontos
   * @param ersteinlage Die Ersteinlage
   */
  public Nummernkonto(String inhaber, double ersteinlage) {
    super(inhaber, ersteinlage);
    setzeKontonummer();
  }
  
  /**
   * Gibt den Inhaber (dessen Kontonummer) zurück.
   * @return Der Inhaber
   */
  @Override
  public String gibInhaber() {
    return "" + kontonummer;
  }
  
  /**
   * Setzt die Kontonummer.
   */
  private void setzeKontonummer() {
    kontonummer = naechsteKontonummer;
    naechsteKontonummer++;
  }
}