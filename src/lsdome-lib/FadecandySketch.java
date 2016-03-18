// Master template for sketches that output to fadecandy. Takes care of common initialization and
// provides some helper functions.

// Typically the .pde file will simply become:
/*

FadecandySketch driver = new SubclassOfFadecandySketch(this, <window size>);

void setup() {
  driver.init();
}

void draw() {
  driver.draw();
}

*/

import java.util.*;
import processing.core.*;

// S is the type of the state that is maintained and updated each frame.
public class FadecandySketch<S> {

    static final int COLOR_STEPS = 100;

    // Reference to processing sketch object
    PApplet app;

    // width and height of processing canvas, in pixels
    int width, height;

    // Reference to fadecandy controller
    OPC opc;

    // Locations of pixels
    ArrayList<PVector> points;

    // Size of a single panel's pixel grid
    int panel_size;

    // Layout configuration of panels
    PanelLayout panel_config_mode;

    // Distance from center to farthest pixel, in panel lengths
    double radius;

    // Ongoing state to be updated each frame (such as positions, directions, etc.).
    S state;

    // Timestamp of the most recent completed frame.
    double last_t;

    FadecandySketch(PApplet app, int size_px) {
        this(app, size_px, size_px);
    }

    FadecandySketch(PApplet app, int width_px, int height_px) {
        this.app = app;
        this.width = width_px;
        this.height = height_px;
    }

    // Override this if you have more specific initialization to perform. Be sure to call
    // super.init()!
    void init() {
        app.size(width, height, app.P2D);

        String hostname = Config.FADECANDY_HOST;
        int port = 7890;
        opc = new OPC(app, hostname, port);

        panel_size = Config.PANEL_SIZE;
        panel_config_mode = Config.PANEL_LAYOUT;
        LayoutUtil.PanelConfig config = LayoutUtil.getPanelConfig(panel_config_mode);
        points = config.fill(panel_size);
        
        LayoutUtil.generateOPCSimLayout(points, app, "layout.json");
        
        radius = (Config.PARTIAL_LAYOUT ?
                    LayoutUtil.getPanelConfig(Config.FULL_PANEL_LAYOUT) :
                    config
                  ).radius;
        LayoutUtil.registerScreenSamples(opc, points, width, height, 2*radius, true);

        app.colorMode(app.HSB, COLOR_STEPS);

        state = initialState();
    }

    // **OVERRIDE** (optional)
    // Return the initial persistent state.
    S initialState() {
        return null;
    }

    // **OVERRIDE** (optional)
    // Update the persistent state from one frame to the next. t_delta is the time elapsed since the
    // previous frame.
    S updateState(S state, double t_delta) {
        return state;
    }

    // Convert a screen pixel position to world coordinates.
    PVector screenToXy(PVector p) {
        // These parameters must match registerScreenSamples() in init()
        return LayoutUtil.screenToXy(p, width, height, 2*radius, true);
    }

    // Write a pixel value to the screen buffer.
    // TODO for sketches that only render pixels directly, is there a cost penalty for going
    // through the screen buffer?
    void setLED(int i, int rgb) {
        int px = opc.pixelLocations[i];
        if (px >= 0) {
            app.pixels[px] = rgb;
        }
    }

    void draw() {
        double t = app.millis() / 1000.;

        _updateState(t);
        beforeFrame(t);
        draw(t);
        afterFrame(t);

        if (Config.DEBUG) {
            System.out.println(app.frameRate);
        }
    }

    void _updateState(double t) {
        if (last_t > 0) {
            state = updateState(state, t - last_t);
        }
        last_t = t;
    }

    // **OVERRIDE** (optional)
    // A hook to be called before the frame is rendered.
    void beforeFrame(double t) { }

    // **OVERRIDE** (optional)
    // A hook to be called after the frame is rendered.
    void afterFrame(double t) { }

    // **OVERRIDE** this with your sketch's drawing code. 't' is the global clock in seconds.
    void draw(double t) {
        System.out.println("nothing to draw");
    }
    
    // Helper function to generate a color. r/g/b all in the range [0, 1].
    int color(double r, double g, double b) {
        return app.color((float)(COLOR_STEPS * r), (float)(COLOR_STEPS * g), (float)(COLOR_STEPS * b));
    }

}