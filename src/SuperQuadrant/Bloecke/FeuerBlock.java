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
public class FeuerBlock extends Element
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
    public FeuerBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
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
        this.durchmesser = this.sollDurchmesser = rd.nextInt(Fenster.GITTERGROESSE / 4) + Fenster.GITTERGROESSE / 4;
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
            this.sollDurchmesser = rd.nextInt(Fenster.GITTERGROESSE * 2);

        spielernLebenAbziehen();
    }

    /**
     * Zieht Spieler 1 und Spieler 2 Leben ab, wenn es auf sie trifft
     */
    private void spielernLebenAbziehen(){
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if(elementQuadrant.equals(spieler1Quadrant))
            Spieler.spielerLebenAbziehen(1);
        if(elementQuadrant.equals(spieler2Quadrant))
            Spieler.spielerLebenAbziehen(2);
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet einen FeuerBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(255, 160, 0, 155));

        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        // 4 Flammen zeichnen
        g.fillRect(
                xx,           // x
                yy + Fenster.GITTERGROESSE - durchmesser,           // y
                Fenster.GITTERGROESSE,                                                      // Breite
                durchmesser                                                       // Hoehe
        );
        g.fillRect(
                xx,           // x
                yy + Fenster.GITTERGROESSE - durchmesser / 4,           // y
                Fenster.GITTERGROESSE,                                                      // Breite
                durchmesser / 4                                                       // Hoehe
        );
        g.fillRect(
                xx + Fenster.GITTERGROESSE / 2 - durchmesser / 2,           // x
                yy + Fenster.GITTERGROESSE - durchmesser / 2,           // y
                durchmesser,                                                      // Breite
                durchmesser / 2                                                      // Hoehe
        );
        g.fillRect(
                xx,           // x
                yy + Fenster.GITTERGROESSE - durchmesser / 3,           // y
                Fenster.GITTERGROESSE,                                                      // Breite
                durchmesser / 3                                                      // Hoehe
        );
    }
}