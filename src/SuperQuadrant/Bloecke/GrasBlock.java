package SuperQuadrant.Bloecke;
import SuperQuadrant.*;

import java.awt.*;
import java.awt.Graphics;
import java.util.Random;

/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.3
 */
public class GrasBlock extends Element
{
    private int durchmesser;
    private int sollDurchmesser;
    Random rd;

    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public GrasBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        sollRechtsGehen = true;
        rd = new Random();
        this.durchmesser = this.sollDurchmesser = rd.nextInt(Fenster.GITTERGROESSE / 2);
    }

    /**
     * Aktualisiert und animiert den FeuerBlock
     * Trifft der FeuerBlock auf einen Spieler, zieht es ihm Leben ab.
     * Ueberschreibt die aktualisieren-Methode aus Element
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta) {
        // Animation des FeuerBlocks
        if(durchmesser > sollDurchmesser)
            durchmesser--;
        else if(durchmesser < sollDurchmesser)
            durchmesser++;
        else
            this.sollDurchmesser = rd.nextInt(Fenster.GITTERGROESSE * 10);

        spielerUnsichtbarMachen();
    }

    /**
     * Macht Spieler 1 und Spieler 2 unsichtbar, wenn er im Gras ist
     */
    private void spielerUnsichtbarMachen(){
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if(elementQuadrant.equals(spieler1Quadrant)) {
            if(Welt.spieler1.unsichtbarFuerGegner < 254)
                Welt.spieler1.unsichtbarFuerGegner += 2;
        }
        if(elementQuadrant.equals(spieler2Quadrant)) {
            if(Welt.spieler2.unsichtbarFuerGegner < 254)
            Welt.spieler2.unsichtbarFuerGegner += 2;
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet einen FeuerBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(66, 165, 49, 155));

        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        // Gras zeichnen
        g.fillRect(
                xx,           // x
                yy - durchmesser / 20,           // y
                Fenster.GITTERGROESSE / 4,                                                      // Breite
                Fenster.GITTERGROESSE + durchmesser / 10                                                     // Hoehe
        );
        g.fillRect(
                xx + Fenster.GITTERGROESSE / 3,           // x
                yy + Fenster.GITTERGROESSE - durchmesser  / 21,           // y
                Fenster.GITTERGROESSE / 4,                                                      // Breite
                durchmesser  / 21                                                       // Hoehe
        );
        g.fillRect(
                xx + Fenster.GITTERGROESSE  - durchmesser / 12,           // x
                yy + Fenster.GITTERGROESSE - durchmesser / 22,           // y
                Fenster.GITTERGROESSE / 2,                                                      // Breite
                durchmesser / 22                                                      // Hoehe
        );
        g.fillRect(
                xx +Fenster.GITTERGROESSE - durchmesser / 19,           // x
                yy - durchmesser / 29,           // y
                Fenster.GITTERGROESSE / 4,                                                      // Breite
                Fenster.GITTERGROESSE + durchmesser / 29                                                      // Hoehe
        );

        g.fillRect(
                xx + Fenster.GITTERGROESSE  - durchmesser / 12,           // x
                yy - durchmesser / 20,           // y
                Fenster.GITTERGROESSE / 4 - durchmesser / 31,                                                      // Breite
                Fenster.GITTERGROESSE + durchmesser / 20                                                     // Hoehe
        );
        g.fillRect(
                xx + durchmesser / 31,           // x
                yy - durchmesser  / 21,           // y
                Fenster.GITTERGROESSE,                                                      // Breite
                durchmesser  / 21    + Fenster.GITTERGROESSE                                                    // Hoehe
        );
        g.fillRect(
                xx + Fenster.GITTERGROESSE  - durchmesser / 22 -  Fenster.GITTERGROESSE / 4 ,           // x
                yy - durchmesser / 22,           // y
                Fenster.GITTERGROESSE / 4 + durchmesser / 51,                                                      // Breite
                durchmesser / 22     + Fenster.GITTERGROESSE                                                  // Hoehe
        );
        g.fillRect(
                xx + Fenster.GITTERGROESSE - durchmesser / 19 - Fenster.GITTERGROESSE / 3 ,           // x
                yy + Fenster.GITTERGROESSE - durchmesser / 29,           // y
                Fenster.GITTERGROESSE / 3 + durchmesser / 33,                                                      // Breite
                durchmesser / 29                                                      // Hoehe
        );
    }
}