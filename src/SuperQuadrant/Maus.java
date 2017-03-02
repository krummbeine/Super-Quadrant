package SuperQuadrant;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * Faengt die Maus-Ereignisse ab und uebergibt sie der Welt.
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.1
 */
public class Maus implements MouseListener, MouseMotionListener {
    private Welt welt;
    static int x, y;

    /**
     * Konstruktor
     *  Wird von Konstruktor der Klasse Fenster aufgerufen.
     * @param welt Die Welt, an die Maus-Ereignisse uebergeben werden
     */
    public Maus(Welt welt) {
        super();
        this.welt = welt;
        x = 0;
        y = 0;
    }

    /**
     * Ruft je nach gedrueckter Maustaste eine zugehoerige Methode in der welt auf
     * und uebergibt ggf. x- und y-Position des Klicks
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == 1)
            welt.linkeMaustasteGeklickt(new Point(e.getX(), e.getY()));
        if(e.getButton() == 3)
            welt.rechteMaustasteGeklickt();
    }

    /**
     * Aktualisiert die Attribute x und y der Maus, wenn sie bewegt wurde
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    // Nicht verwendete Methoden, die jedoch erwartet werden:
    @Override
    public void mousePressed(MouseEvent e) {
        // erwartete, aber nicht verwendete Methode
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // erwartete, aber nicht verwendete Methode
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // erwartete, aber nicht verwendete Methode
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // erwartete, aber nicht verwendete Methode
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // erwartete, aber nicht verwendete Methode
    }
}