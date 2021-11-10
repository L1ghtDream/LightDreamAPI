package dev.lightdream.api.databases;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.DatabaseField;

public abstract class DatabaseEntry {

    @com.j256.ormlite.field.DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    @dev.lightdream.api.annotations.DatabaseField(columnName = "id", autoGenerate = true)
    public int id;
    private IAPI api;

    public DatabaseEntry(IAPI api) {
        this.api = api;
    }

    public void save() {
        save(true);
    }

    public void save(boolean cache){
        api.getDatabaseManager().save(this, cache);
    }

    public void delete() {
        api.getDatabaseManager().delete(this);
    }

    public void setAPI(IAPI api) {
        this.api = api;
    }

}
