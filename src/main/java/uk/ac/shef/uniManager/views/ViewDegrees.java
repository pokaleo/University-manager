package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import uk.ac.shef.uniManager.DAO.DegDAO;
import uk.ac.shef.uniManager.DAO.DepDAO;
import uk.ac.shef.uniManager.model.Degree;
import uk.ac.shef.uniManager.model.Department;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Admin Dashboard")
@Route(value = "/viewDegrees", layout = MainLayout.class)
@RolesAllowed("ROLE_admin")
public class ViewDegrees extends Div {
    TextField filterText = new TextField();
    private Grid<Degree> grid = new Grid<>();
    private Editor<Degree> editor = grid.getEditor();
    private Button addButton = new Button("Add");
    private Button linkButton = new Button("Confirm");
    private SplitLayout splitLayout;
    private TextField degID;
    private TextField degName;
    private ComboBox<String> degBox;
    private ComboBox<String> leadDep;
    private ComboBox<String> leadDep2;
    private ComboBox<String> depBox;
    private ArrayList<String> leadDepList;

    public ViewDegrees() {
        splitLayout = new SplitLayout();

        // set up the form
        DegDAO degDAO = new DegDAO();
        List<Degree> degreeList = degDAO.getDegList(new Degree());
        grid.setItems(degreeList);
        Grid.Column<Degree> degIDColumn = grid.addColumn(Degree::getDegId).setHeader("Degree Code")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Degree> degNameColumn = grid.addColumn(Degree::getDegName).setHeader("Degree Name")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Degree> leadDepColumn = grid.addColumn(Degree::getLeadDep).setHeader("Lead Department")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Degree> editColumn = grid.addComponentColumn(user -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(user);
            });
            return editButton;
        }).setWidth("30%").setFlexGrow(0);

        grid.setHeightFull();

        // set up the in-line editor
        Binder<Degree> degBinder = new Binder<>(Degree.class);
        editor.setBinder(degBinder);
        editor.setBuffered(true);

        // retrieve department list
        DepDAO depDAO = new DepDAO();
        List<Department> departmentList =depDAO.getDepList(new Department());
        leadDepList = new ArrayList<>();
        for (Department dep:
                departmentList) {
            leadDepList.add(dep.getDepId());
        }

        leadDep = new ComboBox<>();
        leadDep.setAllowCustomValue(true);
        leadDep.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            leadDepList.add(customValue);
            leadDep.setItems(leadDepList);
            leadDep.setValue(customValue);
        });
        leadDep.setItems(leadDepList);
        degBinder.forField(leadDep).asRequired("Lead department must not be empty!")
                .bind(Degree::getLeadDep, Degree::setLeadDep);
        leadDepColumn.setEditorComponent(leadDep);

        Button saveButton = new Button("Save", e -> {
            try {
                Degree degToBeUpdated = editor.getItem();
                editor.save();
                degDAO.update(leadDep.getValue(), degToBeUpdated.getDegId());
                degDAO.linkDepDeg(leadDep.getValue(), degToBeUpdated.getDegId());
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        });

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        Button deleteButton = new Button("Delete", e -> {
            degDAO.delete(editor.getItem().getDegId());
            fetchDegrees();
        });
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Search users
        GridListDataView<Degree> dataView = grid.setItems(degreeList);
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(degree -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesDegID = matchesTerm(degree.getDegId(),
                    searchTerm);
            boolean matchesDegName = matchesTerm(degree.getDegName(), searchTerm);
            boolean matchesLeadDep = matchesTerm(degree.getLeadDep(),
                    searchTerm);

            return matchesDegID || matchesDegName || matchesLeadDep;
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

    private void createEditorLayout(SplitLayout splitLayout) {
        SplitLayout wrapper = new SplitLayout();
        // Add new degree
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        degID = new TextField("Degree Code");
        degName = new TextField("Degree Name");

        leadDep2 = new ComboBox<>("Lead Department");
        leadDep2.setAllowCustomValue(true);
        leadDep2.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            leadDepList.add(customValue);
            leadDep2.setItems(leadDepList);
            leadDep2.setValue(customValue);
        });
        leadDep2.setItems(leadDepList);
        leadDep2.setAllowCustomValue(true);

        Component[] fields = new Component[]{degID, degName, leadDep2};

        formLayout.add(fields);
        editorDiv.add(new H3("Add a new Degree:"));
        editorDiv.add(formLayout);
        editorDiv.add(addButton);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            addNewDegree();
        });

        wrapper.addToPrimary(editorDiv);

        // Link a department to a degree
        Div editorLayoutDiv2 = new Div();
        editorLayoutDiv2.setClassName("editor-layout");

        Div editorDiv2 = new Div();
        editorDiv2.setClassName("editor");
        editorLayoutDiv2.add(editorDiv);

        FormLayout formLayout2 = new FormLayout();

        DegDAO degDAO = new DegDAO();
        List<Degree> degList = degDAO.getDegList(new Degree());
        ArrayList<String> degIdList = new ArrayList<>();
        for (Degree deg:
                degList) {
            degIdList.add(deg.getDegId());
        }
        degBox = new ComboBox<>("Degree Code");
        degBox.setAllowCustomValue(true);
        degBox.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            degIdList.add(customValue);
            degBox.setItems(degIdList);
            degBox.setValue(customValue);
        });
        degBox.setItems(degIdList);
        degBox.setAllowCustomValue(true);

        depBox = new ComboBox<>("Lead Department");
        depBox.setAllowCustomValue(true);
        depBox.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            leadDepList.add(customValue);
            depBox.setItems(leadDepList);
            depBox.setValue(customValue);
        });
        depBox.setItems(leadDepList);
        depBox.setAllowCustomValue(true);


        Component[] fields2 = new Component[]{depBox, degBox};

        formLayout2.add(fields2);
        editorDiv2.add(new H3("Link degree to department:"));
        editorDiv2.add(formLayout2);
        editorDiv2.add(linkButton);
        linkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        linkButton.addClickListener(e -> {
            linkDep();
        });

        wrapper.addToPrimary(editorDiv);

        wrapper.addToSecondary(editorDiv2);


        splitLayout.addToSecondary(wrapper);
    }


    private void fetchDegrees(){
        DegDAO degDAO = new DegDAO();
        List<Degree> degList = degDAO.getDegList(new Degree());
        grid.setItems(degList);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void addNewDegree(){
        Degree degToBeAdded = new Degree();
        degToBeAdded.setDegId(degID.getValue());
        degToBeAdded.setDegName(degName.getValue());
        degToBeAdded.setLeadDep(leadDep2.getValue());
        DegDAO degDAO = new DegDAO();
        if(degDAO.addDeg(degToBeAdded)){
            Notification notification = Notification.show("Successfully added!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }else{
            Notification notification = Notification.show("Failed!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
        String degId = degID.getValue();
        String depId = leadDep2.getValue();
        if(degDAO.linkDepDeg(depId, degId)){
            Notification notification = Notification.show("Successfully linked!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }else{
            Notification notification = Notification.show("Failed!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
        fetchDegrees();
    }

    private void linkDep() {
        String degId = degBox.getValue();
        String depId = depBox.getValue();
        DegDAO degDAO = new DegDAO();
        if(degDAO.linkDepDeg(depId, degId)){
            Notification notification = Notification.show("Successfully linked!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }else{
            Notification notification = Notification.show("Failed!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }
}
