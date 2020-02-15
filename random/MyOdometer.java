package random;

import robocode.AdvancedRobot;
import robocode.Condition;

public class MyOdometer extends Condition {
    private boolean is_racing = false;
    private boolean finished = false;
    private double total_distance = 0;
    private Position last;
    private AdvancedRobot robot = null;
    private String name;

    public MyOdometer(String name, AdvancedRobot me){
        robot = me;
        this.name = name;
    }

    @Override
    public boolean test() {
        if((int)robot.getX() == 18 && (int)robot.getY() == 18 && !is_racing && total_distance == 0) {
            System.out.println("start");
            is_racing = true;
            last = new Position(robot.getX(), robot.getY());
        }
        else if ((int)robot.getX() == 18 && (int)robot.getY() == 18 && is_racing && total_distance > 1) {
            System.out.println("end");
            finished = true;
            is_racing = false;

            total_distance += last.distanceFrom(new Position(robot.getX(), robot.getY()));
        }
        else if (is_racing && !finished){
            Position aux= new Position(robot.getX(), robot.getY());
            total_distance += last.distanceFrom(aux);
            last = aux;
        }
        return finished;
    }

    public double getRaceDistance(){
        return this.total_distance;
    }


    class Position{
        public double x, y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public double distanceFrom(Position p){
            return Math.sqrt((p.x - x)*(p.x - x) + (p.y - y)*(p.y - y));
        }

    }
}
