package controllers;

/**
 * Created by DanielDluyz on 28/04/15.
 */

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.tenant.*;
import com.stormpath.sdk.application.*;
import com.stormpath.sdk.application.*;
import com.stormpath.sdk.account.*;
import com.stormpath.sdk.authc.*;
import com.stormpath.sdk.resource.ResourceException;

public class Stormpath {

    private static Stormpath stormpath = new Stormpath();

    private String path = "./stormpath/apiKey.properties";
    private Client client;
    private Application application;
    private Tenant tenant;

    private Stormpath() {

        ApiKey apiKey = ApiKeys.builder().setFileLocation(path).build();
        client = Clients.builder().setApiKey(apiKey).build();

        tenant = client.getCurrentTenant();
        ApplicationList applications = tenant.getApplications(
                Applications.where(Applications.name().eqIgnoreCase("Aura"))
        );

        application = applications.iterator().next();

    }

    public static Stormpath getInstance( ) {
        return stormpath;
    }

    protected Client getClient(){
        return client;
    }

    protected Application getApplication(){
        return application;
    }

    protected Tenant getTenant(){
        return tenant;
    }

    protected boolean addPatientToGroup(Account account)
    {
        DirectoryList directories = tenant.getDirectories(
                Directories.where(
                        Directories.name().eqIgnoreCase("Aura Directory")
                )
        );

        Directory directory = null;
        for(Directory dir : directories) {
            if(dir.getName().equalsIgnoreCase("Aura Directory")) {
                directory = dir;
                break;
            }
        }
        if( directory != null) {
            System.out.println("Href: " + directory.getHref());
        }


        GroupList groups = directory.getGroups();
        Group group = null;
        for(Group grp : groups) {
            if(grp.getName().equals("Patients")) {
                group = grp;
                break;
            }
        }

        if (group != null) {
            group.addAccount(account);
            return true;
        }
        else {
            return false;
        }
    }

    protected boolean addDoctorToGroup(Account account)
    {
        DirectoryList directories = tenant.getDirectories(
                Directories.where(
                        Directories.name().eqIgnoreCase("Aura Directory")
                )
        );

        Directory directory = null;
        for(Directory dir : directories) {
            if(dir.getName().equalsIgnoreCase("Aura Directory")) {
                directory = dir;
                break;
            }
        }
        if( directory != null) {
            System.out.println("Href: " + directory.getHref());
        }


        GroupList groups = directory.getGroups();
        Group group = null;
        for(Group grp : groups) {
            if(grp.getName().equals("Doctors")) {
                group = grp;
                break;
            }
        }

        if (group != null) {
            group.addAccount(account);
            return true;
        }
        else {
            return false;
        }
    }

    protected Account authenticate(String id, String rawPassword){

        //Create an authentication request using the credentials
        AuthenticationRequest request = new UsernamePasswordRequest(id, rawPassword);

        //Now let's authenticate the account with the application:
        try {
            AuthenticationResult result = application.authenticateAccount(request);
            Account account = result.getAccount();
            return account;
        } catch (ResourceException ex) {
            System.out.println(ex.getStatus() + " " + ex.getMessage());
        }

        return null;
    }


}
