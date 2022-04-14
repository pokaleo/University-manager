package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import uk.ac.shef.uniManager.DAO.UserDAO;
import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.SecurityService;
import uk.ac.shef.uniManager.utils.SecurityUtils;
import uk.ac.shef.uniManager.utils.StringUtil;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;
import java.util.List;

@PageTitle("Admin Dashboard")
@Route(value = "/admin", layout = MainLayout.class)
@RolesAllowed("ROLE_admin")
public class Admin extends VerticalLayout {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LoginForm login = new LoginForm();
    private SecurityService securityService;
    TextField filterText = new TextField();

    public Admin(@Autowired SecurityService securityService) {
        VerticalLayout layout = new VerticalLayout();

        Grid<User> grid = new Grid<>();
        Editor<User> editor = grid.getEditor();
        UserDAO userDao = new UserDAO();
        List<User> userList = userDao.getUserList(new User());
        grid.setItems(userList);
        grid.addColumn(User::getUserID).setHeader("UserID");
        grid.addColumn(User::getUsername).setHeader("Username");
        grid.addColumn(User::getType).setHeader("UserType");
        grid.addComponentColumn(user -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(user);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        add(grid);


//        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//        for (Object obj:
//             authorities) {
//            System.out.println(obj.toString());
//        }
//        VaadinServletRequest request = VaadinServletRequest.getCurrent();
//        System.out.println(request.getUserPrincipal());
//        SecurityContext context = SecurityContextHolder.getContext();
//        Authentication authentication = context.getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        System.out.println("User has authorities: " + userDetails.getAuthorities());
//
//        Image img = new Image("images/login2.jpg", "login logo2");
//        img.setWidth("200px");
//
//        Button confirmButton = new Button("Confirm");
//
//        TextField textField = new TextField();
//        textField.setLabel("username");
//        textField.setRequiredIndicatorVisible(true);
//        textField.setErrorMessage("This field is required");
//
//        PasswordField passwordField = new PasswordField();
//        passwordField.setLabel("password");
//        passwordField.setRequiredIndicatorVisible(true);
//        passwordField.setErrorMessage("This field is required");
//
//        class MyClickListener implements ComponentEventListener<ClickEvent<Button>> {
//            int count = 0;
//            @Override
//            public void onComponentEvent(ClickEvent<Button> event) {
//                event.getSource().setText("You have clicked me " +
//                        (++count) + " times");
//                String userName = textField.getValue();
//                String password = passwordField.getValue();
//                if (StringUtil.isEmpty(userName)) {
//                    Notification notification = Notification.show("username cannot be empty");
//                    notification.setPosition(Notification.Position.MIDDLE);
//                    notification.setDuration(2000);
//                }
//                if (StringUtil.isEmpty(password)) {
//                    Notification notification = Notification.show("password cannot be empty");
//                    notification.setPosition(Notification.Position.MIDDLE);
//                    notification.setDuration(2000);
//                }
//                System.out.println(userName+password);
//                String sql = "SELECT * FROM users WHERE userID = 2";
//                UserView user = jdbcTemplate.queryForObject(sql,
//                        BeanPropertyRowMapper.newInstance(UserView.class));
//                System.out.println(user.getUsername());
//            }
//        }
//        confirmButton.addClickListener(new MyClickListener());

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

//        add(img, textField, passwordField, confirmButton);
//
//        // get user role
//        Label label = new Label(SecurityUtils.getUserType().toString());
//        Label label2 = new Label(new Boolean(SecurityUtils.getUserType().toString().contains("admin")).toString());
//        add(label, label2,getToolbar());
//
//        setHeightFull();
//        setAlignItems(Alignment.CENTER);
//        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button addContactButton = new Button("Add contact");

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

}
