package fr.upec.espisen.service;

public interface ServerRunningMBean {
    Boolean getRunning();

    void setRunning(Boolean running);
}