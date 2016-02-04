# Notes on deploying this stuff

## Setting up `fcserver`

Mainly followed this https://learn.adafruit.com/1500-neopixel-led-curtain-with-raspberry-pi-fadecandy/fadecandy-server-setup .

Config file is `/usr/local/bin/fcserver.json`.

Note that brightness limit is set using the whitepoint setting. Can this be updated on the fly?

## Steps taken to run sketches on Raspbian

You have to be in X to run processing sketches. So to autoboot into it, there things were done, in no particular order.

### Exporting the sketch

Export the sketch, targeting Linux (don't need to worry about arch)

### Setup the Pi so that the sketch runs 

We are working with Processing 2.x (most likely 2.2), apparently, Java 7 works best. Install it by

```
sudo apt-get install oracle-java7-jdk
```
and ensure it's the default Java by running:

```
sudo update-alternatives --config java
```

Now try to run the sketch (once you are in X) by typing `sh /path/yoursketch`. If you get something about can't use pixels on this device, it might be this issue: https://github.com/processing/processing/issues/2010.html

The fix is to force 32 bit mode. Adding this to the bottom of `\boot\config.txt` seems to work:

```
framebuffer_depth=32
framebuffer_ignore_alpha=1
```

### Automatically launch into X, then the sketch

`sudo raspi-config` launches a config utility. One of the options there allows you to launch X automatically on boot.

Add a .desktop file to the startup folder (`/etc/xdg/autostart`)

format is something like this:

```
[Desktop Entry]
Name=Cloud Autostart
Exec=sh /path/yoursketch
Type=Application
Terminal=true
```

This also seem to run without screensaver/power saving issues. Not sure if its anything in the config, or just what a Pi with Raspbian does.

#### Possible alternative:

Tried adding `@/bin/sh /path/yoursketch` to end of `/etc/xdg/lxsession/LXDE/autostart` but it didn't work.

Maybe it should be added to `LXDE-pi/autostart`? 

