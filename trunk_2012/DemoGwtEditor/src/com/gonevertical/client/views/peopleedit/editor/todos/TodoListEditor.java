package com.gonevertical.client.views.peopleedit.editor.todos;

import java.util.ArrayList;
import java.util.List;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class TodoListEditor extends Composite implements IsEditor<ListEditor<TodoDataProxy, TodoItemEditor>> {

  private static TodoListEditorUiBinder uiBinder = GWT.create(TodoListEditorUiBinder.class);
  @UiField FlowPanel pWidget;
  @UiField PushButton bAdd;
  @UiField FlowPanel pList;

  interface TodoListEditorUiBinder extends UiBinder<Widget, TodoListEditor> {}


  
  private class TodoItemEditorSource extends EditorSource<TodoItemEditor> {
    @Override
    public TodoItemEditor create(int index) {
      TodoItemEditor widget = new TodoItemEditor();
      pList.insert(widget, index);
      return widget;
    }                   
  }   
  private ListEditor<TodoDataProxy, TodoItemEditor> editor = ListEditor.of(new TodoItemEditorSource());



  public TodoListEditor() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    editor.createEditorForTraversal();
  }

  @Override
  public ListEditor<TodoDataProxy, TodoItemEditor> asEditor() {
    return editor;
  }


//  @Override
//  public void setValue(List<TodoDataProxy> value, boolean fireEvents) {
//    setValue(value);
//  }
//
//  @Override
//  public void setValue(List<TodoDataProxy> value) {
//    editor.setValue(value);
//  }
//
//  @Override
//  public List<TodoDataProxy> getValue() {
//    List<TodoDataProxy> list = editor.getList();
//    return list;
//  }
//
//  @Override
//  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<TodoDataProxy>> handler) {
//    // TODO Auto-generated method stub
//    return null;
//  }
// 
}
