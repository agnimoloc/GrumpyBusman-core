package com.churpi.games.grumpybusman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.churpi.games.grumpybusman.common.AbstractScreen;
import com.churpi.games.grumpybusman.common.ChurpiGame;
import com.churpi.games.grumpybusman.utils.Assets;
import com.churpi.games.grumpybusman.utils.UtilPos;

public class MainMenu extends AbstractScreen {

	private Stage stage;
	private TextButton play;
	private TextButton about;
	private TextButton settings;
	private TextButton exit;
	//private Image selection;
	private int selectionType = ActionType.PLAY;
	
	private class ActionType{
		public static final int PLAY = 0;
		public static final int ABOUT = 1;
		public static final int SETTINGS = 2;
		public static final int EXIT = 3;
	}

	public MainMenu(ChurpiGame game) {
		super(game);		
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}	
	
	@Override
	public void hide() {
		
	}
	@Override
	public void pause() {
		
	}
	@Override
	public void render(float arg0) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
		stage.act();
		stage.draw();
	}
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);		
		setSelectionPos();
	}
	@Override
	public void resume() {
		
	}
	@Override
	public void show() {
		stage = new Stage(new FitViewport(game.VIEWPORT_WIDTH, game.VIEWPORT_WIDTH), game.getBatch());
		
		Skin skin = game.manager.get("data/uiskin.json", Skin.class);
		TextureAtlas atlas = game.manager.get("data/uiskin.atlas", TextureAtlas.class);
		NinePatch selected = atlas.createPatch(Assets.Textures.SELECTED);
			NinePatchDrawable draw = new NinePatchDrawable(selected);
		//selection = new Image(selected);
			//LabelStyle
		play = new TextButton("Jugar", skin);
		play.background(draw);
		play.addListener(click_play);

		about = new TextButton("Acerca de", skin);		

		settings = new TextButton("Configuracion", skin);		

		exit= new TextButton("Salir", skin);
		exit.addListener(click_exit);				
		
		/*if(Gdx.app.getType()!= ApplicationType.Android && Gdx.app.getType()!= ApplicationType.iOS)
			stage.addActor(selection);*/
		//VerticalGroup body = new VerticalGroup();
		Table body = new Table();
		body.setFillParent(true);
		
		
		body.row().pad(10);
		body.add(play);
		body.row().pad(10);
		body.add(about);
		body.row().pad(10);
		body.add(settings);
		body.row().pad(10);
		body.add(exit);
		
		stage.addActor(body);
		
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		stage.addListener(input_event);
	}
	private void setSelectionPos(){
		float margin = 20;
		Button tmp = null;
		switch(selectionType){
		case ActionType.PLAY: tmp = play; break;
		case ActionType.ABOUT: tmp = about; break;
		case ActionType.SETTINGS: tmp = settings; break;
		case ActionType.EXIT: tmp = exit; break;

		}
		//selection.setPosition(tmp.getX()-(margin), tmp.getY()- (margin/2));
		//selection.setSize(tmp.getWidth()+ (margin*2), tmp.getHeight() + margin);
	}
	
	private InputListener input_event = new InputListener(){
		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.BACK || keycode== Keys.ESCAPE){
				Gdx.app.exit();
			}
			if(keycode == Keys.DOWN){
				selectionType++;
				if(selectionType > ActionType.EXIT){
					selectionType = ActionType.EXIT;
				}				
				setSelectionPos();
				
			}
			if(keycode == Keys.UP){
				selectionType--;
				if(selectionType < ActionType.PLAY){
					selectionType = ActionType.PLAY;
				}				
				setSelectionPos();
				
			}
			if(keycode == Keys.ENTER){
				doAction(selectionType);
			}
			return false;
		}
	};
	
	private void doAction(int selType){
		switch(selType){
		case ActionType.PLAY: game.setScreen(new RoutesMenu(game)); break;
		case ActionType.EXIT: Gdx.app.exit(); break;
		}
	}
	
	private ClickListener click_play = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			doAction(ActionType.PLAY);
		}
	};
	
	private ClickListener click_exit = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			doAction(ActionType.EXIT);
			
		}
	};

}
