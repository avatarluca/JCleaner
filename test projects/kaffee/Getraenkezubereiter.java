import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse implementiert einen Gertaenkezubereiter, der 
 * Koffeingetraenke zubereiten kann.
 * 
 * @author tebe
 */
public class Getraenkezubereiter {

  /**
   * Main Methode.
   * @param args Keine Kommandozeilenparameter
   */
  public static void main(String[] args) {
    List<KoffeinGetraenk> getraenke = new ArrayList<KoffeinGetraenk>();
    getraenke.add(new Kaffee());
    getraenke.add(new Tee());
    getraenke.add(new Kaffee());
    Getraenkezubereiter.zubereitenUndTrinken(getraenke);
  }

  /**
   * Bereitet die Uebergebenen Getraenke zu und trinkt sie.
   * @param Die Getraenke, die zubereitet und getrunken werden sollen.
   */
  public static void zubereitenUndTrinken(List<KoffeinGetraenk> getraenke) {
    for (KoffeinGetraenk getraenk : getraenke) {
      getraenk.bereiteZu();
      getraenk.trinke();
    }
  }
}
