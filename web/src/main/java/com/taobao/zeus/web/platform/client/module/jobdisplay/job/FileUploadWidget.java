package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;

public class FileUploadWidget extends Window {
	public FileUploadWidget(final String type,final String id,final UploadCallback callback){
		this.callback=callback;
		setModal(true);
		setSize("400", "150");
		setHeadingText("Upload resouce file");
		
		FramedPanel fp=new FramedPanel();
		fp.setHeaderVisible(false);
		fp.setButtonAlign(BoxLayoutPack.CENTER);
		
		final FormPanel panel = new FormPanel();
		panel.setAction("upload.do?type="+type+"&id="+id);
		panel.setEncoding(Encoding.MULTIPART);
		panel.setMethod(Method.POST);

		fp.add(panel);

		
		
		file = new FileUploadField();
		file.setAllowBlank(false);
		file.setName("uploadedfile");
		panel.add(new FieldLabel(file,"Choose file"));

		TextButton btn = new TextButton("Reset",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				panel.reset();
			}
		});
		fp.addButton(btn);

		btn = new TextButton("Upload",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				if (panel.isValid()) {
					panel.submit();
					return;
				}
			}
		});
		fp.addButton(btn);

		panel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String html=event.getResults().trim();
				if(html.contains("[[uri=")){
					if(callback!=null){
						String value=html.substring(html.indexOf("[[")+6, html.indexOf("]]"));
						String name=file.getValue();
						if(file.getValue().lastIndexOf("\\")!=-1){
							name=name.substring(name.lastIndexOf("\\")+1);
							/** @see com.taobao.zeus.web.FileUploadServlet.doPost(HttpServletRequest, HttpServletResponse)*/
							name=name.replace(' ', '_');
						}
						callback.call(name,value);
					}
					FileUploadWidget.this.hide();
				}else{
					AlertMessageBox alert=new AlertMessageBox("Failed", html);
					alert.show();
				}
			}
		});
		
		add(fp,new MarginData(5));
	}
	private FileUploadField file;
	private UploadCallback callback;
	
	public interface UploadCallback{
		public void call(String name,String uri);
	}
}