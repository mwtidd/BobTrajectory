package com.team319.vision.image;

import java.util.ArrayList;
import java.util.List;

public class ImageManager {

	private byte[] image;

	private static ImageManager instance = null;

	private List<IImageListener> listeners;

	private ImageManager(){
		listeners = new ArrayList<IImageListener>();
	}

	public static ImageManager getInstance(){
		if(instance == null){
			instance = new ImageManager();

		}

		return instance;
	}

	public void registerListener(IImageListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IImageListener listener){
		this.listeners.remove(listener);
	}

	public void setImage(byte[] image) {
		this.image = image;
		for(IImageListener listener: listeners){
			listener.onChange(image);
		}
	}

	public byte[] getImage() {
		return image;
	}



}
