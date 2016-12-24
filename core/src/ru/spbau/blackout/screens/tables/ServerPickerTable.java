package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.screens.MenuScreen;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

public class ServerPickerTable  {

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable);
        for (Network.RoomServerDescription description : Network.ROOM_SERVERS) {
            addButton(
                    middleTable, "Battle between " + description.getPlayersToStart() + " players",
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            screen.changeMiddleTable(MultiplayerTable.getTable(screen, description.getPort()));
                        }
                    }
            );
        }
        addButton(middleTable, MenuScreen.BACK_TEXT, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.changeMiddleTable(PlayScreenTable.getTable(screen));
            }
        });

        return middleTable;
    }
}
