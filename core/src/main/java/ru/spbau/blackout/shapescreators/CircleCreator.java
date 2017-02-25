package ru.spbau.blackout.shapescreators;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.utils.Creator;

public class CircleCreator implements Creator<Shape> {

    public float radius;

    public CircleCreator(float radius) {
        this.radius = radius;
    }

    @Override
    public Shape create() {
        final Shape shape = new CircleShape();
        shape.setRadius(this.radius);
        return shape;
    }
}
