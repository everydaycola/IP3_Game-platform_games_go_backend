package be.kdg.gobackend.domain.repository;

import be.kdg.gobackend.domain.achievements.Achievement;

import java.util.UUID;

public interface AchievementPublisher {
  void unlock(UUID playerId, Achievement achievement);
}