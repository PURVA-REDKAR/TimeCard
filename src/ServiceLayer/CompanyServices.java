package ServiceLayer;
// JAX-RS: Java API for REST Service

import companydata.*;

import javax.ws.rs.*;

@Path("CompanyServices")
public class CompanyServices {

    @Path("delete")
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String deleteCompany(
             @QueryParam("company") String company
    ){
        Department dept = new Department(company,3044);
        dept.getDeptNo();

        return "{\"Area\":\"" + company + "\"}";
    }

}
