Maven2 plugin, that waits on an URL (for a server startup).

------------------------------------------------------------------------------------------------------------------------
Goals
------------------------------------------------------------------------------------------------------------------------

Can be used inside a pre-integration-test phase.

A bit more background on the reasoning behind this plugin: We're doing automated integration tests cargo and Simulator
running on Jboss. There is a long timeout gap after Jboss starts up and before simulator is accessible.
We need to make sure simulator is available by pinging that URl.

------------------------------------------------------------------------------------------------------------------------
POM usage:
------------------------------------------------------------------------------------------------------------------------

  <plugin>
    <groupId>com.tacitknowledge.maven.plugins</groupId>
    <artifactId>tk-maven-wait-plugin</artifactId>

    <!-- define parameters here (defaults)  -->
    <configuration>
      <skip>false</skip>
    </configuration>
    <version>1.0</version>
    <executions>
      <execution>
        <id>server1</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>wait</goal>
        </goals>
        <configuration>
        <!-- or define them here (overwriting defaults) -->
          <protocol>http</protocol>
          <host>mot-jboss</host>
          <port>8080</port>
          <file>/simulator/systems.json</file>
          <maxcount>0</maxcount>
          <timeout>10000</timeout>
          <read>true</read>
        </configuration>
      </execution>

      <execution>
        <id>server2</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>wait</goal>
        </goals>
        <configuration>
          <protocol>http</protocol>
          <host>bsm01.mcloud1.qa3.blurdev.com</host>
          <port>8080</port>
          <file>/simulator/systems.json</file>
          <maxcount>20</maxcount>
          <timeout>10000</timeout>
        </configuration>
      </execution>
    </executions>
  </plugin>

------------------------------------------------------------------------------------------------------------------------
Configuration:
------------------------------------------------------------------------------------------------------------------------

Parameter       Description                                 Type, default-value
skip            true when wait plugin should NOT execute    boolean, false
protocol        URL.protocol                                string, http
host            URL.host                                    string, localhost
port            URL.port                                    int, 8080
file            URL.file                                    string
timeout         connect-timeout in milliseconds             int, 30000
maxcount        max. number of trials (0 = unlimited)       0
read            true when on connection data is read        boolean, true

