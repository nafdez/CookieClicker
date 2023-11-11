package es.ignaciofp.contador.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingData {

    private static RankingData instance;
    private List<Ranking> rankingList;

    public static RankingData getInstance() {
        if (instance == null) {
            instance = new RankingData();
        }
        return instance;
    }

    private RankingData() {
        rankingList = new ArrayList<>();
    }

    public List<Ranking> getRankingList() {
        return Collections.unmodifiableList(rankingList);
    }

    public void setRankingList(List<Ranking> rankingList) {
        this.rankingList = rankingList;
    }

    public void add(Ranking ranking) {
        rankingList.add(ranking);
    }

    public void remove(Ranking ranking) {
        rankingList.remove(ranking);
    }


}