package com.gonevertical.client.views.peopleedit.editor.todos;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.events.DeleteEvent;
import com.gonevertical.client.app.events.DeleteEventHandler;
import com.gonevertical.client.app.events.EditEvent;
import com.gonevertical.client.app.events.EditEventHandler;
import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class TodoItemEditor extends Composite  implements Editor<TodoDataProxy> {

  private static TodoItemEditorUiBinder uiBinder = GWT.create(TodoItemEditorUiBinder.class);
  
  @UiField 
  TextBox todo;
  
  @UiField 
  PushButton bDelete;


  interface TodoItemEditorUiBinder extends UiBinder<Widget, TodoItemEditor> {}

  public TodoItemEditor() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("bDelete")
  void onBDeleteClick(ClickEvent event) {
    fireDeleteEvent();
  }

  private void fireDeleteEvent() {
    fireEvent(new DeleteEvent(this));
  }
  
  public final HandlerRegistration addDeleteHandler(DeleteEventHandler handler) {
    return addHandler(handler, DeleteEvent.TYPE);
  }

}
