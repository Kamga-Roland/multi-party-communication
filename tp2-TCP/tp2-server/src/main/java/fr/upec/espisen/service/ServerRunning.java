package fr.upec.espisen.service;

import fr.upec.espisen.Main;

public  class ServerRunning implements ServerRunningMBean {

    @Override
    public Boolean getRunning() {
        return Main.getRunning();
    }

    @Override
    public void setRunning(Boolean running) {
        Main.setRunning(running);
        Main.logger.info("Server status updated via JMX: {}", running);
    }
    
}
