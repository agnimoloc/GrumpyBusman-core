package com.churpi.games.grumpybusman.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.churpi.games.grumpybusman.utils.Path;

public class Vehicle extends Actor {
	private int currentNode = 0;
	private int currentPath = 0;	
	private Path path;
	private Vector2 vector1 = new Vector2(), vector2 = new Vector2();
	private float force = 30000;
	private short velNumber = 0;
	Vector2 velocity = new Vector2(0, 0); 
	private short maxVel = 5;
	TextureRegion texture;
	
	private Body tracker, body;
	
	ShapeRenderer renderer = new ShapeRenderer();
	
	public Vehicle(World world, Path path,TextureRegion texture, float width, float height){
		this.path = path;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(path.getStart());
		bodyDef.linearDamping = 4;
		bodyDef.angularDamping = 20;
		bodyDef.type = BodyType.DynamicBody;
		
		CircleShape trackerShape = new CircleShape();
		trackerShape.setRadius(width /2);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = trackerShape;
		fixtureDef.friction = .3f;
		fixtureDef.restitution = .3f;
		fixtureDef.density = 20;
		
		tracker = world.createBody(bodyDef);
		tracker.createFixture(fixtureDef);
		
		trackerShape.dispose();
		
		bodyDef.position.y = 0;
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(width / 2, height /2);
		
		fixtureDef.shape = box;
		
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = tracker;
		jointDef.bodyB = body;
		jointDef.localAnchorA.set(tracker.getLocalCenter());
		jointDef.localAnchorB.set(0, width/2);
		
		world.createJoint(jointDef);
				
		this.texture = texture;
		this.setSize(width, height);
		this.setOrigin(width/2 , height/2);
		
	}
	
	private void seek(Vector2 target,Vector2 roadPoint){//, float delta){
		vector1.set(target);
		vector1.sub(tracker.getPosition()).nor().scl(force * velNumber);
		/*vector2.set(roadPoint);
		vector2.sub(tracker.getPosition()).nor().scl((force * velNumber)/3);*/
		vector1.add(vector2);
		
		tracker.applyForce(vector1, tracker.getWorldPoint(vector2.set(0, 6)), true);
	}
	
	public void turnLeft(){
		path.changeToLeftPath(this);
	}
	public void turnRight(){
		
		path.changeToRightPath(this);
	}
	
	@Override
	public void act(float delta) {		
		seek(path.getCurrentPoint(this),path.getRoadSeekPoint(this));
		setPosition(body.getPosition().x, body.getPosition().y);
		setRotation(body.getAngle()* MathUtils.radDeg);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {		
		batch.setColor(Color.WHITE);
		
		batch.draw(texture, getX()-getOriginX(), getY() - getOriginY(), getOriginX(), getOriginY(),
				getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		
	}

	public int getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(int currentNode) {
		this.currentNode = currentNode;
	}

	public int getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(int currentPath) {
		this.currentPath = currentPath;
	}

	public void increaseVelocity() {
		if(++velNumber > maxVel) velNumber = maxVel;
	}
	public void decreaseVelocity() {
		if(--velNumber < 0) velNumber = 0;		
	}
	
	public short getVelocity() {
		return velNumber;
	}
	
	/*public Vector2 getPosition(){
		return new Vector2(position);
	}*/
}
