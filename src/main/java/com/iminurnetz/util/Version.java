/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid.com> and is distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid.com
 */

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
