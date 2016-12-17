package ru.spbau.blackout.androidfeatures;

public interface PlayServices {
    void signIn();
    void signOut();
    void unlockAchievement(int achievementId);
    void submitScore(long highScore, int leaderboardId);
    void showAchievements();
    void showLeaderboards();
    boolean isSignedIn();
    String getPlayerName();

    int getWin1vs1DuelId();
    int getWin2vs2FightId();
    int getWin3vs3Battle();
    int getEarn1000coins();
    int getBuyYourFirstItemId();

    int getCoinsEarnedLeaderboardId();
    int getHighestRatingLeaderboardId();

    void setCoreListener(PlayServicesListener coreListener);
}