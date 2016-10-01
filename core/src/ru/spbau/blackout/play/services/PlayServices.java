package ru.spbau.blackout.play.services;

public interface PlayServices {
    void signIn();
    void signOut();
    void rateGame();
    void unlockAchievement(int achievementId);
    void submitScore(int highScore);
    void showAchievements();
    void showLeaderboards();
    boolean isSignedIn();
    String getPlayerName();

    int getWin1vs1DuelId();
    int getWin2vs2FightId();
    int getWin3vs3Battle();
    int getEarn1000coins();
    int getBuyYourFirstItemId();
}
