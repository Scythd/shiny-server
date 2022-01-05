/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

/**
 *
 * @author Пользователь
 */
public enum GameState {
    STARTING("starting"), RUNNING("running"), STOPPED("stopped"), ENDED("ended");

    String state;

    private GameState(String state) {
        this.state = state.toLowerCase();
    }

    public static GameState getByState(String state) {
        switch (state) {
            case "starting" -> {
                return GameState.STARTING;
            }
            case "running" -> {
                return GameState.RUNNING;
            }
            case "stopped" -> {
                return GameState.STOPPED;
            }
            case "ended" -> {
                return GameState.ENDED;
            }

        }
        throw new RuntimeException("Game state not found" + state);
    }

    public String getState() {
        return state;
    }

}
