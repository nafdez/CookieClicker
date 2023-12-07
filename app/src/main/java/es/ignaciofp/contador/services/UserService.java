package es.ignaciofp.contador.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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

    public boolean addUser(User user) {
        return dbHandler.addUser(user);
    }

    public User getUserByName(String name) {
        ArrayList<User> userList = dbHandler.readUsers(DBHandler.NAME_COLUMN + "='" + name + "'");
        return userList.isEmpty() ? null : userList.get(0);
    }

    public List<User> getTop25Users() {
        return dbHandler.readUsers("1=1 limit 25");
    }

    public boolean updateUser(User user) {
        return dbHandler.updateUser(user);
    }

}
