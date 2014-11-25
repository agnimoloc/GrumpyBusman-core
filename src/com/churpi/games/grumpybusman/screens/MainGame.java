package com.churpi.games.grumpybusman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.churpi.games.grumpybusman.actors.Vehicle;
import com.churpi.games.grumpybusman.common.AbstractScreen;
import com.churpi.games.grumpybusman.common.ChurpiGame;
import com.churpi.games.grumpybusman.layers.Score;
import com.churpi.games.grumpybusman.utils.Assets;
import com.churpi.games.grumpybusman.utils.Path;

public class MainGame extends AbstractScreen {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	
	private Stage layerGame;
	private Score layerScore;
	private Vehicle bus;
	private final float ZOOM_SPEED = 0.5f, TIME_STEP = 1/60f, TILEDMAP_FACTOR = 1f/(game.PIXELS_PER_METER/2);
	
	private Path map;
	private float panX, panY;
		
	public MainGame(ChurpiGame game, short indexRoute) {
		super(game);
	}

	@Override
	public void dispose() {
		map.dispose();
		layerGame.dispose();
		layerScore.dispose();
		world.dispose();
		debugRenderer.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	
	private void calculateCamera(float delta){
		OrthographicCamera camera = (OrthographicCamera)layerGame.getCamera();
		
		camera.position.set(bus.getX(), bus.getY()+ (bus.getHeight() /2), 0);	
		
		/*float seekedZoom = 1+((float)bus.getVelocity()/5);

		if(camera.zoom < seekedZoom){
			camera.zoom += ZOOM_SPEED * delta;
			if(camera.zoom > seekedZoom){
				camera.zoom = seekedZoom;
			}
		}else if (camera.zoom > seekedZoom){
			camera.zoom -= ZOOM_SPEED * delta;
			if(camera.zoom < seekedZoom){
				camera.zoom = seekedZoom;
			}
		}*/
		camera.update();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if(!layerScore.isPause()){
        	world.step(TIME_STEP, 8, 3);
        	calculateCamera(delta);				
        	map.act(delta);
        	layerGame.act(delta);
        	layerScore.act(delta);
        }
		
		map.draw();		
		layerGame.draw();		
		layerScore.draw();
		
		debugRenderer.render(world, layerGame.getCamera().combined);
	}

	@Override
	public void resize(int width, int height) {
		layerGame.getViewport().update(width, height, true);
		layerScore.getViewport().update(width, height, true);
		
		layerGame.getCamera().translate(bus.getX(), bus.getY(), 0);
		layerGame.getCamera().position.set(bus.getX(), bus.getY(), 0);
		layerGame.getCamera().viewportHeight = layerGame.getViewport().getWorldHeight() / game.PIXELS_PER_METER;
		layerGame.getCamera().viewportWidth = layerGame.getViewport().getWorldWidth() / game.PIXELS_PER_METER;
		layerGame.getCamera().update();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		layerGame = new Stage(new FitViewport(game.VIEWPORT_WIDTH, game.VIEWPORT_HEIGTH), game.getBatch());		
		layerScore = new Score(game);		
		layerScore.create();
		
		world = new World(Vector2.Zero, true);
		debugRenderer = new Box2DDebugRenderer();
		
		TextureAtlas atlas = game.manager.get("data/uiskin.atlas", TextureAtlas.class);

		TiledMap tiledMap = new TmxMapLoader().load("data/levels/1_1.tmx"); //game.manager.get("data/levels/1_1.tmx", TiledMap.class);
		map = new Path(tiledMap, layerGame, TILEDMAP_FACTOR);
		
		TextureRegion busTex = atlas.findRegion(Assets.Textures.BUS);
		bus = new Vehicle(world, map, busTex, 3, 6);
		layerGame.addActor(bus);
	
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(layerScore);
		im.addProcessor(new GestureDetector(touch_event));
		Gdx.input.setInputProcessor(im);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		layerScore.addListener(input_event);
		
		Skin skin = game.manager.get("data/uiskin.json", Skin.class);
		Label testLabel = new Label("Test", skin);
		layerGame.addActor(testLabel);
		testLabel.setPosition(300, 400);
	}
	
	private InputListener input_event = new InputListener(){
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.BACK){
				game.setScreen(new RoutesMenu(game));
			}
			if(keycode == Keys.LEFT){
				bus.turnLeft();	
				map.setCurrentPath(bus.getCurrentPath());
			}
			if(keycode == Keys.RIGHT){
				bus.turnRight();	
				map.setCurrentPath(bus.getCurrentPath());
			}
			if(keycode == Keys.UP){
				bus.increaseVelocity();
			}
			if(keycode == Keys.DOWN){
				bus.decreaseVelocity();
			}
			if(keycode == Keys.ESCAPE){
				layerScore.setPause(true);
			}
			return false;
		}
	};
	
	private GestureListener touch_event = new GestureListener() {

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			panX = x;
			panY = y;
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {			
			float deltaX = panX - x;
			float deltaY = panY - y;
			
			if(Math.abs(deltaX) > Math.abs(deltaY)){
				if(deltaX > 0)
					bus.turnLeft();
				else
					bus.turnRight();
			}else{
				if(deltaY> 0)
					bus.increaseVelocity();
				else
					bus.decreaseVelocity();
			}
	        
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
				Vector2 pointer1, Vector2 pointer2) {
			// TODO Auto-generated method stub
			return false;
		}

		
		
		
	};

}
