package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;

public class GroupView extends CardLayoutContainer implements IsWidget{

	private CardInfo info;
	private CardEditGroup editGroup;
	private CardRunning running;
	private CardManual manual;
	private CardOverall overall;
	private GroupPresenter presenter;
	
	public GroupView(GroupPresenter presenter){
		this.presenter=presenter;
		info=new CardInfo(presenter);
		editGroup=new CardEditGroup(presenter);
		running=new CardRunning(presenter);
		manual=new CardManual(presenter);
		overall=new CardOverall(presenter);
		add(editGroup);
	}
	
	public void display(){
		if(getWidgetIndex(info)==-1){
			add(info);
		}
		info.setHeadingText(getBaseTitle());
		setActiveWidget(info);
		info.refresh(presenter.getGroupModel());
	}
	public void displayEditGroup(){
		if(getWidgetIndex(editGroup)==-1){
			add(editGroup);
		}
		editGroup.setHeadingText(getBaseTitle()+" > "+"Edit group");
		setActiveWidget(editGroup);
		editGroup.refresh(presenter.getGroupModel());
	}
	
	public void displayRunning() {
		if(getWidgetIndex(running)==-1){
			add(running);
		}
		running.setHeadingText(getBaseTitle()+" > "+"Running automatic jobs");
		setActiveWidget(running);
		running.refresh(presenter.getGroupModel());
	}

	public void displayManual() {
		if(getWidgetIndex(manual)==-1){
			add(manual);
		}
		manual.setHeadingText(getBaseTitle()+" > "+"Running manual jobs");
		setActiveWidget(manual);
		manual.refresh(presenter.getGroupModel());
	}
	
	private String getBaseTitle(){
		return "组:"+presenter.getGroupModel().getName()+"("+presenter.getGroupModel().getId()+")";
	}

	public void displayOverall() {
		setActiveWidget(overall);
		overall.setHeadingText(getBaseTitle()+" > "+"Job summery in the group");
		overall.refresh(presenter.getGroupModel());
	}
}
