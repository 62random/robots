package tp2;
import java.util.HashMap;

import static tp2.Constants.*;

public class State {
    private String ownerName;
    private HashMap<String, Allie> allies;
    private HashMap<String, Enemy> enemies;


    public State(Allie a) {
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

    public String encode(){
        int i = 0, j;
        String string = "";

        string += allies.get(ownerName).encode();       // Para garantir que os primeiros números do array
                                                        // correspondem sempre aos dados do dono deste objeto
        for (Allie a : allies.values())
            if(!a.getName().equals(ownerName))
                string += a.encode();

        for (Enemy e : enemies.values())
            string += e.encode();

        return string;
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
}
