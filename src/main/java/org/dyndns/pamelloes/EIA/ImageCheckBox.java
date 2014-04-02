package org.dyndns.pamelloes.EIA;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.CheckBox;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericCheckBox;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;

public class ImageCheckBox extends GenericContainer {
	private Texture texoff, texon;
	private GenericCheckBox checkbox;
	private String permission;
	
	public ImageCheckBox(String off, String on) {
		this(off, on, "");
	}
	
	public ImageCheckBox(String off, String on, String permission) {
		this(new GenericTexture(off), new GenericTexture(on), permission);
	}
	
	public ImageCheckBox(Texture texoff, Texture texon) {
		this(texoff, texon, "");
	}
	
	public ImageCheckBox(Texture texoff, Texture texon, String permission) {
		setPermission(permission).setLayout(ContainerType.OVERLAY);
		checkbox = new GenericCheckBox("") {
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				ImageCheckBox.this.onButtonClick(e);
			}
			
			@Override
			public CheckBox setChecked(boolean checked) {
				super.setChecked(checked);
				ImageCheckBox.this.setCheckedPriv(checked);
				return this;
			}
		};
		checkbox.setPriority(RenderPriority.Highest).setVisible(false);
		addChild(checkbox);
		setImages(texoff, texon);
	}
	
	public ImageCheckBox setImages(String off, String on) {
		return setImages(new GenericTexture(off), new GenericTexture(on));
	}
	
	public ImageCheckBox setImages(Texture texoff, Texture texon) {
		if(this.texoff != null) removeChild(this.texoff).removeChild(this.texon);
		this.texoff = texoff;
		texoff.setPriority(RenderPriority.Lowest);
		texoff.setDrawAlphaChannel(false);
		this.texon = texon;
		texon.setPriority(RenderPriority.Lowest);
		texon.setDrawAlphaChannel(false);
		addChild(checkbox.isChecked() ? texon : texoff);
		return this;
	}
	
	@Override
	public Container setWidth(int width) {
		super.setWidth(width);
		texoff.setWidth(width);
		texon.setWidth(width);
		checkbox.setWidth(width);
		return this;
	}
	
	@Override
	public Container setHeight(int height) {
		super.setHeight(height);
		texoff.setHeight(height);
		texon.setHeight(height);
		checkbox.setHeight(height);
		return this;
	}
	
	public boolean isChecked() {
		return checkbox.isChecked();
	}
	
	//This gets called by other classes and updates the state of the checkbox.
	public ImageCheckBox setChecked(boolean checked) {
		if(checked == isChecked()) return this;
		checkbox.setChecked(checked);
		return this;
	}
	
	//This gets called internally to change the image after the checkbox's state is changed.
	private void setCheckedPriv(boolean checked) {
		removeChild(texoff).removeChild(texon);
		addChild(checkbox.isChecked() ? texon : texoff);
	}
	
	public void onButtonClick(ButtonClickEvent e) {}
	
	public ImageCheckBox setPermission(String permission) {
		this.permission = permission;
		return this;
	}
	
	public String getPermission() {
		return permission;
	}
	
	@Override
	public Widget setTooltip(String tooltip) {
		checkbox.setTooltip(tooltip);
		return this;
	}
}
