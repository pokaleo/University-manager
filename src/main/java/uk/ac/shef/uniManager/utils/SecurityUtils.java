package uk.ac.shef.uniManager.utils;

import com.vaadin.flow.server.HandlerHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

public final class SecurityUtils {

    private SecurityUtils() {
        // Util methods only
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) { 
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(RequestType.values())
            .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
    }

    public static String getUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HashSet<String> userTypes = new HashSet<>();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            for (Object userType:
                    authentication.getAuthorities()) {
                userTypes.add(userType.toString());
            }
        }
//        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)
//                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//        for (Object obj:
//                authorities.toArray()) {
//            System.out.println(obj.toString());
//        }

        return userTypes.stream().findFirst().get();
    }

    public static boolean isAccessGranted(Class<?> securedClass) {
        // Allow if no roles are required.
        Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
        if (secured == null) {
            return true; // (1)
        }

        // lookup needed role in user roles
        List<String> allowedRoles = Arrays.asList(secured.value());
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return userAuthentication.getAuthorities().stream() // (2)
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);
    }
}