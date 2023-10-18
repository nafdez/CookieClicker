package es.ignaciofp.contador.models;

import androidx.annotation.NonNull;

public class Option {

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private String tag;

    public Option(@NonNull String name, @NonNull String description, @NonNull String tag) {
        this.name = name;
        this.description = description;
        this.tag = tag;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }
}