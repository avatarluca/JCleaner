import java.util.ArrayList;

/**
 * Simuliert eine Bank.
 * @author Marc Rennhard
 */
public class Banksimulation {
  ArrayList<Bankkonto> konten;

  /**
   * Konstruktor.
   */
  public Banksimulation() {
    konten = new ArrayList<Bankkonto>();
  }
  
  /**
   * Erzeugt einige Konten.
   */
  public void kontenErzeugen() {
    konten.add(new Bankkonto("Lucky Luke"));
    konten.add(new Bankkonto("Jolly Jumper", 30000));
    konten.add(new Nummernkonto("Al Capone", 200000));
    konten.add(new Nummernkonto("Billy the Kid"));
    konten.add(new Salaerkonto("Micky Maus", 5000., 20000));
    konten.add(new Salaerkonto("Donald Duck", 5000));
  }
 
  /**
   * Gibt alle Konten aus.
   */
  public void kontenAusgeben() {
    for (Bankkonto konto: konten) {
      ausgeben(konto);
    }
  }

  /**
   * Zahlt Geld ein.
   */
  public void geldEinzahlen() {
    konten.get(0).einzahlen(5000);
    konten.get(1).einzahlen(-5000);
    konten.get(2).einzahlen(5000);
    konten.get(5).einzahlen(150000);
  }
 
  /**
   * Hebt Geld ab.
   */
  public void geldAbheben() {
    konten.get(0).abheben(6000);
    konten.get(4).abheben(30000);
    konten.get(5).abheben(102000);
  }

  /**
   * Gibt ein Konto aus.
   * @param konto Das auszugebende Konto
   */
  private void ausgeben(Bankkonto konto)  {
    String resultat = "Inhaber: " + konto.gibInhaber() + ", Kontostand: " + konto.gibKontostand();
    if (konto instanceof Salaerkonto) {
      resultat += ", Ueberzugslimite: " + ((Salaerkonto) konto).gibUeberzugsLimite();
    }
    System.out.println(resultat);
  }
  
  /**
   * main Methode.
   * @param args Keine Kommandozeilenparameter
   */
  public static void main(String[] args) {
    Banksimulation simulation = new Banksimulation();
    simulation.kontenErzeugen();
    simulation.kontenAusgeben();
    System.out.println();
    simulation.geldEinzahlen();
    simulation.kontenAusgeben();
    System.out.println();
    simulation.geldAbheben();
    simulation.kontenAusgeben();
  }
}