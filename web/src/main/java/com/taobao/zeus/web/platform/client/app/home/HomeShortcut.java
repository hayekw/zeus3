package com.taobao.zeus.web.platform.client.app.home;

import com.taobao.zeus.web.platform.client.theme.ResourcesTool;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class HomeShortcut extends Shortcut{

	public HomeShortcut(){
		super("welcome", "Home Page");
		setIcon(ResourcesTool.iconResources.home());
	}


}
