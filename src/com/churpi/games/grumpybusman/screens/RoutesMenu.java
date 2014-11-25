package com.churpi.games.grumpybusman.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.churpi.games.grumpybusman.common.AbstractScreen;
import com.churpi.games.grumpybusman.common.ChurpiGame;
import com.churpi.games.grumpybusman.utils.Assets;

public class RoutesMenu extends AbstractScreen {

	private Stage stage;
	
	public RoutesMenu(ChurpiGame game) {
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
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		stage = new Stage(new FitViewport(game.VIEWPORT_WIDTH, game.VIEWPORT_WIDTH), game.getBatch());		
		Skin skin = game.manager.get("data/uiskin.json", Skin.class);
		
		TextureAtlas atlas = game.manager.get("data/uiskin.atlas", TextureAtlas.class);
		TextureRegion texture = atlas.findRegion(Assets.Textures.ROUTES);
		TextureRegion[][] routes = texture.split(128, 128);
		List<TextureRegionDrawable> list = new ArrayList<TextureRegionDrawable>(); 
		
		for(TextureRegion[] textureregionarray : routes){
			for(TextureRegion textureregion: textureregionarray){
				list.add(new TextureRegionDrawable(textureregion));
			}
		}		
		Table body = new Table();
		body.setFillParent(true);
		stage.addActor(body);
		
		body.row().padBottom(10);
		Label selectRoute = new Label("Selecciona ruta:", skin);
		body.add(selectRoute);
		
		Table container = new Table();
		
		ScrollPane scroll = new ScrollPane(container);

		body.row();		
		
		Button[] buttons = new Button[list.size()];
		short i = 0;
		for(TextureRegionDrawable route : list){
			if(i%3==0)
				container.row();
			buttons[i] = new Button(route);
			buttons[i].setName(String.valueOf(i));
			final short index= i;
			buttons[i].addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					OpenRoute(index);
				}
			});
			container.add(buttons[i]).pad(10);
			i++;
		}
		body.add(scroll).expand().fill();
		
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		stage.addListener(input_event);
	}
	
	private void OpenRoute(short index){
		
		if(index == 0){
			game.setScreen(new MainMenu(game));
		}else{
			game.setScreen(new MainGame(game, index));
		}
	}
	private InputListener input_event = new InputListener(){
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
				game.setScreen(new MainMenu(game));
			}
			return false;
		}
	};

}
