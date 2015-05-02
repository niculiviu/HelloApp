package com.example.liviu.helloapp;

/**
 * Created by liviu on 26.04.2015.
 */
public class Projects {


    private String project_name;
    private String project_description;
    private String id;
    private String organization;

    public Projects() {
    }

    public Projects(String project_name, String project_description, String id) {
        super();
        this.project_name = project_name;
        this.project_description = project_description;
        this.id = id;
    }

    public Projects(String project_name, String project_description, String id, String organization) {
        super();
        this.project_name = project_name;
        this.project_description = project_description;
        this.id = id;
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_description() {
        return project_description;
    }

    public void setProject_description(String project_description) {
        this.project_description = project_description;
    }
}
