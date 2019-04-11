package ServiceLayer;
// JAX-RS: Java API for REST Service

import companydata.DataLayer;
import companydata.Department;
import com.google.gson.Gson;

import javax.ws.rs.*;
import java.util.List;

class Message{

    private String error;
    private String success;

    Message() {

    }
    public void setError(String error) {
        this.error = error;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }
    public String getError() {
        return error;
    }
}

@Path("CompanyServices")
public class CompanyServices {

    Department dept = new Department();
    DataLayer data = null;
    Gson j = new Gson();
    Message m = new Message();

    @Path("company")
    @DELETE
    @Produces("application/json")
    @Consumes("text/plain")
    public String deleteCompany(
            @QueryParam("company") String company
    ) {
        dept.setDeptName(company);

        try {
            data = new DataLayer("production");
            int rows = data.deleteCompany(company);
            if (rows <= 0) {

               m.setError( company+" does not exists");


            }
            else {
                m.setSuccess(company + " information deleted");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return j.toJson(m);
    }

    @Path("department")
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String getDepartment(
             @QueryParam("company") String company,
             @QueryParam("dept_id") int dept_id
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Department department = data.getDepartment(company,dept_id);
        if(department == null){
             m.setError( company+" with  department id "+dept_id+" does not exists");
            return j.toJson(m);
        }
        else{

            return j.toJson(department);
        }

    }

    @Path("department")
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String getAllDepartment(
            @QueryParam("company") String company
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Department> departments = data.getAllDepartment(company);
        if(departments.size() == 0){
            m.setError( company+" does not exists");
            return j.toJson(m);
        }
        else{

            return j.toJson(departments);
        }

    }

    @Path("department")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    public String insertDepartment(
            @QueryParam("company") String department
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Department departments = j.fromJson(department, Department.class);
        String company  = departments.getCompany();
        String deptName  = departments.getDeptName();
        String deptNo  = departments.getDeptNo();
        String location  = departments.getLocation();
        int DeptId = departments.getId();

        Department ndepartment = data.getDepartment(company,DeptId);
        if(ndepartment == null){
            data.insertDepartment(departments);
            if (departments.getId() > 0) {
                j.toJson(departments);
            }
            else{
                m.setError( "cannot insert ");
            }
        }
        else{
            m.setError( "department already exists ");
        }



        return j.toJson(m);
    }

    @Path("department")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String updateDepartment(
            @QueryParam("company") String department
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Department departments = j.fromJson(department, Department.class);
        String company  = departments.getCompany();
        String deptName  = departments.getDeptName();
        String deptNo  = departments.getDeptNo();
        String location  = departments.getLocation();
        List<Department> cdepartment = data.getAllDepartment(company);
        for(Department d : cdepartment ){
            if(d.getDeptNo() == deptNo && d.getCompany()== company ){

                m.setError( company+" with deptNo"+deptNo +" and "+"  exists");
                return j.toJson(m);
            }
        }
        Department updatedDepartments = data.updateDepartment(departments);
        m.setSuccess(updatedDepartments.toString() );
        return j.toJson(m);
    }
    public static void main(String args[]) {

        CompanyServices cs = new CompanyServices();
       System.out.println( cs.updateDepartment("pr3044"));
    }

}
