package com.sales.management.dto;

public class UserDailyReportDto {

    private long totalLeads;
    private long leadsToday;

    private long totalTasks;
    private long tasksToday;
    private long completedTasksToday;

    private long totalProjects;
    private long activeProjects;

    // ===== getters & setters =====

    public long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public long getLeadsToday() {
        return leadsToday;
    }

    public void setLeadsToday(long leadsToday) {
        this.leadsToday = leadsToday;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getTasksToday() {
        return tasksToday;
    }

    public void setTasksToday(long tasksToday) {
        this.tasksToday = tasksToday;
    }

    public long getCompletedTasksToday() {
        return completedTasksToday;
    }

    public void setCompletedTasksToday(long completedTasksToday) {
        this.completedTasksToday = completedTasksToday;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(long activeProjects) {
        this.activeProjects = activeProjects;
    }
}
