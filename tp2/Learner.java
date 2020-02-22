package tp2;

import robocode.*;
import org.tensorflow.*;

import java.io.IOException;

public class Learner extends TeamRobot {

    private State state;

    public void run(){
        state = new State(generateAllie());
        double a = getRadarHeading();
        addCustomEvent(new Updater());

        // APENAS PARA TESTAR A RECOLHA DE INFORMAÇÃO E COMUNICAÇÃO
        while((int) getRadarHeading() != ((int) a + 180) % 360) {
            setTurnRadarRight(90);
            execute();
        }

        System.out.println(state);

        // APENAS PARA TESTAR A RECOLHA DE INFORMAÇÃO E COMUNICAÇÃO


    }

    public Allie generateAllie(){
        return new Allie(   getName(), getX(), getY(), getHeading(),
                            getRadarHeading(), getGunHeading(), getEnergy(),
                            getGunHeat(), getVelocity());
    }


    public void onMessageReceived(MessageEvent event) {
        state.updateRobot(event.getMessage());
    }

    public void onScannedRobot(ScannedRobotEvent event) {
        if(isTeammate(event.getName()))
            return;
        System.out.print(event.getName());

        Enemy e = generateEnemy(event);

        state.updateRobot(e);

        try {
            broadcastMessage(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Enemy generateEnemy(ScannedRobotEvent e){
        double enemyBearing = getHeading() + e.getBearing();
        int enemyX = (int) Math.round(getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing)));
        int enemyY = (int) Math.round(getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing)));

        return new Enemy(e.getName(), enemyX, enemyY, e.getHeading(),e.getEnergy(), e.getVelocity());
    }



    private class Updater extends Condition {

        @Override
        public boolean test() {
            try {
                broadcastMessage(generateAllie());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
