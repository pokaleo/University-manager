package uk.ac.shef.uniManager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.shef.uniManager.utils.SecurityService;
import uk.ac.shef.uniManager.utils.SecurityUtils;

import javax.annotation.security.PermitAll;


@PageTitle("Login Successfully")
@Route(value = "")
@PermitAll
public class LandingView extends HorizontalLayout implements BeforeEnterObserver {
    @Autowired
    private SecurityService securityService;

    public LandingView(@Autowired SecurityService securityService) {

        Label label = new Label(SecurityUtils.getUserType().toString());
        Label label2 = new Label(new Boolean(SecurityUtils.getUserType().toString().contains("admin")).toString());
        add(label, label2);

        // Logout button
        this.securityService = securityService;

        H1 logo = new H1("Welcome, " + SecurityUtils.getUserType().toString());
        logo.addClassName("logo");
        if (securityService.getAuthenticatedUser() != null) {
            Button logout = new Button("Logout", click ->
                    securityService.logout());
            add(logout);
        }
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.BETWEEN);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()){
            // get user role
            String userType = SecurityUtils.getUserType();
            if("ROLE_admin".equals(userType)){
                Notification notification = Notification.show("Login successfully! Your role is admin.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.TOP_CENTER);
                event.forwardTo(ViewUsers.class);
            }
        }
    }
}
