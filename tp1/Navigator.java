package tp1;
import robocode.*;
import java.awt.*;
import standardOdometer.Odometer;


public class Navigator extends AdvancedRobot {

    private Odometer odometer = new Odometer("IsRacing", this);
    private MyOdometer odometer2 = new MyOdometer("MyOdometer", this);

    private Obstacle[] obstacles = new Obstacle[3];
    private int i = 0;
    private boolean circle = false;

    public void run() {
        setColors(Color.red, Color.red, Color.red); // body,gun,radar
        addCustomEvent(odometer);
        addCustomEvent(odometer2);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);


        while ((int)getX() != 18 || (int)getY() != 18) {
            goTo(18, 18);
        }

        turnRight(45 - getHeading());
        execute();
        setTurnRadarRight(-getRadarHeading());
        execute();

        while(i < 3) {
            setTurnRadarRight(360);
            execute();
        }

        while (Math.abs(getX() - obstacles[0].x + 40) >= 1 || Math.abs(getY() - obstacles[0].y - 40) >= 1) {
            goTo(obstacles[0].x - 40, obstacles[0].y + 40);
        }

        while (Math.abs(getX() - obstacles[1].x - 40) >= 1 || Math.abs(getY() - obstacles[1].y - 40) >= 1) {
            goTo(obstacles[1].x + 40, obstacles[1].y + 40);
        }

        while (Math.abs(getX() - obstacles[2].x - 40) >= 1 || Math.abs(getY() - obstacles[2].y + 40) >= 1) {
            goTo(obstacles[2].x + 40, obstacles[2].y - 40);
        }

        while ((int)getX() != 18 || (int)getY() != 18) {
            goTo(18, 18);
        }

        System.out.println(odometer.getRaceDistance() + " distance from standard odometer");
        System.out.println(odometer2.getRaceDistance() + " distance from my odometer");


    }

    public void onHitWall(HitWallEvent e) {
        goTo(18,18);
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("IsRacing"))
            this.odometer.getRaceDistance();
    }

    private void goTo(int x, int y) {
        double a;
        setTurnRightRadians(Math.tan(
                a = Math.atan2(x -= (int) getX(), y -= (int) getY())
                        - getHeadingRadians()));
        execute();
        setAhead(Math.hypot(x, y) * Math.cos(a) * 0.1 );
        execute();
    }

    public void onScannedRobot(ScannedRobotEvent e) {
            if (e.getVelocity() == 0 && i < 3) {
                for (int j = 0; j < i; j++)
                    if (e.getName().equals(obstacles[j].name)) {
                        return;
                    }
                double enemyBearing = getHeading() + e.getBearing();
                // Calculate enemy's position
                int enemyX = (int) Math.round(getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing)));
                int enemyY = (int) Math.round(getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing)));

                Obstacle aux, o = new Obstacle(enemyX, enemyY, e.getName());

                for (int j = 0; j < i; j++) {
                    if ((o.y / (float) o.x) > (obstacles[j].y / (float) obstacles[j].x)) {
                        aux = obstacles[j];
                        obstacles[j] = o;
                        o = aux;

                    }
                }
                obstacles[i++] = o;
            }
        }

    class Obstacle {
        public int x, y;
        public String name;

        public Obstacle(int x, int y, String s){
            this.x = x;
            this.y = y;
            this.name = s;
        }
    }
}