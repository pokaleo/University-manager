package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.ModuleDAO;
import uk.ac.shef.uniManager.DAO.StudentDAO;
import uk.ac.shef.uniManager.DAO.StudentModDAO;
import uk.ac.shef.uniManager.model.Module;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.model.StudentModule;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Registrar Dashboard")
@Route(value = "/manageModules", layout = MainLayout.class)
@RolesAllowed("ROLE_registrar")
public class ManageModules extends Div {
    private SplitLayout splitLayout = new SplitLayout();
    private Grid<StudentModule> grid = new Grid<>();
    private Button addButton = new Button("Add");
    private Button dropButton = new Button("Drop");
    private ComboBox<String> studentsBox;
    private ComboBox<String> modulesBox;
    private List<StudentModule> choiceList;
    private int level = 0;
    private int sum = 0;


    ManageModules() {
        splitLayout = new SplitLayout();

        // set up the form
        grid.setHeightFull();

        splitLayout.addToPrimary(grid);
        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(60);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private void fetchChoices(String username) {
        StudentModDAO studentModDAO = new StudentModDAO();
        System.out.println("66666" + username);
        choiceList = studentModDAO.getList(username);
        level = choiceList.get(0).getPeriodOfStudy();
        sum = 0;
        for (StudentModule studentModule:
             choiceList) {
            sum += studentModule.getCredits();
        }
        grid.setItems(choiceList);
        Grid.Column<StudentModule> usernameColumn = grid.addColumn(StudentModule::getUsername).setHeader("Username").setAutoWidth(true);
        Grid.Column<StudentModule> nameColumn = grid.addColumn(StudentModule::getFullName).setHeader("Full Name").setAutoWidth(true);
        Grid.Column<StudentModule> moduleColumn = grid.addColumn(StudentModule::getModuleId).setHeader("Module Selected").setAutoWidth(true);
        Grid.Column<StudentModule> creditsColumn = grid.addColumn(StudentModule::getCredits).setHeader("Credits").setAutoWidth(true);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        StudentDAO studentDAO = new StudentDAO();
        List<Student> studentList = studentDAO.getStudentList(new Student());
        ArrayList<String> studentUsernameList = new ArrayList<>();
        for (Student student :
                studentList) {
            studentUsernameList.add(student.getUsername());
        }

        ModuleDAO moduleDAO = new ModuleDAO();
        List<Module> moduleList = moduleDAO.getModuleList(new Module());
        ArrayList<String> moduleIdList = new ArrayList<>();
        for (Module module:
                moduleList) {
            moduleIdList.add(module.getModuleId());
        }

        studentsBox = StringUtil.fillComboBoxByList("Student Username", studentUsernameList);
        modulesBox = StringUtil.fillComboBoxByList("Module Code", moduleIdList);

        FormLayout formLayout = new FormLayout();

        Component[] fields = new Component[]{studentsBox, modulesBox};

        formLayout.add(fields);
        editorDiv.add(new H3("Select and click button to start:"));
        editorDiv.add(fields);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dropButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(addButton, dropButton);
        editorLayoutDiv.add(buttonLayout);
        addButton.addClickListener(e -> {
            addModule();
        });
        dropButton.addClickListener(e -> {
            dropModule();
        });
    }

    private void addModule() {
        if (!StringUtil.isEmpty(studentsBox.getValue()) && !StringUtil.isEmpty(modulesBox.getValue())) {
            StudentModDAO studentModDAO = new StudentModDAO();
            if (studentModDAO.addModule(studentsBox.getValue(), modulesBox.getValue())){
                Notification notification = Notification.show("Successfully added.");
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                notification.setDuration(20000);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }else{
                Notification notification = Notification.show("Failed, please check.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(20000);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }
            fetchChoices(studentsBox.getValue());
        } else {
            Notification notification = Notification.show("Please select a student and a module before proceed.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setDuration(20000);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }

    private void dropModule() {
        if (!StringUtil.isEmpty(studentsBox.getValue()) && !StringUtil.isEmpty(modulesBox.getValue())) {
            StudentModDAO studentModDAO = new StudentModDAO();
            if (studentModDAO.deleteModule(studentsBox.getValue(), modulesBox.getValue())){
                Notification notification = Notification.show("Successfully dropped.");
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                notification.setDuration(20000);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }else{
                Notification notification = Notification.show("Failed, please check.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(20000);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }
            fetchChoices(studentsBox.getValue());
        } else {
            Notification notification = Notification.show("Please select a student and a module before proceed.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setDuration(20000);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }


}
