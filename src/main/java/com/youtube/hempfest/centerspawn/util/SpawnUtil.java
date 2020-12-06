package com.youtube.hempfest.centerspawn.util;

import java.util.HashMap;
import java.util.UUID;

public class SpawnUtil {

    SpawnManager manager;

    public SpawnUtil(SpawnManager manager) {
        this.manager = manager;
    }

    // store methods

    public static HashMap<UUID, Boolean> spawnProtected = new HashMap<>();

    public static HashMap<UUID, Boolean> pastProtect = new HashMap<>();


}
