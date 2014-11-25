package com.churpi.games.grumpybusman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.churpi.games.grumpybusman.actors.Vehicle;
import com.churpi.games.grumpybusman.common.AbstractScreen;
import com.churpi.games.grumpybusman.common.ChurpiGame;

public class TestScreen extends AbstractScreen implements InputProcessor {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	
	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private ShapeRenderer renderer;
	
	private Vector2[][] paths;
	
	private SpriteBatch batch;
		
	private final float TIME_STEP = 1/60f;
	
	private float forceY = 0, forceX = 0, acc = 10000;
	
	private int currentPath = 0, currentNode = 0;
	
	private Body bus, box;
	
	private Vector2 tmp = new Vector2(), tmp2 = new Vector2(), velocity = new Vector2();
	
	private final float TILEDMAP_FACTOR = 1f/24, DISTANCE_TO_NODE = 60f * TILEDMAP_FACTOR, DISTANCE_TO_ROAD = 10f * TILEDMAP_FACTOR;
	
	private boolean changeRoad = false;
		
	public TestScreen(ChurpiGame game) {
		super(game);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		update();
		
		world.step(TIME_STEP, 8, 3);		
		
		camera.position.set(box.getPosition().x, box.getPosition().y, 0);
		camera.update();
		
		
		
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
		renderer.identity();
		
		renderer.setColor(Color.RED);
		for(int j = 0; j < paths.length; j++){
		for(int i = 0; i< paths[j].length; i++){
			if((i+1) <  paths[j].length){
				renderer.line(paths[j][i].x, paths[j][i].y, paths[j][i+1].x, paths[j][i+1].y );
				//renderer.circle(paths[j][i].x, paths[j][i].y, DISTANCE_TO_NODE);
			}
		}		
		}
		renderer.end();
		
		debugRenderer.render(world, camera.combined);

	}
	public void update() {
		float rot = (float) (bus.getTransform().getRotation() + Math.PI / 2);
		float x = MathUtils.cos(rot);
		float y = MathUtils.sin(rot);
		
		seek(getCurrentPoint(),getRoadSeekPoint());
		/*tmp2.set(forceX, 0).scl(acc);
		tmp.set(x, y).scl(forceY).add(tmp2);
		

		//bus.applyForce(tmp.set(forceX, forceY), tmp2.set(0, 6), true);		
		bus.applyForce(tmp, 
				bus.getWorldPoint(tmp2.set(0, 6)), 
				true);*/		
	}
	
	private void seek(Vector2 target,Vector2 roadPoint){
		tmp2.set(target);
		tmp.set(bus.getPosition());
		tmp2.sub(tmp).nor().scl(forceY);
		roadPoint.sub(tmp).nor().scl(forceY/3);
		tmp2.add(roadPoint);
		bus.applyForce(tmp2, bus.getWorldPoint(tmp.set(0, 6)), true);	
		/*newVelocity.sub(velocity).scl((1/bus.getMass()));
		velocity.add(newVelocity);		
		position.add(velocity);*/
	}
	
	public Vector2 getCurrentPoint() {
		Vector2 node = paths[currentPath][currentNode];
		for(int i = 0; i < paths.length; i++ ){
			Vector2 tmpnode = paths[i][currentNode];
			if(tmpnode.dst(bus.getPosition().x, bus.getPosition().y) <= DISTANCE_TO_NODE){
				currentNode++;
				if(currentNode >= paths[currentPath].length)
					currentNode = 0;
				node = paths[currentPath][currentNode];
				return node;
			}
		}		
		return node;
	}
	public Vector2 getRoadSeekPoint() {				
		Vector2 currentPos =new Vector2(bus.getPosition().x, bus.getPosition().y);
		if(!changeRoad){
			return currentPos;
		}
		if(currentNode == 0)
			return currentPos;
		
		Vector2 nearest = new Vector2();
		Intersector.nearestSegmentPoint(
				paths[currentPath][currentNode -1].x, 
				paths[currentPath][currentNode -1].y, 
				paths[currentPath][currentNode].x, 
				paths[currentPath][currentNode].y, 
				currentPos.x, currentPos.y, nearest);
		
		if(nearest.dst(currentPos) <= DISTANCE_TO_ROAD){
			changeRoad = false;
			return currentPos;
		}
		return nearest;
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height /25;
		camera.viewportWidth = width /25;
	}

	@Override
	public void show() {
		world = new World(Vector2.Zero, true);
		
		debugRenderer = new Box2DDebugRenderer();
		
		renderer = new ShapeRenderer();
		
		camera = new OrthographicCamera();
		
		TiledMap tiledMap = new TmxMapLoader().load("data/levels/1_1.tmx");
		GetPaths(tiledMap);
		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		
		bodyDef.linearDamping = 4;
		bodyDef.angularDamping = 10;
		bodyDef.type = BodyType.DynamicBody;
		
		bodyDef.position.x = paths[currentPath][currentNode].x; 
		bodyDef.position.y = paths[currentPath][currentNode].y;
		/*PolygonShape busShape = new PolygonShape();
		busShape.setAsBox(3, 6);*/
		CircleShape busShape = new CircleShape();
		busShape.setRadius(1.5f);
		busShape.setPosition(tmp.set(0,1.5f));
		
		fixtureDef.density = 20;
		fixtureDef.friction = .9f;
		fixtureDef.restitution = .1f;
		fixtureDef.shape = busShape;
		
		bus =  world.createBody(bodyDef);
		bus.createFixture(fixtureDef);
		
		busShape.dispose();
		
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(1.5f, 3);
		
		fixtureDef.shape = boxShape;
		
		box = world.createBody(bodyDef);
		box.createFixture(fixtureDef);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = bus;
		jointDef.bodyB = box;
		jointDef.localAnchorA.set(bus.getLocalCenter());
		jointDef.localAnchorB.set(0,1.5f);
		world.createJoint(jointDef);
				
		batch = new SpriteBatch();		
		
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, TILEDMAP_FACTOR, batch);
		Gdx.input.setInputProcessor(this);
	}
	
	private void GetPaths(TiledMap map){
		MapObjects mapPaths = map.getLayers().get("paths").getObjects();	
		paths = new Vector2[mapPaths.getCount()][];
		for(int i = 0; i < mapPaths.getCount(); i++){
			MapObject mapPath = mapPaths.get(i);
			if(mapPath instanceof PolylineMapObject){
				PolylineMapObject polyLine = (PolylineMapObject)mapPath;
				float[] vertices = polyLine.getPolyline().getTransformedVertices();
				paths[i] = new Vector2[(vertices.length/2)];
				for(int j = 0; j< vertices.length; j+=2){
					paths[i][j/2] = new Vector2(vertices[j] * TILEDMAP_FACTOR, vertices[j+1] * TILEDMAP_FACTOR);
					createCheckPoint(paths[i][j/2]);
				}
			}	
		}		
	}
	private void createCheckPoint(Vector2 point){
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(point);
		def.linearDamping = 1;
		
		CircleShape shape = new CircleShape();
		shape.setRadius(DISTANCE_TO_NODE);
		
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.density = 1;
		fixture.friction = 1;
		fixture.restitution = 0;
		
		Body tmp = world.createBody(def);
		tmp.createFixture(fixture);
		
		
		
		shape.dispose();
		
	}

	@Override
	public void hide() {
				
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		tiledMapRenderer.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode){
		case Keys.UP:
			//bus.setLinearVelocity(0,100);			
			forceY += acc;
			break;
		case Keys.DOWN:
			forceY -= acc;
			//bus.setLinearVelocity(0,0);
			//bus.applyForceToCenter(0, -100, true);
			break;
		case Keys.LEFT:
			if(++currentPath >= paths.length){
				currentPath = paths.length -1;
			}else{
				changeRoad = true;
			}
			break;
		case Keys.RIGHT:
			if(--currentPath < 0){
				currentPath = 0;
			}else{
				changeRoad = true;
			}
			break;
		
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode){
		case Keys.UP:
			//bus.setLinearVelocity(Vector2.Zero);
			//bus.applyForceToCenter(Vector2.Zero, true);
			break;
		
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
