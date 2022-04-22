package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.DepDAO;
import uk.ac.shef.uniManager.model.Department;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Admin Dashboard")
@Route(value = "/viewDepartments", layout = MainLayout.class)
@RolesAllowed("ROLE_admin")
public class ViewDepartments extends Div {
    private Grid<Department> grid = new Grid<>();
    private Editor<Department> editor = grid.getEditor();
    private Button addButton = new Button("Add");
    private SplitLayout splitLayout;
    private TextField depName;
    private TextField depId;

    ViewDepartments() {
        splitLayout = new SplitLayout();

        // set up the form
        DepDAO depDAO = new DepDAO();
        List<Department> depList = depDAO.getDepList(new Department());
        grid.setItems(depList);
        Grid.Column<Department> depIdColumn = grid.addColumn(Department::getDepId).setHeader("Department Code")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Department> depNameColumn = grid.addColumn(Department::getDepName).setHeader("Department Name")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Department> editColumn = grid.addComponentColumn(department -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(department);
            });
            return editButton;
        }).setWidth("30%").setFlexGrow(0);

        grid.setHeightFull();

        // set up the in-line editor
        Binder<Department> depBinder = new Binder<>(Department.class);
        editor.setBinder(depBinder);
        editor.setBuffered(true);

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        Button deleteButton = new Button("Delete", e -> {
            depDAO.delete(editor.getItem().getDepId());
            fetchDeps();
        });
        HorizontalLayout actions = new HorizontalLayout(
                deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Search users
        GridListDataView<Department> dataView = grid.setItems(depList);
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(department -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesUserID = matchesTerm(department.getDepId(),
                    searchTerm);
            boolean matchesUsername = matchesTerm(department.getDepName(), searchTerm);

            return matchesUserID || matchesUsername;
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(searchField, grid);
        splitLayout.addToPrimary(wrapper);

        // Add new user
        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(60);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        depId = new TextField("Department Code");
        depName = new TextField("Department Name");

        com.vaadin.flow.component.Component[] fields = new Component[]{depId, depName};

        formLayout.add(fields);
        editorDiv.add(new H3("Add a new department:"));
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(addButton);
        editorLayoutDiv.add(buttonLayout);
        addButton.addClickListener(e -> {
            addNewDep();
        });
    }

    private void addNewDep(){
        Department depToBeAdded = new Department();
        depToBeAdded.setDepId(depId.getValue());
        depToBeAdded.setDepName(depName.getValue());
        DepDAO depDAO = new DepDAO();
        if(depDAO.addDep(depToBeAdded)){
            Notification notification = Notification.show("Successfully added!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }else{
            Notification notification = Notification.show("Failed!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
        fetchDeps();
    }

    private void fetchDeps(){
        DepDAO depDAO = new DepDAO();
        List<Department> departmentListList = depDAO.getDepList(new Department());
        grid.setItems(departmentListList);
    }
}
