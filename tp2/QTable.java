package tp2;
import static tp2.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class QTable {

    public HashMap<String, double[]> table;

    public QTable(){
        this.table = new HashMap<String, double[]>(Constants.getNPossibleStates());
    }

    public void UpdateQValue(String state1, String state2, int action, double reward){
        double[] arr;
        if(table.containsKey(state1)){
            arr = table.get(state1);
        }
        else {
            arr = new double[N_POSSIBLE_ACTIONS];
        }

        double maxa = 0;
        if(table.containsKey(state2)){
            maxa = Double.MIN_VALUE;
            double[] aux = table.get(state2);
            for(int i= 0; i< N_POSSIBLE_ACTIONS; i++)
                if(aux[i] > maxa)
                    maxa = aux[i];
        }
        arr[action] = (1-ALPHA) * arr[action] + ALPHA*(reward + GAMMA*maxa);
    }

}
