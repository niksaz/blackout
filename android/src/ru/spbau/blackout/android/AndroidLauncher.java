package ru.spbau.blackout.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.androidfeatures.PlayServices;
import ru.spbau.blackout.androidfeatures.PlayServicesListener;

public class AndroidLauncher extends AndroidApplication implements PlayServices {

    private static final String TAG = "AndroidLauncher";

    private GameHelper gameHelper;
    private PlayServicesListener coreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GameHelperListener gameHelperListener = new FailureResistantGameHelperListener();

        gameHelper = new GameHelper(this, GameHelper.CLIENT_ALL);
        gameHelper.enableDebugLog(true);
        gameHelper.setShowErrorDialogs(false);
        gameHelper.setConnectOnStart(false);
        gameHelper.setup(gameHelperListener);

        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final BlackoutGame game = BlackoutGame.get();
        game.initializePlayServices(this);
        initialize(game, config);
    }

    public void setCoreListener(PlayServicesListener coreListener) {
        this.coreListener = coreListener;
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn() {
        try {
            runOnUiThread(() -> gameHelper.beginUserInitiatedSignIn());
        } catch (Exception e) {
            Gdx.app.log(TAG, "Log in failed: " + e.getMessage());
        }
    }

    @Override
    public void signOut() {
        try {
            runOnUiThread(() -> gameHelper.signOut());
        } catch (Exception e) {
            Gdx.app.log(TAG, "Log out failed: " + e.getMessage());
        }
    }

    @Override
    public void unlockAchievement(int achievementId) {
        if (isSignedIn()) {
            Games.Achievements.unlock(gameHelper.getApiClient(), getString(achievementId));
        } else {
            showUserIsNotSignedInDialog();
        }
    }

    @Override
    public void incrementAchievement(int achievementId, int steps) {
        if (isSignedIn()) {
            Games.Achievements.increment(gameHelper.getApiClient(), getString(achievementId), steps);
        } else {
            showUserIsNotSignedInDialog();
        }
    }

    @Override
    public void submitScore(long highScore, int leaderboardId) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(leaderboardId), highScore);
        } else {
            showUserIsNotSignedInDialog();
        }
    }

    @Override
    public void showAchievements() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()),
                                   RequestCodes.REQUEST_ACHIEVEMENTS.ordinal());
        } else {
            showUserIsNotSignedInDialog();
        }
    }

    @Override
    public void showLeaderboards() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()),
                                   RequestCodes.REQUEST_LEADERBOARDS.ordinal());
        } else {
            showUserIsNotSignedInDialog();
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

    private void showUserIsNotSignedInDialog() {
        try {
            runOnUiThread(() ->
                    gameHelper.makeSimpleDialog(getString(R.string.not_signed_message)).show());
        } catch (Exception e) {
            Gdx.app.log(TAG, "showUserIsNotSignedInDialog: " + e.getMessage());
        }
    }

    @Override
    public int getDuelistAchievementID() {
        return R.string.achievement1;
    }

    @Override
    public int getBattleOfThreeAchievementID() {
        return R.string.achievement2;
    }

    @Override
    public int getStrategistAchievementID() {
        return R.string.achievement3;
    }

    @Override
    public int getFirstUpgradesAchievementID() {
        return R.string.achievement4;
    }

    @Override
    public int getMinterAchievementID() {
        return R.string.achievement5;
    }

    @Override
    public int getNumberOfCoinsForMinterAchievement() {
        return getResources().getInteger(R.integer.value_for_achievement_4);
    }

    @Override
    public int getCoinsEarnedLeaderboardID() {
        return R.string.leaderboard1;
    }


    private enum RequestCodes {
        REQUEST_ACHIEVEMENTS, REQUEST_LEADERBOARDS
    }

    private class FailureResistantGameHelperListener implements GameHelperListener {

        @Override
        public void onSignInFailed() {
            final GameHelper.SignInFailureReason reason = gameHelper.getSignInError();
            final String text;
            if (reason == null) {
                text = getString(R.string.result_sign_in_failed_ms);
            } else {
                Log.v(TAG, reason.toString());

                final int resultCode = reason.getActivityResultCode();
                switch (resultCode) {
                    case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED:
                        text = getString(R.string.result_app_misconfigured_ms);
                        break;

                    case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED:
                        text = getString(R.string.result_sign_in_failed_ms);
                        break;

                    case GamesActivityResultCodes.RESULT_LICENSE_FAILED:
                        text = getString(R.string.result_license_failed_ms);
                        break;

                    default:
                        text = getString(R.string.result_default_error);
                        break;
                }
            }
            coreListener.onSignInFailed(text);
        }

        @Override
        public void onSignInSucceeded() {
            coreListener.onSignInSucceeded();
        }
    }
}
