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
public class KopfmonsterBlock extends Element
{
    private int durchmesser;
    private boolean durchmesserVerkleinern;
    private Point startPosition;
    private boolean wach;
    private int warten;
    private int aufladung;
    Random random;
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public KopfmonsterBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.startPosition = startPosition;
        random = new Random();
        if(random.nextInt(2) == 0)
            sollRechtsGehen = true;
        else
            sollLinksGehen = true;
        this.durchmesser = Fenster.GITTERGROESSE * 4;
        this.durchmesserVerkleinern = true;
        this.wach = true;
        this.warten = 0;
        this.aufladung = 0;
    }

    /**
     * Da sich das KopfmonsterBlock bewegt, muss zum Speichern die StartPosition extra gesichert werden !!
     * Ansonsten wird das KopfmonsterBlock nach dem Laden an einer anderen Position erstellt
     * @return Gibt die StartPosition zurueck
     */
    @Override
    public Point getStartPosition(){
        return startPosition;
    }

    /**
     * Aktualisiert und animiert das KopfmonsterBlock
     * Laesst das Monster in Richtung des Spielers gehen, der am naechsten ist.
     * Trifft das Monster auf einen Spieler, zieht es ihm Leben ab.
     * Huepft der Spieler auf das Kopfmonster, wird es betaebt und zieht seine Stacheln ein.
     * Ueberschreibt die aktualisieren-Methode aus Element
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        super.fallen(delta);
        super.gehen(delta);
        gehrichtungAendern();
        stachelnBeruehren();
        betaeuben();
        aufwachen();
        animieren();
        springenLassen();
    }

    /**
     * Animiert das KopfMonster
     */
    private void animieren(){
        if(wach) {
            // Animation des KopfMonsters
            if (durchmesserVerkleinern) {
                durchmesser--;
                if (durchmesser < Fenster.GITTERGROESSE * 3)
                    durchmesserVerkleinern = !durchmesserVerkleinern;
            } else {
                durchmesser++;
                if (durchmesser > Fenster.GITTERGROESSE * 5) {
                    durchmesserVerkleinern = !durchmesserVerkleinern;
                }
            }
        }
        else{
            // Betaeubt .. Stacheln einfahren
            if (durchmesser > 0) {
                durchmesser--;
            }
        }
    }

    /**
     * Aendert die Gehrichtung des Kopfmonsters, wenn es auf ein Hindernis trifft
     */
    private void gehrichtungAendern(){
        if(wach) {
            if(!gehtGerade) {
                // Das Monster stieß auf ein Hindernis
                // Die Gehrichtung soll nun geändert werden
                sollLinksGehen = sollRechtsGehen;
                sollRechtsGehen = !sollLinksGehen;
            }
        }
        else{
            // Monster ist betaebt
            sollLinksGehen = false;
            sollRechtsGehen = false;
        }
    }

    /**
     * Betaeubt das Kopfmonster, wenn ein Spieler auf den Quadranten ueber ihm springt
     */
    private void betaeuben(){
        // Kopfmonster betaeuben
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        // Bei Sprung auf den Quadranten ueber dem KopfMonster
        elementQuadrant.y -= Fenster.GITTERGROESSE;
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if(elementQuadrant.equals(spieler1Quadrant) || elementQuadrant.equals(spieler2Quadrant)) {
            wach = false;
            // Zufallszahl [100,300]
            warten = random.nextInt(1001) + 100;
        }
    }

    /**
     * Laesst das Kopfmonster nach einer Betaeubung wieder aufwachen, wenn eine zufaellige Zeit verstrichen ist.
     */
    private void aufwachen(){
        if(warten > 0)
            warten--;
        else if(!wach){
            wach = true;
            // Zufallszahl [0,1]
            if(random.nextInt(2) == 0){
                sollLinksGehen = true;
            }
            else
                sollRechtsGehen = true;
        }
    }

    /**
     * Laesst den Spieler, der auf das KopfMonster springt, erneut springen.
     * Dies ist moeglich, wenn das KopfMonster betaebt ist (!wach).
     */
    private void springenLassen(){
        if(!wach){
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

            // Trampolin ist aufgeladen .. bei Beruehrung springt der Spieler
            if (elementQuadrant.equals(spieler1Quadrant)) {
                Welt.spieler1.sprungkraft = 1.5f;
                Welt.spieler1.sollSpringen = true;
                aufladung = -2;
            }
            if (elementQuadrant.equals(spieler2Quadrant)) {
                Welt.spieler2.sprungkraft = 1.5f;
                Welt.spieler2.sollSpringen = true;
                aufladung = -3;
            }
        }
        else
        {
            // Setzt sollSpringen zurueck!
            if (aufladung < 0) {
                if (aufladung == -2)
                    Welt.spieler1.sollSpringen = false;
                if (aufladung == -3)
                    Welt.spieler2.sollSpringen = false;
                aufladung = 0;
            }
            else{
                aufladung++;
            }
        }
    }

    /**
     * Zieht Spieler 1 und Spieler 2 Leben ab, wenn sie den todesQuadranten beruehren
     * @param todesQuadrant Der Quadrant, der bei Beruehrung mit dem Spieler ihn toetet
     */
    private void spielernLebenAbziehen(Point todesQuadrant) {

        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if (todesQuadrant.equals(spieler1Quadrant))
            Spieler.spielerLebenAbziehen(1);
        if (todesQuadrant.equals(spieler2Quadrant))
            Spieler.spielerLebenAbziehen(2);
    }

    /**
     * Toetet den Spieler, wenn er die Stacheln des Kopfmonsters beruehrt
     */
    private void stachelnBeruehren(){
        if(wach){
            Point todesQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
            // Im KopfMonster
            spielernLebenAbziehen(todesQuadrant);
            // Linker Stachel
            todesQuadrant.x -= Fenster.GITTERGROESSE;
            spielernLebenAbziehen(todesQuadrant);
            // Rechter Stachel
            todesQuadrant.x += Fenster.GITTERGROESSE * 2;
            spielernLebenAbziehen(todesQuadrant);
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet ein KopfmonsterBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(25, 50, 120, 255));

        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        // Stacheln zeichnen
        g.fillRect(
                xx ,           // x
                yy,           // y
                Fenster.GITTERGROESSE,                                                      // Breite
                Fenster.GITTERGROESSE                                                       // Hoehe
        );
        int[] xPunkte = {
                xx + Fenster.GITTERGROESSE / 2,
                xx + Fenster.GITTERGROESSE / 2 + durchmesser / 3,
                xx + Fenster.GITTERGROESSE / 2,
                xx + Fenster.GITTERGROESSE / 2 - durchmesser / 3
        };
        int[] yPunkte = {
                yy +  durchmesser / 10,
                yy + Fenster.GITTERGROESSE / 3 - durchmesser / 8,
                yy + Fenster.GITTERGROESSE / 3 + durchmesser / 6,
                yy + Fenster.GITTERGROESSE / 3 - durchmesser / 8
        };
        g.fillPolygon(xPunkte, yPunkte, 4);
        int[] xPunkte2 = {
                xx + Fenster.GITTERGROESSE / 2,
                xx + Fenster.GITTERGROESSE / 2 + durchmesser / 4,
                xx + Fenster.GITTERGROESSE / 2,
                xx + Fenster.GITTERGROESSE / 2 - durchmesser / 4
        };
        int[] yPunkte2 = {
                yy +  durchmesser / 10,
                yy + Fenster.GITTERGROESSE / 3 + durchmesser / 8,
                yy + Fenster.GITTERGROESSE / 3 + durchmesser / 6,
                yy + Fenster.GITTERGROESSE / 3 + durchmesser / 8
        };
        g.fillPolygon(xPunkte2, yPunkte2, 4);
    }
}