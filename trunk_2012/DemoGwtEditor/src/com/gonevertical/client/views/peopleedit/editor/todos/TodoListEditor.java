package com.gonevertical.client.views.peopleedit.editor.todos;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.events.DeleteEvent;
import com.gonevertical.client.app.events.DeleteEventHandler;
import com.gonevertical.client.app.requestfactory.PeopleDataRequest;
import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;

/**
 * be sure the server side class is annotated appropriately
 *     @Persistent(defaultFetchGroup = "true", dependentElement = "true")
 *     and use .with("todos"); with the request factory context finding
 */
public class TodoListEditor extends Composite implements IsEditor<ListEditor<TodoDataProxy, TodoItemEditor>> {

  private static TodoListEditorUiBinder uiBinder = GWT.create(TodoListEditorUiBinder.class);
  
  @UiField 
  FlowPanel pWidget;
  
  @UiField 
  PushButton bAdd;
  
  @UiField 
  FlowPanel plist;

  
  interface TodoListEditorUiBinder extends UiBinder<Widget, TodoListEditor> {}
  
  
  private class TodoItemEditorSource extends EditorSource<TodoItemEditor> {
    @Override
    public TodoItemEditor create(final int index) {
      TodoItemEditor subEditor = new TodoItemEditor();
      plist.insert(subEditor, index);
      subEditor.addDeleteHandler(new DeleteEventHandler() {
        public void onDeleteEvent(DeleteEvent event) {
          
          // TODO this doesn't work either
          editor.getList().remove(index);
          TodoItemEditor sub = (TodoItemEditor) event.getSource();
          dispose(sub);
          
          // TODO won't work yet, doesn't like tdp (autobean maybe different)
//          final TodoItemEditor sub = (TodoItemEditor) event.getSource();
//          TodoDataProxy tdp = editor.getList().get(index);
//          clientFactory.getRequestFactory().getTodoDataRequest().remove().using(tdp).fire(new Receiver<Boolean>() {
//            public void onSuccess(Boolean response) {
//              if (response == null) {
//                // TODO
//              } else if (response == false) {
//                // TODO
//              } else if (response == true) {
                  //editor.getList().remove(index);
//                dispose(sub);
//              }
//            }
//          });
//          
          // TODO another way maybe to compare lists 
        }
      });
      return subEditor;
    }     
 
    @Override
    public void dispose(TodoItemEditor subEditor) {
      subEditor.removeFromParent();
      
    }
    @Override
    public void setIndex(TodoItemEditor editor, int index) {
      plist.insert(editor, index);
    }
  }   
  private ListEditor<TodoDataProxy, TodoItemEditor> editor = ListEditor.of(new TodoItemEditorSource());

  private PeopleDataRequest context;

  private ClientFactory clientFactory;

  public TodoListEditor() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    add();
  }

  private void add() {
    TodoDataProxy e = context.create(TodoDataProxy.class);
    editor.getList().add(e);
  }

  @Override
  public ListEditor<TodoDataProxy, TodoItemEditor> asEditor() {
    return editor;
  }

  public void setContext(PeopleDataRequest context) {
    this.context = context;
  }



}
