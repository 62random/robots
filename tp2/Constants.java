package tp2;

public final class Constants {


    // ENVIRONMENT CONSTANTS
    public final int N_POSSIBLE_STATES = (MAP_MODEL_PARTITION*MAP_MODEL_PARTITION*DEGREES_PARTITIONS*DEGREES_PARTITIONS*DEGREES_PARTITIONS*ENERGY_PARTITIONS*VELOCITY_PARTITIONS*2)*NUM_ELEMENTS_TEAM +
                                            (MAP_MODEL_PARTITION*MAP_MODEL_PARTITION*DEGREES_PARTITIONS*ENERGY_PARTITIONS*VELOCITY_PARTITIONS)*NUM_ELEMENTS_TEAM;

    public static int getNPossibleStates(){
        return new Constants().N_POSSIBLE_STATES;
    }

    public static final int N_POSSIBLE_ACTIONS = 5;

    public static final double MAP_HEIGHT = 600;
    public static final double MAP_WIDTH = 800;
    public static final int MAP_MODEL_PARTITION = 8;
    public static final int DEGREES_PARTITIONS = 10;
    public final static int ENERGY_PARTITIONS = 4;
    public final static int VELOCITY_PARTITIONS = 3;
    public static final int NUM_ELEMENTS_TEAM = 4;

    public static final double MAX_ENERGY = 120;
    public static final int ALLIE_INPUT_VARS = 8;
    public static final int ENEMY_INPUT_VARS = 5;


    // LEARNING MODEL CONSTANTS
    public static final int MAX_TABLE_ENTRIES = 10000000;

    public static final double ALPHA = 0.2;
    public static final double GAMMA = 0.5;


}
