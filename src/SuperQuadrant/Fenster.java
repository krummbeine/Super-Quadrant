package SuperQuadrant;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

/**
 * Das angezeigte Fenster, das die main-Methode enthaelt und das Spiel startet.
 * Verwaltet, wann das Spiel aktualisiert werden soll bezueglich der Systemzeit und nicht dem CPU-Takt.
 * Durch das Erstellen eines Fenster wird durch implements Runnable die Funktion run() aufgerufen.
 * @author Helbig Christian www.krummbeine.de
 * @version 1.7
 */
public class Fenster extends Applet implements Runnable{
    public static int BREITE = 1220;
    public static int HOEHE = 800;
    public static float SKALIERUNG = 1f;
    public static float SKALIERUNG_KORREKTUR = 1 / SKALIERUNG;
    public static int GITTERGROESSE = (int)(SKALIERUNG * 40);
    public String FENSTER_TITEL = "Super Quadrant by Helbig Christian www.krummbeine.de";
    public long gewuenschte_FPS = 200;
    public long desiredDeltaLoop = (1000 * 1000 * 1000) / gewuenschte_FPS;
    public Welt welt;
    private boolean running = true;
    private BufferStrategy bufferStrategy;
    private JFrame frame;
    private Canvas canvas;

    /**
     * Konstruktor.
     * Wird von main-Methode in der Klasse Fenster aufgerufen.
     * Initialisiert eine neue Instanz welt, maus und tastatur.
     */
    public Fenster() {
        // Erstelle ein neues Fenster, eine Welt, Maus und Tastatur
        frame = new JFrame(FENSTER_TITEL);
        welt = new Welt("level0");
        Maus maus = new Maus(welt);
        Tastatur tastatur = new Tastatur(welt);

        // Erstelle ein neues Canvas, in das gezeichnet werden soll
        canvas = new Canvas();
        canvas.setBounds(0, 0, BREITE, HOEHE);
        canvas.setIgnoreRepaint(true);

        // Erstelle ein neues Panel, dem das Canvas zugewiesen wird
        JPanel panel = (JPanel) frame.getContentPane();
        panel.setPreferredSize(new Dimension(BREITE, HOEHE));
        panel.setLayout(null);
        panel.add(canvas);

        // Fenster-Attribute festlegen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.getContentPane();
        frame.setLocationRelativeTo(null);

        // Canvas-Attribute festlegen und Listener zuweisen, um Maus- und Tastatur-Ereignisse zu registrieren
        canvas.createBufferStrategy(2);
        canvas.addMouseListener(maus);
        canvas.addMouseMotionListener(maus);
        canvas.addKeyListener(tastatur);
        canvas.requestFocus();
        bufferStrategy = canvas.getBufferStrategy();

        // Das Fenster anzeigen
        frame.setVisible(true);
    }

    /**
     * Aktualisiert Gittergroesse und die Skalierungs-Korrektur, wenn die Skalierung veraendert wurde
     */
    public static void aktualisiereSkalierung(){
        SKALIERUNG_KORREKTUR = 1 / SKALIERUNG;
        GITTERGROESSE = (int)(SKALIERUNG * 40);
    }

    /**
     * Startet den Timer des Spiels zur Aktualisierung von Berechnungen und GUI.
     * Wird durch das erstellen einer Instanz Fenster aufgerufen durch implements Runnable.
     * Beinhaltet eine while-Schleife, die Berechnungen und GUI abhängig von der gewünschten FPS-Rate aktualisiert.
     * Ist noch keine Aktualisierung erforderlich, wird mit Thread.sleep(..) gewartet.
     */
    public void run(){
        long beginLoopTime;
        long endLoopTime;
        long currentUpdateTime = System.nanoTime();
        long lastUpdateTime;
        long deltaLoop;

        while(running){
            // Spiel wird ausgefuehrt
            beginLoopTime = System.nanoTime();

            render();

            lastUpdateTime = currentUpdateTime;
            currentUpdateTime = System.nanoTime();
            // Aktualisieren und delta uebergeben
            // delta bestimmt, wie stark Werte geaendert werden sollen im Verhaeltnis zur verstrichenen Zeit
            // delta macht unabhaengig von der unzuverlaessigen Taktzeit / Auslastung der CPU
            update((int) ((currentUpdateTime - lastUpdateTime) / (1000 * 1000)));

            endLoopTime = System.nanoTime();
            deltaLoop = endLoopTime - beginLoopTime;

            if(deltaLoop > desiredDeltaLoop){
                // Aktualisierung ueberfaellig
            }else{
                try{
                    // Noch keine Aktualisierung notwendig, warten
                    Thread.sleep((desiredDeltaLoop - deltaLoop) / (1000 * 1000));
                }catch(InterruptedException e){
                    // notwendiger, aber nicht definierter Block
                }
            }
        }
    }

    /**
     * Zeichnet erstmalig das Fenster und initialisiert damit die GUI.
     */
    private void render() {
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, BREITE, HOEHE);
        render(g);
        g.dispose();
        bufferStrategy.show();
    }

    /**
     * Aktualisiert das Fenster
     * Wird von der Schleife in run() aufgerufen.
     * @param deltaTime  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    protected void update(int deltaTime){
        welt.aktualisieren((int)(deltaTime * Fenster.SKALIERUNG));
    }

    /**
     * Zeichnet das Fenster.
     * Wird von der Schleife in run() aufgerufen.
     * @param g Die grafische Oberflaeche, auf der gezeichnet werden soll
     */
    protected void render(Graphics2D g){
        welt.zeichnen(g);
    }

    /**
     * Main-Methode des Spiels.
     * Ruft den Konstruktor des Fensters auf.
     * @param args Die Argumente, die dem Programm beim Start uebergeben werden
     */
    public static void main(String [] args){
        Fenster ex = new Fenster();
        new Thread(ex).start();
    }

    /**
     * Rechnet eine eindimensionale Position auf die eindimensionale Position im Gitter um.
     * @param position entweder die vertikale oder horizontale Position, von der der Quadrant ermittelt werden soll
     * @return Gibt den Quadranten zurueck (eindimensionale Position)
     */
    public static int posToGitterPos(int position){
        return (position) - (position) % GITTERGROESSE;
    }

    /**
     * Rechnet die Position auf die Position des Quadranten um, in der sie sich befindet und
     * gibt die Mitte des Elements im Quadranten zurück.
     * @param x Die X-Position innerhalb eines Quadranten
     * @param y Die Y-Position innerhalb eines Quadranten
     * @return Die Position in der Mitte eines Quadranten, in dem sich (x, y) befindet.
     */
    public static Point posToPosMitteQuadrant(int x, int y){
        return new Point(
                posToGitterPos(x + GITTERGROESSE / 2),
                posToGitterPos(y + GITTERGROESSE / 2)
        );
    }
}