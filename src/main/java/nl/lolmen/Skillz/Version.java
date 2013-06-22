/*
 *  Copyright 2013 Lolmewn <info@lolmewn.nl>.
 */

package nl.lolmen.Skillz;

/**
 *
 * @author Lolmewn <info@lolmewn.nl>
 */
public class Version {

    private double major;
    private double minor;
    private boolean snapshot;
    private String input;
    
    public Version(String input) {
        this.input = input;
        if(!input.contains(".")){
            System.out.println("Version doesn't contain '.', can't find version.");
            this.major = 0;
            this.minor = 0;
            this.snapshot = false;
            return;
        }
        major = Double.parseDouble(input.substring(0, input.indexOf(".")));
        if(input.contains("-SNAPSHOT")){
            this.snapshot = true;
        }
        minor = Double.parseDouble(input.substring(input.indexOf(".") + 1, snapshot?input.indexOf("-") -1 : input.length()));
    }

    public String getInput() {
        return input;
    }

    public double getMajor() {
        return major;
    }

    public double getMinor() {
        return minor;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

}
