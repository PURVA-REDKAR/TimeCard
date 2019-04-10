package ServiceLayer;

import javax.ws.rs.core.Application;
import java.util.Set;

@javax.ws.rs.ApplicationPath("resources")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return getRestResourceClasses();
    }

    private Set<Class<?>> getRestResourceClasses() {
        Set<Class<?>> resources =
                new java.util.HashSet<Class<?>>();

        resources.add(ServiceLayer.CompanyServices.class);
        return resources;
    }
}
