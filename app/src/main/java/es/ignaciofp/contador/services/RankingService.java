package es.ignaciofp.contador.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.ignaciofp.contador.models.Ranking;
import es.ignaciofp.contador.models.RankingData;

public class RankingService {

    private static RankingService instance;

    private final RankingData RANKING_DATA = RankingData.getInstance();

    private final String PREFS_NAME = "ranking_prefs";
    private final String RANKING_LIST_KEY = "ranking_list_key";

    private RankingService(Context context) {
        recoverData(context);
    }

    public static RankingService getInstance(Context context) {
        if (instance == null) {
            instance = new RankingService(context);
        }
        return instance;
    }

    public List<Ranking> getRankingList() {
        return Collections.unmodifiableList(RANKING_DATA.getRankingList());
    }

    public void addToRankingList(Ranking ranking) {
        RANKING_DATA.add(ranking);
    }

    /**
     * Saves the current state of the shop to the preferences.
     *
     * @param context the context where it's being called from
     */
    public void saveData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(RANKING_DATA.getRankingList());
        editor.putString(RANKING_LIST_KEY, json);

        editor.apply();
    }

    /**
     * Removes all the shop data stored in the preferences.
     *
     * @param context the context where it's being called from
     */
    public void resetData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        RANKING_DATA.setRankingList(new ArrayList<>());

        editor.clear();
        editor.apply();
    }

    /**
     * Recovers all the data from the preferences and if none of them are previously saved
     * uses the defaults, later it assigns them to the ShopData instance.
     * <p>
     * It should only be called at initialization of ShopService instance.
     *
     * @param context the context where it's being called from
     */
    private void recoverData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(RANKING_LIST_KEY, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Ranking>>() {
            }.getType();
            RANKING_DATA.setRankingList(gson.fromJson(json, type));
        }


        RANKING_DATA.add(new Ranking("Nacho", "133.90$"));
        RANKING_DATA.add(new Ranking("Pedro", "113.90$"));
        RANKING_DATA.add(new Ranking("Pablo", "103.90$"));
        RANKING_DATA.add(new Ranking("Nuria", "93.90$"));
        RANKING_DATA.add(new Ranking("Abel", "83.90$"));

    }
}