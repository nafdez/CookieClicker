package es.ignaciofp.contador.models;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ranking {

    @NonNull
    private final String name;

    @NonNull
    private final String date;

    @NonNull
    private final String coins;

    public Ranking(@NonNull String name, @NonNull String date, @NonNull String coins) {
        this.name = name;
        this.date = date;
        this.coins = coins;
    }

    public Ranking(@NonNull String name, @NonNull String coins) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime date = LocalDateTime.now();

        this.name = name;
        this.date = dtf.format(date);
        this.coins = coins;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public String getCoins() {
        return coins;
    }

    @NonNull
    @Override
    public String toString() {
        return "Ranking{" + "name='" + name + '\'' + ", date='" + date + '\'' + ", coins='" + coins + '\'' + '}';
    }
}