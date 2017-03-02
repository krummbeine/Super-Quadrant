package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;

/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de www.krummbeine.de
 * @version 2.00
 */
public class StartBlock extends Element
{
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public StartBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den Startblock
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(0, 60, 205, 55));

        // StartElement zeichnen
        g.fillRect(
                (int)(x - kamera.x),                   // x
                (int)(y - kamera.y),                   // y
                Fenster.GITTERGROESSE - 1,      // Breite
                Fenster.GITTERGROESSE - 1       // Hoehe
        );

        // Levelnummer der derzeitigen Welt anzeigen
        g.drawString(
                "L" + (Welt.levelNummer),
                (int)(x - kamera.x),                   // x
                (int)(y - kamera.y)                    // y
        );
    }
}