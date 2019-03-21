package communication.Messages;

import logic.item.Item;

/**
 * Message envoyé au serveur par un client pour indiquer qu'il a sélectionné un item dans le shop
 */
public class ShopMessage extends  Message {

    private Item item;

    public ShopMessage(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
