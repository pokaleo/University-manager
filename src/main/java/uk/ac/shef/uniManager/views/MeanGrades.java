package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.GradesDAO;
import uk.ac.shef.uniManager.DAO.ModuleDAO;
import uk.ac.shef.uniManager.DAO.StudentDAO;
import uk.ac.shef.uniManager.model.Grades;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Registrar Dashboard")
@Route(value = "/meanGrades", layout = MainLayout.class)
@RolesAllowed("ROLE_teacher")
public class MeanGrades extends Div {
    private SplitLayout splitLayout;
    private Grid<Grades> grid = new Grid<>();
    private List<Grades> meanGradesList = new ArrayList<>();
    private int level = 0;
    private GradesDAO gradesDAO = new GradesDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private ComboBox<String> studentsBox;
    private ComboBox<String> levelBox;
    private Div editorLayoutDiv = new Div();
    private ModuleDAO moduleDAO = new ModuleDAO();
    private Student student;


    MeanGrades() {
        splitLayout = new SplitLayout();

        // set up the form
        grid.setHeightFull();

        splitLayout.addToPrimary(grid);
        create1stEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(60);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);
    }

    private void create1stEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        List<Student> studentList = studentDAO.getStudentList(new Student());
        ArrayList<String> studentUsernameList = new ArrayList<>();
        for (Student student :
                studentList) {
            studentUsernameList.add(student.getUsername());
        }

        studentsBox = StringUtil.fillComboBoxByList("Student Username", studentUsernameList);

        FormLayout formLayout = new FormLayout();

        Component[] fields = new Component[]{studentsBox};

        formLayout.add(fields);
        editorDiv.add(new H3("Select a student to start:"));
        editorDiv.add(fields);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        Button confirmStudentButton = new Button("Confirm");
        confirmStudentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(confirmStudentButton);
        editorLayoutDiv.add(buttonLayout);

        confirmStudentButton.addClickListener(e -> {
            if (StringUtil.isNotEmpty(studentsBox.getValue())) {
                // fetch levels
                ArrayList<String> levelList = gradesDAO.queryLevels(studentsBox.getValue());
                if (!levelList.isEmpty()) {
                    levelBox = StringUtil.fillComboBoxByList("Level of Study", levelList);
                    create2ndEditorLayout(splitLayout, studentsBox.getValue());
                } else {
                    Notification notification = Notification.show("No grades records found for this student");
                    Notification notification2 = Notification.show("Select another student to continue");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.BOTTOM_CENTER);
                    notification2.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification2.setPosition(Notification.Position.BOTTOM_CENTER);
                }
            } else {
                Notification notification = Notification.show("Please select a student before proceed!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
            }
        });
        splitLayout.addToSecondary(editorLayoutDiv);
        splitLayout.setSplitterPosition(60);
    }

    private void create2ndEditorLayout(SplitLayout splitLayout, String studentUsername) {
        editorLayoutDiv = new Div();
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        Component[] fields = new Component[]{levelBox};

        formLayout.add(fields);
        editorDiv.add(new H3("Select a level of study to check the weighted-mean grade:"));
        editorDiv.add(fields);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        Button confirmLevelButton = new Button("Confirm");
        confirmLevelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button returnButton = new Button("Return");
        returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(confirmLevelButton, returnButton);
        editorLayoutDiv.add(buttonLayout);
        confirmLevelButton.addClickListener(e -> {
            if (!levelBox.isEmpty()) {
                level = Integer.parseInt(levelBox.getValue());
                fetchGrades(studentUsername);
                create3rdEditorLayout(splitLayout, studentUsername);
            } else {
                Notification notification = Notification.show("Please select a level to continue");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
            }
        });
        returnButton.addClickListener(e -> create1stEditorLayout(splitLayout));
        splitLayout.addToSecondary(editorLayoutDiv);
        splitLayout.setSplitterPosition(60);
    }

    private void create3rdEditorLayout(SplitLayout splitLayout, String studentUsername) {
        student = studentDAO.query(studentUsername);
        editorLayoutDiv = new Div();
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        editorDiv.add(new H3("Grades for each module shows above."));
        double meanGrade = gradesDAO.meanGrades(studentUsername, level);
        int currentLevel = Integer.parseInt(studentDAO.query(studentUsername).getPeriodOfStudy().substring(8, 9));
        BigDecimal bd = new BigDecimal(meanGrade);
        bd = bd.round(new MathContext(3));
        double rounded = bd.doubleValue();
        editorDiv.add(new H4("You are checking the weighted mean grade of level " + level + " for student: "
                + studentUsername));
        editorDiv.add(new H4("The weighted mean grade is " + rounded));
        editorDiv.add(new H4(" Outcome: " + gradesDAO.outcome(studentUsername,level)));
        gradesDAO.concededBoundaries(studentUsername, 4);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        Button returnButton = new Button("Return");
        Button progressButton = new Button("Progress to next level");
        boolean fail = gradesDAO.outcome(studentUsername, level).equals("fail");
        if (level == currentLevel) {
            if (fail){
                if(level>2){
                    progressButton.setText("Graduate");
                } else {
                    progressButton.setText("Repeat the current level");
                }
            }
            if (!fail){
                if (level < 3 || (currentLevel == 3 && moduleDAO.oneYearMaster(student.getRegDeg()))) {
                    progressButton.setText("Progress to next level");
                } else{
                    progressButton.setText("Graduate");
                }
            }
            progressButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonLayout.add(progressButton, returnButton);

        } else {
            editorDiv.add(new H4("You can return and select the current level of study for students to " +
                    "progress them to next level of study"));
            returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonLayout.add(returnButton);
        }
        editorLayoutDiv.add(buttonLayout);
        progressButton.addClickListener(e -> {
            progress(currentLevel, fail, studentUsername);
            create1stEditorLayout(splitLayout);
        });
        returnButton.addClickListener(e -> create1stEditorLayout(splitLayout));
        splitLayout.addToSecondary(editorLayoutDiv);
        splitLayout.setSplitterPosition(60);
    }

    private void fetchGrades(String username) {
        meanGradesList = gradesDAO.getMeanGradesList(username, level);
        grid.removeAllColumns();
        grid.setItems(meanGradesList);
        Grid.Column<Grades> nameColumn = grid.addColumn(Grades::getModId).setHeader("Module Code").setAutoWidth(true);
        Grid.Column<Grades> moduleColumn = grid.addColumn(Grades::getGrades1Str).setHeader("Best Attempt").setAutoWidth(true);
    }

    private void progress (int currentLevel, boolean fail, String studentUsername) {
        if (fail) {
            if (currentLevel < 3 ) {
                if(gradesDAO.repeatYear(studentUsername,level)){
                    Notification notification = Notification.show("Set student repeat the level");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setPosition(Notification.Position.BOTTOM_CENTER);
                } else {
                    Notification notification = Notification.show("Operation failed, please check");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.BOTTOM_CENTER);
                }
            } else {
                String outcome = gradesDAO.graduate(studentUsername,level);
                Notification notification = Notification.show("Student graduated with degree: " + outcome);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
            }
        } else {
            if (level < 3) {
                progressToNextLevel(studentUsername);
            } else if (moduleDAO.oneYearMaster(student.getRegDeg()) && currentLevel == 3) {
                progressToNextLevel(studentUsername);
            } else {
                String outcome = gradesDAO.graduate(studentUsername,level);
                Notification notification = Notification.show("Student graduated with degree: " + outcome);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
            }
        }
    }

    private void progressToNextLevel(String studentUsername) {
        if (gradesDAO.progress(studentUsername, level)) {
            Notification notification = Notification.show("Set student progressed to next level");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_CENTER);
        } else {
            Notification notification = Notification.show("Operation failed, please check");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.BOTTOM_CENTER);
        }
    }

}
