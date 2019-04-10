package ServiceLayer;
// JAX-RS: Java API for REST Service

import companydata.DataLayer;
import companydata.Department;
import com.google.gson.Gson;

import javax.ws.rs.*;


@Path("CompanyServices")
public class CompanyServices {

    @Path("delete")
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String deleteCompany(
            @QueryParam("company") String company
    ) {
        Department dept = new Department();
        Gson j = new Gson();
        dept.setDeptName(company);
        DataLayer data = null;
        try {
            data = new DataLayer("production");
            int rows = data.deleteCompany(company);
            if (rows < 0) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "{\"Area\":\"" + company + "\"}";
    }

}
