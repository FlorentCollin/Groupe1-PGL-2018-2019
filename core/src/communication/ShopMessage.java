package communication;

import logic.item.Item;

public class ShopMessage extends  Message {

    private Item item;

    public ShopMessage(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
