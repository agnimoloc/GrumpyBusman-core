package com.churpi.games.grumpybusman.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class UtilPos {
	public static void centerButtons(float xCenter, float yCenter, Button... buttons){
		
		int countButtons = buttons.length;
		float heightButton = buttons[0].getHeight() + (buttons[0].getHeight()/2);
		float yCurrent = ((heightButton*countButtons)/2)+ yCenter-(buttons[0].getHeight()/2);
		for(Button button: buttons){
			button.setPosition(xCenter - (button.getWidth()/2), yCurrent);
			yCurrent -= heightButton;
		}
	}
	public static void floatLeftButtons(float xBox, float yBox, float widthBox, float heightBox, float leftMargin, float topMargin, Button... buttons){
		float xCurrent = xBox + leftMargin;
		float yCurrent = yBox+ heightBox - topMargin;
		float maxHeight = 0;
		for(Button button : buttons){
			if((xCurrent + button.getWidth())> (widthBox - yBox)){
				yCurrent -= (maxHeight + topMargin);
				xCurrent = xBox + leftMargin;
			}
			button.setPosition(xCurrent, yCurrent - button.getHeight());
			if(maxHeight < button.getHeight())
				maxHeight = button.getHeight();
			xCurrent += button.getWidth() + (leftMargin*2);			
		}
	}
}
