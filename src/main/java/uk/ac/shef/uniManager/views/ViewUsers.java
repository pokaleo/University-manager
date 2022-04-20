package uk.ac.shef.uniManager.views;

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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.ac.shef.uniManager.DAO.UserDAO;
import uk.ac.shef.uniManager.model.User;
import uk.ac.shef.uniManager.utils.SecurityService;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PageTitle("Admin Dashboard")
@Route(value = "/viewUsers", layout = MainLayout.class)
@RolesAllowed("ROLE_admin")
public class ViewUsers extends Div {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    TextField filterText = new TextField();
    private Grid<User> grid = new Grid<>();
    private Editor<User> editor = grid.getEditor();
    private Button addButton = new Button("Add");
    private SplitLayout splitLayout;
    private TextField userID;
    private TextField username;
    private List<String> userTypes = new ArrayList<>(Arrays.asList("admin", "student", "teacher", "registrar"));
    private ComboBox<String> userType;
    private ComboBox<String> userType2;
    private PasswordField passwordField;

    public ViewUsers(@Autowired SecurityService securityService) {
        splitLayout = new SplitLayout();

        // set up the form
        UserDAO userDao = new UserDAO();
        List<User> userList = userDao.getUserList(new User());
        grid.setItems(userList);
        Grid.Column<User> userIDColumn = grid.addColumn(User::getUserID).setHeader("UserID").setAutoWidth(true);
        Grid.Column<User> userNameColumn = grid.addColumn(User::getUsername).setHeader("Username").setAutoWidth(true);
        Grid.Column<User> userTypeColumn = grid.addColumn(User::getType).setHeader("UserType").setAutoWidth(true);
        Grid.Column<User> editColumn = grid.addComponentColumn(user -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(user);
            });
            return editButton;
        }).setWidth("30%").setFlexGrow(0);

        grid.setHeightFull();

        // set up the in-line editor
        Binder<User> userBinder = new Binder<>(User.class);
        editor.setBinder(userBinder);
        editor.setBuffered(true);

        TextField userName = new TextField();
        userBinder.forField(userName).asRequired("Username must not be empty!")
                        .bind(User::getUsername, User::setUsername);
        userNameColumn.setEditorComponent(userName);

        userType = new ComboBox<>("UserType");
        userType.setAllowCustomValue(true);
        userType.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            userTypes.add(customValue);
            userType.setItems(userTypes);
            userType.setValue(customValue);
        });
        userType.setItems(userTypes);
        userBinder.forField(userType).asRequired("UserType must not be empty!")
                .bind(User::getType, User::setType);
        userTypeColumn.setEditorComponent(userType);

        Button saveButton = new Button("Save", e -> {
            try {
                User userToBeUpdated = editor.getItem();
                editor.save();
                userDao.update(userToBeUpdated);
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        });

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        Button deleteButton = new Button("Delete", e -> {
            userDao.delete(editor.getItem().getUserID());
            fetchUsers();
        });
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Search users
        GridListDataView<User> dataView = grid.setItems(userList);
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(user -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesUserID = matchesTerm(String.valueOf(user.getUserID()),
                    searchTerm);
            boolean matchesUsername = matchesTerm(user.getUsername(), searchTerm);
            boolean matchesUserType = matchesTerm(user.getType(),
                    searchTerm);

            return matchesUserID || matchesUsername || matchesUserType;
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(searchField, grid);
        splitLayout.addToPrimary(wrapper);

        // Add new user
        createEditorLayout(splitLayout);
        splitLayout.setSplitterPosition(60);
        setHeightFull();
        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        add(splitLayout);


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

//        // Logout button
//        this.securityService = securityService;
//
//        H1 logo = new H1("Vaadin CRM");
//        logo.addClassName("logo");
//        HorizontalLayout header;
//        if (securityService.getAuthenticatedUser() != null) {
//            Button logout = new Button("Logout", click ->
//                    securityService.logout());
//            add(logout);
//        }

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

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        username = new TextField("Username");
        userType2 = new ComboBox<>("UserType");
        userType2.setAllowCustomValue(true);
        userType2.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            userTypes.add(customValue);
            userType2.setItems(userTypes);
            userType2.setValue(customValue);
        });
        userType2.setItems(userTypes);

        passwordField = new PasswordField("Password");
        com.vaadin.flow.component.Component[] fields = new Component[]{username, userType2, passwordField};

        formLayout.add(fields);
        editorDiv.add(new H3("Add a new user:"));
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
            addNewUser();
        });
    }

    private void fetchUsers(){
        UserDAO userDao = new UserDAO();
        List<User> userList = userDao.getUserList(new User());
        grid.setItems(userList);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                populateForm(event.getValue());
            }
        });
    }

    private void populateForm(User user){
        userID.setValue(String.valueOf(user.getUserID()));
        username.setValue(user.getUsername());
        userType.setValue(user.getType());
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void addNewUser(){
        User userToBeAdded = new User();
        userToBeAdded.setUsername(username.getValue());
        userToBeAdded.setType(userType2.getValue());
        userToBeAdded.setPassword(new BCryptPasswordEncoder().encode(passwordField.getValue()));
        UserDAO userDao = new UserDAO();
        if(userDao.addUser(userToBeAdded)){
            Notification notification = Notification.show("Successfully added!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }else{
            Notification notification = Notification.show("Failed!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        }
        fetchUsers();
    }


}
