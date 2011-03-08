package com.iminurnetz.util;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A persistent Version class that uses a properties file.
 * 
 * The properties file is expected to have the following parameters:
 * 		project:		the name of the project
 * 		version:		the main version number
 * 		minor_version:	the minor version number or build number
 *      build_date:		the date of the last build
 * 
 * @author <a href="mailto:sunkid@iminurnetz.com">sunkid</a>
 *
 */
public class Version {
	protected PersistentProperty versionProp;
	private static Logger log = Logger.getLogger(Version.class.getName());

	private String project = "Unknown";

	/**
	 * @return the project name
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param project the project name to set
	 */
	public void setProject(String project) {
		this.project = project;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the build number
	 */
	public String getMinorVersion() {
		return minor_version;
	}

	/**
	 * @param build the build number to set
	 */
	public void setMinorVersion(String build) {
		this.minor_version = build;
	}

	/**
	 * @return the buildDate
	 */
	public Date getBuildDate() {
		return buildDate;
	}

	/**
	 * @param buildDate the buildDate to set
	 */
	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}

	private String version = "?";
	private String minor_version = "?";
	private Date buildDate;
	
	public Version() {
		this(null);
	}

	public Version(URL resource) {
		if (resource == null)
			resource = getClass().getResource("/version.txt");
				
		try {
			versionProp = new PersistentProperty(resource);
		} catch (IOException e) {
			log.log(Level.WARNING, "versioning not enabled; using defaults", e);
		}
		
		load();
	}
	
	private void load() {
		setProject(versionProp.getString("project", project));
		setVersion(versionProp.getString("version", version));
		setMinorVersion(versionProp.getString("minor_version", minor_version));
		setBuildDate(versionProp.getDate("build_date", new Date()));		
	}
	
	public static void main(String[] args) {
		Version v = new Version();
		System.out.println(v.toString());
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Project: " + getProject() + "\n");
		s.append("Version: " + getVersion());
		s.append("." + getMinorVersion() + " - " + getBuildDate().toString());
		
		return s.toString();
	}
}
