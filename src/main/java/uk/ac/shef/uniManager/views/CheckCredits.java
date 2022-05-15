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
import uk.ac.shef.uniManager.DAO.StudentDAO;
import uk.ac.shef.uniManager.DAO.StudentModDAO;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.model.StudentModule;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Registrar Dashboard")
@Route(value = "/checkCredits", layout = MainLayout.class)
@RolesAllowed("ROLE_registrar")
public class CheckCredits extends Div {
    private SplitLayout splitLayout = new SplitLayout();
    private Grid<StudentModule> grid = new Grid<>();
    private Button checkButton  = new Button("Check Module Choice");
    private ComboBox<String> studentsBox;
    private List<StudentModule> choiceList;
    private int level = 0;
    private int sum = 0;


    CheckCredits() {
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
        choiceList = studentModDAO.getList(username);
        level = choiceList.get(0).getPeriodOfStudy();
        sum = 0;
        for (StudentModule studentModule:
             choiceList) {
            sum += studentModule.getCredits();
        }
        grid.setItems(choiceList);
        Grid.Column<StudentModule> usernameColumn = grid.addColumn(StudentModule::getUsername).setHeader("Username")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<StudentModule> nameColumn = grid.addColumn(StudentModule::getFullName).setHeader("Full Name")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<StudentModule> moduleColumn = grid.addColumn(StudentModule::getModuleId)
                .setHeader("Module Selected").setAutoWidth(true).setSortable(true);
        Grid.Column<StudentModule> creditsColumn = grid.addColumn(StudentModule::getCredits).setHeader("Credits")
                .setAutoWidth(true).setSortable(true);
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

        studentsBox = StringUtil.fillComboBoxByList("Student Username", studentUsernameList);

        FormLayout formLayout = new FormLayout();

        Component[] fields = new Component[]{studentsBox};

        formLayout.add(fields);
        editorDiv.add(new H3("Choose a student to check:"));
        editorDiv.add(fields);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        checkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(checkButton);
        editorLayoutDiv.add(buttonLayout);
        checkButton.addClickListener(e -> {
            checkCredits();
        });
    }

    private void checkCredits() {
        StudentModDAO studentModDAO = new StudentModDAO();
        fetchChoices(studentsBox.getValue());
        boolean checker = false;
        if(level<4 && sum==120){
            checker =true;
        }else if(level==4&&sum==180){
            checker =true;
        }
        Notification notification1 = Notification.show("Total module credits of this student is " +sum+ ".");
        notification1.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification1.setDuration(50000);
        notification1.setPosition(Notification.Position.BOTTOM_CENTER);
        if (checker){
            Notification notification = Notification.show("The student are in level " + level + " and the credits sum is valid.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(50000);
            notification.setPosition(Notification.Position.BOTTOM_CENTER);
        }else {
            Notification notification = Notification.show("The student are in level " + level + " and the credits sum is invalid.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setDuration(50000);
            notification.setPosition(Notification.Position.BOTTOM_CENTER);
        }
    }


}
