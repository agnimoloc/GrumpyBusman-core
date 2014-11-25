package com.churpi.games.grumpybusman.layers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.churpi.games.grumpybusman.common.ChurpiGame;
import com.churpi.games.grumpybusman.screens.RoutesMenu;

public class Score extends Stage {
	private ChurpiGame game;
	private boolean pause;
	private Label infoText;
	
	
	public Score(ChurpiGame game) {
		super(new FitViewport(game.VIEWPORT_WIDTH, game.VIEWPORT_HEIGTH),game.getBatch());
		this.game = game;
	}
	
	public void create(){		
		Skin skin = game.manager.get("data/uiskin.json", Skin.class);
		
		Table body = new Table(skin);
		body.debug();
		body.setFillParent(true);
		body.row();
		TextButton label = new TextButton("Salir", skin);
		body.add(label).left();
		body.add(new Label("centro", skin)).expandX();
		body.add(new Label("izq", skin)).right();
		body.row();
		Table hgroup= new Table(skin);
		hgroup.debug();
		for(int i = 0; i <5 ; i++){
			if(i % 2== 0)
				hgroup.row();
			hgroup.add(new Label(String.valueOf(i),skin));
		}
		body.add(hgroup).top();
		
		body.add().colspan(2).expand();
		this.addActor(body);
		
		label.addListener(new ClickListener(){
			 @Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new RoutesMenu(game));
			}
			});
		
		//infoText = new Label("0,0", skin);
		//body.add(infoText);
	}	
	
	@Override
	public void dispose() {
		super.dispose();
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void setInfo(String string) {
		infoText.setText(string);
		infoText.pack();
	}
	public void setWindowPos (){
		
	}
}
