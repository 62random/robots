package tp2;
import robocode.*;

import java.io.Serializable;

import static tp2.Constants.*;

public class Enemy implements Serializable {

    private String name;
    private double x;
    private double y;
    private double heading;
    private double energy;
    private double velocity;

    public Enemy(String name, double x, double y, double heading, double energy, double velocity) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.energy = energy;
        this.velocity = velocity;
    }

    public String encode() {
        String string = "";
        string += (x*MAP_MODEL_PARTITION/MAP_WIDTH);
        string += (y*MAP_MODEL_PARTITION/MAP_HEIGHT);
        string += (heading*DEGREES_PARTITIONS/360);
        string += (energy*ENERGY_PARTITIONS/MAX_ENERGY);
        string += (velocity*VELOCITY_PARTITIONS/Rules.MAX_VELOCITY);

        return string;
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

    public double getEnergy() {
        return energy;
    }

    public double getVelocity() {
        return velocity;
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", heading=" + heading +
                ", energy=" + energy +
                ", velocity=" + velocity +
                '}';
    }
}
