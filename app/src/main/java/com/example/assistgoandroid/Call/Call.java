package com.example.assistgoandroid.Call;

// Demonstration of Abstract Factory, creational design pattern https://refactoring.guru/design-patterns/abstract-factory

public interface Call {

    void turnOnSpeaker();
    void turnOffSpeaker();
    void mute();
    void unmute();
    void hangup();
}
