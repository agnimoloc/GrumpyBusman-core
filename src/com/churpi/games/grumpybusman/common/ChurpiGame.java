package com.churpi.games.grumpybusman.common;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ChurpiGame extends Game {

	public final AssetManager manager = new AssetManager();
	
	public final int VIEWPORT_WIDTH = 600,VIEWPORT_HEIGTH=800;
	public final int PIXELS_PER_METER = 24;
	
	private static Batch batch;
	
	public synchronized Batch getBatch(){
		if(batch == null ) batch = new SpriteBatch();
		return batch;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if(batch != null ) batch.dispose();
	}


}
