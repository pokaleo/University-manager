package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import uk.ac.shef.uniManager.DAO.UserDAO;
import uk.ac.shef.uniManager.model.User;

import javax.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Admin Dashboard")
@Route(value = "/test")
@PermitAll
public class UserView extends Div {
    private Grid<User> grid = new Grid<>();
    private Editor<User> editor = grid.getEditor();
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private List<User> userList;

    public UserView(){
        UserDAO userDao = new UserDAO();
        userList = userDao.getUserList(new User());

        SplitLayout splitLayout = new SplitLayout();

        grid.setItems(userList);
        grid.addColumn(User::getUserID).setHeader("UserID").setAutoWidth(true);
        grid.addColumn(User::getUsername).setHeader("Username").setAutoWidth(true);
        grid.addColumn(User::getType).setHeader("UserType").setAutoWidth(true);
        grid.addComponentColumn(user -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(user);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        grid.setHeight("100%");
        splitLayout.addToPrimary(grid);

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        TextField email = new TextField("Email");
        TextField phone = new TextField("Phone");
        TextField occupation = new TextField("Occupation");
        Component[] fields = new Component[]{firstName, lastName, email, phone, occupation};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        editorDiv.setHeight("100%");

        splitLayout.addToSecondary(editorLayoutDiv);

        splitLayout.setSplitterPosition(70);
        setHeight("100%");
        splitLayout.setHeight("100%");
        add(splitLayout);


    }

}

