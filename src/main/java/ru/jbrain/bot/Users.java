package ru.jbrain.bot;

import ru.jbrain.domain.ProfileData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {
    private static final Map<Long, String> usersLogged = new HashMap<>();
    private static final Map<Long, ProfileData> lastProfile = new HashMap<>();
    private static final Map<Long, List<ProfileData>> lastMatchs = new HashMap<>();

    public static Map<Long, String> getUsersLogged() {
        return usersLogged;
    }

    public static Map<Long, ProfileData> getLastProfile() {
        return lastProfile;
    }

    public static Map<Long, List<ProfileData>> getLastMatchs() {
        return lastMatchs;
    }
}
