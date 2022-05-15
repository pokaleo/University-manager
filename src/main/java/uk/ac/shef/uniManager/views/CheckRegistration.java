package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.*;
import uk.ac.shef.uniManager.model.Student;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Registrar Dashboard")
@Route(value = "/checkRegistration", layout = MainLayout.class)
@RolesAllowed("ROLE_registrar")
public class CheckRegistration extends Div {
    private Grid<Student> grid = new Grid<>();
    private List<Student> studentList;

    public CheckRegistration() {

        // set up the form
        StudentDAO studentDAO = new StudentDAO();
        studentList = studentDAO.getStudentList(new Student());
        grid.setItems(studentList);
        Grid.Column<Student> titleColumn = grid.addColumn(Student::getTitle).setHeader("Title")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> userNameColumn = grid.addColumn(Student::getUsername).setHeader("Username")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> ForenameColumn = grid.addColumn(Student::getForename).setHeader("Forename")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> SurnameColumn = grid.addColumn(Student::getSurname).setHeader("Surname")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> degreeColumn = grid.addColumn(Student::getRegDeg).setHeader("Degree")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> emailColumn = grid.addColumn(Student::getEmail).setHeader("Email")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> tutorColumn = grid.addColumn(Student::getTutor).setHeader("Tutor")
                .setAutoWidth(true).setSortable(true);
        Grid.Column<Student> regNumberColumn = grid.addColumn(Student::getRegNumber)
                .setHeader("Registration Number").setAutoWidth(true).setSortable(true);
        Grid.Column<Student> periodColumn = grid.addColumn(Student::getPeriodOfStudy)
                .setHeader("Period of Study").setAutoWidth(true).setSortable(true);

        grid.setHeightFull();

        // Search students
        GridListDataView<Student> dataView = grid.setItems(studentList);
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(student -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesTitle = matchesTerm(student.getTitle(), searchTerm);
            boolean matchesUserName = matchesTerm(student.getUsername(), searchTerm);
            boolean matchesForeName = matchesTerm(student.getForename(), searchTerm);
            boolean matchesSurName = matchesTerm(student.getSurname(), searchTerm);
            boolean matchesDegree = matchesTerm(student.getRegDeg(), searchTerm);
            boolean matchesEmail = matchesTerm(student.getEmail(), searchTerm);
            boolean matchesTutor = matchesTerm(student.getTutor(), searchTerm);
            boolean matchesRegNumber = matchesTerm(String.valueOf(student.getRegNumber()), searchTerm);
            boolean matchesPeriod = matchesTerm(student.getPeriodOfStudy(), searchTerm);

            return matchesTitle || matchesUserName || matchesForeName|| matchesSurName || matchesDegree ||
                    matchesEmail || matchesTutor ||  matchesRegNumber || matchesPeriod;
        });

        add(searchField, grid);
        setHeightFull();
    }


    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
