package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.GradesDAO;
import uk.ac.shef.uniManager.model.Grades;
import uk.ac.shef.uniManager.utils.SecurityUtils;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Teacher Dashboard")
@Route(value = "/checkGrades", layout = MainLayout.class)
@RolesAllowed("ROLE_student")
public class CheckGrades extends Div {
    private Grid<Grades> grid = new Grid<>();
    public CheckGrades() {
        String username = SecurityUtils.getUserName();
        GradesDAO gradesDao = new GradesDAO();
        List<Grades> gradesList = gradesDao.getStudentGradesList(username);
        grid.addColumn(Grades::getModId).setHeader("Module").setAutoWidth(true).setSortable(true);
        grid.addColumn(Grades::getGrades1).setHeader("1st Attempt").setAutoWidth(true).setSortable(true);
        grid.addColumn(Grades::getGrades2).setHeader("Resit").setAutoWidth(true).setSortable(true);
        grid.addColumn(Grades::getLevelOfStudy).setHeader("Level of Study").setAutoWidth(true).setSortable(true);
        grid.setItems(gradesList);
//        add(grid);

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
        add(wrapper);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
