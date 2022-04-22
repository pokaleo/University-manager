package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.GradesDAO;
import uk.ac.shef.uniManager.DAO.StudentDAO;
import uk.ac.shef.uniManager.model.Student;
import uk.ac.shef.uniManager.utils.SecurityUtils;

import javax.annotation.security.RolesAllowed;

@PageTitle("Teacher Dashboard")
@Route(value = "/checkStatus", layout = MainLayout.class)
@RolesAllowed("ROLE_student")
public class CheckStatus extends Div {
    CheckStatus() {
        StudentDAO studentDAO = new StudentDAO();
        GradesDAO gradesDAO = new GradesDAO();
        String username = SecurityUtils.getUserName();
        Student student = studentDAO.query(username);
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
        add(studentStatusForm);
        if (studentDAO.isGradu(username)){
            Notification notification = Notification.show("You have graduated with degree: "+
                    gradesDAO.graduate(username, Integer.
                            parseInt(studentDAO.period(username))));
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setPosition(Notification.Position.MIDDLE);
        }
    }
}
