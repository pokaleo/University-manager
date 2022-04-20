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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.ac.shef.uniManager.DAO.*;
import uk.ac.shef.uniManager.model.Degree;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.PasswordUtils;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@PageTitle("Registrar Dashboard")
@Route(value = "/manageStudents", layout = MainLayout.class)
@RolesAllowed("ROLE_registrar")
public class ManageStudents extends Div {
    private Grid<Student> grid = new Grid<>();
    private Editor<Student> editor = grid.getEditor();
    private Button deleteButton = new Button("Delete");
    private SplitLayout splitLayout = new SplitLayout();
    private ComboBox<String> title = StringUtil.
            fillComboBoxByList("Title", new ArrayList<>(Arrays.asList("Mr", "Ms")));
    private TextField forename = new TextField("Forename");
    private TextField surname = new TextField("Surname");
    private TextField tutor = new TextField("tutor");
    private TextField username = new TextField("Username");
    private ComboBox<String> registeredDegree;
    private Button addButton = new Button("Register");
    private List<Student> studentList;

    public ManageStudents() {
        splitLayout = new SplitLayout();

        // set up the form
        StudentDAO studentDAO = new StudentDAO();
        studentList = studentDAO.getStudentList(new Student());
        grid.setItems(studentList);
        Grid.Column<Student> userNameColumn = grid.addColumn(Student::getUsername).setHeader("Username").setAutoWidth(true);
        Grid.Column<Student> nameColumn = grid.addColumn(Student::getFullName).setHeader("Name").setAutoWidth(true);
        Grid.Column<Student> degreeColumn = grid.addColumn(Student::getRegDeg).setHeader("Degree").setAutoWidth(true);
        Grid.Column<Student> regNumberColumn = grid.addColumn(Student::getRegNumber).setHeader("Registration Number").setAutoWidth(true);
        Grid.Column<Student> periodColumn = grid.addColumn(Student::getPeriodOfStudy).setHeader("Period of Study").setAutoWidth(true);
        Grid.Column<Student> editColumn = grid.addComponentColumn(student -> {
            com.vaadin.flow.component.button.Button editButton = new com.vaadin.flow.component.button.Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(student);
            });
            return editButton;
        }).setWidth("20%").setFlexGrow(0);
        grid.setHeightFull();

        // set up the in-line editor
        Binder<Student> studentBinder = new Binder<>(Student.class);
        editor.setBinder(studentBinder);
        editor.setBuffered(true);

        com.vaadin.flow.component.button.Button cancelButton = new com.vaadin.flow.component.button.Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        com.vaadin.flow.component.button.Button deleteButton = new com.vaadin.flow.component.button.Button("Delete", e -> {
            if(studentDAO.delete(editor.getItem().getUsername())) {
                Notification notification = Notification.show("Successfully deleted!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.TOP_CENTER);
            } else {
                Notification notification = Notification.show("Failed!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }
            fetchStudents();
        });
        HorizontalLayout actions = new HorizontalLayout(deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Search students
        GridListDataView<Student> dataView = grid.setItems(studentList);
        com.vaadin.flow.component.textfield.TextField searchField = new com.vaadin.flow.component.textfield.TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(student -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesUserName = matchesTerm(student.getUsername(), searchTerm);
            boolean matchesName = matchesTerm(student.getFullName(), searchTerm);
            boolean matchesDegree = matchesTerm(student.getRegDeg(), searchTerm);
            boolean matchesRegNumber = matchesTerm(String.valueOf(student.getRegNumber()), searchTerm);
            boolean matchesPeriod = matchesTerm(student.getPeriodOfStudy(), searchTerm);

            return matchesUserName || matchesName || matchesDegree || matchesRegNumber || matchesPeriod;
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(searchField, grid);
        splitLayout.addToPrimary(wrapper);

        // Add new student
        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(50);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private void fetchStudents() {
        StudentDAO studentDAO = new StudentDAO();
        studentList = studentDAO.getStudentList(new Student());
        grid.setItems(studentList);
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

        DegDAO degDAO = new DegDAO();
        List<Degree> degList = degDAO.getDegList(new Degree());
        ArrayList<String> degIdList = new ArrayList<>();
        for (Degree deg:
                degList) {
            degIdList.add(deg.getDegId());
        }

        registeredDegree = StringUtil.fillComboBoxByList("Registered Degree", degIdList);

        FormLayout formLayout = new FormLayout();


        com.vaadin.flow.component.Component[] fields = new Component[]{title, forename, surname, tutor, username, registeredDegree};

        formLayout.add(fields);
        editorDiv.add(new H3("Register a student:"));
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
            addNewStudent();
        });
    }

    private void addNewStudent() {
        ModuleDAO moduleDAO = new ModuleDAO();
        int level = 1;
        if (moduleDAO.oneYearMaster(registeredDegree.getValue())) {
            level =4;
        }
        Calendar date = Calendar.getInstance();
        String yearString = String.valueOf(date.get(Calendar.YEAR));
        yearString = yearString.substring(2,4);
        int year = Integer.parseInt(yearString);
        String peiodOfStudy = "A-"+year+"-"+(year+1)+"-"+level;
        int temp = 0;
        String email = null;
        String uniemail = "@sheffield.ac.uk";
        do {
            temp++;
            email = forename.getValue().substring(0, 1) + surname.getValue() + Integer.toString(temp) + uniemail;
            email = email.toUpperCase();
        } while (EmailCheck.checkExist(email));
        int regNum = email.hashCode();
        if (regNum < 0){
            regNum = 0 - regNum;
        }

        if (StringUtil.isEmpty(forename.getValue())) {
            Notification notification = Notification.show("Forename cannot be empty!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            return;
        }
        if (StringUtil.isEmpty(surname.getValue())) {
            Notification notification = Notification.show("Surname name cannot be empty!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            return;
        }
        if (StringUtil.isEmpty(tutor.getValue())) {
            Notification notification = Notification.show("Tutor cannot be empty!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            return;
        }
        if (StringUtil.isEmpty(username.getValue())) {
            Notification notification = Notification.show("Username cannot be empty!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            return;
        }
        if (StringUtil.isEmpty(registeredDegree.getValue())) {
            Notification notification = Notification.show("Registered degree cannot be empty!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            return;
        }
        User user = new User();
        Student student = new Student();
        student.setTitle(title.getValue());
        student.setForeName(forename.getValue());
        student.setSurName(surname.getValue());
        student.setTutor(tutor.getValue());
        student.setRegDeg(registeredDegree.getValue());
        student.setUsername(username.getValue());
        student.setEmail(email);
        student.setRegNumber(regNum);
        student.setPeriodOfStudy(peiodOfStudy);
        user.setUsername(username.getValue());
        user.setType("student");
        String passwordGenerated = PasswordUtils.getSalt(10).substring(0,10);
        user.setPassword(new BCryptPasswordEncoder().encode(passwordGenerated));
        UserDAO userDao = new UserDAO();
        StudentDAO studentDAO = new StudentDAO();
        if (userDao.addUser(user) && studentDAO.addStudent(student) && studentDAO.addCoreModule( level, username.getValue(),
                registeredDegree.getValue())) {
            Notification notification = Notification.show("Successfully added! The password generated is " + passwordGenerated +
                    "The email address generated is " + email + " !");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.setDuration(9000);
            fetchStudents();
        } else {
            Notification notification = Notification.show("Failed");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }
}
