package com.taobao.zeus.web.platform.client.module.jobdisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.info.Info;
import com.taobao.zeus.web.platform.client.module.jobdisplay.group.GroupPresenter;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class AdminConfigWindow extends Window{

	private JobPresenter jobPresenter;
	private GroupPresenter groupPresenter;
	private List<ZUser> admins=new ArrayList<ZUser>();
	private List<ZUser> allUsers=new ArrayList<ZUser>();
	private HTMLPanel label=new HTMLPanel("");
	private TextButton add=new TextButton("Create new admin", new SelectHandler() {
		private ListStore<Map<String, String>> store=new ListStore<Map<String, String>>(new ModelKeyProvider<Map<String, String>>() {
			public String getKey(Map<String, String> item) {
				return item.get("name")+"("+item.get("uid")+")";
			}
		});
		private ComboBox<Map<String, String>> combo;
		private TextButton submit=new TextButton("Create", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				Map<String, String> md=combo.getValue();
				if(combo.validate() && md!=null){
					if(jobPresenter!=null){
						RPCS.getJobService().addJobAdmin(jobPresenter.getJobModel().getId(), md.get("uid"), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								win.hide();
								Info.display("Succeeded", "New admin was created successfully");
								AdminConfigWindow.this.refresh();
							}
						});
					}else if(groupPresenter!=null){
						RPCS.getGroupService().addGroupAdmin(groupPresenter.getGroupModel().getId(), md.get("uid"), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								win.hide();
								Info.display("Succeeded","New admin was created successfully");
								AdminConfigWindow.this.refresh();
							}
						});
					}
				}
			}
		});
		private Window win=new Window();
		{
			win.setHeadingText("Create new admin");
			win.setSize("350", "100");
			win.setModal(true);
			combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
				public String getLabel(Map<String, String> item) {
					return item.get("name");
				}
			});
			combo.setForceSelection(true);
			combo.setTriggerAction(TriggerAction.QUERY);
			combo.setStore(store);
			win.add(new FieldLabel(combo, "Select an account"));
			win.addButton(submit);
		}
		public void onSelect(SelectEvent event) {
			for(ZUser s:allUsers){
				Map<String, String> md=new HashMap<String, String>();
				md.put("name", s.getName()+"("+s.getUid()+")");
				md.put("uid", s.getUid());
				store.add(md);
			}
			combo.reset();
			win.show();
		}
	});
	private TextButton remove=new TextButton("Delete admin", new SelectHandler() {
		private ComboBox<Map<String, String>> combo;
		private ListStore<Map<String, String>> store=new ListStore<Map<String, String>>(new ModelKeyProvider<Map<String, String>>() {
			public String getKey(Map<String, String> item) {
				return item.get("uid");
			}
		});
		private TextButton submit=new TextButton("Delete", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				Map<String, String> md=combo.getValue();
				if(md==null){
					combo.markInvalid("Mandatory");
				}else{
					if(jobPresenter!=null){
						RPCS.getJobService().removeJobAdmin(jobPresenter.getJobModel().getId(), md.get("uid"), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								win.hide();
								Info.display("Succeeded", "Admin was deleted successfully");
								AdminConfigWindow.this.refresh();
							}
						});
					}else if(groupPresenter!=null){
						RPCS.getGroupService().removeGroupAdmin(groupPresenter.getGroupModel().getId(), md.get("uid"), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								win.hide();
								Info.display("Succeeded", "Admin was deleted successfully");
								AdminConfigWindow.this.refresh();
							}
						});
					}
				}
			}
		});
		private Window win=new Window();
		{
			win.setHeadingText("Delete admin");
			win.setSize("350", "100");
			win.setModal(true);
			combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
				public String getLabel(Map<String, String> item) {
					return item.get("name");
				}
			});
			combo.setTriggerAction(TriggerAction.ALL);
			combo.setEditable(false);
			combo.setStore(store);
			win.add(new FieldLabel(combo, "Select an account"));
			win.addButton(submit);
		}
		public void onSelect(SelectEvent event) {
			for(ZUser u:admins){
				Map<String, String> md=new HashMap<String, String>();
				md.put("name", u.getName()+"("+u.getUid()+")");
				md.put("uid", u.getUid());
				store.add(md);
			}
			combo.reset();
			win.show();
		}
	});
	
	private TextButton transfer=new TextButton("Transfer ownership", new SelectHandler() {
		private ListStore<Map<String, String>> store=new ListStore<Map<String, String>>(new ModelKeyProvider<Map<String, String>>() {
			public String getKey(Map<String, String> item) {
				return item.get("name");
			}
		});
		private ComboBox<Map<String, String>> combo;
		private TextButton submit=new TextButton("Transfer", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				Map<String, String> md=combo.getValue();
				if(combo.validate() && md!=null){
					if(jobPresenter!=null){
						RPCS.getJobService().transferOwner(jobPresenter.getJobModel().getId(), md.get("uid"), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								win.hide();
								Info.display("Succeeded", "Ownership was transferred successfully");
								
								AdminConfigWindow.this.refresh();
							}
						});
					}else if(groupPresenter!=null){
						RPCS.getGroupService().transferOwner(groupPresenter.getGroupModel().getId(), md.get("uid"), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								win.hide();
								Info.display("Succeeded", "Ownership was transferred successfully");
								AdminConfigWindow.this.refresh();
							}
						});
					}
				}
			}
		});
		private Window win=new Window();
		{
			win.setHeadingText("Transfer ownership");
			win.setSize("350", "100");
			win.setModal(true);
			combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
				public String getLabel(Map<String, String> item) {
					return item.get("name");
				}
			});
			combo.setForceSelection(true);
			combo.setTriggerAction(TriggerAction.QUERY);
			combo.setStore(store);
			win.add(new FieldLabel(combo, "Select an account"));
			win.addButton(submit);
		}
		public void onSelect(SelectEvent event) {
			for(ZUser s:allUsers){
				Map<String, String> md=new HashMap<String, String>();
				md.put("name", s.getName()+"("+s.getUid()+")");
				md.put("uid", s.getUid());
				store.add(md);
			}
			combo.reset();
			win.show();
		}
	});
	private AdminConfigWindow(){
		setHeadingText("Admin configuration");
		setModal(true);
		setSize("400", "250");
		add(label);
		addButton(add);
		addButton(remove);
		addButton(transfer);
		RPCS.getUserService().getAllUsers(new AbstractAsyncCallback<List<ZUser>>() {
			public void onSuccess(List<ZUser> result) {
				allUsers=result;
			}
		});
		addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if(jobPresenter!=null){
					RPCS.getJobService().getUpstreamJob(jobPresenter.getJobModel().getId(), new AbstractAsyncCallback<JobModel>() {
						@Override
						public void onSuccess(JobModel result) {
							jobPresenter.display(result);
						}
					});
				}else{
					RPCS.getGroupService().getUpstreamGroup(groupPresenter.getGroupModel().getId(), new AbstractAsyncCallback<GroupModel>() {
						public void onSuccess(GroupModel result) {
							groupPresenter.display(result);
						}
					});
				}
			}
		});
	}
	public AdminConfigWindow(JobPresenter presenter){
		this();
		this.jobPresenter=presenter;
		refresh();
	}
	private void refresh(){
		if(jobPresenter!=null){
			RPCS.getJobService().getJobAdmins(jobPresenter.getJobModel().getId(), new AbstractAsyncCallback<List<ZUser>>() {
				public void onSuccess(List<ZUser> result) {
					admins=result;
					StringBuffer sb=new StringBuffer();
					for(ZUser u:admins){
						sb.append("<span title='"+u.getUid()+"'>"+u.getName()+",</span>");
					}
					label.getElement().setInnerHTML(sb.toString());
				}
			});
		}else{
			RPCS.getGroupService().getGroupAdmins(groupPresenter.getGroupModel().getId(), new AbstractAsyncCallback<List<ZUser>>() {
				public void onSuccess(List<ZUser> result) {
					admins=result;
					StringBuffer sb=new StringBuffer();
					for(ZUser u:admins){
						sb.append("<span title='"+u.getUid()+"'>"+u.getName()+",</span>");
					}
					label.getElement().setInnerHTML(sb.toString());
				}
			});
		}
		
	}
	public AdminConfigWindow(GroupPresenter presenter){
		this();
		this.groupPresenter=presenter;
		refresh();
	}
}
