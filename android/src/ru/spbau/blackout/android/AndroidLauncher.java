package ru.spbau.blackout.android;

import android.app.AlertDialog;
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
import ru.spbau.blackout.play.services.BlackoutSnapshot;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesListener;

public class AndroidLauncher extends AndroidApplication implements PlayServices {

    private static final String TAG = "AndroidLauncher";

    private GameHelper gameHelper;
    private PlayServicesListener coreListener;
    private SnapshotManager snapshotManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GameHelperListener gameHelperListener = new FailureResistantGameHelperListener();

        gameHelper = new GameHelper(this, GameHelper.CLIENT_ALL);
        gameHelper.enableDebugLog(true);
        gameHelper.setShowErrorDialogs(false);
        gameHelper.setConnectOnStart(false);
        gameHelper.setup(gameHelperListener);

        snapshotManager = new SnapshotManager(this);

        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final BlackoutGame game = BlackoutGame.get();
        game.initializePlayServices(this);
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
            Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(achievementId));
        } else {
            showUserIsNotSignedInDialog();
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

    @Override
    public void startLoadingSnapshot() {
        try {
            runOnUiThread(() -> snapshotManager.startLoadingSnapshot());
        } catch (Exception e) {
            Gdx.app.log(TAG, "startLoadingSnapshot: " + e.getMessage());
        }

    }

    @Override
    public void saveSnapshot(BlackoutSnapshot blackoutSnapshot) {
        snapshotManager.saveSnapshot(blackoutSnapshot);
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

    private enum RequestCodes {
        REQUEST_ACHIEVEMENTS, REQUEST_LEADERBOARDS
    }

    private class FailureResistantGameHelperListener implements GameHelperListener {
        @Override
        public void onSignInFailed() {
            final GameHelper.SignInFailureReason reason = gameHelper.getSignInError();
            if (reason == null) {
                gameHelper.beginUserInitiatedSignIn();
                return;
            }
            Log.v(TAG, reason.toString());

            final int resultCode = reason.getActivityResultCode();
            final String text;

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

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setMessage(text);
            builder.setNeutralButton(R.string.try_again, (dialogInterface, i) -> signIn());
            builder.create().show();
        }

        @Override
        public void onSignInSucceeded() {
            coreListener.onSignInSucceeded();
        }
    }
}
