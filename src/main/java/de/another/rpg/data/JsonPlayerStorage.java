package de.another.rpg.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.another.rpg.api.component.SkillComponent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonPlayerStorage implements PlayerStorage {

    private final File dataFolder;
    private final Gson gson;

    public JsonPlayerStorage(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public SkillComponent loadPlayer(UUID playerId) {
        File file = getPlayerFile(playerId);
        if (!file.exists()) {
            return new SkillComponent();
        }

        try (FileReader reader = new FileReader(file)) {
            SkillComponent data = gson.fromJson(reader, SkillComponent.class);
            return data != null ? data : new SkillComponent();
        } catch (IOException e) {
            e.printStackTrace();
            return new SkillComponent();
        }
    }

    @Override
    public void savePlayer(UUID playerId, SkillComponent skillData) {
        File file = getPlayerFile(playerId);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(skillData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getPlayerFile(UUID playerId) {
        return new File(dataFolder, playerId.toString() + ".json");
    }
}

