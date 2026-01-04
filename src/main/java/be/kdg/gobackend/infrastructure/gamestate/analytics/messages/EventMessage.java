package be.kdg.gobackend.infrastructure.gamestate.analytics.messages;

public interface EventMessage {
    String event_type();
    String timestamp();
}
