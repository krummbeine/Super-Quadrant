package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;
import java.util.Random;


/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class MuenzenBlock extends Element
{
    private int breite;
    private boolean breiteVerkleinern;
    private Random random;
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public MuenzenBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        random = new Random();
        // Zufallszahl zwischen [Fenster.GITTERGROESSE / 2, Fenster.GITTERGROESSE * 2 ]
        this.breite = random.nextInt(Fenster.GITTERGROESSE) + Fenster.GITTERGROESSE;
        this.breiteVerkleinern = true;
    }

    /**
     * Aktualisiert und animiert die MuenzenBlock
     * Erhoeht die Anzahl der Muenzen eines Spielers, wenn er die MuenzenBlock beruehrt und verschwindet dann,
     * damit sie nicht nochmal eingesammelt werden kann. Animiert die MuenzenBlock.
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        if(breite != 0) {
            // MuenzenBlock noch nicht eingesammelt

            // Animation der MuenzenBlock
            if(breiteVerkleinern) {
                breite--;
                if(breite < Fenster.GITTERGROESSE / 5)
                    breiteVerkleinern = !breiteVerkleinern;
            } else{
                breite++;
                if(breite == Fenster.GITTERGROESSE * 2){
                    breiteVerkleinern = !breiteVerkleinern;
                }
            }

            // Einsammeln der MuenzenBlock bei Beruehrung
            Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
            Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
            Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

            if (elementQuadrant.equals(spieler1Quadrant)) {
                Welt.spieler1.muenzen++;
                Welt.punktestandAktualisieren();
                breite = 0;
            }
            else if (elementQuadrant.equals(spieler2Quadrant)) {
                Welt.spieler2.muenzen++;
                Welt.punktestandAktualisieren();
                breite = 0;
            }
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet eine MuenzenBlock
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(250, 205,70, 255));

        // MuenzenBlock zeichnen
        g.fillOval(
                (int)(x - kamera.x + Fenster.GITTERGROESSE / 2 - breite / 4),              // x
                (int)(y - kamera.y),                                                       // y
                breite / 2,                                                         // Breite
                Fenster.GITTERGROESSE - 1                                           // Hoehe
        );

        if(breite > 0) {
            g.setColor(new Color(250, 250, 250, 255));
            g.drawOval(
                    (int)(x - kamera.x + Fenster.GITTERGROESSE / 2 - breite / 4),              // x
                    (int)(y - kamera.y),                                                       // y
                    breite / 2,                                                         // Breite
                    Fenster.GITTERGROESSE - 1                                           // Hoehe
            );
        }
    }
}