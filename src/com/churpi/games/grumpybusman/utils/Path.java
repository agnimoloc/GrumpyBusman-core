package com.churpi.games.grumpybusman.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.churpi.games.grumpybusman.actors.Vehicle;

public class Path {
	private Vector2[][] paths;
	private Vector2 vector1 = new Vector2(), vector2= new Vector2();
	private OrthogonalTiledMapRenderer renderer;
	private TiledMap map;
	private ShapeRenderer shaperenderer = new ShapeRenderer();
	private final float DISTANCE_TO_NODE;
	private final float DISTANCE_TO_ROAD;
	private final boolean drawPaths = false;
	private int currentPath = 0;
	private Stage stage;
	
	public Path (TiledMap map, Stage stage, float TILEDMAP_FACTOR){
		DISTANCE_TO_NODE = 60 * TILEDMAP_FACTOR;
		DISTANCE_TO_ROAD = 10 * TILEDMAP_FACTOR;
		this.map = map;
		this.stage = stage;
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
				}
			}	
		}		
		renderer = new OrthogonalTiledMapRenderer(map, TILEDMAP_FACTOR);
	}


	public void act(float arg0) {
		renderer.setView((OrthographicCamera)stage.getCamera());
	}


	public void draw() {
		renderer.render();
		shaperenderer.setProjectionMatrix(this.stage.getCamera().combined);
		
		if(drawPaths){
			shaperenderer.begin(ShapeType.Line);
			shaperenderer.identity();
			
			for(int j = 0; j< paths.length; j++){
				float factor = (float)j;
				shaperenderer.setColor(Color.BLACK);
				for(int i = 0; i< paths[j].length; i++){
					shaperenderer.circle(paths[j][i].x, paths[j][i].y, DISTANCE_TO_NODE);
					if((i+1) <  paths[currentPath].length)
						shaperenderer.line(paths[j][i].x, paths[j][i].y, paths[j][i+1].x, paths[j][i+1].y );
				}		
			}
			shaperenderer.end();	
		}
		shaperenderer.begin(ShapeType.Line);
		shaperenderer.identity();
		
		shaperenderer.setColor(Color.RED);
		for(int i = 0; i< paths[currentPath].length; i++){
			if((i+1) <  paths[currentPath].length)
				shaperenderer.line(paths[currentPath][i].x, paths[currentPath][i].y, paths[currentPath][i+1].x, paths[currentPath][i+1].y );
		}		
		shaperenderer.end();	
	}
	
	public void dispose() {
		renderer.dispose();
		map.dispose();		
	}

	public Vector2 getStart() {
		return paths[currentPath][0];
	}

	public Vector2 getCurrentPoint(Vehicle vehicle) {
		int currentPath = vehicle.getCurrentPath();
		int currentNode = vehicle.getCurrentNode();
		Vector2 node = paths[currentPath][currentNode];
		for(int i = 0; i < paths.length; i++ ){
			Vector2 tmpnode = paths[i][currentNode];						
			if(tmpnode.dst(vehicle.getX(), vehicle.getY()) <= DISTANCE_TO_NODE){
				currentNode++;
				if(currentNode >= paths[currentPath].length)
					currentNode = 0;
				node = paths[currentPath][currentNode];
				vehicle.setCurrentNode(currentNode);
				break;
			}
		}			
		return node;
	}

	public Vector2 getRoadSeekPoint(Vehicle vehicle) {
		int currentNode = vehicle.getCurrentNode();
		int currentPath = vehicle.getCurrentPath();
				
		vector1.set(vehicle.getX(), vehicle.getY());		
		
		if(vehicle.getCurrentNode()== 0)
			return vector1;
		
		Intersector.nearestSegmentPoint(
				paths[currentPath][currentNode -1].x, 
				paths[currentPath][currentNode -1].y, 
				paths[currentPath][currentNode].x, 
				paths[currentPath][currentNode].y, 
				vector1.x, vector1.y, vector2);
		
		
		
		if(vector2.dst(vector1) <= DISTANCE_TO_ROAD){
			return vector1;
		}
		return vector2;
	}

	public void changeToRightPath(Vehicle vehicle) {
		int currentPath = vehicle.getCurrentPath();
		if(--currentPath < 0){
			currentPath = 0;
		}
		vehicle.setCurrentPath(currentPath);
	}

	public void changeToLeftPath(Vehicle vehicle) {
		int currentPath = vehicle.getCurrentPath();
		if(++currentPath >= paths.length){
			currentPath = paths.length -1;
		}
		vehicle.setCurrentPath(currentPath);
	}

	public void setCurrentPath(int currentPath) {
		this.currentPath = currentPath;
		
	}
}
