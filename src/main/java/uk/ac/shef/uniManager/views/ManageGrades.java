package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
import uk.ac.shef.uniManager.DAO.*;
import uk.ac.shef.uniManager.model.Grades;
import uk.ac.shef.uniManager.model.Module;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Teacher Dashboard")
@Route(value = "/manageGrades", layout = MainLayout.class)
@RolesAllowed("ROLE_teacher")
public class ManageGrades extends Div {
    private Grid<Grades> grid = new Grid<>();
    private Editor<Grades> editor = grid.getEditor();
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");
    private SplitLayout splitLayout;
    private TextField grades1 = new TextField();
    private TextField grades2 = new TextField();
    private ComboBox<String> studentUsername;
    private ComboBox<String> moduleId;
    private TextField grades1Field = new TextField("1st Attempt");
    private TextField grades2Field = new TextField("Resit");
    private TextField level = new TextField("Level of Study");
    private Button addButton = new Button("Add");
    private List<Grades> gradesList;

    public ManageGrades() {
        splitLayout = new SplitLayout();

        // set up the form
        GradesDAO gradesDAO = new GradesDAO();
        gradesList = gradesDAO.getGradesList(new Grades());
        grid.setItems(gradesList);
        Grid.Column<Grades> usernameColumn = grid.addColumn(Grades::getUsername).setHeader("Username")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> moduleColumn = grid.addColumn(Grades::getModId).setHeader("Module")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> grade1Column = grid.addColumn(Grades::getGrades1).setHeader("1st Attempt")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> grade2Column = grid.addColumn(Grades::getGrades2).setHeader("Resit")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> levelColumn = grid.addColumn(Grades::getLevelOfStudy).setHeader("Level of Study")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> editColumn = grid.addComponentColumn(grade -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener( e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(grade);
            });
            return editButton;
        }).setWidth("30%").setFlexGrow(0);
        grid.setHeightFull();

        // set up the in-line editor
        Binder<Grades> gradesBinder = new Binder<>(Grades.class);
        editor.setBinder(gradesBinder);
        editor.setBuffered(true);

        grades1 = new TextField();
        gradesBinder.forField(grades1).asRequired("Grade must not be empty!")
                        .bind(Grades::getGrades1Str, Grades::setGrades1ByStr);
        grade1Column.setEditorComponent(grades1);

        gradesBinder.forField(grades2).asRequired("Grade must not be empty!")
                .bind(Grades::getGrades2Str, Grades::setGrades2ByStr);
        grade2Column.setEditorComponent(grades2);

        saveButton.addClickListener(e -> {
            if (StringUtil.isNotEmpty(grades1.getValue()) && StringUtil.isNotEmpty(grades2.getValue())) {
                try {
                    Grades gradesToBeUpdated = editor.getItem();
                    editor.save();
                    if (gradesDAO.update(gradesToBeUpdated)) {
                        Notification notification = Notification.show("Successfully updated!");
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        notification.setPosition(Notification.Position.TOP_CENTER);
                    } else {
                        Notification notification = Notification.show("Failed!");
                        notification.setPosition(Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                    fetchGrades();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Notification notification = Notification.show(ex.toString());
                    notification.setPosition(Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setDuration(1500);
                }
            } else {
                Notification notification = Notification.show("Please enter a valid grade for each attempt!");
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());

        deleteButton.addClickListener(e -> {
            if (gradesDAO.delete(editor.getItem().getUsername(), editor.getItem().getModId(), editor.getItem().getLevelOfStudy())) {
                Notification notification = Notification.show("Successfully deleted!");
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification notification = Notification.show("Failed, please check!");
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            fetchGrades();
        });

        HorizontalLayout actions = new HorizontalLayout(saveButton,
                deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Search grades
        GridListDataView<Grades> dataView = grid.setItems(gradesList);
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(grades -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesUsername = matchesTerm(grades.getUsername(), searchTerm);
            boolean matchesGrades1 = matchesTerm(grades.getGrades1Str(), searchTerm);
            boolean matchesGrades2 = matchesTerm(grades.getGrades2Str(), searchTerm);
            boolean matchesLevel = matchesTerm(grades.getLevelOfStudy(), searchTerm);

            return matchesUsername || matchesGrades1 || matchesGrades2 || matchesLevel;
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(searchField, grid);
        splitLayout.addToPrimary(wrapper);

        // Add new grades
        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(50);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private void fetchGrades() {
        GradesDAO gradesDAO = new GradesDAO();
        gradesList = gradesDAO.getGradesList(new Grades());
        grid.setItems(gradesList);
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

        ModuleDAO moduleDAO = new ModuleDAO();
        List<Module> moduleList = moduleDAO.getModuleList(new Module());
        ArrayList<String> moduleIdList = new ArrayList<>();
        for (Module module:
                moduleList) {
            moduleIdList.add(module.getModuleId());
        }

        StudentDAO studentDAO = new StudentDAO();
        List<Student> studentList = studentDAO.getStudentList(new Student());
        ArrayList<String> studentUsernameList = new ArrayList<>();
        for (Student student:
             studentList) {
            studentUsernameList.add(student.getUsername());
        }

        moduleId = StringUtil.fillComboBoxByList("Module", moduleIdList);
        studentUsername = StringUtil.fillComboBoxByList("Student", studentUsernameList);

        FormLayout formLayout = new FormLayout();

        Component[] fields = new Component[]{studentUsername, moduleId, grades1Field, grades2Field, level};

        formLayout.add(fields);
        editorDiv.add(new H3("Add a new grade record:"));
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        // editorDiv.setHeight("70%");

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(addButton);
        editorLayoutDiv.add(buttonLayout);
        addButton.addClickListener(e -> {
            addNewGrade();
        });
    }

    private void addNewGrade() {
        GradesDAO gradesDAO = new GradesDAO();
        if (StringUtil.isNotEmpty(studentUsername.getValue()) && StringUtil.isNotEmpty(moduleId.getValue())
                && StringUtil.isNotEmpty(grades1Field.getValue()) && StringUtil.isNotEmpty(grades2Field.getValue())
        && StringUtil.isNotEmpty(level.getValue())){
            Grades grades = new Grades();
            grades.setUsername(studentUsername.getValue());
            grades.setModId(moduleId.getValue());
            try {
                grades.setGrades1(Integer.parseInt(grades1Field.getValue()));
                grades.setGrades2(Integer.parseInt(grades2Field.getValue()));
            } catch (Exception e) {
                Notification notification = Notification.show("Please enter valid values for grades!");
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            grades.setLevelOfStudy(level.getValue());
            if (gradesDAO.addGrade(grades)) {
                Notification notification = Notification.show("Successfully added!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.TOP_CENTER);
            } else {
                Notification notification = Notification.show("Failed, please check!");
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            fetchGrades();
        } else {
            Notification notification = Notification.show("Please enter a set of valid values before proceed!");
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
