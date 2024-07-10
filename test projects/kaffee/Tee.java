
/**
 * Diese Klasse bietet die Funktionalitaet, um einen Tee
 * zu kochen.
 * 
 * @author tebe
 */
public class Tee extends KoffeinGetraenk {
  
  @Override
  protected void fuegeZutatenHinzu() {
    System.out.println("Fuege einen Zitronenschnitz hinzu.");
  }

  @Override
  protected void braue() {
    System.out.println("Gib Teebeutel hinzu und lasse Tee "
        + "fuer 3-4 Minuten ziehen.");  
  }
}
