package tp2;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;


public class AntiSocial extends TeamRobot {
    private static double WALL_STICK = 140;
    private java.awt.geom.Rectangle2D.Double _fieldRect;
    private double _bfWidth;
    private double _bfHeight;


    private State state;
    private int tick_count;
    private String lockedRadarEnemy;
    private String attackingEnemy;

    public void run(){
        setColors(Color.black, Color.black, Color.black, Color.black, Color.black);

        tick_count = 0;

        _bfWidth = getBattleFieldWidth();
        _bfHeight = getBattleFieldHeight();
        _fieldRect = new java.awt.geom.Rectangle2D.Double(18, 18,
                        _bfWidth-36, _bfHeight-36);

        state= new State(this);

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);


        // LOOP INFINITO
        do{
            if(Math.random() < 0.1) {
                back(50);
            }

            update();

            scanForTarget();

            attackEnemy();

            doMove();
        } while (true);
    }

    public  void scanForTarget() {

        if(lockedRadarEnemy == null) {
            setTurnRadarRight(Double.MAX_VALUE);
            execute();
        }

        double min = 500, dist;
        String min_name = null;

        for(Enemy e : state.enemies.values()){
            dist = State.dist(getX(), getY(), e.getX(), e.getY());
            boolean b = true;
            for(Allie a: state.allies.values())
                if(!a.getName().equals(getName()))
                    if(Math.abs(normalizeBearing(getGunHeadingRadians() - absoluteBearing(getX(), getY(), a.getX(), a.getY()))) < 60 && State.dist(getX(), getY(), a.getX(), a.getY()) > 500)
                        b = false;

            if(b)
                if (dist < min) {
                        min = dist;
                        min_name = e.getName();
                }
        }

        if(min_name != null)
            attackingEnemy = min_name;

    }

    private void attackEnemy() {
        if(attackingEnemy == null)
            return;
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
            Enemy en = state.enemies.get(attackingEnemy);
            for (Allie a: state.allies.values())
                if(!a.getName().equals(getName()))
                    if(State.dist(a.getX(), a.getY(), getX(), getY()) < State.dist(getX(), getY(), en.getX(), en.getY()) &&
                        Math.abs(normalizeBearing(absoluteBearing(getX(), getY(), a.getX(), a.getY()))) < 30)
                        b = false;

                if(b)
                    setFire(firePower);
        }
        execute();
    }

    public Allie generateAllie(){
        return new Allie(   getName(), getX(), getY(), getHeading(),
                            getRadarHeading(), getGunHeading(), getEnergy(),
                            getGunHeat(), getVelocity(), lockedRadarEnemy, attackingEnemy);
    }



    public void onMessageReceived(MessageEvent event) {
            state.updateRobot(event.getMessage());
    }


    @Override
    public void onHitByBullet(HitByBulletEvent event) {
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        //back(100);
    }

    @Override
    public void onDeath(DeathEvent event) {
    }

    public void onScannedRobot(ScannedRobotEvent event) {
        String name = event.getName();
        if(isTeammate(name))
            return;

        if(lockedRadarEnemy == null ) {

            boolean b = true;
            if(state.allies.size() < state.enemies.size())
                b = false;

            for( Allie a : state.allies.values()){
                if(a.getLockedRadar() != null && a.getLockedRadar().equals(name))
                    b = false;
            }


            lockedRadarEnemy = name;

            if(b) {
                lockedRadarEnemy = name;
                double radarTurn =
                        // Absolute bearing to target
                        getHeadingRadians() + event.getBearingRadians()
                                // Subtract current radar heading to get turn required
                                - getRadarHeadingRadians();

                setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
            }

        } else if (lockedRadarEnemy.equals(name)){
            double radarTurn =
                    // Absolute bearing to target
                    getHeadingRadians() + event.getBearingRadians()
                            // Subtract current radar heading to get turn required
                            - getRadarHeadingRadians();

            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
        }

        Enemy e = generateEnemy(event);
        state.updateRobot(e);

        try {
            broadcastMessage(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if(isTeammate(event.getName()))
            turnRight(90);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        String name = event.getName();

        state.removeRobot(name);

        if (state.enemies.size() > state.allies.size())
            lockedRadarEnemy = null;


        if(name.equals(lockedRadarEnemy))
            lockedRadarEnemy = null;

        if(name.equals(attackingEnemy))
            attackingEnemy = null;
    }

    public Enemy generateEnemy(ScannedRobotEvent e){

        double enemyBearing = getHeading() + e.getBearing();
        int enemyX = (int) Math.round(getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing)));
        int enemyY = (int) Math.round(getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing)));

        return new Enemy(e.getName(), enemyX, enemyY, e.getHeading(),e.getEnergy(), e.getVelocity());
    }

    @Override
    public void onCustomEvent(CustomEvent event) {
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

    private void doMove() {
        if (state.enemies.size() <= state.enemies.size() && state.enemies.size() <=2)
            chase();
        else
            evade();
    }

    private void evade() {
        double xForce=0, yForce= 0;
        for(Allie a : state.allies.values()){
            if(!a.getName().equals(getName())) {
                double absBearing = Utils.normalAbsoluteAngle(Math.atan2(a.getX() - getX(), a.getY() - getY()));
                double distance = State.dist(getX(), getY(), a.getX(), a.getY())/50.91;
                xForce -= Math.sin(absBearing) / (distance * distance);
                yForce -= Math.cos(absBearing) / (distance * distance);
            }
        }

        for(Enemy a : state.enemies.values()){
                double absBearing = Utils.normalAbsoluteAngle(Math.atan2(a.getX() - getX(), a.getY() - getY()));
                double distance = State.dist(getX(), getY(), a.getX(), a.getY())/50.91;
                xForce -= Math.sin(absBearing) / (distance * distance);
                yForce -= Math.cos(absBearing) / (distance * distance);
        }

        long t = getTime();


        double angle = Math.atan2(xForce, yForce);
        angle = wallSmoothing(getX(), getY(), angle);

        if (xForce == 0 && yForce == 0) {
            // If no force, do nothing
        } else if(Math.abs(angle-getHeadingRadians())<Math.PI/2){
            setTurnRightRadians(Utils.normalRelativeAngle(angle-getHeadingRadians()));
            setAhead(Double.POSITIVE_INFINITY);
        } else {
            setTurnRightRadians(Utils.normalRelativeAngle(angle+Math.PI-getHeadingRadians()));
            setAhead(Double.NEGATIVE_INFINITY);
        }



        execute();

    }

    public double wallSmoothing(double x, double y, double startAngle) {

        double angle = startAngle;

        angle += (4*Math.PI);

        double testX = x + (Math.sin(angle)*WALL_STICK);
        double testY = y + (Math.cos(angle)*WALL_STICK);
        double wallDistanceX = Math.min(x - 18, _bfWidth - x - 18);
        double wallDistanceY = Math.min(y - 18, _bfHeight - y - 18);
        double testDistanceX = Math.min(testX - 18, _bfWidth - testX - 18);
        double testDistanceY = Math.min(testY - 18, _bfHeight - testY - 18);

        double adjacent = 0;
        int g = 0; // avoid infinite loops

        while (!_fieldRect.contains(testX, testY) && g++ < 25) {
            if (testDistanceY < 0 && testDistanceY < testDistanceX) {
                // wall smooth North or South wall
                angle = ((int)((angle + (Math.PI/2)) / Math.PI)) * Math.PI;
                adjacent = Math.abs(wallDistanceY);
            } else if (testDistanceX < 0 && testDistanceX <= testDistanceY) {
                // wall smooth East or West wall
                angle = (((int)(angle / Math.PI)) * Math.PI) + (Math.PI/2);
                adjacent = Math.abs(wallDistanceX);
            }

            angle += (Math.abs(Math.acos(adjacent/WALL_STICK)) + 0.005);

            testX = x + (Math.sin(angle)*WALL_STICK);
            testY = y + (Math.cos(angle)*WALL_STICK);
            testDistanceX = Math.min(testX - 18, _bfWidth - testX - 18);
            testDistanceY = Math.min(testY - 18, _bfHeight - testY - 18);

        }

        return normalizeBearing(angle); // you may want to normalize this
    }



    private void chase() {
        double xForce=0, yForce= 0;
        for(Allie a : state.allies.values()){
            if(!a.getName().equals(getName())) {
                double absBearing = Utils.normalAbsoluteAngle(Math.atan2(a.getX() - getX(), a.getY() - getY()));
                double distance = State.dist(getX(), getY(), a.getX(), a.getY())/50.91;
                xForce -= Math.sin(absBearing) / (distance * distance);
                yForce -= Math.cos(absBearing) / (distance * distance);
            }
        }

        for(Enemy a : state.enemies.values()){
            double absBearing = Utils.normalAbsoluteAngle(Math.atan2(a.getX() - getX(), a.getY() - getY()));
            double distance = State.dist(getX(), getY(), a.getX(), a.getY())/50.91;
            xForce += 3*Math.sin(absBearing) / (distance * distance);
            yForce += 3*Math.cos(absBearing) / (distance * distance);
        }
        long t = getTime();

        double angle = Math.atan2(xForce, yForce);
        angle = wallSmoothing(getX(), getY(), angle);

        if (xForce == 0 && yForce == 0) {
            // If no force, do nothing
        } else if(Math.abs(angle-getHeadingRadians())<Math.PI/2){
            setTurnRightRadians(Utils.normalRelativeAngle(angle-getHeadingRadians()));
            setAhead(Double.POSITIVE_INFINITY);
        } else {
            setTurnRightRadians(Utils.normalRelativeAngle(angle+Math.PI-getHeadingRadians()));
            setAhead(Double.NEGATIVE_INFINITY);
        }

        execute();
    }

    public void manageRepeatedLocks() throws IOException {
        for (Allie a : state.allies.values()){
            if(!a.getName().equals(getName())) {
                if (a.getLockedRadar() != null && a.getLockedRadar().equals(lockedRadarEnemy)) {
                    lockedRadarEnemy = null;
                }
            }
        }
    }

    private void goTo(int x, int y) {
        double a;
        setTurnRightRadians(Math.tan(
                a = Math.atan2(x -= (int) getX(), y -= (int) getY())
                        - getHeadingRadians()));
        execute();
        setAhead(Math.hypot(x, y) * Math.cos(a));
        execute();
    }



    public boolean update() {
            Allie a = generateAllie();
            state.updateRobot(a);
            try {
                manageRepeatedLocks();
                broadcastMessage(a);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
    }

}


