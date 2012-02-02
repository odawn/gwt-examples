package com.gonevertical.client.views.peopleedit.editor.todos;

import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class TodoItemEditor extends Composite  implements Editor<TodoDataProxy>, HasValue<String>, TakesValue<String> {

  private static TodoItemEditorUiBinder uiBinder = GWT.create(TodoItemEditorUiBinder.class);
  
  @UiField 
  ValueBoxEditorDecorator<String> todo;

  interface TodoItemEditorUiBinder extends UiBinder<Widget, TodoItemEditor> {}

  public TodoItemEditor() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public HandlerRegistration addValueChangeHandler(
      ValueChangeHandler<String> handler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getValue() {
    return "test";
  }

  @Override
  public void setValue(String value) {
    System.out.println("test set");
  }

  @Override
  public void setValue(String value, boolean fireEvents) {
    System.out.println("test set2");
  }

}
