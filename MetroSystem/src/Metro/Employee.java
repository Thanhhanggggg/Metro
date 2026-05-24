package Metro;

public abstract class Employee {
	protected String employeeId;
    protected String name;
    protected String password;
    public Employee(String employeeId, String name, String password) {
        this.employeeId = employeeId;
        this.name = name;
        this.password = password;
    }
    public String getEmployeeId() {
        return employeeId;
    }
    public String getName() {
        return name;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    
    public boolean login(String inputPassword) {
        if(password.equals(inputPassword)) {
            System.out.println(name + " login successful!");
            return true;
        }
        System.out.println("Login failed!");
        return false;
    }
    
    public void logout() {
        System.out.println(name + " logged out!");
    }

}
