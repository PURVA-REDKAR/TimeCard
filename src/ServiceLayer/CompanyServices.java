package ServiceLayer;
// JAX-RS: Java API for REST Service

import companydata.DataLayer;
import companydata.Department;
import com.google.gson.Gson;
import companydata.Employee;
import companydata.Timecard;


import java.sql.Timestamp;
import java.text.ParseException;
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
    public String updateDepartment(
            @FormParam("dept_id") int dept_id,
            @FormParam("company") String company,
            @FormParam("dept_name") String dept_name,
            @FormParam("dept_no") String dept_no,
            @FormParam("location") String location
    ){

        try {


            data = new DataLayer("production");

            Department departments = data.getDepartment(company,dept_id);
            if(departments == null){
                  m.setError( "department doesnt  exists ");
                  return j.toJson(m);
            }

            departments.setDeptName(dept_name);
            departments.setDeptNo(dept_no);
            departments.setCompany(company);
            departments.setLocation(location);

            departments = data.updateDepartment(departments);

           if (departments.getId() > 0) {

                return j.toJson(departments);

           }

        else{
                m.setError( "cannot insert ");
                return j.toJson(m);

        }


        } catch (Exception e) {
            return "Exception";

        }


    }

    @Path("department")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String insertDepartment(
            //String department
           @QueryParam("company") String department
    ){
        try {
            data = new DataLayer("production");
            Gson j = new Gson();
            Department departments = j.fromJson(department, Department.class);
            String company  = departments.getCompany();
            String deptName  = departments.getDeptName();
            String deptNo  = departments.getDeptNo();
            String location  = departments.getLocation();
            System.out.println(company+"company"+deptName+"deptName"+deptNo+"location"+location);
            List<Department> cdepartment = data.getAllDepartment(company);
            for(Department d : cdepartment ){
            if(d.getDeptNo().equals(deptNo) ){

                m.setError( company+" with deptNo"+deptNo +" and "+"  exists");
                return j.toJson(m);
            }
          }

          Department updatedDepartments = data.insertDepartment(departments);
            System.out.println(updatedDepartments.getId()+"--no--"+updatedDepartments.getDeptName());
          //m.setSuccess(updatedDepartments.toString() );
          return j.toJson(updatedDepartments);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return j.toJson(m);
    }

    @Path("department")
    @DELETE
    @Produces("application/json")
    public String deleteDepartment(
            @QueryParam("company") String company,
            @QueryParam("dept_id") int dept_id
    ){

        try {
            data = new DataLayer("production");
            int deleted = data.deleteDepartment(company,dept_id);
            if (deleted >= 1) {
                m.setSuccess("Department"+dept_id+" from "+company+" deleted.");
            } else {
                m.setError("Department not deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
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


        long time2 = System.currentTimeMillis();
        java.sql.Date current_date = new java.sql.Date(time2);
        if (!(hire_date).before(current_date ) || !hire_date.equals(current_date)){

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
//                         int emp_id,
//                         String emp_name,
//                         String emp_no,
//                         String hire_date,
//                         String job,
//                         Double salary,
//                         int dept_id,
//                         int mng_id
            @FormParam("emp_id") int emp_id,
            @FormParam("emp_name") String emp_name,
            @FormParam("emp_no") String emp_no,
            @FormParam("hire_date") String hire_date,
            @FormParam("job") String job,
            @FormParam("salary") Double salary,
            @FormParam("dept_id") int dept_id,
            @FormParam("mng_id") int mng_id
       ){

        Employee emp ;
        try {
            data = new DataLayer("production");


            if(dept_id == 0){
                m.setError( "department id cannot be 0");
                return j.toJson(m);
            }
            Department dept = data.getDepartment("pr3044",dept_id);
            if(dept == null){

                m.setError( "Department with  department id "+dept_id+" does not exists for company pr3044");
                return j.toJson(m);

            }
            if(mng_id != 0) {
                emp = data.getEmployee(mng_id);
                if (emp == null) {
                    m.setError("Manager with  employee id " + mng_id + " does not exists");
                    return j.toJson(m);
                }
            }
            Date hire_dates = null;
            Date current_date = null;
            if(hire_date != null) {
                 hire_dates = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);

                long time2 = System.currentTimeMillis();
                current_date = new Date();


                if ((hire_dates).after(current_date)) {

                    m.setError(hire_date + " hire date should be before today's date");
                    return j.toJson(m);
                }
            }

            emp = data.getEmployee(emp_id);
            if(emp == null){
                m.setError( " employee id "+emp_id+" does not exists");
                return j.toJson(m);
            }
            emp = data.getEmployee(emp_id);

            System.out.println(emp.getId());
            if(emp_name != null) {
                emp.setEmpName(emp_name);
            }
            if(emp_no != null){
            emp.setEmpNo(emp_no);
            }
            if(hire_dates != null) {

                java.sql.Date sqlDate = new java.sql.Date(hire_dates.getTime());
                emp.setHireDate(sqlDate);
            }
            if(salary != 0){
             emp.setSalary(salary);
            }
            if(mng_id != 0) {
                emp.setMngId(mng_id);
            }
            if(dept_id != 0) {
                emp.setDeptId(dept_id);
            }
            if(job != null) {
                emp.setJob(job);
            }
            emp = data.updateEmployee(emp);
            System.out.println(emp);
            if(emp == null){
                m.setError( "could not update");
                return j.toJson(m);
            }
            return j.toJson(emp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return j.toJson(m);
    }
    @Path("employee")
    @DELETE
    @Produces("application/json")
    public String deleteEmployee(
            @QueryParam("emp_id") int emp_id
    ){
        try {
            data = new DataLayer("production");
            int deleted = data.deleteEmployee(emp_id);
            if (deleted >= 1) {
                m.setSuccess("Employee "+emp_id+" deleted.");
            } else {
                m.setError("Employee not deleted check emp id");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    @GET
    @Produces("application/json")
    @Consumes("text/plain")
    public String getAllTimecard(
            @QueryParam("timecard_id") int timecard_id
    ){
        try {
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timecard timecards = data.getTimecard(timecard_id);
        if(timecards == null){
            m.setError( timecard_id+" does not exists");
            return j.toJson(m);
        }
        else{

            return j.toJson(timecards);
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
      //  Timestamp start_time = timecard.getStartTime();
        //Timestamp end_time = timecard.getEndTime();
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
    @PUT
    @Produces("application/json")
    public String updateTimecard(
            @FormParam("timecard_id") int timecard_id,
            @FormParam("emp_id") int emp_id,
            @FormParam("start_time") String start_time,
            @FormParam("end_time") String end_time
    ){

       List<Employee> emp = data.getAllEmployee("pr3044");

        int flag =0;
        for(Employee d : emp ){
            if(d.getId() == emp_id){
               flag = 1;
            }
        }

        if(flag == 0 ){

            m.setError( " employee id "+emp_id+" does not exists in pr3044");
            return j.toJson(m);
        }
        try {
            data = new DataLayer("production");
            Timestamp start_time_t = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time).getTime());
            Timestamp end_time_t = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time).getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";

    }

    @Path("timecard")
    @DELETE
    @Produces("application/json")
    public String deleteTimecard(
            @QueryParam("timecard_id") int timecard_id
    ){
        try {
            data = new DataLayer("production");
            int deleted = data.deleteTimecard(timecard_id);
            if (deleted >= 1) {
                m.setSuccess("Time card "+timecard_id+" deleted.");
            } else {
                m.setError("Time card not deleted check timecard_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return j.toJson(m);
    }
//    public static void main(String args[]) {
//
//        DataLayer data = null;
//        Gson j = new Gson();
//        Message m = new Message();
//        CompanyServices cs = new CompanyServices();
//      //  String jk = "{\"company\":\"pr3044\",\"dept_name\":\"CSE\",\"dept_no\":\"39444\",\"location\":\"buffalo\"}";
//      //  System.out.println(cs.insertDepartment(jk));
//        System.out.println( cs.UpdateEmployee(261,"frenchs","pr32","2012-12-12","prog",5000.0,298,263));
//    }

}

