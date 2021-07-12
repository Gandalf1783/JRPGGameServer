package de.gandalf1783.gameserver.statics;

public class DBNames {

    private static final String DB_PLAYERS_DE_DEV = "de_jrpg_dev";
    private static final String DB_PLAYERS_DE_DEPLOY = "de_jrpg_deploy";

    public static String retrieveDBFromCode(String region) {
        if(region.equalsIgnoreCase("DEV")) {
            return DB_PLAYERS_DE_DEV;
        } else if(region.equalsIgnoreCase("DE")) {
            return DB_PLAYERS_DE_DEPLOY;
        } else {
            return "ERR";
        }
    }

}
