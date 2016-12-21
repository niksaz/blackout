package ru.spbau.blackout.worlds;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.entities.GameObject;


/**
 * GameWorld which is used on client side of multi-player game. It receives a serialized stepNumber of the world from
 * the server and updates its own.
 */
public class ClientGameWorld extends GameWorld {

    private final AtomicReference<ObjectInputStream> externalWorldStream = new AtomicReference<>();

    public ClientGameWorld(List<GameObject.Definition> definitions) {
        super(definitions);
    }


    public Object setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        getGameObjects().get(0).setState(in);

        /*long newStepNumber = in.readLong();

        // this GameWorld is outdated
        if (newStepNumber > stepNumber) {
            Gdx.app.log("WTF", "step number = " + newStepNumber);
            stepNumber = newStepNumber;

            class ExistIterator {
                final ListIterator<GameObject> it;
                boolean endOfStream = false;
                // null means the end of the array
                GameObject go;

                ExistIterator() {
                    it = getGameObjects().listIterator();
                    step();
                }

                void step() {
                    if (it.hasNext()) {
                        go = it.next();
                    } else {
                        endOfStream = true;
                    }
                }
            }

            ExistIterator exist = new ExistIterator();

            class InputIterator {
                int length;
                boolean endOfStream = false;
                long uid = 0;
                int defNumber;

                InputIterator(ObjectInputStream in) throws IOException {
                    length = in.readInt();
                    step(in);
                }

                void step(ObjectInputStream in) throws IOException {
                    if (length == 0) {
                        endOfStream = true;
                    } else {
                        length -= 1;
                        uid = in.readLong();
                        defNumber = in.readInt();
                    }
                }
            }

            InputIterator input = new InputIterator(in);

            while (!exist.endOfStream || !input.endOfStream) {
                if (exist.endOfStream || (!input.endOfStream && exist.go.getUid() > input.uid)) {
                    GameObject newObj = getDefinitions().get(input.defNumber).makeInstance(input.uid);
                    exist.it.add(newObj);
                    newObj.setState(in);
                    input.step(in);
                } else if (input.endOfStream || exist.go.getUid() < input.uid) {
                    exist.go.kill();
                    exist.it.remove();
                    exist.step();
                } else {
                    assert !exist.endOfStream && !input.endOfStream && exist.go.getUid() == input.uid;
                    Gdx.app.log("WTF", "begin update state for unit " + exist.go.getUid());
                    exist.go.setState(in);
                    Gdx.app.log("WTF", "end update state for unit " + exist.go.getUid());
                    exist.step();
                    input.step(in);
                }
            }
        }*/

        return null;
    }

    @Override
    public void doneLoading() {
        super.doneLoading();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (externalWorldStream.get() != null) {
            try {
                setState(externalWorldStream.getAndSet(null));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setExternalWorldStream(ObjectInputStream externalWorldStream) {
        this.externalWorldStream.set(externalWorldStream);
    }
}
