package de.gandalf1783.gameserver.inventory;

import de.gandalf1783.gameserver.items.Item;

import java.io.Serializable;
import java.util.ArrayList;


public class Inventory implements Serializable {

    private boolean active = false;
    private ArrayList<Item> inventoryItems = new ArrayList<>();;

    public Inventory(){

    }

    // Inventory methods

    public void addItem(Item item){
        for(Item i : inventoryItems){
            if(i.getId() == item.getId()){
                i.setCount(i.getCount() + item.getCount());
                return;
            }
        }
        inventoryItems.add(item);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<Item> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(ArrayList<Item> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }
}
