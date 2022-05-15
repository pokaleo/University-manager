package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.GradesDAO;
import uk.ac.shef.uniManager.DAO.StudentDAO;
import uk.ac.shef.uniManager.model.Grades;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Teacher Dashboard")
@Route(value = "/checkStudents", layout = MainLayout.class)
@RolesAllowed("ROLE_teacher")
public class CheckStudents extends Div {
    private SplitLayout splitLayout;
    private ComboBox<String> studentsBox = new ComboBox<>();
    private Button checkGradesButton = new Button("Check Grades");
    private Button checkStatusButton = new Button("Check Status");
    private List<Grades> gradesList;
    private StudentDAO studentDAO = new StudentDAO();
    private GradesDAO gradesDAO = new GradesDAO();

    CheckStudents() {
        splitLayout = new SplitLayout();

        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(65);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private void  createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        // Set up the students combobox
        List<Student> studentList = studentDAO.getStudentList(new Student());
        ArrayList<String> studentUsernameList = new ArrayList<>();
        for (Student student:
             studentList) {
            studentUsernameList.add(student.getUsername());
        }

        studentsBox = StringUtil.fillComboBoxByList("Student's Username", studentUsernameList);

        FormLayout formLayout = new FormLayout();

        Component[] fields = new Component[]{studentsBox};

        formLayout.add(fields);
        editorDiv.add(new H3("Select a student to start:"));
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        checkGradesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        checkStatusButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(checkGradesButton, checkStatusButton);
        editorLayoutDiv.add(buttonLayout);
        checkGradesButton.addClickListener(e -> {
            if (!studentsBox.isEmpty()) {
                gradesList = gradesDAO.getStudentGradesList(studentsBox.getValue());
                if (!gradesList.isEmpty()) {
                    fetchGrades(splitLayout);
                } else {
                    Notification notification = Notification.show("No grades records found for this student");
                    Notification notification2 = Notification.show("Select another student to check grades");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.BOTTOM_CENTER);
                    notification2.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification2.setPosition(Notification.Position.BOTTOM_CENTER);
                }
            } else {
                Notification notification = Notification.show("Please select a student and retry!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
            }
        });
        checkStatusButton.addClickListener(e -> {
            if (!studentsBox.isEmpty()) {
                fetchStatus(splitLayout);
                fetchStatus(splitLayout);
            } else {
                Notification notification = Notification.show("Please select a student and retry!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
            }
        });
    }

    private void fetchGrades(SplitLayout splitLayout) {
        Grid<Grades> grid = new Grid<>();
        Grid.Column<Grades> moduleColumn = grid.addColumn(Grades::getModId).setHeader("Module")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> grade1Column = grid.addColumn(Grades::getGrades1).setHeader("1st Attempt")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> grade2Column = grid.addColumn(Grades::getGrades2).setHeader("Resit")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Grades> levelColumn = grid.addColumn(Grades::getLevelOfStudy)
                .setHeader("Level of Study").setAutoWidth(true).setSortable(true);
        grid.setHeightFull();

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

            boolean matchesModuleId = matchesTerm(grades.getModId(), searchTerm);
            boolean matchesGrades1 = matchesTerm(grades.getGrades1Str(), searchTerm);
            boolean matchesGrades2 = matchesTerm(grades.getGrades2Str(), searchTerm);
            boolean matchesLevel = matchesTerm(grades.getLevelOfStudy(), searchTerm);

            return matchesModuleId || matchesGrades1 || matchesGrades2 || matchesLevel;
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(searchField, grid);
        splitLayout.addToPrimary(wrapper);
        splitLayout.setSplitterPosition(65);
    }

    private void fetchStatus(SplitLayout splitLayout) {
        Student student = studentDAO.query(studentsBox.getValue());
        FormLayout studentStatusForm = new FormLayout();
        Label name = new Label("Name:");
        Label nameInfo = new Label(student.getName());
        Label regDeg = new Label("Registered Degree:");
        Label regDegInfo = new Label(student.getRegDeg());
        Label regNumber = new Label("Registration Number:");
        Label regNumberInfo = new Label(String.valueOf(student.getRegNumber()));
        Label email = new Label("Email:");
        Label emailInfo = new Label(student.getEmail());
        Label tutor = new Label("Personal Tutor:");
        Label tutorInfo = new Label(student.getTutor());
        Label level = new Label("Period of Study:");
        Label levelInfo = new Label(student.getPeriodOfStudy());
        studentStatusForm.add(name, nameInfo, regDeg, regDegInfo, regNumber, regNumberInfo, email, emailInfo, tutor,
        tutorInfo, level, levelInfo);
        splitLayout.addToPrimary(studentStatusForm);
        splitLayout.setSplitterPosition(65);
        if (studentDAO.isGradu(studentsBox.getValue())){
            Notification notification = Notification.show("The student has graduated with degree: "+
                    gradesDAO.graduate(studentsBox.getValue(), Integer.
                            parseInt(studentDAO.period(studentsBox.getValue()))));
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
