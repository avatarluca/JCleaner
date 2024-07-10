/**
 * Diese Klasse verwaltet ein Salaerkonto.
 * @author Marc Rennhard
 */
public class Salaerkonto extends Bankkonto {  
  private static final int MAX_UEBERZUGSLIMITE_RAPPEN = 1000000;
  private int ueberzugsLimiteRappen;

  /**
   * Erzeugt ein Salaerkonto mit dem spezifizierten Inhaber und
   * der spezifizierten Ueberzugslimite.
   * @param inhaber Der Inhaber des Kontos
   */
  public Salaerkonto(String inhaber, int ueberzugsLimite) {
    super(inhaber);
    setzeUeberzugsLimite(ueberzugsLimite);
  }

  /**
   * Erzeugt ein Salaerkonto mit dem spezifizierten Inhaber, einer Ersteinlage und
   * der spezifizierten Ueberzugslimite.
   * @param inhaber Der Inhaber des Kontos
   * @param ersteinlage Die Ersteinlage
   * @param ueberzugsLimite Die Ueberzugslimite
   */
  public Salaerkonto(String inhaber, double ersteinlage, int ueberzugsLimite) {
    super(inhaber, ersteinlage);
    setzeUeberzugsLimite(ueberzugsLimite);
  }
  
  /**
   * Gibt die Ueberzugslimite in Franken zurueck.
   * @return Die Ueberzugslimite in Franken
   */
  public double gibUeberzugsLimite() {
    return rappenZuFranken(ueberzugsLimiteRappen);
  }

  /**
   * Hebt betrag Franken von Konto ab, falls der Betrag >= 0 ist. Es wird nur
   * soviel abgehoben, damit die Ueberzugslimite nicht unterschritten wird.
   * @param betrag Der auszuzahlende Betrag
   * @return Der effektiv ausbezahlte Betrag
   */
  @Override
  public double abheben(double betragFranken) {
    if (betragFranken <= 0) {
      return 0;
    }
    if (frankenZuRappen(betragFranken) > (gibKontostandRappen() + ueberzugsLimiteRappen)) {
      double betragFrankenAusbezahlt = rappenZuFranken(gibKontostandRappen() + ueberzugsLimiteRappen);
      setzeKontostandRappen(-ueberzugsLimiteRappen);
      return betragFrankenAusbezahlt;
    } else {
      setzeKontostandRappen(gibKontostandRappen() - frankenZuRappen(betragFranken));
      return betragFranken;
    }
  }
  
  /**
   * Setzt die Ueberzugslimite. Diese darf nicht kleiner als 0 und nicht groesser
   * als der definierte maximale Wert sein.
   * @param ueberzugsLimite Die Ueberzugslimite
   */
  private void setzeUeberzugsLimite(int ueberzugsLimite) {
    if (ueberzugsLimite <= 0) {
      ueberzugsLimiteRappen = 0;
    } else if (frankenZuRappen(ueberzugsLimite) > MAX_UEBERZUGSLIMITE_RAPPEN) {
      ueberzugsLimiteRappen = MAX_UEBERZUGSLIMITE_RAPPEN;
    } else {
      ueberzugsLimiteRappen = frankenZuRappen(ueberzugsLimite);
    }
  }
}
