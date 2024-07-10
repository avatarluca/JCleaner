
/**
 * Diese Klasse bietet die Funktionalitaet, um einen Kaffee
 * zu kochen.
 * 
 * @author tebe
 */
public class Kaffee extends KoffeinGetraenk {
  
  @Override
  protected void fuegeZutatenHinzu() {
    System.out.println("Gebe Zucker und Milch in Kaffee.");   
  }

  @Override
  protected void braue() {
    System.out.println("Fuelle Filter mit gemalten Kaffeebohnen. "
        + "Giesse Wasser in Filter. Warte bis Wasser"
        + " durchgelaufen.");
  }
}
