package DMV;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

//This class handles all of the graphics for the DMV.
public class GraphicsProcessor {

    //THESE VALUES MADE TO BE EDITED

    private final int[] initialCharacterDraw = {50, 240};
    private final int[] entryLineStart = {85, 275};
    private final int[] signInTime = {140, 278};

    private final int[] entryToRegLineBreak = {85, 160};
    private final int[] entryToLicLineBreak = {85, 380};

    private final int[] regLinePoint = {320, 235};
    private final int[] regTime = {305, 175};
    private final int[] licLinePoint = {325, 345};
    private final int[] licTime = {308, 442};

    private final int[] regCharacterDraw = {285, 210};
    private final int[] licCharacterDraw = {290, 300};

    private final int[] cashLinePoint = {510, 190};
    private final int[] cashTime = {561, 205};
    private final int[] cashCharacterDraw = {470, 160};

    private final int[] checkLinePoint = {580, 350};
    private final int[] checkTime = {571, 443};
    private final int[] checkCharacterDraw = {550, 300};

    private final int[] exitLinePoint = {525, 50};
    private final int[] exitCharacterDraw = {470, 40};

    //END EDITABLE VALUES

    //the "character" image, this is the image that moves from station to station
    private final Image character = new Image("DMV/character.png");

    //positions of the previous character/line, used to erase old character image and fill back in the line
    private int[] oldCharacter;
    private int[] lastMoveStart, lastMoveEnd;

    //name of the person
    private String name;

    //shows whether the person took the reg or the lic track, only used when proceeding to exit
    private boolean reg;

    //used to easily differentiate the font for the timers at each station and the font of the name. def = default
    private Font def, timeFont;

    //reads in all the necessary variables and forms the base graphic
    public GraphicsProcessor(GraphicsContext g, String name) {
        this.name = name;
        def = g.getFont();
        timeFont = new Font(def.getName(), 30);
        g.setStroke(Color.RED);

        //layout is the image with all the stations and faces (except the character)
        g.drawImage(new Image("DMV/layout.png"), 0, 0);

        //if it's the blank slate at the very start, dont show the character
        if (name.equals(""))
            return;

        g.drawImage(character, initialCharacterDraw[0], initialCharacterDraw[1]);
        g.fillText(name, initialCharacterDraw[0], initialCharacterDraw[1] - 5);

        oldCharacter = initialCharacterDraw;
    }

    //this method and the following methods move the character from one station to the next, based on the instance variables at the start.
    //It would probably be more efficient to have a single method with a switch statement for different variations, however this works
    //(and that didn't occur to me until I wrote all these, so totally not worth)
    public void fromEntryToRegistration(GraphicsContext g) {
        reg = true;
        clearOldCharacter(g);

        g.strokeLine(entryLineStart[0], entryLineStart[1], entryToRegLineBreak[0], entryToRegLineBreak[1]);
        g.strokeLine(entryToRegLineBreak[0], entryToRegLineBreak[1], regLinePoint[0], regLinePoint[1]);

        lastMoveStart = entryToRegLineBreak;
        lastMoveEnd = regLinePoint;

        g.drawImage(character, regCharacterDraw[0], regCharacterDraw[1]);
        g.fillText(name, regCharacterDraw[0], regCharacterDraw[1] - 5);
        oldCharacter = regCharacterDraw;
    }

    public void fromEntryToLicense(GraphicsContext g) {
        reg = false;
        clearOldCharacter(g);

        g.strokeLine(entryLineStart[0], entryLineStart[1], entryToLicLineBreak[0], entryToLicLineBreak[1]);
        g.strokeLine(entryToLicLineBreak[0], entryToLicLineBreak[1], licLinePoint[0], licLinePoint[1]);

        lastMoveStart = entryToLicLineBreak;
        lastMoveEnd = licLinePoint;

        g.drawImage(character, licCharacterDraw[0], licCharacterDraw[1]);
        g.fillText(name, licCharacterDraw[0], licCharacterDraw[1] - 5);
        oldCharacter = licCharacterDraw;
    }

    public void fromRegistrationToCashier(GraphicsContext g) {
        clearOldCharacter(g);
        refreshLastLine(g);

        g.strokeLine(regLinePoint[0], regLinePoint[1], cashLinePoint[0], cashLinePoint[1]);

        lastMoveStart = regLinePoint;
        lastMoveEnd = cashLinePoint;

        g.drawImage(character, cashCharacterDraw[0], cashCharacterDraw[1]);
        g.fillText(name, cashCharacterDraw[0], cashCharacterDraw[1] - 5);
        oldCharacter = cashCharacterDraw;
    }

    public void fromLicenseToCashier(GraphicsContext g) {
        clearOldCharacter(g);
        refreshLastLine(g);

        g.strokeLine(licLinePoint[0], licLinePoint[1], cashLinePoint[0], cashLinePoint[1]);

        lastMoveStart = licLinePoint;
        lastMoveEnd = cashLinePoint;

        g.drawImage(character, cashCharacterDraw[0], cashCharacterDraw[1]);
        g.fillText(name, cashCharacterDraw[0], cashCharacterDraw[1] - 5);
        oldCharacter = cashCharacterDraw;
    }

    public void fromCashierToCheckApprover(GraphicsContext g) {
        clearOldCharacter(g);
        refreshLastLine(g);

        g.strokeLine(cashLinePoint[0], cashLinePoint[1], checkLinePoint[0], checkLinePoint[1]);

        lastMoveStart = cashLinePoint;
        lastMoveEnd = checkLinePoint;

        g.drawImage(character, checkCharacterDraw[0], checkCharacterDraw[1]);
        g.fillText(name, checkCharacterDraw[0], checkCharacterDraw[1] - 5);
        oldCharacter = checkCharacterDraw;
    }

    public void fromCheckApproverToCashier(GraphicsContext g) {
        clearOldCharacter(g);
        refreshLastLine(g);

        g.strokeLine(checkLinePoint[0], checkLinePoint[1], cashLinePoint[0], cashLinePoint[1]);

        lastMoveStart = checkLinePoint;
        lastMoveEnd = cashLinePoint;

        g.drawImage(character, cashCharacterDraw[0], cashCharacterDraw[1]);
        g.fillText(name, cashCharacterDraw[0], cashCharacterDraw[1] - 5);
        oldCharacter = cashCharacterDraw;
    }

    public void fromCashierToExit(GraphicsContext g) {
        clearOldCharacter(g);
        refreshLastLine(g);
        if (reg) {
            lastMoveStart = regLinePoint;
            lastMoveEnd = cashLinePoint;
        } else {
            lastMoveStart = licLinePoint;
            lastMoveEnd = cashLinePoint;
        }
        refreshLastLine(g);

        g.strokeLine(cashLinePoint[0], cashLinePoint[1], exitLinePoint[0], exitLinePoint[1]);

        lastMoveStart = cashLinePoint;
        lastMoveEnd = exitLinePoint;

        g.drawImage(character, exitCharacterDraw[0], exitCharacterDraw[1]);
        g.fillText(name, exitCharacterDraw[0], exitCharacterDraw[1] - 5);
        oldCharacter = exitCharacterDraw;
    }

    //this method and the following methods set the time at each particular station
    public void setSignInTime(GraphicsContext g, int time) {
        g.setFill(Color.WHITE);
        g.fillRect(signInTime[0], signInTime[1] - 25, 35, 25);
        g.setFill(Color.RED);
        g.setFont(timeFont);
        g.fillText("" + time, signInTime[0], signInTime[1]);
        g.setFont(def);
    }

    public void setRegTime(GraphicsContext g, int time) {
        g.setFill(Color.WHITE);
        g.fillRect(regTime[0], regTime[1] - 25, 35, 25);
        g.setFill(Color.RED);
        g.setFont(timeFont);
        g.fillText("" + time, regTime[0], regTime[1]);
        g.setFont(def);
    }

    public void setLicTime(GraphicsContext g, int time) {
        g.setFill(Color.WHITE);
        g.fillRect(licTime[0], licTime[1] - 25, 35, 25);
        g.setFill(Color.RED);
        g.setFont(timeFont);
        g.fillText("" + time, licTime[0], licTime[1]);
        g.setFont(def);
    }

    public void setCashTime(GraphicsContext g, int time) {
        g.setFill(Color.WHITE);
        g.fillRect(cashTime[0], cashTime[1] - 25, 35, 25);
        g.setFill(Color.RED);
        g.setFont(timeFont);
        g.fillText("" + time, cashTime[0], cashTime[1]);
        g.setFont(def);
    }

    public void setCheckTime(GraphicsContext g, int time) {
        g.setFill(Color.WHITE);
        g.fillRect(checkTime[0], checkTime[1] - 25, 35, 25);
        g.setFill(Color.RED);
        g.setFont(timeFont);
        g.fillText("" + time, checkTime[0], checkTime[1]);
        g.setFont(def);
    }

    //removes the old character from the graphic by drawing a blank rectangle over it
    private void clearOldCharacter(GraphicsContext g) {
        g.setFill(Color.WHITE);
        g.fillRect(oldCharacter[0], oldCharacter[1] - 15, character.getWidth(), character.getHeight() + 15);
        g.setFill(Color.BLACK);
    }

    //after the old character is removed, there is a gap where the character picture overlapped the line. This method fills in that gap
    private void refreshLastLine(GraphicsContext g) {
        g.setStroke(Color.WHITE);
        g.strokeLine(lastMoveStart[0], lastMoveStart[1], lastMoveEnd[0], lastMoveEnd[1]);
        g.setStroke(Color.RED);
        g.strokeLine(lastMoveStart[0], lastMoveStart[1], lastMoveEnd[0], lastMoveEnd[1]);
    }

    //writes the different errors to the screen for 2 seconds, then removes them
    public void writeError(GraphicsContext g, int code) {
        int sleepAmount = 2000;
        Thread thread = new Thread() {
            public void run() {
                try {
                    switch (code) {
                        case 1:
                            write(g, "ERROR: Not enough parameters passed!");
                            break;
                        case 2:
                            write(g, "ERROR: Improper Arrival Time!");
                            break;
                        case 3:
                            write(g, "ERROR: Improper Reg/Lic Character!");
                            break;
                        case 4:
                            write(g, "ERROR: Improper Payment Method!");
                            break;
                        case 5:
                            write(g, "ERROR: Name Cannot Exceed 11 Characters!");
                            break;
                    }
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException in writeError method of GraphicsProcessor.java");
                }
            }

            public void write(GraphicsContext g, String error) throws InterruptedException {
                g.setFill(Color.RED);
                g.fillText(error, 10, 20);
                Thread.sleep(sleepAmount);
                g.setFill(Color.WHITE);
                g.fillRect(0, 0, 250, 25);
            }
        };
        thread.start();
    }

}













