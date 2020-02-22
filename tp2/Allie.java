package tp2;
import robocode.*;

import java.io.Serializable;

import static tp2.Constants.*;

public class Allie implements Serializable {


    private String name;
    private double x;
    private double y;
    private double heading;
    private double radar_heading;
    private double gun_heading;
    private double energy;
    private double gun_heat;
    private double velocity;

    public Allie(String name, double x, double y, double heading, double radar_heading, double gun_heading, double energy, double gun_heat, double velocity) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.radar_heading = radar_heading;
        this.gun_heading = gun_heading;
        this.energy = energy;
        this.gun_heat = gun_heat;
        this.velocity = velocity;
    }

    public double[] NormalizedArray() {
        double[] array = new double[ALLIE_INPUT_VARS];
        array[0] = x/MAP_WIDTH;
        array[1] = y/MAP_HEIGHT;
        array[2] = heading/360;
        array[3] = radar_heading/360;
        array[4] = gun_heading/360;
        array[5] = energy/MAX_ENERGY;
        array[6] = gun_heat/3;
        array[7] = velocity/ Rules.MAX_VELOCITY;

        return array;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    public double getRadar_heading() {
        return radar_heading;
    }

    public double getGun_heading() {
        return gun_heading;
    }

    public double getEnergy() {
        return energy;
    }

    public double getGun_heat() {
        return gun_heat;
    }

    public double getVelocity() {
        return velocity;
    }

    @Override
    public String toString() {
        return "Allie{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", heading=" + heading +
                ", radar_heading=" + radar_heading +
                ", gun_heading=" + gun_heading +
                ", energy=" + energy +
                ", gun_heat=" + gun_heat +
                ", velocity=" + velocity +
                '}';
    }
}
