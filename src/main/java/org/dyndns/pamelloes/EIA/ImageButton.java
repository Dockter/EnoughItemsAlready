package org.dyndns.pamelloes.EIA;

import org.getspout.spoutapi.gui.Texture;

public class ImageButton extends ImageCheckBox {
	
	public ImageButton(String url) {
		super(url, url);
	}
	
	public ImageButton(String url, String permission) {
		super(url, url, permission);
	}
	
	public ImageButton(Texture tex) {
		super(tex, tex);
	}
	
	public ImageButton(Texture tex, String permission) {
		super(tex, tex, permission);
	}
}
