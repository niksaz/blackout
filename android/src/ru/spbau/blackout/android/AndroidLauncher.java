package ru.spbau.blackout.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.play.services.BlackoutSnapshot;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesListener;

public class AndroidLauncher extends AndroidApplication implements PlayServices {

    private static final String TAG = "AndroidLauncher";
    private static final String NOT_SIGNED_MESSAGE = "Unsuccessful. You is not signed in to Google Play Games Services.";

    private static final int REQUEST_ACHIEVEMENTS = 918273645;
    private static final int REQUEST_LEADERBOARDS = 918273644;

    private BlackoutGame game;
    private GameHelper gameHelper;
    private PlayServicesListener coreListener;
    private boolean foreground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameHelperListener gameHelperListener = new GameHelperListener() {
            @Override
            public void onSignInFailed() {
                coreListener.onSignInFailed();
            }

            @Override
            public void onSignInSucceeded() {
                coreListener.onSignInSucceeded();
            }
        };

        gameHelper = new GameHelper(this, GameHelper.CLIENT_ALL);
        gameHelper.enableDebugLog(true);
        gameHelper.setConnectOnStart(false);
        gameHelper.setup(gameHelperListener);

        SnapshotManager.getInstance().initialize(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        game = new BlackoutGame(this);
        initialize(game, config);
    }

    public GameHelper getGameHelper() {
        return gameHelper;
    }

    public PlayServicesListener getCoreListener() {
        return coreListener;
    }

    public void setCoreListener(PlayServicesListener coreListener) {
        this.coreListener = coreListener;
    }

    @Override
    protected void onStart() {
        foreground = true;
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        foreground = false;
        super.onStop();
        writeSnapshot(game.getSnapshot());
    }

    public boolean isForeground() {
        return foreground;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        } catch (Exception e) {
            Gdx.app.log(TAG, "Log in failed: " + e.getMessage());
        }
    }

    @Override
    public void signOut() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.signOut();
                }
            });
        } catch (Exception e) {
            Gdx.app.log(TAG, "Log out failed: " + e.getMessage());
        }
    }

    @Override
    public void unlockAchievement(int achievementId) {
        if (isSignedIn()) {
            Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(achievementId));
        } else {
            userIsNotSignedInDialog();
        }
    }

    @Override
    public void submitScore(long highScore, int leaderboardId) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                                           getResources().getString(leaderboardId), highScore);
        }
    }

    @Override
    public void showAchievements() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()),
                                   REQUEST_ACHIEVEMENTS);
        } else {
            userIsNotSignedInDialog();
        }
    }

    @Override
    public void showLeaderboards() {
        if (isSignedIn()) {startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()),
                                                  REQUEST_LEADERBOARDS);
        } else {
            userIsNotSignedInDialog();
        }
    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }

    @Override
    public String getPlayerName() {
        return Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getDisplayName();
    }

    @Override
    public void startLoadingSnapshot() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SnapshotManager.getInstance().startLoadingSnapshot();
                }
            });
        } catch (Exception e) {
            Gdx.app.log(TAG, "startLoadingSnapshot: " + e.getMessage());
        }

    }

    public void writeSnapshot(final BlackoutSnapshot blackoutSnapshot) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SnapshotManager.getInstance().writeSnapshot(blackoutSnapshot);
                }
            });
        } catch (Exception e) {
            Gdx.app.log(TAG, "writeSnapshot: " + e.getMessage());
        }
    }

    private void userIsNotSignedInDialog() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.makeSimpleDialog(NOT_SIGNED_MESSAGE).show();
                }
            });
        } catch (Exception e) {
            Gdx.app.log(TAG, "userIsNotSignedInDialog: " + e.getMessage());
        }
    }

    @Override
    public int getWin1vs1DuelId() {
        return R.string.achievement_win_1vs1_duel;
    }

    @Override
    public int getWin2vs2FightId() {
        return R.string.achievement_win_2vs2_fight;
    }

    @Override
    public int getWin3vs3Battle() {
        return R.string.achievement_win_3vs3_battle;
    }

    @Override
    public int getEarn1000coins() {
        return R.string.achievement_earn_1000_coins;
    }

    @Override
    public int getBuyYourFirstItemId() {
        return R.string.achievement_buy_your_first_item;
    }

    @Override
    public int getCoinsEarnedLeaderboardId() {
        return R.string.leaderboard_coins_earned;
    }

    @Override
    public int getHighestRatingLeaderboardId() {
        return R.string.leaderboard_highest_rating;
    }

}
