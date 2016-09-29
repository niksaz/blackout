package ru.spbau.blackout.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.play.services.PlayServices;

public class AndroidLauncher extends AndroidApplication implements PlayServices {

    private final static String TAG = "AndroidLauncher";

    private GameHelper gameHelper;
    private final static int requestCode = 918273645;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new BlackoutGame(this), config);

        GameHelperListener gameHelperListener = new GameHelperListener() {
            @Override
            public void onSignInFailed() {
                Gdx.app.log(TAG, "FAIL");
            }

            @Override
            public void onSignInSucceeded() {
                Gdx.app.log(TAG, "SUCCESS");
            }
        };

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);
        gameHelper.setup(gameHelperListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
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
            Gdx.app.log(TAG, "Log in failed: " + e.getMessage() + ".");
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
            Gdx.app.log(TAG, "Log out failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void rateGame() {
    }

    @Override
    public void unlockAchievement() {
    }

    @Override
    public void submitScore(int highScore) {
    }

    @Override
    public void showAchievement() {
        if (isSignedIn()) {
        }
    }

    @Override
    public void showScore() {

    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }

    @Override
    public String getPlayerName() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "permissions!";
        }
        return Games.getCurrentAccountName(gameHelper.getApiClient());
    }

}
