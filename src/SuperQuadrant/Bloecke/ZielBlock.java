package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;

/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class ZielBlock extends Element
{
    private int durchmesser;
    private boolean durchmesserVerkleinern;

    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public ZielBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.durchmesser = Fenster.GITTERGROESSE * 2;
        this.durchmesserVerkleinern = true;
    }

    /**
     * Aktualisiert Zielblock
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        if(Welt.alleMuenzenEingesammelt) {
            animieren();
            beruehrungErkennen();
        }
    }

    /**
     * Animiert den Ziel-Block
     */
    private void animieren(){
        // Animation des ZielBlocks
        if (durchmesserVerkleinern) {
            durchmesser--;
            if (durchmesser < Fenster.GITTERGROESSE * 2)
                durchmesserVerkleinern = !durchmesserVerkleinern;
        } else {
            durchmesser++;
            if (durchmesser > Fenster.GITTERGROESSE * 10) {
                durchmesserVerkleinern = !durchmesserVerkleinern;
            }
        }
    }

    /**
     * Prueft, ob ein Spieler den Ziel-Block beruehrt und wenn ja, aendere den Zustand des Spieles auf 2 (gewonnen),
     * falls alle Muenzen eingesammelt wurden.
     */
    private void beruehrungErkennen(){
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if (elementQuadrant.equals(spieler1Quadrant))
            Welt.zustand = 2; // Zustand: gewonnen
        if (elementQuadrant.equals(spieler2Quadrant))
            Welt.zustand = 2; // Zustand: gewonnen
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den Zielblock
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(0, 205, 120, 100));

        // MuenzenBlock zeichnen
        g.fillRect(
                (int)(x - kamera.x),                   // x
                (int)(y - kamera.y),                   // y
                Fenster.GITTERGROESSE - 1,      // Breite
                Fenster.GITTERGROESSE - 1       // Hoehe
        );

        if(Welt.alleMuenzenEingesammelt) {
            // Animation zeichnen
            int sichtbarkeit = 255 - 255 * durchmesser / (Fenster.GITTERGROESSE * 10);
            g.setColor(new Color(0, 205, 120, sichtbarkeit));
            if (durchmesser > 0) {
                g.fillRect(
                        (int)(x - kamera.x + Fenster.GITTERGROESSE / 2 - durchmesser / 8 - 1),           // x
                        (int)(y - kamera.y + Fenster.GITTERGROESSE / 2 - durchmesser / 8 - 1),           // y
                        durchmesser / 4,                                                  // Breite
                        durchmesser / 4                                                    // Hoehe
                );
            }
        }

        // LevelNummer der naechsten Welt anzeigen
        g.drawString(
                "L" + (Welt.levelNummer + 1),
                (int)(x - kamera.x),                   // x
                (int)(y - kamera.y)                    // y
        );
    }
}