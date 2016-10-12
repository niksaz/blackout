package ru.spbau.blackout.android;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.play.services.BlackoutSnapshot;

class SnapshotManager {
    private static SnapshotManager manager;

    private static final String TAG = "SnapshotManager";
    private static final String GAME_SAVED_NAME = "GAME_SAVED_NAME";
    private static final String CANNOT_LOAD_SNAPSHOT = "Can not load snapshot";

    private AndroidLauncher launcher;
    private Snapshot snapshot;

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
        if (!launcher.isSignedIn()) {
            launcher.getGameHelper().makeSimpleDialog(CANNOT_LOAD_SNAPSHOT);
            launcher.getCoreListener().finishedLoadingSnapshot(null);
            return;
        }

        AsyncTask<Void, Void, BlackoutSnapshot> task = new AsyncTask<Void, Void, BlackoutSnapshot>() {
            @Override
            protected BlackoutSnapshot doInBackground(Void... voids) {
                BlackoutSnapshot resultSnapshot = null;
                boolean first = true;
                for (int i = 0; ; i++) {
                    boolean doAgain;
                    do {
                        doAgain = false;
                        Snapshots.OpenSnapshotResult result = Games.Snapshots
                                .open(launcher.getGameHelper().getApiClient(), GAME_SAVED_NAME, true)
                                .await();

                        if (result.getStatus().isSuccess()) {
                            snapshot = result.getSnapshot();
                            try {
                                byte[] gameData = snapshot.getSnapshotContents().readFully();
                                if (first || gameData == null || gameData.length == 0) {
                                    Log.v(TAG, "NO game data");
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    ObjectOutputStream oos = new ObjectOutputStream(out);
                                    oos.writeObject(new BlackoutSnapshot());
                                    oos.close();

                                    snapshot.getSnapshotContents().writeBytes(out.toByteArray());
                                    SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder().build();
                                    Games.Snapshots.commitAndClose(launcher.getGameHelper().getApiClient(), snapshot, metadataChange);
                                    doAgain = true;
                                    first = false;
                                } else {
                                    ByteArrayInputStream in = new ByteArrayInputStream(gameData);
                                    ObjectInputStream ois = new ObjectInputStream(in);
                                    resultSnapshot = (BlackoutSnapshot) ois.readObject();
                                    ois.close();
                                }
                            } catch (IOException e) {
                                Log.v(TAG, "IOException " + e.getMessage());
                            } catch (ClassNotFoundException e) {
                                Log.v(TAG, "ClassNotFoundException " + e.getMessage());
                            }
                        } else {
                            Log.v(TAG, result.getStatus().toString());
                            Log.v(TAG, CANNOT_LOAD_SNAPSHOT);
                        }
                    } while (doAgain);
                    if (resultSnapshot != null) {
                        break;
                    }
                }
                return resultSnapshot;
            }

            @Override
            protected void onPostExecute(BlackoutSnapshot snapshot) {
                if (snapshot == null) {
                    launcher.getGameHelper().makeSimpleDialog(CANNOT_LOAD_SNAPSHOT);
                }
                launcher.getCoreListener().finishedLoadingSnapshot(snapshot);
            }
        };

        task.execute();
    }

    void saveSnapshot(final BlackoutSnapshot blackoutSnapshot) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Snapshots.OpenSnapshotResult result = Games.Snapshots
                        .open(launcher.getGameHelper().getApiClient(), GAME_SAVED_NAME, true)
                        .await();

                if (result.getStatus().isSuccess()) {
                    snapshot = result.getSnapshot();
                    try {
                        commitAndClose(snapshot, blackoutSnapshot);
                    } catch (IOException e) {
                        Log.v(TAG, "IOException " + e.getMessage());
                    }
                } else {
                    Log.v(TAG, "Cannot close snapshot");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        };
        task.execute();
    }

    private void commitAndClose(Snapshot snapshot, BlackoutSnapshot blackoutSnapshot) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(blackoutSnapshot);
        oos.close();

        snapshot.getSnapshotContents().writeBytes(out.toByteArray());
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder().build();
        Games.Snapshots.commitAndClose(launcher.getGameHelper().getApiClient(), snapshot, metadataChange);
    }

}
