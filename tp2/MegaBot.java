package tp2;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;


public class MegaBot extends TeamRobot {

    private State state;
    private int tick_count;
    private String lockedRadarEnemy;
    private ArrayList<String> unlockedRobots;
    private String attackingEnemy;

    private int direction = 1;

    public void run(){
        setColors(Color.red, Color.black, Color.red, Color.black, Color.red);

        tick_count = 0;

        state= new State(generateAllie());
        unlockedRobots = new ArrayList<>();

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        addCustomEvent(new Updater());


        // LOOP INFINITO
        do{
            if(lockedRadarEnemy == null)
                setTurnRadarRight(360);

            double min = Double.MAX_VALUE, dist;
            String min_name = null;

            for(Enemy e : state.enemies.values()){
                dist = 0;
                for (Allie a: state.allies.values()){
                    dist += Math.sqrt((a.getX() - e.getX())*(a.getX() - e.getX()) + (a.getY() - e.getY())*(a.getY() - e.getY()));
                }
                if (dist < min && dist > 100) {
                    min = dist;
                    min_name = e.getName();
                }
            }
            if(min_name != null){
                attackingEnemy = min_name;
                attackEnemy();
            }


            setAhead(100*direction);
            //setTurnRightRadians(Math.random()*2 -1);
            execute();
        } while (true);
    }

    private void attackEnemy() {
        Enemy e = state.enemies.get(attackingEnemy);
        double dist = Math.sqrt((getX() - e.getX())*(getX() - e.getX()) + (getY() - e.getY())*(getY() - getY()));
        // calculate firepower based on distance
        double firePower = Math.min(500 / dist, 3);
        // calculate speed of bullet
        double bulletSpeed = 20 - firePower * 3;
        // distance = rate * time, solved for time
        long time = (long)(dist / bulletSpeed);

        double futureX = e.getFutureX(time);
        double futureY = e.getFutureY(time);
        double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);

        setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            boolean b = true;
            double allie_x, allie_y;
            for(Allie a : state.allies.values()){
                allie_x = a.getX();
                allie_y = a.getY();
                if(!a.getName().equals(getName()))
                    if(Math.abs(normalizeBearing(absoluteBearing(getX(), getY(), allie_x, allie_y))) < 20) {
                        b = false;
                    }
            }

            if (b)
                setFire(firePower);
        }
        execute();
    }

    public Allie generateAllie(){
        return new Allie(   getName(), getX(), getY(), getHeading(),
                            getRadarHeading(), getGunHeading(), getEnergy(),
                            getGunHeat(), getVelocity());
    }



    public void onMessageReceived(MessageEvent event) {
        if(event.getMessage() instanceof String) {
            String name = (String) event.getMessage();
            if( name.substring(0,1).equals("U")){
                unlockedRobots.add(name.substring(1));
            }
            else {
                unlockedRobots.remove(name.substring(1));
            }

        }
        else
            state.updateRobot(event.getMessage());
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        direction *= -1;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        direction *= -1;
    }

    @Override
    public void onDeath(DeathEvent event) {
        if(lockedRadarEnemy != null) {
            try {
                broadcastMessage("U" + lockedRadarEnemy);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onScannedRobot(ScannedRobotEvent event) {
        if(isTeammate(event.getName()))
            return;

        if(lockedRadarEnemy == null && (!state.enemies.containsKey(event.getName()) || unlockedRobots.contains(event.getName()) )) {
            unlockedRobots.remove(event.getName());
            try {
                broadcastMessage("L" + event.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            lockedRadarEnemy = event.getName();

            double radarTurn =
                    // Absolute bearing to target
                    getHeadingRadians() + event.getBearingRadians()
                            // Subtract current radar heading to get turn required
                            - getRadarHeadingRadians();

            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        } else if (lockedRadarEnemy == null) {

        } else if (lockedRadarEnemy.equals(event.getName())){
            System.out.print("locked robot on" );
            double radarTurn =
                    // Absolute bearing to target
                    getHeadingRadians() + event.getBearingRadians()
                            // Subtract current radar heading to get turn required
                            - getRadarHeadingRadians();

            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
        }

        Enemy e = generateEnemy(event);
        state.updateRobot(e);

        System.out.println(e.getX() + ", " + e.getY());



        try {
            broadcastMessage(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        state.removeRobot(event.getName());

        if(event.getName().equals(lockedRadarEnemy))
            lockedRadarEnemy = null;

        if(event.getName().equals(attackingEnemy))
            attackingEnemy = null;
    }

    public Enemy generateEnemy(ScannedRobotEvent e){

        double enemyBearing = getHeading() + e.getBearing();
        int enemyX = (int) Math.round(getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing)));
        int enemyY = (int) Math.round(getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing)));

        return new Enemy(e.getName(), enemyX, enemyY, e.getHeading(),e.getEnergy(), e.getVelocity());
    }


    private void turnGunTo(double x, double y){
        double absDeg = absoluteBearing(getX(), getY(), x, y);
        setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
    }

    double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }


    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }


    private class Updater extends Condition {

        @Override
        public boolean test() {
            Allie a = generateAllie();
            state.updateRobot(a);
            try {
                broadcastMessage(a);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
