package com.dozingcatsoftware.vectorpinball.editor;

import java.util.Set;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.dozingcatsoftware.vectorpinball.editor.elements.EditableField;
import com.dozingcatsoftware.vectorpinball.editor.elements.EditableFieldElement;
import com.dozingcatsoftware.vectorpinball.editor.inspector.ElementInspector;
import com.dozingcatsoftware.vectorpinball.editor.inspector.GlobalPropertiesInspector;
import com.dozingcatsoftware.vectorpinball.editor.inspector.PropertyContainer;

public class ElementInspectorView extends VBox {

    EditableField editableField;
    UndoStack undoStack;
    Runnable changeCallback;

    Label selectionLabel;
    ElementInspector currentInspector;
    Pane inspectorPane;
    Button deleteElementButton;

    public ElementInspectorView() {
        selectionLabel = new Label("");
        this.getChildren().add(selectionLabel);

        deleteElementButton = new Button("Delete");
        deleteElementButton.setOnAction((event) -> deleteSelectedElements());
        update();
    }

    public void setEditableField(EditableField field) {
        editableField = field;
    }

    public void setUndoStack(UndoStack stack) {
        undoStack = stack;
    }

    public void setChangeCallback(Runnable callback) {
        changeCallback = callback;
    }

    public void updateInspectorValues() {
        if (currentInspector != null) {
            currentInspector.updateControlValuesFromElement();
        }
    }

    public void deleteSelectedElements() {
        Set<EditableFieldElement> selected = editableField.getSelectedElements();
        editableField.clearSelection();
        editableField.removeElements(selected);
        if (changeCallback != null) {
            changeCallback.run();
        }
    }

    public void update() {
        if (editableField == null) {
            // Shouldn't happen.
            return;
        }
        if (editableField.hasSelection()) {
            EditableFieldElement elem = editableField.getSelectedElements().iterator().next();
            if (currentInspector==null || currentInspector.getPropertyContainer().getOwner()!=elem) {
                String className = (String)elem.getProperty(EditableFieldElement.CLASS_PROPERTY);
                selectionLabel.setText(className);
                if (inspectorPane!=null) this.getChildren().remove(inspectorPane);
                this.getChildren().remove(deleteElementButton);
                this.getChildren().add(inspectorPane = new Pane());

                currentInspector = null;
                try {
                    String inspectorClass = "com.dozingcatsoftware.vectorpinball.editor.inspector." +
                            className + "Inspector";
                    currentInspector = (ElementInspector)Class.forName(inspectorClass).newInstance();
                    currentInspector.initialize(inspectorPane, PropertyContainer.forFieldElement(elem), changeCallback);
                }
                catch(InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                this.getChildren().add(deleteElementButton);
            }
        }
        else {
            selectionLabel.setText("Field properties");
            if (inspectorPane!=null) this.getChildren().remove(inspectorPane);
            this.getChildren().remove(deleteElementButton);
            this.getChildren().add(inspectorPane = new Pane());
            currentInspector = new GlobalPropertiesInspector();
            currentInspector.initialize(inspectorPane, PropertyContainer.forField(editableField), changeCallback);
        }
    }
}
