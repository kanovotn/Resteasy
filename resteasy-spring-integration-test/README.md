# EAP 6.2.0 Resteasy 2.3.7.Final-redhat-2 #




# Variant include dependencies in deployment #

war archive contains resteasy-spring and spring dependencies in WEB-INF/lib folder

unzip EAP

unzip EAP maven repo

run 

  mvn -Djboss-eap-maven-repository -Dmaven-repo-as=PATH_TO_MAVEN_REPO_AS -Djboss.home=PATH_TO_EAP -Dversion.org.jboss.resteasy=EAP_RESTEASY_VERSION clean verify
  
example

  mvn -Djboss-eap-maven-repository -Dmaven-repo-as=/home/JBEAP-6.2.0.ER7-maven-repo-as/ -Djboss.home=/home/JBEAP-6.2.0.ER7/jboss-eap-6.2 -Dversion.org.jboss.resteasy=2.3.7.Final-redhat-2 clean verify

to run test on application server which is running add

 -DallowConnectingToRunningServer=true

to use different version of spring framework use property versions.org.springframework

Examples:

 -Dversion.org.springframework=4.0.2.RELEASE
 
 -Dversion.org.springframework=3.2.8.RELEASE
 
 -Dversion.org.springframework=3.1.4.RELEASE
 
 -Dversion.org.springframework=3.0.7.RELEASE
 
 -Dversion.org.springframework=2.5.6.SEC03
 
 
# Variant without dependencies in deployment #

AS modules resteasy-spring and spring have to be preinstalled to be able to deploy application

war archive do not contain resteasy-spring and spring dependencies, module dependencies are specified in MANIFEST.MF
  File META-INF/MANIFEST.MF contains line
	Dependencies: org.jboss.resteasy.resteasy-spring


run 

  mvn -Djboss-eap-maven-repository -Dmaven-repo-as=PATH_TO_MAVEN_REPO_AS -Djboss.home=PATH_TO_EAP -Dversion.org.jboss.resteasy=EAP_RESTEASY_VERSION -Das-modules -Dspring-3.2.x clean verify

to test deployment with dependency on resteasy-spring and spring AS module. It will install snowdrop module as well, but it is not used as deployment dependency

add -Dmodule.resteasy-spring.spring-module=org.jboss.snowdrop to use snowdrop as deployment dependency
