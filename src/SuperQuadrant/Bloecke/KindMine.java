package SuperQuadrant.Bloecke;

import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;

/**
 * @author Helbig Christian
 */
public class KindMine extends VaterMine {
    /**
     * Konstruktor
     *
     * @param startPosition         Die Startposition des Monsters
     * @param typ                   Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit       Die Geschwindigkeit des Elements
     * @param kamera                Die Kamera
     * @param mitKameraVerfolgen    Legt fest, ob die Kamera dem Spieler folgen soll
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public KindMine(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(startPosition, typ, geschwindigkeit, kamera, mitKameraVerfolgen, sollGespeichertWerden);
    }

    /**
     * Zeichnet die KindMine
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
            // Uebernehmen Farbe der Eltern
            g.setColor(new Color(0, 0, 0, 255));
            if (super.typ == 19)
                g.setColor(new Color(184, 73, 72, 255));

            int xx = (int) (x - kamera.x);
            int yy = (int) (y - kamera.y);

            // Stacheln zeichnen
            g.fillRect(
                    xx + Fenster.GITTERGROESSE / 2 - durchmesser / 8,           // x
                    yy + Fenster.GITTERGROESSE / 2 - durchmesser / 8,           // y
                    durchmesser / 4,                                            // Breite
                    durchmesser / 4                                             // Hoehe
            );

            int[] xPunkte = {
                    xx + Fenster.GITTERGROESSE / 2,
                    xx + Fenster.GITTERGROESSE / 2 + durchmesser / 6,
                    xx + Fenster.GITTERGROESSE / 2,
                    xx + Fenster.GITTERGROESSE / 2 - durchmesser / 6
            };
            int[] yPunkte = {
                    yy + Fenster.GITTERGROESSE / 2 - durchmesser / 6,
                    yy + Fenster.GITTERGROESSE / 2,
                    yy + Fenster.GITTERGROESSE / 2 + durchmesser / 6,
                    yy + Fenster.GITTERGROESSE / 2
            };
            g.fillPolygon(xPunkte, yPunkte, 4);
    }

    /**
     * Zieht den Spielern bei Beruehrung Leben ab, solange das Leben groesser 0 ist
     */
    @Override
    protected void beruehrung() {
            Point elementQuadrant = Fenster.posToPosMitteQuadrant((int) x, (int) y);
            Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler1.x, (int) Welt.spieler1.y);
            Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler2.x, (int) Welt.spieler2.y);

            if (elementQuadrant.equals(spieler1Quadrant))
                Spieler.spielerLebenAbziehen(1);


            if (elementQuadrant.equals(spieler2Quadrant))
                Spieler.spielerLebenAbziehen(2);
    }
}
