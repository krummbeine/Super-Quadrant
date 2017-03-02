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
 * @version 1.3
 */
public class TrampolinBlock extends Element {
    private int durchmesser;
    private boolean durchmesserVerkleinern;
    private boolean ausgeloest;
    private int aufladung;

    /**
     * Konstruktor
     *
     * @param kamera                Die Kamera
     * @param mitKameraVerfolgen    Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition         Die Startposition des Spielers
     * @param typ                   Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit       Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public TrampolinBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.durchmesser = Fenster.GITTERGROESSE * 2;
        this.durchmesserVerkleinern = false;
        this.ausgeloest = false;
        this.aufladung = Fenster.GITTERGROESSE * 8;
    }

    /**
     * Aktualisiert und animiert den TrampolinBlock
     * Bei Beruehrung springt der Spieler mit erhoehter SprungKraft und das Trampolin laedt neu auf.
     * Nach der Aufladung, kann der Spieler wieder springen.
     *
     * @param delta Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta) {
        animieren();
        springenLassen();
    }

    /**
     * Animiert das Trampolin
     */
    private void animieren(){
        // Animation des TrampolinBlock
        if (durchmesserVerkleinern) {
            durchmesser--;
            if (durchmesser < Fenster.GITTERGROESSE * 2)
                durchmesserVerkleinern = !durchmesserVerkleinern;
        } else {
            durchmesser++;
            if (durchmesser > Fenster.GITTERGROESSE * 4) {
                durchmesserVerkleinern = !durchmesserVerkleinern;
            }
        }
    }

    /**
     * Laesst den Spieler, der das Trampolin beruehrt, springen.
     * Das Trampolin laedt sich danach wieder auf, um erneut benutzt werden zu koennen.
     */
    private void springenLassen(){
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if (!ausgeloest) {
            // Trampolin ist aufgeladen .. bei Beruehrung springt der Spieler
            if (elementQuadrant.equals(spieler1Quadrant)) {
                ausgeloest = true;
                Welt.spieler1.sprungkraft = 1.3f;
                Welt.spieler1.sollSpringen = true;
                aufladung = -2;
            }
            if (elementQuadrant.equals(spieler2Quadrant)) {
                ausgeloest = true;
                Welt.spieler2.sprungkraft = 1.3f;
                Welt.spieler2.sollSpringen = true;
                aufladung = -3;
            }
        } else {
            // Setzt sollSpringen zurueck!
            if (aufladung < 0) {
                if (aufladung == -2)
                    Welt.spieler1.sollSpringen = false;
                if (aufladung == -3)
                    Welt.spieler2.sollSpringen = false;
            }
            // Laedt das Trampolin wieder auf
            if (aufladung < Fenster.GITTERGROESSE * 8) {
                aufladung++;
            } else
                ausgeloest = false;
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den TrampolinBlock und auch die Animation, die die Aufladung des TrampolinBlocks zeigt.
     *
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(255, 0, 120, 100));
        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        // TrampolinBlock zeichnen
        g.fillRect(
                xx,                   // x
                yy,                   // y
                Fenster.GITTERGROESSE - 1,      // Breite
                Fenster.GITTERGROESSE - 1       // Hoehe
        );

        // Animation zeichnen
        g.setColor(new Color(255, 0, 120, 255));
        if (durchmesser > 0) {
            g.fillRect(
                    xx + Fenster.GITTERGROESSE / 2 - durchmesser / 8 - 1,           // x
                    yy + Fenster.GITTERGROESSE - aufladung / 5 - 1,           // y
                    durchmesser / 4,                                                  // Breite
                    aufladung / 5                                                   // Hoehe
            );
        }
    }
}