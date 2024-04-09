package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Menu implements Serializable {
    String menuName;
    int menuPrice;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }
}
