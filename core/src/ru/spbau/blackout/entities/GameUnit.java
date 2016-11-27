package ru.spbau.blackout.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;

import java.io.IOException;
import java.io.ObjectInputStream;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Utils;

import static ru.spbau.blackout.utils.Utils.projectVec;
import static ru.spbau.blackout.utils.Utils.sqr;


/**
 * Unit is a dynamic object which can move by itself and cast abilities.
 */
public abstract class GameUnit extends DynamicObject {
    public static class Animations extends DynamicObject.Animations {
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";

        public static final float WALK_SPEED_FACTOR = 3f;

        protected Animations() {}
    }

    public static final float SLOW_DOWN_FACTOR = 0.02f;

    public static final float DEFAULT_LINEAR_FRICTION = 5f;
    public static final float DEFAULT_ANGULAR_FRICTION = 5f;

    // Movement:
    private Vector2 selfVelocity = new Vector2();
    private float speed;
    transient private final FrictionJoint frictionJoint;
    transient private final Ability[] abilities;


    protected GameUnit(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        this.speed = def.speed;
        this.animation.setAnimation(Animations.STAY, -1);
        this.abilities = def.abilities;

        this.frictionJoint = gameWorld.addFriction(body, DEFAULT_LINEAR_FRICTION, DEFAULT_ANGULAR_FRICTION);
    }

    @Override
    public void doneLoading(AssetManager assets) {
        super.doneLoading(assets);
        for (Ability ability : abilities) {
            ability.doneLoading(assets, this);
        }
    }

    public final Ability getAbility(int num) {
        return this.abilities[num];
    }

    /** See <code>GameWorld</code> documentation */
    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();

        float projection = projectVec(selfVelocity, velocity);
        if (projection < 0) {
            float slowDown = -projection * SLOW_DOWN_FACTOR;

//             decrease velocity
            if (velocity.len2() <= sqr(slowDown)) {
                velocity.set(0, 0);
            } else {
                velocity.set(
                        velocity.x - Math.signum(velocity.x) * slowDown,
                        velocity.y - Math.signum(velocity.y) * slowDown
                );
            }
        }

        body.setLinearVelocity(
                selfVelocity.x,
                selfVelocity.y
        );
    }

    public final Vector2 getSelfVelocity() {
        return selfVelocity;
    }

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GameUnit other = (GameUnit) super.inplaceDeserialize(in);
        //this.selfVelocity.set(other.selfVelocity);

//        this.speed = other.speed;
        return other;
    }

    public void setSelfVelocity(final Vector2 vel) {
        if (Utils.isZeroVec(vel)) {
            // on stop walking
            if (!Utils.isZeroVec(selfVelocity.x, selfVelocity.y)) {
                animation.setAnimation(Animations.STAY, -1);
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(selfVelocity.x, selfVelocity.y)) {
                animation.setAnimation(Animations.WALK, -1);
            }

            animationSpeed = vel.len() * Animations.WALK_SPEED_FACTOR;
            setDirection(vel);
        }

        selfVelocity.set(vel.x * speed, vel.y * speed);
    }

    public static abstract class Definition extends DynamicObject.Definition {
        public static final float DEFAULT_SPEED = 7f;

        public float speed = DEFAULT_SPEED;
        public Ability[] abilities;

        @Override
        public void load(AssetManager assets) {
            super.load(assets);
            for (Ability ability : abilities) {
                ability.load(assets);
            }
        }

        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY,
                          Ability[] abilities) {
            super(modelPath, shapeCreator, initialX, initialY);
            this.abilities = abilities;
        }
    }
}
