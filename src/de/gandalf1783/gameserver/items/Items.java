package de.gandalf1783.gameserver.items;

public class Items {

    public static Item getWoodItem() {
        Item woodItem;
        woodItem = new Item();
        woodItem.setId(0);
        woodItem.setName("Wood");
        return woodItem;
    }

    public static Item getCactusItem() {
        Item cactusItem = new Item();
        cactusItem.setId(2);
        cactusItem.setName("Cactus");
        return cactusItem;
    }

    public static Item getSignItem201() {
        Item signItem201 = new Item();
        signItem201.setId(201);
        signItem201.setName("Double-Sign");
        return signItem201;
    }

    public static Item getSignItem202() {
        Item signItem202 = new Item();
        signItem202.setId(202);
        signItem202.setName("Sign");
        return signItem202;
    }

}
