package es.ignaciofp.contador.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.ignaciofp.contador.models.Ranking;
import es.ignaciofp.contador.models.RankingData;
import es.ignaciofp.contador.models.User;

public class RankingService {

    private static RankingService instance;
    private final UserService USER_SERVICE;
    private static final RankingData RANKING_DATA = RankingData.getInstance();

    private RankingService(Context context) {
        USER_SERVICE = UserService.getInstance(context);

        List<User> users = new ArrayList<>();

        USER_SERVICE.getTop25Users().stream().sorted().forEach(users::add);

        for (User user : users) {
            addToRankingList(new Ranking(user.getName(), user.getLastSaveDate(), user.getCoins().withSuffix("§")));
        }
    }

    public static RankingService getInstance(Context context) {
        if (instance == null) {
            instance = new RankingService(context);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
        RANKING_DATA.setRankingList(new ArrayList<>());
    }

    public List<Ranking> getRankingList() {
        return Collections.unmodifiableList(RANKING_DATA.getRankingList());
    }

    public void addToRankingList(Ranking ranking) {
        RANKING_DATA.add(ranking);
    }

}