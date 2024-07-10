
/**
 * Diese Klasse bietet die Funktionalitaet, um ein 
 * koffeinhaltiges Getraenk zu kochen und zu trinken.
 * 
 * @author tebe
 */
public abstract class KoffeinGetraenk implements Trinkbar {
	
	/**
	 * Bereitet das Getraenk zu.
	 */
	public final void bereiteZu() {
		kocheWasser();
		braue();
		giesseInTasse();
		fuegeZutatenHinzu();
	}
	
	/**
	 * Trinkt das Getraenk.
	 */
	public final void trinke() {	
		System.out.println("Ich trinke einen " + getClass().getSimpleName());
	}

	private void kocheWasser() {
		System.out.println("Koche Wasser.");
	}
	
	private void giesseInTasse() {
		System.out.println("Giesse Getraenk in Tasse.");
	}
	
	protected abstract void fuegeZutatenHinzu();

	protected abstract void braue();
}
