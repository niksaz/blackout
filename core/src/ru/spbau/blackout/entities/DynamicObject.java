package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.Physics;
import ru.spbau.blackout.utils.Utils;

public abstract class DynamicObject extends GameObject {
    public static class Animations {
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";
        public static final float WALK_SPEED_FACTOR = 3f;
    }

    // Movement:
    final private Vector2 velocity = new Vector2();
    final private Vector2 selfVelocity = new Vector2();
    private float speed;

    // Appearance:
    protected final AnimationController animation;
    private float animationSpeed = 1f;

    protected DynamicObject(Definition def, Model model, Physics physics) {
        super(def, model, physics);
        speed = def.speed;
        animation = new AnimationController(this.model);
        animation.setAnimation(Animations.STAY, -1);

    }

    public final Vector2 getSelfVelocity() {
        return selfVelocity;
    }

    public void setSelfVelocity(final Vector2 vel) {
        // avoid excessive allocation
        // Vector2 old = new Vector2(selfVelocity);
        final float oldX = selfVelocity.x;
        final float oldY = selfVelocity.y;

        selfVelocity.set(vel.x, vel.y);
        if (Utils.isZeroVec(selfVelocity)) {
            // on stop walking
            if (!Utils.isZeroVec(oldX, oldY)) {
                animation.setAnimation(Animations.STAY, -1);
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(oldX, oldY)) {
                animation.setAnimation(Animations.WALK, -1);
            }

            animationSpeed = selfVelocity.len() * Animations.WALK_SPEED_FACTOR;
            setDirection(selfVelocity);
        }
    }

    public final Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public void addVelocity(Vector2 velocity) {
        this.velocity.x += velocity.x;
        this.velocity.y += velocity.y;
    }

    @Override
    public void update(float delta) {
        super.update(delta);


        float newX = getPosition().x + (getVelocity().x + getSelfVelocity().x * speed) * delta;
        float newY = getPosition().y + (getVelocity().y + getSelfVelocity().y * speed) * delta;
        setPosition(newX, newY);

        animation.update(delta * animationSpeed);
    }

    public static abstract class Definition extends GameObject.Definition {
        public static final float DEFAULT_SPEED = 10f;

        // Movement:
        private float speed = DEFAULT_SPEED;

        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
