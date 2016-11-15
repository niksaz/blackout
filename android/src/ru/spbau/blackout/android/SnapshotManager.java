package ru.spbau.blackout.android;

import android.app.AlertDialog;
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

    private static final String TAG = "SnapshotManager";

    private static final int MAX_ATTEMPTS = 5;
    private static final int CONFLICT_RESOLUTION_POLICY = RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

    private static final long AWAIT_TIME_MS = 10000;

    private final AndroidLauncher launcher;

    SnapshotManager(AndroidLauncher launcher) {
        this.launcher = launcher;
    }

    void startLoadingSnapshot() {
        new LoadingAsyncTask().execute();
    }

    void saveSnapshot(BlackoutSnapshot blackoutSnapshot) {
        new SavingTask(blackoutSnapshot).execute();
    }

    /**
     *  Must sign in before initiating this task.
     */
    private class LoadingAsyncTask extends AsyncTask<Void, Void, LoadingAsyncTaskResult> {
        @Override
        protected LoadingAsyncTaskResult doInBackground(Void... voids) {
            BlackoutSnapshot blackoutSnapshot = null;
            String resultMessage = launcher.getString(R.string.default_load_message);

            retry_loop:
            for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {

                if (!launcher.getGameHelper().getApiClient().isConnected()) {
                    break;
                }

                Snapshots.OpenSnapshotResult result = Games.Snapshots
                    .open(launcher.getGameHelper().getApiClient(),
                          launcher.getString(R.string.game_saved_name), true,
                          CONFLICT_RESOLUTION_POLICY)
                    .await(AWAIT_TIME_MS, TimeUnit.MILLISECONDS);


                Log.v(TAG, "LoadingAsyncTask: " + result.getStatus() + " " + result.getStatus());
                switch (result.getStatus().getStatusCode()) {
                    case GamesStatusCodes.STATUS_OK:
                        final Snapshot snapshot = result.getSnapshot();
                        try {
                            byte[] gameData = snapshot.getSnapshotContents().readFully();
                            if (gameData == null) {
                                blackoutSnapshot = new BlackoutSnapshot();
                                break retry_loop;
                            }
                            ByteArrayInputStream byteStream = new ByteArrayInputStream(gameData);
                            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
                            blackoutSnapshot = (BlackoutSnapshot) objectStream.readObject();
                            objectStream.close();
                            if (blackoutSnapshot == null) {
                                blackoutSnapshot = new BlackoutSnapshot();
                            }
                        } catch (ClassNotFoundException | IOException e) {
                            launcher.log(TAG, "Exception while loading snapshot " + e.getMessage());
                            blackoutSnapshot = new BlackoutSnapshot();
                        }
                        break retry_loop;

                    case GamesStatusCodes.STATUS_SNAPSHOT_CREATION_FAILED:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT:
                    case GamesStatusCodes.STATUS_INTERRUPTED:
                    case GamesStatusCodes.STATUS_TIMEOUT:
                        break;

                    case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                        launcher.getGameHelper().reconnectClient();
                        break;

                    case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                        resultMessage =
                                launcher.getString(R.string.status_network_error_no_data_message);
                        break retry_loop;

                    case GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED:
                        resultMessage =
                                launcher.getString(R.string.status_license_check_failed_message);
                        break retry_loop;

                    case GamesStatusCodes.STATUS_SNAPSHOT_NOT_FOUND:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_FOLDER_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT_MISSING:
                    case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                    default:
                        break retry_loop;
                }
            }

            return new LoadingAsyncTaskResult(blackoutSnapshot, resultMessage);
        }

        @Override
        protected void onPostExecute(LoadingAsyncTaskResult result) {
            if (result.getSnapshot() != null) {
                launcher.getCoreListener().finishedLoadingSnapshot(result.getSnapshot());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(launcher.getContext());
                builder.setCancelable(false);
                builder.setMessage(result.getMessage());
                builder.setNeutralButton(R.string.try_again,
                                         (dialogInterface, i) -> new LoadingAsyncTask().execute());
                builder.create().show();
            }
        }
    }

    private static class LoadingAsyncTaskResult {
        BlackoutSnapshot snapshot;
        String message;

        LoadingAsyncTaskResult(BlackoutSnapshot snapshot, String message) {
            this.snapshot = snapshot;
            this.message = message;
        }

        BlackoutSnapshot getSnapshot() {
            return snapshot;
        }

        String getMessage() {
            return message;
        }
    }

    /**
     *  Must sign in before initiating this task.
     */
    private class SavingTask extends AsyncTask<Void, Void, Void> {

        private final BlackoutSnapshot blackoutSnapshot;

        SavingTask(BlackoutSnapshot blackoutSnapshot) {
            this.blackoutSnapshot = blackoutSnapshot;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            retry_loop:
            for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
                if (!launcher.getGameHelper().getApiClient().isConnected()) {
                    break;
                }

                Snapshots.OpenSnapshotResult result = Games.Snapshots
                        .open(launcher.getGameHelper().getApiClient(),
                              launcher.getString(R.string.game_saved_name), true,
                              CONFLICT_RESOLUTION_POLICY)
                        .await(AWAIT_TIME_MS, TimeUnit.MILLISECONDS);

                Log.v(TAG, "SavingTask: " + result.getStatus().toString());
                switch (result.getStatus().getStatusCode()) {
                    case GamesStatusCodes.STATUS_OK:
                        final Snapshot snapshot = result.getSnapshot();
                        try {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(out);
                            oos.writeObject(blackoutSnapshot);
                            oos.close();

                            snapshot.getSnapshotContents().writeBytes(out.toByteArray());
                            SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder().build();

                            if (!launcher.getGameHelper().getApiClient().isConnected()) {
                                break;
                            }
                            Games.Snapshots.commitAndClose(launcher.getGameHelper().getApiClient(), snapshot, metadataChange);
                        } catch (IOException e) {
                            launcher.log(TAG, "Exception while saving snapshot " + e.getMessage());
                            break;
                        }
                        break retry_loop;

                    case GamesStatusCodes.STATUS_SNAPSHOT_CREATION_FAILED:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT:
                    case GamesStatusCodes.STATUS_TIMEOUT:
                    case GamesStatusCodes.STATUS_INTERRUPTED:
                        break;

                    case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                        launcher.getGameHelper().reconnectClient();
                        break;

                    case GamesStatusCodes.STATUS_SNAPSHOT_NOT_FOUND:
                    case GamesStatusCodes.STATUS_NETWORK_ERROR_NO_DATA:
                    case GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_FOLDER_UNAVAILABLE:
                    case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT_MISSING:
                    case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                    default:
                        break retry_loop;
                }
            }
            return null;
        }
    }

}
