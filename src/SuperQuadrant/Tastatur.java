package SuperQuadrant;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * Faengt Tastatur-Ereignisse ab und gibt sie an die Welt weiter.
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.1
 */
public class Tastatur extends Frame implements KeyListener {
    private Welt welt;

    /**
     * Konstruktor.
     * Wird von Konstruktor der Klasse Fenster aufgerufen
     * @param welt Die Welt, an die die Tastatureingaben uebergeben werden
     */
    public Tastatur(Welt welt) {
        super();
        this.welt = welt;
    }

    /**
     * uebergibt der Welt eine Taste, wenn diese losgelassen wurde
     */
    @Override
    public void keyReleased(KeyEvent e) {
        try {
            welt.tasteBetaetigt(e.getKeyChar(), false);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * uebergibt der Welt eine Taste, wenn diese gedrueckt wurde
     */
    @Override
    public void keyPressed(KeyEvent e) {
        try {
            welt.tasteBetaetigt(e.getKeyChar(), true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // erwartete, aber nicht verwendete Methode
    }
}