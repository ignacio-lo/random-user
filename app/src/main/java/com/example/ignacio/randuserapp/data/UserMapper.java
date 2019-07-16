package com.example.ignacio.randuserapp.data;

import java.util.List;

public class UserMapper {

    public static List<User> mapList(List<User> users) {
        for (User user : users) {
            user.setFullName(capitalizeName(user.getName().getFirst()) + " " + capitalizeName(user.getName().getLast()));
        }

        return users;
    }

    public static String capitalizeName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
