/**
 * Diese Klasse verwaltet ein Bankkonto.
 * @author Marc Rennhard
 */
public class Bankkonto {
  private static final int MAX_KONTOSTAND_RAPPEN = 10000000;
  private String inhaber;
  private int kontostandRappen;

  /**
   * Erzeugt ein Bankkonto mit dem spezifizierten Inhaber.
   * @param inhaber Der Inhaber des Kontos
   */
  public Bankkonto(String inhaber) {
    this(inhaber, 0);
  }

  /**
   * Erzeugt ein Bankkonto mit dem spezifizierten Inhaber und einer Ersteinlage.
   * @param inhaber Der Inhaber des Kontos
   * @param ersteinlage Die Ersteinlage
   */
  public Bankkonto(String inhaber, double ersteinlage) {
    this.inhaber = inhaber;
    einzahlen(ersteinlage);
  }

  /**
   * Gibt den Kontostand in Franken zurueck.
   * @return Der Kontostand in Franken
   */
  public double gibKontostand() {
    return rappenZuFranken(kontostandRappen);
  }

  /**
   * Gibt den Inhaber zurück.
   * @return Der Inhaber
   */
  public String gibInhaber() {
    return inhaber;
  }

  /**
   * Zahlt betrag Franken auf Konto ein, falls der Betrag >= 0 ist. Es wird nur
   * soviel einbezahlt, damit der maximale Kontostand nicht überschritten wird.
   * @param betrag Der einzuzahlende Betrag
   * @return Der effektiv einbezahlte Betrag
   */
  public double einzahlen(double betragFranken) { 
    if (betragFranken <= 0) { 
      return 0;
    }
    if ((kontostandRappen + frankenZuRappen(betragFranken)) <= MAX_KONTOSTAND_RAPPEN) {
      kontostandRappen += frankenZuRappen(betragFranken);
      return betragFranken;
    } else {
      double betragFrankenEinbezahlt = rappenZuFranken(MAX_KONTOSTAND_RAPPEN - kontostandRappen);
      kontostandRappen = MAX_KONTOSTAND_RAPPEN;
      return betragFrankenEinbezahlt;
    }
  }

  /**
   * Hebt betrag Franken von Konto ab, falls der Betrag >= 0 ist. Es wird nur
   * soviel abgehoben, damit der Kontostand nicht negativ wird.
   * @param betrag Der auszuzahlende Betrag
   * @return Der effektiv ausbezahlte Betrag
   */
  public double abheben(double betragFranken) {
    if (betragFranken <= 0) {
      return 0;
    }
    if (frankenZuRappen(betragFranken) > kontostandRappen) {
      double betragFrankenAusbezahlt = rappenZuFranken(kontostandRappen);
      kontostandRappen = 0;
      return betragFrankenAusbezahlt;
    } else {
      kontostandRappen = kontostandRappen - frankenZuRappen(betragFranken);
      return betragFranken;
    }
  }

  /**
   * Wandelt einen Frankenbetrag in Rappen, wobei auf Rappen gerundet wird.
   * Ist der Rappenbetrag zu gross für einen int, so wird der grösstmögliche
   * int-Wert zurückgegeben. ist der übergebene Betrag negativ, so wird 0 
   * zurückgegeben.
   * @param franken Der Betrag in Franken
   * @return
   */
  protected int frankenZuRappen(double franken) {
    if (franken < 0) {
      return 0;
    } else if ((franken * 100) < Integer.MAX_VALUE) {
      return (int) (Math.round(franken * 100));
    } else {
      return Integer.MAX_VALUE;
    }
  }

  /**
   * Wandelt einen Rappenbetrag in entsprechenden Frankenbetrag.
   * @param rappen Der Betrag in Rappen
   * @return Der Betrag in Franken
   */
  protected double rappenZuFranken(int rappen) {
    return rappen/100.0;
  }
  
  /**
   * Gibt den Kontostand in Rappen zurueck.
   * @return Der Kontostand in Rappen
   */
  protected int gibKontostandRappen() {
    return kontostandRappen;
  }
  
  /**
   * Setzt den Kontostand in Rappen.
   * @param kontostandRappen Der Kontostand
   */
  protected void setzeKontostandRappen(int kontostandRappen) {
    this.kontostandRappen = kontostandRappen;
  }
}