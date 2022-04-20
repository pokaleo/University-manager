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
import uk.ac.shef.uniManager.DAO.ModuleDAO;
import uk.ac.shef.uniManager.model.Degree;
import uk.ac.shef.uniManager.model.Module;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Admin Dashboard")
@Route(value = "/viewModules", layout = MainLayout.class)
@RolesAllowed("ROLE_admin")
public class ViewModules extends Div {
    private Grid<Module> grid = new Grid<>();
    private Editor<Module> editor = grid.getEditor();
    private Button addButton = new Button("Add");
    private Button linkButton = new Button("Confirm");
    private SplitLayout splitLayout;
    private TextField moduleID;
    private TextField moduleName;
    private ComboBox<String> taughtSem = new ComboBox<String>("Taught Semester");
    private ComboBox<String> linkedDeg = new ComboBox<String>("Degree Code");
    private ComboBox<String> linkedModule;
    private ComboBox<String> levelOfStudy;
    private ComboBox<String> moduleType;
    private ArrayList<String> degList;
    private List<Module> moduleList;

    public ViewModules() {
        splitLayout = new SplitLayout();

        // set up the form
        ModuleDAO moduleDAO = new ModuleDAO();
        moduleList = moduleDAO.getModuleList(new Module());
        grid.setItems(moduleList);
        Grid.Column<Module> moduleIDColumn = grid.addColumn(Module::getModuleId).setHeader("Module Code").setAutoWidth(true);
        Grid.Column<Module> moduleNameColumn = grid.addColumn(Module::getModuleName).setHeader("Module Name").setAutoWidth(true);
        Grid.Column<Module> taughtSemColumn = grid.addColumn(Module::getTaughtSem).setHeader("Taught Semester").setAutoWidth(true);
        Grid.Column<Module> editColumn = grid.addComponentColumn(module-> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(module);
            });
            return editButton;
        }).setWidth("30%").setFlexGrow(0);

        grid.setHeightFull();

        // set up the in-line editor
        Binder<Module> moduleBinder = new Binder<>(Module.class);
        editor.setBinder(moduleBinder);
        editor.setBuffered(true);

        // retrieve degree list
        DegDAO degDAO = new DegDAO();
        List<Degree> degreeList = degDAO.getDegList(new Degree());
        degList = new ArrayList<>();
        for (Degree degree:
                degreeList) {
            degList.add(degree.getDegId());
        }

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        Button deleteButton = new Button("Delete", e -> {
            moduleDAO.delete(editor.getItem().getModuleId());
            fetchModules();
        });
        HorizontalLayout actions = new HorizontalLayout(deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Search modules
        GridListDataView<Module> dataView = grid.setItems(moduleList);
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(module -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesModuleID = matchesTerm(module.getModuleId(),
                    searchTerm);
            boolean matchesModuleName = matchesTerm(module.getModuleName(), searchTerm);
            boolean matchesTaughtSem = matchesTerm(module.getTaughtSem(),
                    searchTerm);

            return matchesModuleID || matchesModuleName || matchesTaughtSem;
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(searchField, grid);
        splitLayout.addToPrimary(wrapper);

        // Add new user
        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(50);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        SplitLayout wrapper = new SplitLayout();

        // Add new module
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        moduleID = new TextField("Module Code");
        moduleName = new TextField("Module Name");


        List<String> taughtSemList = List.of(new String[]{"autumn", "spring", "year"});
        taughtSem = new ComboBox<>("Taught Semester");
        taughtSem.setAllowCustomValue(true);
        taughtSem.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            taughtSemList.add(customValue);
            taughtSem.setItems(taughtSemList);
            taughtSem.setValue(customValue);
        });
        taughtSem.setItems(taughtSemList);

        Component[] fields = new Component[]{moduleID, moduleName, taughtSem};

        formLayout.add(fields);
        editorDiv.add(new H3("Add a new module:"));
        editorDiv.add(formLayout);
        editorDiv.add(addButton);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            addNewModule();
        });

        wrapper.addToPrimary(editorDiv);

        // Link a degree to a module
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


        linkedDeg.setAllowCustomValue(true);
        linkedDeg.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            degIdList.add(customValue);
            linkedDeg.setItems(degIdList);
            linkedDeg.setValue(customValue);
        });
        linkedDeg.setItems(degIdList);

        ArrayList<String> moduleIdList = new ArrayList<>();
        for (Module module:
                moduleList) {
            moduleIdList.add(module.getModuleId());
        }
        linkedModule = new ComboBox<>("Module Code");
        linkedModule.setAllowCustomValue(true);
        linkedModule.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            moduleIdList.add(customValue);
            linkedModule.setItems(moduleIdList);
            linkedModule.setValue(customValue);
        });
        linkedModule.setItems(moduleIdList);
        linkedModule.setAllowCustomValue(true);

        List<String> levelOfStudyList = List.of(new String[]{"1", "2", "3", "4"});
        levelOfStudy = new ComboBox<>("Level of Study");
        levelOfStudy.setAllowCustomValue(true);
        levelOfStudy.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            levelOfStudyList.add(customValue);
            levelOfStudy.setItems(levelOfStudyList);
            levelOfStudy.setValue(customValue);
        });
        levelOfStudy.setItems(levelOfStudyList);

        List<String> moduleTypeList = List.of(new String[]{"Non-Core", "Core"});
        moduleType = new ComboBox<>("Module Type");
        moduleType.setAllowCustomValue(true);
        moduleType.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            levelOfStudyList.add(customValue);
            moduleType.setItems(moduleTypeList);
            moduleType.setValue(customValue);
        });
        moduleType.setItems(moduleTypeList);


        Component[] fields2 = new Component[]{linkedDeg, linkedModule, levelOfStudy, moduleType};

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


    private void fetchModules(){
        ModuleDAO moduleDAO = new ModuleDAO();
        List<Module> moduleList = moduleDAO.getModuleList(new Module());
        grid.setItems(moduleList);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void addNewModule(){
        ModuleDAO moduleDAO = new ModuleDAO();
        Module moduleToBeAdded = new Module();
        moduleToBeAdded.setModuleId(moduleID.getValue());
        moduleToBeAdded.setModuleName(moduleName.getValue());
        moduleToBeAdded.setTaughtSem(taughtSem.getValue());
        DegDAO degDAO = new DegDAO();
        if(moduleDAO.addMod(moduleToBeAdded)){
            Notification notification = Notification.show("Successfully added!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
            fetchModules();
        }else{
            Notification notification = Notification.show("Failed!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }

    private void linkDep() {
        String moduleId = linkedModule.getValue();
        String depId = linkedDeg.getValue();
        String level = levelOfStudy.getValue();
        int type = -1;
        if (moduleType.getValue().equals("Core")) {
            type = 1;
        } else if (moduleType.getValue().equals("Non-Core")) {
            type = 0;
        }
        ModuleDAO moduleDAO = new ModuleDAO();
        if(moduleDAO.linkDegMod(depId, moduleId, level, type)){
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
