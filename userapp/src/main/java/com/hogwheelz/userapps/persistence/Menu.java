package com.hogwheelz.userapps.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Startup on 3/2/17.
 */

public class Menu {

    public String idMenu;
    public String name;
    public List<Item> item;


    public Menu()
    {
        idMenu="";
        name="";
        item= new ArrayList<Item>();
    }
}
