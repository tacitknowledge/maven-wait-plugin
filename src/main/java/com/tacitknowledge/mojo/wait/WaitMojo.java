package com.tacitknowledge.mojo.wait;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The Mojo that waits on a certain URL to become available
 *
 * @author Vladimir Pertu (vpertu@tacitknowledge.com)
 * @goal wait
 */
public class WaitMojo extends AbstractMojo
{
    /** @parameter default-value="http" */
    String protocol;
    /** @parameter default-value="localhost" */
    String host;
    /** @parameter default-value="8080" */
    int port;
    /** @parameter default-value="" */
    String file;
    /** @parameter default-value="30000" */
    int timeout;
    /** @parameter default-value="0" */
    int maxcount;
    /** @parameter default-value="false" */
    boolean skip;
    /** @parameter default-value="false" */
    boolean read;

    /**
     * Execute the plugin
     *
     * @throws MojoExecutionException in case or execution error
     * @throws MojoFailureException   in case or execution error
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            getLog().info("Skipped waiting for " + protocol + "://" + host);
            return;
        }

        URL url = getURL();
        int count = maxcount;
        int trials = 1;
        getLog().info("(timeout: " + timeout + " maxcount: " + maxcount + ")");

        // try to connect
        while (true)
        {
            try
            {
                getLog().info(trials + ": Trying to connect to " + url);

                // obtain the connection
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(timeout);

                InputStream stream = connection.getInputStream();

                // if read is required, read everything from URL
                if (read)
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null)
                    {
                        getLog().debug(inputLine);
                    }

                    in.close();
                }

                getLog().info("Success: - reached " + url);
                stream.close();
                break;
            }
            catch (IOException e)
            {
                if (count > 1)
                {
                    count--;
                }
                else if (count != 0)
                {
                    getLog().warn("Cannot connect to " + url, e);
                    throw new MojoExecutionException("Cannot connect to " + url, e);
                }
                try
                {
                    Thread.sleep(timeout);
                }
                catch (InterruptedException e1)
                { // do nothing
                }
                
                trials++;
            }
        }
    }

    /**
     * Construct the URL to connect to
     *
     * @return the well-formed URL
     * @throws MojoExecutionException in case of malformed URL
     */
    public URL getURL() throws MojoExecutionException
    {
        try
        {
            return new URL(protocol, host, port, file);
        }
        catch (MalformedURLException e)
        {
            throw new MojoExecutionException(
                    protocol + ", " + host + ", " + port + ", " + file + ": cannot create URL", e);
        }
    }
}
