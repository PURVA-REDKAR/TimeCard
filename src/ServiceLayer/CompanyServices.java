package ServiceLayer;
// JAX-RS: Java API for REST Service

import companydata.DataLayer;
import companydata.Department;
import com.google.gson.Gson;
import companydata.Employee;
import companydata.Timecard;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.ws.rs.*;


import java.text.SimpleDateFormat;
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

    @Path("departments")
    @GET
    @Produces("application/json")
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
    public String insertDepartment(
            @FormParam("company") String department
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

    @Path("department")
    @DELETE
    @Produces("application/json")
    public String deleteDepartment(
            @QueryParam("company") String company,
            @QueryParam("dept_id") int dept_id
    ){
        int deleted = data.deleteDepartment(company,dept_id);
        if (deleted >= 1) {
            m.setSuccess("Department"+dept_id+" from "+company+" deleted.");
         } else {
            m.setError("Department not deleted");
         }
        return j.toJson(m);
    }


    @Path("employee")
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String getEmployee(
            @QueryParam("emp_id") int emp_id
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Employee employee = data.getEmployee(emp_id);
        if(employee == null){
            m.setError( "Employee with  department id "+emp_id+" does not exists");
            return j.toJson(m);
        }
        else{

            return j.toJson(employee);
        }

    }

    @Path("employees")
    @GET
    @Produces("application/json")
    public String getAllEmployee(
            @QueryParam("company") String company
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Employee> employees = data.getAllEmployee(company);
        if(employees.size() == 0){
            m.setError( company+" does not exists");
            return j.toJson(m);
        }
        else{

            return j.toJson(employees);
        }

    }


    @Path("employee")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String insertEmployee(
            @QueryParam("company") String employee
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Employee employees = j.fromJson(employee, Employee.class);

         String emp_name = employees.getEmpName();
         String emp_no = employees.getEmpNo();
         Date hire_date = employees.getHireDate();
         String job = employees.getJob();
         Double salary = employees.getSalary();
         int dept_id = employees.getDeptId();
         int mng_id = employees.getMngId();

         Department dept = data.getDepartment("pr3044",dept_id);
         if(dept == null){

             m.setError( "Department with  department id "+dept_id+" does not exists for company pr3044");
             return j.toJson(m);

         }
        Employee emp = data.getEmployee(mng_id);
         if(emp == null){
             m.setError( "Manager with  employee id "+mng_id+" does not exists");
             return j.toJson(m);
         }

        if (!(hire_date).before(new Date()) || !hire_date.equals(new Date())){

            m.setError( hire_date+" hire date should be before today's date");
            return j.toJson(m);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(hire_date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(day == 0 || day == 7){
            m.setError( hire_date+" hire day  cannot be saturday or sunday");
            return j.toJson(m);
        }
        employees.setId(30444);

         emp = data.insertEmployee(employees);
        if (emp.getId() > 0) {
           return j.toJson(emp);
        } else {
            return j.toJson("error while inserting record");
        }
    }

    @Path("employee")
    @PUT
    @Produces("application/json")
    public String UpdateEmployee(
            @FormParam("emp_id") int emp_id,
            @FormParam("emp_name") String emp_name,
            @FormParam("emp_no") String emp_no,
            @FormParam("hire_date") Date hire_date,
            @FormParam("job") String job,
            @FormParam("salary") Double salary,
            @FormParam("dept_id") int dept_id,
            @FormParam("mng_id") int mng_id
       ){

        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }


        Department dept = data.getDepartment("pr3044",dept_id);
        if(dept == null){

            m.setError( "Department with  department id "+dept_id+" does not exists for company pr3044");
            return j.toJson(m);

        }
        Employee emp = data.getEmployee(mng_id);
        if(emp == null){
            m.setError( "Manager with  employee id "+mng_id+" does not exists");
            return j.toJson(m);
        }

        if (!(hire_date).before(new Date()) || !hire_date.equals(new Date())){

            m.setError( hire_date+" hire date should be before today's date");
            return j.toJson(m);
        }

         emp = data.getEmployee(emp_id);
        if(emp == null){
            m.setError( " employee id "+emp_id+" does not exists");
            return j.toJson(m);
        }
        emp.setId(emp_id);
        emp.setEmpName(emp_name);
        emp.setEmpNo(emp_no);
        emp.setHireDate((java.sql.Date) hire_date);
        emp.setSalary(salary);
        emp.setMngId(mng_id);
        emp.setDeptId(dept_id);
        emp.setJob(job);
        emp = data.updateEmployee(emp);
        if(emp == null){
            m.setError( "could not update");
            return j.toJson(m);
        }

         return j.toJson(emp);
    }
    @Path("employee")
    @DELETE
    @Produces("application/json")
    public String deleteEmployee(
            @QueryParam("emp_id") int emp_id
    ){
        int deleted = data.deleteEmployee(emp_id);
        if (deleted >= 1) {
            m.setSuccess("Employee "+emp_id+" deleted.");
        } else {
            m.setError("Department not deleted");
        }
        return j.toJson(m);

    }

    @Path("timecards")
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String getAllTimecards(
            @QueryParam("emp_id") int emp_id
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Timecard> Timecards = data.getAllTimecard(emp_id);
        if(Timecards.size() == 0){
            m.setError( emp_id+" does not exists");
            return j.toJson(m);
        }
        else{

            return j.toJson(Timecards);
        }

    }

    @Path("timecard")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String insertTimecard(
            @QueryParam("company") String timecards
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timecard timecard = j.fromJson(timecards, Timecard.class);


        int timecard_id = timecard.getId();
        Timestamp start_time = timecard.getStartTime();
        Timestamp end_time = timecard.getEndTime();
        int emp_id = timecard.getId();

        Employee emp = data.getEmployee(emp_id);
        if(emp == null){
            m.setError( " employee id "+emp_id+" does not exists");
            return j.toJson(m);
        }
        Timecard t = data.insertTimecard(timecard);


        return j.toJson(t);

    }

    @Path("timecard")
    @POST
    @Produces("application/json")
    public String updateTimecard(
            @FormParam("timecard_id") int timecard_id,
            @FormParam("emp_id") int emp_id,
            @FormParam("start_time") String start_time,
            @FormParam("end_time") String end_time
    ){




        return "";

    }

}

