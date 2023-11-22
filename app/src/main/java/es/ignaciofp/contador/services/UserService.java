package es.ignaciofp.contador.services;

import android.content.Context;

import java.util.ArrayList;

import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.utils.DBHandler;

public class UserService {

    private static UserService instance;
    private final DBHandler dbHandler;

    private UserService(Context context) {
        dbHandler = new DBHandler(context.getApplicationContext());
    }

    public static UserService getInstance(Context context) {
        if (instance == null) {
            instance = new UserService(context);
        }
        return instance;
    }

    public void addUser(User user) {
        dbHandler.addUser(user);
    }

    public User getUserById(int id) {
        return dbHandler.readUsers(DBHandler.ID_COLUMN + "='" + id + "'").get(0);
    }

    public User getUserByName(String name) {
        ArrayList<User> userList = dbHandler.readUsers(DBHandler.NAME_COLUMN + "='" + name +"'");
        return userList.isEmpty() ? null : userList.get(0);
    }

    public void updateUser(User user) {
        dbHandler.updateUser(user);
    }


}
