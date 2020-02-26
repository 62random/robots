package tp2;
import robocode.*;

import java.io.Serializable;

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

    public double getFutureX(long when){
        return x +Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
    }

    public double getFutureY(long when){
        return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
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
