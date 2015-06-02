package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.taobao.zeus.web.platform.client.module.jobmanager.TreeKeyProviderTool;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class NewGroupWindow extends Window{
	
	private GroupPresenter presenter;
	private FormPanel panel;
	private TextField name;
	private ToggleGroup group;
	private Radio dir;
	private Radio leaf;

	public NewGroupWindow(GroupPresenter presenter) {
		this.presenter=presenter;
		setHeadingText("Create new group");
		setWidth(350);
		setHeight(150);
		setModal(true);

		panel = new FormPanel();
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p,new MarginData(5));
		name = new TextField();
		name.setToolTipConfig(new ToolTipConfig("Group name", "Mandatory, editable"){{setDismissDelay(0);}});

		dir = new Radio();
		dir.setBoxLabel("Group dir");
		dir.setToolTipConfig(new ToolTipConfig("Group dir, non-editable", "Only groups can be added, jobs can not"){{setDismissDelay(0);}});
		leaf = new Radio();
		leaf.setBoxLabel("Group");
		leaf.setToolTipConfig(new ToolTipConfig("Group, non-editable", "Only jobs can be added, groups can not"){{setDismissDelay(0);}});
		HorizontalPanel radios=new HorizontalPanel();
		radios.add(dir);
		radios.add(leaf);
		group=new ToggleGroup();
		group.add(dir);
		group.add(leaf);
		
		p.add(new FieldLabel(name, "Group name(*)"), new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(radios,"Group type(*)"), new VerticalLayoutData(1, -1));

		add(panel,new MarginData(5));

		addButton(new TextButton("Ok", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (name.getValue()==null || name.getValue().trim().equals("")
						|| group.getValue() == null) {
					AlertMessageBox alert=new AlertMessageBox("Warning", "Required items can not be emtpy");
					alert.show();
				}else{
					submit();
				}
			}
		}));
		addButton(new TextButton("Cancel", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				NewGroupWindow.this.hide();
			}
		}));
	}
	
	
	private void submit(){
		String gname=name.getValue();
		Boolean isDirectory=group.getValue()==dir;
		RPCS.getGroupService().createGroup(gname, presenter.getGroupModel().getId(), isDirectory, new AbstractAsyncCallback<String>() {
			@Override
			public void onSuccess(String groupId) {
				NewGroupWindow.this.hide();
				Info.display("Scuceeded", "New group was created successfully");
				TreeNodeChangeEvent event=new TreeNodeChangeEvent();
				event.setNeedSelectProviderKey(TreeKeyProviderTool.genGroupProviderKey(groupId));
				presenter.getPlatformContext().getPlatformBus().fireEvent(event);
			}
		});
	}

}