package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.SecurityService;
import uk.ac.shef.uniManager.utils.SecurityUtils;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.PermitAll;

@PageTitle("Login Page")
@Route(value = "12")
@PermitAll
public class Login2 extends VerticalLayout {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LoginForm login = new LoginForm();
    private SecurityService securityService;

    public Login2(@Autowired SecurityService securityService) {
        VerticalLayout layout = new VerticalLayout();

        Image img = new Image("images/login2.jpg", "login logo2");
        img.setWidth("200px");

        Button confirmButton = new Button("Confirm");

        TextField textField = new TextField();
        textField.setLabel("username");
        textField.setRequiredIndicatorVisible(true);
        textField.setErrorMessage("This field is required");

        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("password");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setErrorMessage("This field is required");

        class MyClickListener implements ComponentEventListener<ClickEvent<Button>> {
            int count = 0;
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                event.getSource().setText("You have clicked me " +
                        (++count) + " times");
                String userName = textField.getValue();
                String password = passwordField.getValue();
                if (StringUtil.isEmpty(userName)) {
                    Notification notification = Notification.show("username cannot be empty");
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.setDuration(2000);
                }
                if (StringUtil.isEmpty(password)) {
                    Notification notification = Notification.show("password cannot be empty");
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.setDuration(2000);
                }
                System.out.println(userName+password);
                String sql = "SELECT * FROM users WHERE userID = 2";
                User user = jdbcTemplate.queryForObject(sql,
                        BeanPropertyRowMapper.newInstance(User.class));
                System.out.println(user.getUsername());
            }
        }
        confirmButton.addClickListener(new MyClickListener());

        // Logout button
        this.securityService = securityService;

        H1 logo = new H1("Vaadin CRM");
        logo.addClassName("logo");
        HorizontalLayout header;
        if (securityService.getAuthenticatedUser() != null) {
            Button logout = new Button("Logout", click ->
                    securityService.logout());
            add(logout);
        }

        add(img, textField, passwordField, confirmButton);

        // get user role
        Label label = new Label(SecurityUtils.getUserType().toString());
        Label label2 = new Label(new Boolean(SecurityUtils.getUserType().toString().contains("admin")).toString());
        add(label, label2);

        setHeightFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

}
