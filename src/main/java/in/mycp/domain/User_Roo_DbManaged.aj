// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package in.mycp.domain;

import in.mycp.domain.AccountLog;
import in.mycp.domain.Asset;
import in.mycp.domain.Department;
import in.mycp.domain.Project;
import in.mycp.domain.Role;
import in.mycp.domain.User;
import in.mycp.domain.Workflow;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

privileged aspect User_Roo_DbManaged {
    
    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<Project> User.projects;
    
    @OneToMany(mappedBy = "userId")
    private Set<AccountLog> User.accountLogs;
    
    @OneToMany(mappedBy = "user")
    private Set<Asset> User.assets;
    
    @OneToMany(mappedBy = "user")
    private Set<Workflow> User.workflows;
    
    @ManyToOne
    @JoinColumn(name = "department", referencedColumnName = "id")
    private Department User.department;
    
    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    private Role User.role;
    
    @Column(name = "email", length = 45, unique = true)
    @NotNull
    private String User.email;
    
    @Column(name = "password", length = 90)
    private String User.password;
    
    @Column(name = "registereddate")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date User.registereddate;
    
    @Column(name = "active")
    private Boolean User.active;
    
    @Column(name = "loggedInDate")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date User.loggedInDate;
    
    @Column(name = "firstName", length = 90)
    private String User.firstName;
    
    @Column(name = "lastName", length = 90)
    private String User.lastName;
    
    @Column(name = "phone")
    private Integer User.phone;
    
    @Column(name = "designation", length = 90)
    private String User.designation;
    
    @Column(name = "quota")
    private Integer User.quota;
    
    public Set<Project> User.getProjects() {
        return projects;
    }
    
    public void User.setProjects(Set<Project> projects) {
        this.projects = projects;
    }
    
    public Set<AccountLog> User.getAccountLogs() {
        return accountLogs;
    }
    
    public void User.setAccountLogs(Set<AccountLog> accountLogs) {
        this.accountLogs = accountLogs;
    }
    
    public Set<Asset> User.getAssets() {
        return assets;
    }
    
    public void User.setAssets(Set<Asset> assets) {
        this.assets = assets;
    }
    
    public Set<Workflow> User.getWorkflows() {
        return workflows;
    }
    
    public void User.setWorkflows(Set<Workflow> workflows) {
        this.workflows = workflows;
    }
    
    public Department User.getDepartment() {
        return department;
    }
    
    public void User.setDepartment(Department department) {
        this.department = department;
    }
    
    public Role User.getRole() {
        return role;
    }
    
    public void User.setRole(Role role) {
        this.role = role;
    }
    
    public String User.getEmail() {
        return email;
    }
    
    public void User.setEmail(String email) {
        this.email = email;
    }
    
    public String User.getPassword() {
        return password;
    }
    
    public void User.setPassword(String password) {
        this.password = password;
    }
    
    public Date User.getRegistereddate() {
        return registereddate;
    }
    
    public void User.setRegistereddate(Date registereddate) {
        this.registereddate = registereddate;
    }
    
    public Boolean User.getActive() {
        return active;
    }
    
    public void User.setActive(Boolean active) {
        this.active = active;
    }
    
    public Date User.getLoggedInDate() {
        return loggedInDate;
    }
    
    public void User.setLoggedInDate(Date loggedInDate) {
        this.loggedInDate = loggedInDate;
    }
    
    public String User.getFirstName() {
        return firstName;
    }
    
    public void User.setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String User.getLastName() {
        return lastName;
    }
    
    public void User.setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Integer User.getPhone() {
        return phone;
    }
    
    public void User.setPhone(Integer phone) {
        this.phone = phone;
    }
    
    public String User.getDesignation() {
        return designation;
    }
    
    public void User.setDesignation(String designation) {
        this.designation = designation;
    }
    
    public Integer User.getQuota() {
        return quota;
    }
    
    public void User.setQuota(Integer quota) {
        this.quota = quota;
    }
    
}
