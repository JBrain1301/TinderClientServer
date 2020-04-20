package ru.jbrain.commands.client.api;

public interface Client {
    String nextRequest(Long id);

    String loginRequest(String jsonObject);

    String registerRequest(String jsonObject);

    String changeDescriptionRequest(String jsonObject);

    String mathcsRequest(Long id);

    String addMatchRequest(String jsonObject);

    String removeRequest(String jsonObject);
}
