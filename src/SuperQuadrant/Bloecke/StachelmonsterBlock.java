package SuperQuadrant.Bloecke;
import SuperQuadrant.*;

import java.awt.*;
import java.awt.Graphics;

/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class StachelmonsterBlock extends Element
{
    private int durchmesser;
    private boolean durchmesserVerkleinern;
    private Point startPosition;

    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public StachelmonsterBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
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
        this.durchmesser = Fenster.GITTERGROESSE * 4;
        this.durchmesserVerkleinern = true;
    }

    /**
     * Da sich das StachelmonsterBlock bewegt, muss zum Speichern die StartPosition extra gesichert werden !!
     * Ansonsten wird das StachelmonsterBlock nach dem Laden an einer anderen Position erstellt
     * @return Gibt die StartPosition zurueck
     */
    @Override
    public Point getStartPosition(){
        return startPosition;
    }

    /**
     * Aktualisiert und animiert das StachelmonsterBlock
     * Laesst das Monster gehen und wenn es auf ein Hindernis trifft die Gehrichtung aendern.
     * Trifft das Monster auf einen Spieler, zieht es ihm Leben ab.
     * Ueberschreibt die aktualisieren-Methode aus Element
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        super.fallen(delta);
        super.gehen(delta);
        super.springen(delta);
        inRichtungDesSpielersGehen();
        stachelnBeruehren();
        animieren();
    }

    /**
     * Animiert das StachelMonster
     */
    void animieren(){
        if(durchmesserVerkleinern) {
            durchmesser--;
            if(durchmesser < Fenster.GITTERGROESSE * 3)
                durchmesserVerkleinern = !durchmesserVerkleinern;
        } else{
            durchmesser++;
            if(durchmesser > Fenster.GITTERGROESSE * 5){
                durchmesserVerkleinern = !durchmesserVerkleinern;
            }
        }
    }

    /**
     * Sorgt dafuer, dass das StachelMonster in die Richtung des Spielers laeft, der am naechsten ist und
     * wartet ggf. ungeduldig unter ihm.
     */
    void inRichtungDesSpielersGehen(){
        // Fuehrt zu einem "Ungeduldig-Warten-Effekt", da die Richtung nicht aktualisiert wird, wenn der betreffende
        // Spieler sich im Toleranzbereich befindet.. also z.B. direkt ueber dem Monster ist. Verlaesst das Monster
        // den Toleranzbereich, wechselt es seine Richtung und so geht es links rechts links unter dem Spieler.
        int toleranz = Fenster.GITTERGROESSE;
        // Springen zuruecksetzen
        sollSpringen = false;

        if ((abstandZwischenZweiPunkten((int)x, (int)y, (int)Welt.spieler1.x, (int)Welt.spieler1.y) <
                abstandZwischenZweiPunkten((int)x, (int)y, (int)Welt.spieler2.x, (int)Welt.spieler2.y))
                && Welt.spieler1.leben != 0 || Welt.spieler2.leben == 0
                ) {
            if(Welt.spieler1.unsichtbarFuerGegner < 10) {
                // Spieler 1 ist naeher dran und er lebt auch
                // Folge Spieler 1
                if (Welt.spieler1.x + toleranz < x)
                    sollLinksGehen = true;
                else if (Welt.spieler1.x - toleranz > x)
                    sollLinksGehen = false;
                sollRechtsGehen = !sollLinksGehen;

                if (Welt.spieler1.y + toleranz < y) {
                    // Spieler 1 über ihm --> Springen
                    sollSpringen = true;
                }
                if (!gehtGerade) {
                    // Stoesst auf ein Hindernis --> Versucht durch Springen zu Umgehen
                    sollSpringen = true;
                }
            }
        } else {
            if (Welt.spieler2.unsichtbarFuerGegner < 10) {
                // Folge Spieler 2
                if (Welt.spieler2.x + toleranz < x)
                    sollLinksGehen = true;
                else if (Welt.spieler2.x - toleranz > x)
                    sollLinksGehen = false;
                sollRechtsGehen = !sollLinksGehen;

                if (Welt.spieler2.y + toleranz < y) {
                    // Spieler 2 über ihm --> Springen
                    sollSpringen = true;
                }
                if (!gehtGerade) {
                    // Stoesst auf ein Hindernis --> Versucht durch Springen zu Umgehen
                    sollSpringen = true;
                }
            }
        }
    }

    /**
     * Brechnet den Abstand zwischen Punkt1(x1,y1) und Punkt2(x2,y2)
     * @param x1 x-Position 1
     * @param y1 y-Position 1
     * @param x2 x-Position 2
     * @param y2 y-Position 2
     * @return Gibt den Abstand zwischen den Punkten zurueck
     */
    private int abstandZwischenZweiPunkten(int x1, int y1, int x2, int y2){
        return (int)Math.sqrt(
                Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)
        );
    }

    /**
     * Zieht Spieler 1 oder Spieler 2 Leben ab, wenn diese die Stacheln beruehren
     */
    private void stachelnBeruehren(){
        Point todesQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        // Im KopfMonster
        spielernLebenAbziehen(todesQuadrant);
        // Linker Stachel
        todesQuadrant.x -= Fenster.GITTERGROESSE;
        spielernLebenAbziehen(todesQuadrant);
        // Rechter Stachel
        todesQuadrant.x += Fenster.GITTERGROESSE * 2;
        spielernLebenAbziehen(todesQuadrant);
        // Oberer Stachel
        todesQuadrant.x -= Fenster.GITTERGROESSE;
        todesQuadrant.y -= Fenster.GITTERGROESSE;
        spielernLebenAbziehen(todesQuadrant);
    }

    /**
     * Zieht Spieler 1 und Spieler 2 Leben ab, wenn sie den TodesQuadranten beruehren
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
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet ein StachelmonsterBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(255, 0, 0, 255));

        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        // Stacheln zeichnen
        g.fillRect(
                xx + Fenster.GITTERGROESSE / 2 - durchmesser / 8,           // x
                yy + Fenster.GITTERGROESSE / 2 - durchmesser / 8,           // y
                durchmesser / 4,                                                      // Breite
                durchmesser / 4                                                       // Hoehe
        );
        int[] xPunkte = {
                xx + Fenster.GITTERGROESSE / 2,
                xx + Fenster.GITTERGROESSE / 2 + durchmesser / 6,
                xx + Fenster.GITTERGROESSE / 2,
                xx + Fenster.GITTERGROESSE / 2- durchmesser / 6
        };
        int[] yPunkte = {
                yy + Fenster.GITTERGROESSE / 2 - durchmesser / 6,
                yy + Fenster.GITTERGROESSE / 2,
                yy + Fenster.GITTERGROESSE / 2 + durchmesser / 6,
                yy + Fenster.GITTERGROESSE / 2
        };
        g.fillPolygon(xPunkte, yPunkte, 4);
    }
}