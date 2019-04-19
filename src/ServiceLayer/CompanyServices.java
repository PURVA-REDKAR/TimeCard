package ServiceLayer;
// JAX-RS: Java API for REST Service

import com.google.gson.Gson;
import companydata.DataLayer;
import companydata.Department;
import companydata.Employee;
import companydata.Timecard;

import javax.ws.rs.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    Department dept = new Department( );
    DataLayer data = null;
    Gson j = new Gson( );
    Message m = new Message( );


    @Path( "company" )
    @DELETE
    @Produces( "application/json" )
    public String deleteCompany (
            @QueryParam( "company" ) String company
    ) {
        dept.setDeptName( company );
        try {
            if ( company.equals( null ) ) {
                m.setError( "company cannot be null " );
                return j.toJson( m );
            }
            data = new DataLayer( "production" );
            //Get all employess to delete that belong to the company
            List <Employee> employees = data.getAllEmployee( company );
            for ( Employee employe : employees ) {
                //Before deleting the employee delete the employess Time card
                List <Timecard> timecards = data.getAllTimecard( employe.getId( ) );
                for ( Timecard timecard : timecards ) {
                    data.deleteTimecard( timecard.getId( ) );
                }
                //Delete the Employee
                data.deleteEmployee( employe.getId( ) );
            }
            //Delete the company
            int rows = data.deleteCompany( company );
            if ( rows <= 0 ) {
                //if deletion was not possible
                m.setError( company + " does not exists" );
            } else {
                //if deletion was sucessfull
                m.setSuccess( company + " information deleted" );
            }
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
        return j.toJson( m );
    }

    @Path("department")
    @GET
    @Produces("application/json")
    public String getDepartment(
            @DefaultValue( "pr3044" ) @QueryParam( "company" ) String company,
            @QueryParam("dept_id") int dept_id
    ){
        try {
            //Validate Query Params
            if ( company.equals( null ) ) {
                m.setError( "company cannot be null " );
                return j.toJson( m );
            }
            if ( dept_id == 0 ) {
                m.setError( "dept_id cannot be 0 " );
                return j.toJson( m );
            }
            //initialize Data Layer
            data = new DataLayer( "production" );
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Get Departments
        Department department = data.getDepartment(company,dept_id);
        if(department == null){
             m.setError( company+" with  department id "+dept_id+" does not exists");
            return j.toJson( m );
        }
        else{
            m.setSuccess( department.toString( ) );
            return j.toJson(department);
        }

    }

    @Path("departments")
    @GET
    @Produces("application/json")
    public String getAllDepartment(
            @DefaultValue( "pr3044" ) @QueryParam( "company" ) String company
    ){
        try {
            //Validate Query Params
            if ( company.equals( null ) ) {
                m.setError( "company cannot be null " );
                return j.toJson( m );
            }
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //GET aLL Departments that belong to the comapany
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
            @DefaultValue( "pr3044" ) @FormParam( "company" ) String company,
            @FormParam("dept_name") String dept_name,
            @FormParam("dept_no") String dept_no,
            @FormParam("location") String location
    ){

        if ( company.equals( null ) ) {
            m.setError( "Company  cannot be null " );
            return j.toJson( m );
        }
        if ( dept_id == 0 ) {
            m.setError( "dept_id  cannot be null " );
            return j.toJson( m );
        }
        try {

            //initialize Data Layer
            data = new DataLayer("production");
            //GET the department
            Department departments = data.getDepartment(company,dept_id);
            if(departments == null){
                  m.setError( "department doesnt  exists ");
                  return j.toJson(m);
            }
            //set the variable that are required to update
            if ( !dept_name.equals( null ) ) {
                departments.setDeptName( dept_name );
            }
            if ( !dept_no.equals( null ) ) {
                departments.setDeptNo( dept_no );
            }
            if ( !company.equals( null ) ) {
                departments.setCompany( company );
            }
            if ( !location.equals( null ) ) {
                departments.setLocation( location );
            }

            departments = data.updateDepartment(departments);
            //Check if updation was Success
           if (departments.getId() > 0) {
                return j.toJson(departments);
           } else {
                m.setError( "cannot insert ");
                return j.toJson(m);
           }

        } catch (Exception e) {
            return "Form Params Missing Exception";
        }
    }

    @Path("department")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String insertDepartment(
            String department
    ){
        //Validate if JSON is present
        if ( department.equals( null ) ) {
            m.setError( "Input cannot be null " );
            return j.toJson( m );
        }
        try {
            // initialize DATA layer
            data = new DataLayer("production");
            //Extract JSON to variables
            Department departments = j.fromJson(department, Department.class);

            String company  = departments.getCompany();
            if ( company.equals( null ) ) {
                m.setError( "Company cannot be null " );
                return j.toJson( m );
            }
            String deptName  = departments.getDeptName();
            if ( deptName.equals( null ) ) {
                m.setError( "deptName cannot be null " );
                return j.toJson( m );
            }
            String deptNo  = departments.getDeptNo();
            if ( deptNo.equals( null ) ) {
                m.setError( "deptNo cannot be null " );
                return j.toJson( m );
            }
            String location  = departments.getLocation();
            if ( location.equals( null ) ) {
                m.setError( "location cannot be null " );
                return j.toJson( m );
            }
            //Check if Department Exists for the comapany
            List<Department> cdepartment = data.getAllDepartment(company);
            for(Department d : cdepartment ){
            if(d.getDeptNo().equals(deptNo) ){

                m.setError( company+" with deptNo"+deptNo +" and "+"  exists");
                return j.toJson(m);
            }
          }
            //Insert the Department
          Department updatedDepartments = data.insertDepartment(departments);
          return j.toJson(updatedDepartments);

        } catch (Exception e) {
            e.printStackTrace();
        }
        m.setError( " did not processs");
        return j.toJson(m);
    }

    @Path("department")
    @DELETE
    @Produces("application/json")
    public String deleteDepartment(
            @DefaultValue( "pr3044" ) @QueryParam( "company" ) String company,
            @QueryParam("dept_id") int dept_id
    ){

        try {
            //Validation for Query Params
            if ( company.equals( null ) ) {
                m.setError( "Company cannot be null " );
                return j.toJson( m );
            }
            if ( dept_id == 0 ) {
                m.setError( "dept_id cannot be 0 " );
                return j.toJson( m );
            }
            //Initialize Data Layer
            data = new DataLayer("production");
            int deleted = data.deleteDepartment(company,dept_id);
            //Check if deletion was Sucessfull
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
        if ( emp_id == 0 ) {
            m.setError( "emp_id cannot be 0" );
            return j.toJson( m );

        }
        try {
            //initialize Data layer
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //GET Employees
        Employee employee = data.getEmployee(emp_id);
        if(employee == null){
            //Error for employee
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
            @DefaultValue( "pr3044" ) @QueryParam( "company" ) String company
    ){
        try {
            //Initialize Data Layer
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //List all the employees
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
    public String insertEmployee(String employee){
        //Validate Employee
        if ( employee.equals( null ) ) {
            m.setError( "input  cannot be null" );
            return j.toJson( m );
        }
        try {
            //Initialize Data Layer
            data = new DataLayer("production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Convert employee to json
        Employee employees = j.fromJson(employee, Employee.class);
        //Validate input
         String emp_name = employees.getEmpName();
        if(emp_name == null){
            m.setError( "emp name cannot be null");
            return j.toJson(m);
        }
         String emp_no = employees.getEmpNo();
         if(emp_no == null){
              m.setError( "emp no cannot be null");
              return j.toJson(m);
         }
        //Date Conversions
         Date hire_dates1 = employees.getHireDate();
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         String hire_date = df.format(hire_dates1);

         String job = employees.getJob();
         if(job == null){
              m.setError( "job cannot be null");
              return j.toJson(m);
         }
         Double salary = employees.getSalary();
         if(salary == 0.00){
             m.setError( "salary cannot be 0.00");
             return j.toJson(m);
         }
         int dept_id = employees.getDeptId();
         if(dept_id == 0){
             m.setError( "dept_id cannot be 0");
             return j.toJson(m);
         }
         int mng_id = employees.getMngId();
         if(mng_id == 0){
            m.setError( "mng_id cannot be 0");
            return j.toJson(m);
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



        Date hire_dates = null;
        Date current_date = null;
        if(hire_date != null) {
            try {
                hire_dates = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            current_date = new Date();


            if ((hire_dates).after(current_date)) {

                m.setError(hire_date + " hire date should be before today's date");
                return j.toJson(m);
            }
        }
        else{

            m.setError(" hire date doesnt not exists");
            return j.toJson(m);
        }
        // Insert Employee
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
            @FormParam( "emp_name" ) String emp_name,
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

            //Validate Inputs
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
            //CHECK Dates
            Date hire_dates = null;
            Date current_date = null;
            if(hire_date != null) {
                 hire_dates = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);

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
            //Update Employee
            emp = data.updateEmployee(emp);

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
        if ( emp_id == 0 ) {
            m.setError( "Employee cannot be 0" );
            return j.toJson( m );
        }
        try {
            //initialize data layer
            data = new DataLayer("production");
            //Before deleting the employee delete the employess Time card
            List <Timecard> timecards = data.getAllTimecard( emp_id );
            for ( Timecard timecard : timecards ) {
                data.deleteTimecard( timecard.getId( ) );

            }
            //Delete Employee
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

    public String validateTimecard (Timecard timecard, Message m1) {

        // Timecard timecard = j.fromJson( timecards, Timecard.class );
        Timestamp start_time_t = timecard.getStartTime( );
        Timestamp end_time_t = timecard.getEndTime( );
        int emp_id = timecard.getEmpId( );
        List <Employee> emp = data.getAllEmployee( "pr3044" );

        int flag = 0;
        for ( Employee d : emp ) {
            if ( d.getId( ) == emp_id ) {
                flag = 1;
            }
        }

        if ( flag == 0 ) {

            m1.setError( " employee id " + emp_id + " does not exists in pr3044" );
            return j.toJson( m1 );
        }

        Date current_date = new Date( );
        Calendar c = Calendar.getInstance( );
        Calendar c1 = Calendar.getInstance( );
        Date sdate = new Date( start_time_t.getTime( ) );
        c.setTime( sdate );
        int syear = c.get( Calendar.YEAR );
        int smonth = c.get( Calendar.MONTH );
        int sday = c.get( Calendar.DAY_OF_MONTH );
        int shour = c.get( Calendar.HOUR );
        Date edate = new Date( end_time_t.getTime( ) );
        c1.setTime( edate );
        int eyear = c1.get( Calendar.YEAR );
        int emonth = c1.get( Calendar.MONTH );
        int eday = c1.get( Calendar.DAY_OF_MONTH );
        int ehour = c1.get( Calendar.HOUR );
        if ( (syear != eyear) || (smonth != emonth) || (sday != eday) ) {
            m1.setError( "start date " + sdate + " not equals to end date " + edate );
            return j.toJson( m1 );
        }
        if ( shour < 6 || shour > 18 ) {
            m1.setError( "start hours should be between the hours (in 24 hour format) of 06:00:00 and 18:00:00 " );
            return j.toJson( m1 );

        }
        if ( ehour < 6 || ehour > 18 ) {
            m1.setError( "end hours should be between the hours (in 24 hour format) of 06:00:00 and 18:00:00 " );
            return j.toJson( m1 );

        }
        c.setTime( start_time_t );
        c.add( Calendar.HOUR_OF_DAY, 1 );

        if ( end_time_t.before( start_time_t ) ) {
            m1.setError( "end time should be before start time " );
            return j.toJson( m1 );
        }

        c.setTime( current_date );
        c.add( Calendar.DATE, -7 );

        if ( start_time_t.after( c.getTime( ) ) && start_time_t.before( current_date ) ) {
            c.setTime( start_time_t );
            int week = c.get( Calendar.DAY_OF_WEEK );

            if ( week != 1 && week != 7 ) {
                c.setTime( end_time_t );
                int week_e = c.get( Calendar.DAY_OF_WEEK );
                if ( week != 0 && week != 7 ) {
                    List <Timecard> timecards1 = data.getAllTimecard( emp_id );
                    start_time_t = timecard.getStartTime( );
                    c.setTime( start_time_t );
                    int styear = c.get( Calendar.YEAR );
                    int stmonth = c.get( Calendar.MONTH );
                    int stday = c.get( Calendar.DAY_OF_MONTH );

                    for ( Timecard time : timecards1 ) {
                        Date itime = new Date( time.getStartTime( ).getTime( ) );
                        c1.setTime( itime );

                        int eiyear = c1.get( Calendar.YEAR );
                        int eimonth = c1.get( Calendar.MONTH );
                        int eiday = c1.get( Calendar.DAY_OF_MONTH );
                        if ( (eiyear == styear) && (stmonth == eimonth) && (stday == eiday) ) {
                            m1.setError( start_time_t + " already present " );
                            return j.toJson( m1 );
                        }
                    }
                } else {
                    m1.setError( " day should not be sunday or saturday " );
                    return j.toJson( m1 );

                }
            } else {
                m1.setError( " day should not be sunday or saturday " );
                return j.toJson( m1 );

            }
        } else {
            m1.setError( "start_time must be before either equal to current date or  up to 1 week ago from the current date " );
            return j.toJson( m1 );
        }

        return "true";
    }



    @Path("timecard")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String insertTimecard(String timecards){
        try {
            data = new DataLayer("production");
           } catch (Exception e) {
             e.printStackTrace();
           }


        Timecard timecard = j.fromJson( timecards, Timecard.class );

        int emp_id = timecard.getEmpId( );
        if ( emp_id == 0 ) {
            m.setError( "emp_id  cannot be null" );
            return j.toJson( m );
        }
        String check = validateTimecard( timecard, new Message( ) );


        if ( check.equals( "true" ) ) {


            Timestamp start_time_t = timecard.getStartTime( );
            if ( start_time_t.equals( null ) ) {
                m.setError( " start_time_t cannot be null " );
                return j.toJson( m );
            }
            Timestamp end_time_t = timecard.getEndTime( );
            if ( end_time_t.equals( null ) ) {
                m.setError( " end_time_t cannot be null " );
                return j.toJson( m );
            }

            timecard.setEndTime( end_time_t );
            timecard.setStartTime( start_time_t );
            timecard = data.insertTimecard( timecard );
            return j.toJson( timecard );

        } else {

            return j.toJson( check );
        }

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

        try {
            data = new DataLayer( "production" );
            if ( timecard_id == 0 ) {
                m.setError( "timecard_id  cannot be null" );
                return j.toJson( m );
            }
            Timecard timecard = data.getTimecard( timecard_id );

            return timecard.getId( ) + "here";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "did not process";

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


//         CompanyServices cs = new CompanyServices();
//        String jk = "{\"emp_id\":\"263\",\"start_time\":\"Apr 16, 2019 8:30:00 AM\",\"end_time\":\"Apr 16, 2019 18:30:00 AM\"}";
//        System.out.println(cs.insertTimecard(jk));
//         String jk = "{\"company\":\"pr3044\",\"dept_name\":\"CSE\",\"dept_no\":\"39444\",\"location\":\"buffalo\"}";
//        System.out.println( cs.insertDepartment(jk));
//        System.out.println( cs.UpdateEmployee(261,"frenchs","pr32","2012-12-12","prog",5000.0,298,263));
//        System.out.println( cs.updateTimecard(137,263,"2019-04-17 8:30:00","2019-04-17 18:31:00"));
//
//       String jk = "{\"emp_id\":\"263\",\"emp_name\":\"purva\",\"hire_date\":\"Dec 11, 2012\",\"job\":\"data analyst\",\"salary\":\"1000000\",\"dept_id\":\"298\",\"emp_no\":\"pr34449\",\"mng_id\":\"263\"}";
//        System.out.println(cs.insertEmployee(jk));
    //   }

}

