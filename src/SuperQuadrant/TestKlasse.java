package SuperQuadrant;

import SuperQuadrant.Bloecke.GoldregenBlock;
import SuperQuadrant.Bloecke.ZufallsBlock;
import org.junit.*;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestKlasse {
    private static Welt welt;

    @BeforeClass
    public static void setUpBeforeClass() {
        welt = new Welt("testWelt");
    }

    @Before
    public void vorJedemTest() throws IOException {
        //
    }

    @After
    public void nachJedemTest() {
        // Achtung, testWelt muss geloescht werden
        Element.kopf = null;
        welt.speichern("testWelt");
    }

    @Test
    public void testMuenzenGesamt() {
        // 2 gelbe Muenzen (Typ5 - 1 Muenze wert) hinzufuegen und pruefen, ob die GesamtAnzahl der Muenzen uebereinstimmt
        welt.elementHinzufuegen(0, 0, 5);
        welt.elementHinzufuegen(0, 0, 5);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 2);

        // 2 rote Muenzen (Typ12 - 2 Muenzen wert) hinzufuegen und pruefen, ob die GesamtAnzahl der Muenzen uebereinstimmt
        welt.elementHinzufuegen(0, 0, 12);
        welt.elementHinzufuegen(0, 0, 12);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 6);

        // 2 Zufallsbloecke (Typ 7) hinzufuegen, die noch nicht ausgeloest sind
        // Ist ein Zufallsblock nicht ausgeloest, zaehlt er 1 Muenze (die noch nicht eingesammelt wurde)
        ZufallsBlock z1 = new ZufallsBlock(
                new Point(Fenster.GITTERGROESSE * 50, Fenster.GITTERGROESSE * 50), 7, 0f, welt.kamera, false, true);
        ZufallsBlock z2 = new ZufallsBlock(
                new Point(Fenster.GITTERGROESSE * 51, Fenster.GITTERGROESSE * 50), 7, 0f, welt.kamera, false, true);

        // Indirekt wird auch geprueft, ob die Methode hinzufuegen ein Element korrekt in die Liste einfuegt :)
        Element.hinzufuegen(z1);
        Element.hinzufuegen(z2);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 8);

        // Jetzt ausgeloest bei ZufallsBlock2 aktivieren --> zaehlt nicht mehr in die Bewertung
        z2.ausgeloest = true;
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 7);

        // Wieder zuruecksetzen
        z2.ausgeloest = false;
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 8);

        // Spieler1 wird NICHT unter sondern neben den ZufallsBlock z2 gesetzt --> sollte NICHT ausgeloest werden
        // Daher zaehlt der Block mit --> GesamtAnzahl bleibt 8
        welt.spieler1.x = welt.spieler2.x = z2.x + Fenster.GITTERGROESSE * 5;
        welt.spieler1.y = welt.spieler2.y = z2.y + Fenster.GITTERGROESSE;
        z2.aktualisieren(1);
        assertTrue("Wurde ausgeloest (erwartet nicht)", z2.ausgeloest == false);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 8);

        // Spieler1 unter den ZufallsBlock z2 setzen --> sollte ausgeloest werden
        // Durch das richtige Ausloesen, wird eine Muenze vom ZufallsBlock erstellt,
        // die nun in die Bewertung einfliesst
        // Der ZufallsBlock selbst wird nicht mehr mitgezaehlt --> AnzahlGesamt == 7
        // Ist die GesamtAnzahl 8, wurde die Muenze von ihm erstellt --> AnzahlGesamt wieder == 8
        welt.spieler1.x = welt.spieler2.x = z2.x;
        welt.spieler1.y = welt.spieler2.y = z2.y + Fenster.GITTERGROESSE;
        z2.aktualisieren(1);
        assertTrue("Wurde nicht ausgeloest (aber erwartet)", z2.ausgeloest == true);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 8);

        // 2 GoldregenBloecke (Typ 11) hinzufuegen, die noch nicht ausgeloest sind
        // Ist ein GoldregenBlock nicht ausgeloest, zaehlt er 5*2 Muenzen (die noch nicht eingesammelt wurden)
        GoldregenBlock gr1 = new GoldregenBlock(
                new Point(Fenster.GITTERGROESSE * 150, Fenster.GITTERGROESSE * 50), 11, 0f, welt.kamera, false, true);
        GoldregenBlock gr2 = new GoldregenBlock(
                new Point(Fenster.GITTERGROESSE * 151, Fenster.GITTERGROESSE * 50), 11, 0f, welt.kamera, false, true);
        Element.hinzufuegen(gr1);
        Element.hinzufuegen(gr2);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 28);

        // Jetzt ausgeloest bei GoldRegenBlock aktivieren --> zaehlt nicht mehr in die Bewertung
        gr2.ausgeloest = true;
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 18);

        // Wieder zuruecksetzen
        gr2.ausgeloest = false;
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 28);

        // Spieler1 wird NICHT unter sondern neben den GoldregenBlock gr2 gesetzt --> sollte NICHT ausgeloest werden
        // Daher zaehlt der Block mit --> GesamtAnzahl bleibt 28
        welt.spieler1.x = welt.spieler2.x = gr2.x + Fenster.GITTERGROESSE * 5;
        welt.spieler1.y = welt.spieler2.y = gr2.y + Fenster.GITTERGROESSE;
        gr2.aktualisieren(1);
        assertTrue("Wurde ausgeloest (erwartet nicht)", gr2.ausgeloest == false);
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 28);

        // Spieler1 unter den GoldregenBlock gr2 setzen --> sollte ausgeloest werden
        // Durch das richtige Ausloesen, werden 5 Rote Muenzen vom GoldregenBlock erstellt,
        // die nun in die Bewertung einfliessen
        // Der GoldregenBlock selbst wird nicht mehr mitgezaehlt --> AnzahlGesamt == 18
        // Ist die GesamtAnzahl 28, wurden die Muenzen von ihm erstellt --> AnzahlGesamt wieder == 28
        // Aber die Muenzen werden erst nach gewisser Zeit erstellt .. mehrmals aktualisieren() aufrufen!
        welt.spieler1.x = welt.spieler2.x = gr2.x;
        welt.spieler1.y = welt.spieler2.y = gr2.y + Fenster.GITTERGROESSE;
        gr2.aktualisieren(1);
        assertTrue("Wurde nicht ausgeloest (aber erwartet)", gr2.ausgeloest == true);
        // Wurde ausgeloest --> Damit wurde Element.existiert geprueft
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 18);
        for(int i = 0; i < 5000; i++)
            gr2.aktualisieren(1);
        // Nun muessten alle Muenzen ausgeschuettet worden sein
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 28);

        // z1 entfernen --> GesamtAnzahl sollte auf 27 sinken
        // Damit wird indirekt auch das entfernen eines Elements aus der Liste geprueft
        Element.entfernen(new Point((int)z1.x, (int)z2.y));
        assertTrue("Anzahl Muenzen", Element.anzMuenzenBloeckeGesamt() == 27);
    }
}