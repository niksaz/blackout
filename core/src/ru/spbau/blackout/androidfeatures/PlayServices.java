package ru.spbau.blackout.androidfeatures;

public interface PlayServices {
    void signIn();
    void signOut();
    void unlockAchievement(int achievementId);
    void incrementAchievement(int achievementId, int steps);
    void submitScore(long highScore, int leaderboardId);
    void showAchievements();
    void showLeaderboards();
    boolean isSignedIn();
    String getPlayerName();

    int getDuelistAchievementID();
    int getBattleOfThreeAchievementID();
    int getStrategistAchievementID();
    int getFirstUpgradesAchievementID();
    int getMinterAchievementID();
    int getNumberOfCoinsForMinterAchievement();

    int getCoinsEarnedLeaderboardID();

    void setCoreListener(PlayServicesListener coreListener);
}
