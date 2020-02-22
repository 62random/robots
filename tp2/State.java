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

    public double[] NormalizedArray(){
        double[] array = new double[NUM_ELEMENTS_TEAM*(ENEMY_INPUT_VARS + ALLIE_INPUT_VARS)];
        int i = 0, j;
        double[] arr;

        Allie aux = allies.get(ownerName);          // Para garantir que os primeiros números do array
                                                    // correspondem sempre aos dados do dono deste objeto
        for (j = 0; j < ALLIE_INPUT_VARS; j++) {
            arr = aux.NormalizedArray();
            array[i + j] = arr[j];
        }
        i += j;

        for (Allie a : allies.values()) {
            if(!a.getName().equals(ownerName)) {
                for (j = 0; j < ALLIE_INPUT_VARS; j++) {
                    arr = a.NormalizedArray();
                    array[i + j] = arr[j];
                }
                i += j;
            }
        }

        for (Enemy e : enemies.values()) {
            for(j = 0; j < ENEMY_INPUT_VARS; j++) {
                arr = e.NormalizedArray();
                array[i + j] = arr[j];
            }
            i += j;
        }
        return array;
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
