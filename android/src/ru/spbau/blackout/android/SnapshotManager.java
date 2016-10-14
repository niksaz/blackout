package ru.spbau.blackout.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

import ru.spbau.blackout.play.services.BlackoutSnapshot;

import static com.google.android.gms.games.snapshot.Snapshots.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

class SnapshotManager {

    private static SnapshotManager manager;

    private static final String TAG = "SnapshotManager";
    private static final String GAME_SAVED_NAME = "GAME_SAVED_NAME";
    private static final String DEFAULT_MESSAGE = "Could not connect to the server.";
    private static final String STATUS_NETWORK_ERROR_NO_DATA_MESSAGE
            = "Network error while loading game info. Check your internet connection and try again.";
    private static final String STATUS_LICENSE_CHECK_FAILED_MESSAGE
            = "You do not have license for this game.";

    private static final int MAX_ATTEMPTS = 3;
    private static final int CONFLICT_RESOLUTION_POLICY = RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

    private static final long AWAIT_TIME_MS = 5000;

    private AndroidLauncher launcher;
    private Snapshot snapshot;
    private BlackoutSnapshot blackoutSnapshot;
    private String resultMessage;

    private SnapshotManager() {}

    static SnapshotManager getInstance() {
        if (manager == null) {
            manager = new SnapshotManager();
        }
        return manager;
    }

    void initialize(AndroidLauncher launcher) {
        this.launcher = launcher;
    }

    void startLoadingSnapshot() {
        new LoadingAsyncTask().execute();
    }

    void saveSnapshot(BlackoutSnapshot blackoutSnapshot) {
        new SavingTask().execute(blackoutSnapshot);
    }

    /**
     *  Must sign in before initiating this task.
     */
    private class LoadingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            blackoutSnapshot = null;
            resultMessage = DEFAULT_MESSAGE;
            boolean shouldTry = true;

            for (int attempts = 0; blackoutSnapshot == null && shouldTry && attempts < MAX_ATTEMPTS; attempts++) {
                shouldTry = false;

                if (!launcher.getGameHelper().getApiClient().isConnected()) {
                    break;
                }

                Snapshots.OpenSnapshotResult result = Games.Snapshots
                    .open(launcher.getGameHelper().getApiClient(), GAME_SAVED_NAME, true,
                            CONFLICT_RESOLUTION_POLICY)
                    .await(AWAIT_TIME_MS, TimeUnit.MILLISECONDS);


                Log.v(TAG, "LoadingAsyncTask: " + result.getStatus().toString());
                switch (result.getStatus().getStatusCode()) {
                    case GamesStatusCodes.STATUS_OK:
                        snapshot = result.getSnapshot();
                        try {
                            byte[] gameData = snapshot.getSnapshotContents().readFully();
                            if (gameData == null) {
                                gameData = new byte[0];
                            }
                            ByteArrayInputStream byteStream = new ByteArrayInputStream(gameData);
                            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
                            blackoutSnapshot = (BlackoutSnapshot) objectStream.readObject();
                            objectStream.close();
                        } catch (ClassNotFoundException | IOException e) {
                            blackoutSnapshot = new BlackoutSnapshot();
                        }
                        break;

                    case GamesStatusCodes.STATUS_SNAPSHOT_NOT_FOUND:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CREATION_FAILED:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT:
                    case GamesStatusCodes.STATUS_INTERRUPTED:
                        shouldTry = true;
                        break;

                    case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                        resultMessage = STATUS_NETWORK_ERROR_NO_DATA_MESSAGE;
                        break;

                    case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                        launcher.getGameHelper().reconnectClient();
                        shouldTry = true;
                        break;

                    case GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED:
                        resultMessage = STATUS_LICENSE_CHECK_FAILED_MESSAGE;
                        break;

                    case GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_FOLDER_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT_MISSING:
                    case GamesStatusCodes.STATUS_TIMEOUT:
                    case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                    default:
                        break;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (blackoutSnapshot != null) {
                launcher.getCoreListener().finishedLoadingSnapshot(blackoutSnapshot);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(launcher.getContext());
                builder.setCancelable(false);
                builder.setMessage(resultMessage);
                builder.setNeutralButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new LoadingAsyncTask().execute();
                    }
                });
                builder.create().show();
            }
        }
    }

    /**
     *  Must sign in before initiating this task.
     */
    private class SavingTask extends AsyncTask<BlackoutSnapshot, Void, Void> {
        @Override
        protected Void doInBackground(BlackoutSnapshot... blackoutSnapshots) {
            boolean shouldTry = true;

            for (int attempts = 0; shouldTry && attempts < MAX_ATTEMPTS; attempts++) {
                shouldTry = false;

                if (!launcher.getGameHelper().getApiClient().isConnected()) {
                    break;
                }

                Snapshots.OpenSnapshotResult result = Games.Snapshots
                        .open(launcher.getGameHelper().getApiClient(), GAME_SAVED_NAME, true,
                                CONFLICT_RESOLUTION_POLICY)
                        .await(AWAIT_TIME_MS, TimeUnit.MILLISECONDS);

                Log.v(TAG, "SavingTask: " + result.getStatus().toString());
                switch (result.getStatus().getStatusCode()) {
                    case GamesStatusCodes.STATUS_OK:
                        snapshot = result.getSnapshot();
                        try {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(out);
                            oos.writeObject(blackoutSnapshots[0]);
                            oos.close();

                            snapshot.getSnapshotContents().writeBytes(out.toByteArray());
                            SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder().build();
                            Games.Snapshots.commitAndClose(launcher.getGameHelper().getApiClient(), snapshot, metadataChange);
                        } catch (IOException e) {
                            shouldTry = true;
                        }
                        break;

                    case GamesStatusCodes.STATUS_SNAPSHOT_NOT_FOUND:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CREATION_FAILED:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT:
                    case GamesStatusCodes.STATUS_INTERRUPTED:
                        shouldTry = true;
                        break;

                    case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                        launcher.getGameHelper().reconnectClient();
                        shouldTry = true;
                        break;

                    case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                    case GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_FOLDER_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT_MISSING:
                    case GamesStatusCodes.STATUS_TIMEOUT:
                    case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                    default:
                        break;
                }
            }
            return null;
        }
    }

}
