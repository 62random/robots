package tp2;

import java.util.*;


public class State {
    private String ownerName;
    public HashMap<String, Allie> allies;
    public HashMap<String, Enemy> enemies;

    public State(Electron r) {
        Allie a = r.generateAllie();
        this.allies = new HashMap<>();
        ownerName = a.getName();
        allies.put(a.getName(), a);
        this.enemies = new HashMap<>();
    }


    public void updateRobot(Object o){
        if(o instanceof  Allie)
            allies.put(((Allie) o).getName(), (Allie) o);
        else if (o instanceof  Enemy)
            enemies.put(((Enemy) o).getName(), (Enemy) o);
    }


    @Override
    public String toString() {
        String str_allies = "";
        for(Allie a : allies.values()) {
            if (str_allies.equals(""))
                str_allies += "\n\t\t" + a.toString();
            else
                str_allies += ", \n\t\t" + a.toString();
        }

        String str_enemies = "";
        for(Enemy e : enemies.values()) {
            if (str_enemies.equals(""))
                str_enemies += "\n\t\t" + e.toString();
            else
                str_enemies += ", \n\t\t" + e.toString();
        }


        return "State{\n" +
                "\n\townerName='" + ownerName + '\'' +
                ", \n\tallies=[" + str_allies +
                "], \n\tenemies=[ " + str_enemies +
                "]}";
    }

    public void removeRobot(String name) {

        if(allies.containsKey(name))
            allies.remove(name);
        else if(enemies.containsKey(name))
            enemies.remove(name);
    }


    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
}
