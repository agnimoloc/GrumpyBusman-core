package com.churpi.games.grumpybusman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.churpi.games.grumpybusman.common.AbstractScreen;
import com.churpi.games.grumpybusman.common.ChurpiGame;
import com.churpi.games.grumpybusman.utils.Assets;

public class Splash extends AbstractScreen {

	private Stage stage;
	private long time;
	private static final short DELAY_TIME = 2000; 
	
	public Splash(ChurpiGame game) {
		super(game);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float arg0) {
		Gdx.gl.glClearColor(1, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        long current_time = TimeUtils.millis();
        if(game.manager.update() && this.time + DELAY_TIME < current_time  ){
        	game.setScreen(new MainMenu(game));
        }else{
        	stage.act();
        	stage.draw();
        }
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		this.time = TimeUtils.millis();
		stage = new Stage(new FitViewport(game.VIEWPORT_WIDTH, game.VIEWPORT_WIDTH), game.getBatch());
		game.manager.load("data/uiskin.json", Skin.class);
		game.manager.finishLoading();
		TextureAtlas atlas = game.manager.get("data/uiskin.atlas", TextureAtlas.class);
		TextureRegion texture = atlas.findRegion(Assets.Textures.LOGO);
		
		Image image = new Image(texture);
		image.setWidth(stage.getWidth());
		image.setHeight(stage.getHeight());
		image.setPosition(0, 0);
		stage.addActor(image);		
		
	}

}
