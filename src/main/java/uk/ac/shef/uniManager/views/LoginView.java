package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        login.setForgotPasswordButtonVisible(false);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");

        add(new H1("University Management System"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }

//    @Override
//    public void beforeLeave(BeforeLeaveEvent event) {
//        if (SecurityUtils.isUserLoggedIn()){
//            // get user role
//            String userType = SecurityUtils.getUserType();
//            if("admin".equals(userType)){
//                Notification notification = Notification.show("Welcome, directing you to the admin dashboard...");
//                notification.setPosition(Notification.Position.MIDDLE);
//                ConfirmDialog dialog = new ConfirmDialog();
//                dialog.setHeader("Export failed");
//                dialog.setText(new Paragraph("Welcome, directing you to the admin dashboard..."));
//                dialog.setConfirmText("OK");
//                dialog.open();
//                event.forwardTo(Login.class);
//            }
//        }
//    }
}
