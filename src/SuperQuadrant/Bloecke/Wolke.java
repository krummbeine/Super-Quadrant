package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;

public class Wolke extends Element
{
    private float verschiebeX;
    private float verschiebeXRichtung;
    private Point startPosition;
    float geschwindigkeit = 0.1f;

    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public Wolke (Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.startPosition = startPosition;
        sollRechtsGehen = true;
        this.verschiebeX = 0;
        this.verschiebeXRichtung = geschwindigkeit;
        this.geschwindigkeit = geschwindigkeit;
    }

    /**
     * Da sich die Wolke bewegt, muss zum Speichern die StartPosition extra gesichert werden !!
     * Ansonsten wird die Wolke nach dem Laden an einer anderen Position erstellt
     * @return Gibt die StartPosition zurueck
     */
    @Override
    public Point getStartPosition(){
        return startPosition;
    }

    /**
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Animation der Wolke
        //
        if(verschiebeX < -Fenster.GITTERGROESSE * 2)
            verschiebeXRichtung = geschwindigkeit;    // nach rechts
        if(verschiebeX > Fenster.GITTERGROESSE * 2)
            verschiebeXRichtung = -geschwindigkeit;   // nach links

        verschiebeX = verschiebeX + verschiebeXRichtung;

        aufWolkeBleiben();
    }

    @Override
    public void zeichnen(Graphics g) {
        int xx = (int) (x - kamera.x);
        int yy = (int) (y - kamera.y);

        g.setColor(new Color(255, 250, 250, 255));

        int mitteKaestchenBreite = xx + Fenster.GITTERGROESSE / 2;
        int mitteKaestchenHoehe = yy + Fenster.GITTERGROESSE / 2;

        int wolkeBreite = Fenster.GITTERGROESSE * 2;
        int wolkeHoehe = (int) (Fenster.GITTERGROESSE * 1.5f);

        // Wolke zeichnen
        g.fillOval(
                mitteKaestchenBreite - wolkeBreite / 2 + (int) verschiebeX,           // x
                mitteKaestchenHoehe - wolkeHoehe / 2,                                // y
                wolkeBreite,             // Breite
                wolkeHoehe               //Höhe
        );


        //////////////////////////////////////////////////////////// Zum Praesentieren - Anschaulichkeit
        if (false) {
            // Mittelpunkt der Wolke zeichnen
            g.setColor(new Color(255, 2, 2, 255));
            g.fillOval(
                    mitteKaestchenBreite - 2 + (int) verschiebeX,           // x
                    mitteKaestchenHoehe - 2,                                // y
                    4,             // Breite
                    4               //Höhe
            );

            // Kästchen der Wolke
            g.setColor(new Color(50, 39, 255, 255));
            g.drawRect(
                    xx + (int) verschiebeX,           // x
                    yy,                                // y
                    Fenster.GITTERGROESSE,             // Breite
                    Fenster.GITTERGROESSE               //Höhe
            );

            // Kästchen-Position zeichnen
            g.setColor(new Color(255, 190, 72, 255));
            g.fillOval(
                    xx + (int) verschiebeX,           // x
                    yy,                                // y
                    4,             // Breite
                    4               //Höhe
            );
        }
    }

    /**
     * Sorgt dafuer, dass der Spieler auf der Wolke bleibt und sie sich
     * nicht unter ihm wegbewegt
     */
    private void aufWolkeBleiben() {
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)(x + verschiebeX), (int) y);
        // Bei Sprung auf den Quadranten ueber dem KopfMonster
        elementQuadrant.y -= Fenster.GITTERGROESSE;
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler1.x, (int) Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler2.x, (int) Welt.spieler2.y);

        if (elementQuadrant.equals(spieler1Quadrant)) {
            Welt.spieler1.x += verschiebeXRichtung;
            Welt.spieler1.istAufWolke = true;
            if(!Welt.spieler1.sollSpringen)
                Welt.spieler1.y = elementQuadrant.y;
        }
        if (elementQuadrant.equals(spieler2Quadrant)) {
            Welt.spieler2.x += verschiebeXRichtung;
            Welt.spieler2.istAufWolke = true;
            if(!Welt.spieler2.sollSpringen)
                Welt.spieler2.y = elementQuadrant.y;
        }
        // Zuruecksetzen in Welt.aktualisieren (istAufWolke)
    }
}
