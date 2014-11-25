package com.churpi.games.grumpybusman;

import com.churpi.games.grumpybusman.common.ChurpiGame;
import com.churpi.games.grumpybusman.screens.MainGame;
import com.churpi.games.grumpybusman.screens.Splash;
import com.churpi.games.grumpybusman.screens.TestScreen;

public class GrumpyBusmanGame extends ChurpiGame {
		
	@Override
	public void create () {
		setScreen(new Splash(this));
	}
}
