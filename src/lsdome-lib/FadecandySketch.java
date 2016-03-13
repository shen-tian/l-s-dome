import java.util.*;
import processing.core.*;

enum PanelLayout {
    _13,
    _24
}

public class FadecandySketch {

    static final int COLOR_STEPS = 100;

    PApplet app;
    int width, height;
    OPC opc;
    ArrayList<PVector> points;
    int panel_size;
    PanelLayout panel_config_mode;
    double radius;

    FadecandySketch(PApplet app, int size_px) {
        this(app, size_px, size_px);
    }

    FadecandySketch(PApplet app, int width_px, int height_px) {
        this.app = app;
        this.width = width_px;
        this.height = height_px;
    }

    void init() {
        app.size(width, height, app.P2D);

        String hostname = Config.FADECANDY_HOST;
        int port = 7890;
        opc = new OPC(app, hostname, port);

        panel_size = Config.PANEL_SIZE;
        panel_config_mode = Config.PANEL_LAYOUT;
        switch (panel_config_mode) {
        case _13:
            points = LayoutUtil.fillLSDome13(panel_size);
            break;
        case _24:
            points = LayoutUtil.fillLSDome24(panel_size);
            break;
        default:
            throw new RuntimeException();
        }
        radius = LayoutUtil.lsDomeRadius(panel_config_mode);
        LayoutUtil.registerScreenSamples(opc, points, width, height, 2*radius, true);

        app.colorMode(app.HSB, COLOR_STEPS);
    }
    
    // TODO for sketches that only render pixels directly, is there a cost penalty for going
    // through the screen buffer?
    void setLED(int i, int rgb) {
        int px = opc.pixelLocations[i];
        if (px >= 0) {
            app.pixels[px] = rgb;
        }
    }

    void draw() {
        draw(app.millis() / 1000.);
        if (Config.DEBUG) {
            System.out.println(app.frameRate);
        }
    }

    void draw(double t) {
        System.out.println("nothing to draw");
    }
    
    int color(double r, double g, double b) {
        return app.color((float)(COLOR_STEPS * r), (float)(COLOR_STEPS * g), (float)(COLOR_STEPS * b));
    }

}