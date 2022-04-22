package uk.ac.shef.uniManager.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.springframework.core.io.ResourceLoader;
import uk.ac.shef.uniManager.utils.SecurityService;
import uk.ac.shef.uniManager.utils.SecurityUtils;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        /**
         * Simple wrapper to create icons using LineAwesome icon set. See
         * https://icons8.com/line-awesome
         */
        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");



            // link.add(new LineAwesomeIcon(iconClass), text);
            Image icon = new Image("icons/"+ iconClass, "menu-icon");
            icon.setHeight("24px");
            icon.setWidth("24px");
            link.add(icon, text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }


    }

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("University Manager");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        if (SecurityUtils.isUserLoggedIn()){
            // get user role
            String userType = SecurityUtils.getUserType();
            if ("ROLE_admin".equals(userType)) {
                return new MenuItemInfo[]{
                        new MenuItemInfo("Manage Users", "users.svg", ViewUsers.class),

                        new MenuItemInfo("Manage Departments", "deps.svg", ViewDepartments.class),

                        new MenuItemInfo("Manage Degrees", "degs.svg", ViewDegrees.class),

                        new MenuItemInfo("Manage Modules", "mods.svg", ViewModules.class),

                        new MenuItemInfo("About", "mods.svg", Login2.class),
                };
            }
            if ("ROLE_registrar".equals(userType)) {
                return new MenuItemInfo[]{
                        new MenuItemInfo("Manage Students", "users.svg", ManageStudents.class),

                        new MenuItemInfo("Check Registrations", "registration.svg", CheckRegistration.class),

                        new MenuItemInfo("Check Credits", "modules.svg", CheckCredits.class),

                        new MenuItemInfo("Manage Modules", "mods.svg", ManageModules.class),
                };
            }
            if ("ROLE_teacher".equals(userType)) {
                return new MenuItemInfo[]{
                        new MenuItemInfo("Manage Grades", "grades.svg", ManageGrades.class),

                        new MenuItemInfo("Weighted grades & Progress", "mods.svg", MeanGrades.class),
                };
            }
        }
        return new MenuItemInfo[]{
        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");
        // Logout button

        H1 logo = new H1("Vaadin CRM");
        logo.addClassName("logo");
        SecurityService securityService = new SecurityService();
        if (securityService.getAuthenticatedUser() != null) {
            Button logout = new Button("Logout", click ->
                    securityService.logout());
            layout.add(logout);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
