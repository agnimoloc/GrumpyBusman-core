package com.churpi.games.grumpybusman.common;

import com.badlogic.gdx.Screen;

public abstract class AbstractScreen implements Screen {
	protected ChurpiGame game;
	
	public AbstractScreen(ChurpiGame game){
		this.game = game;
	}
}
