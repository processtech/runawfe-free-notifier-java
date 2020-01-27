/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.runa.notifier.util;

/**
 * 
 *
 * @author eugene
 */


public class ServerUrl {
    
    private String url = ResourcesManager.getDefaultServerUrl();
    
    public static final ServerUrl SERVER_URL = new ServerUrl();
    
    public String getUrl(){
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
}
