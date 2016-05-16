/*
 * FastODS - a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016 J. Férard
 * SimpleODS - A lightweight java library to create simple OpenOffice spreadsheets
*    Copyright (C) 2008-2013 Martin Schulz <mtschulz at users.sourceforge.net>
*
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
*/
package com.github.jferard.fastods;

/**
 * @author Julien Férard
 *         Copyright (C) 2016 J. Férard 
 * @author Martin Schulz
 *         Copyright 2008-2013 Martin Schulz <mtschulz at users.sourceforge.net>
 *
 *         This file ConfigItem.java is part of FastODS.
 */
public class ConfigItem {
	private final String sItemName;
	private final String sType;
	private final String sValue;
	private String xml;

	public ConfigItem(final String sName, final String sType,
			final String sValue) {
		this.sItemName = sName;
		this.sType = sType;
		this.sValue = sValue;
	}

	/**
	 * Get the name of this ConfigItem.
	 * 
	 * @return The name of this ConfigItem
	 */
	public String getName() {
		return this.sItemName;
	}

	public String getType() {
		return this.sType;
	}

	public String getValue() {
		return this.sValue;
	}

	/**
	 * Write the XML format for this object.<br>
	 * This is used while writing the ODS file.
	 * 
	 * @return The XML string for this object.
	 */
	public String toXML(Util util) {
		if (this.xml == null) {
			this.xml = new StringBuilder("<config:config-item config:name=\"")
					.append(util.escapeXMLAttribute(this.sItemName))
					.append("\" config:type=\"")
					.append(util.escapeXMLAttribute(this.sType)).append("\">")
					.append(util.escapeXMLContent(this.sValue))
					.append("</config:config-item>").toString();
		}
		return this.xml;
	}
}