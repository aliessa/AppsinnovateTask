package com.task.aliessa.appsinnovatetask.model;

/**
 * Created by Ali Essa on 11/2/2017.
 */

public class Friend {

   private String id;
   private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Friend(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
