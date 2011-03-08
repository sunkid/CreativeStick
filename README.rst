=============================================
CreativeStick - A Bukkit Plugin for Minecraft
=============================================
By sunkid

CreativeStick - Build, replace, or remove blocks at a distance

Latest release: `CreativeStick v0.4.20 <https://github.com/downloads/sunkid/CreativeStick/CreativeStick-0.4.20.zip>`_
Discussion Forum Link: http://bit.ly/cs4bukkit
Source: https://github.com/sunkid/CreativeStick

CREDITS
-------

This plugin was originally written by Nijikokun and was named iStick. Most of the functionality was developed by him and he deserves all the credit!

CreativeStick will allow you to build, replace, or remove blocks at a distance by simply shaking a stick at them:

WHAT's NEW
-----------
All item specification can be now done using either the name of the item (e.g. 'stone'), the id, or a "fuzzy string" shortcut:

* fuzzy strings always start with capital letters
* upper case letters are the first letters of the words in an item's name
* using first an upper case and then any number of lower case letters specifies the beginning of a name but does not include spaces
* a wildcard '*' is accepted and can stand for any sequence of letters and spaces
* only strings resulting in single items are accepted, if more than one item matches, only 'placeable' items will be considered (right now, placeable is defined as the item being a block; I will work on including other placeable items like cake... everybody likes cake and you can still use it by specifying 'cake')
* if the string matches more than one placeable item, a list of those items is returned
* examples:
	* 'IA' is short for 'iron axe'
	* 'G*re' is short for 'gold ore'
	* 'So' results in 'soil' (note that it doesn't match 'soul sand' as that item has two words but no wild card was used)
	* 'IB' results in 'iron block' as the other matching item ('iron boots') is not placeable
	* 'S' is invalid as it matches multiple placeable items ('stone', 'sand', etc.)

Functionality:
--------------

#) Enable / Disable /cs -t|toggle
#) Right click for removal / switching of building block (optional).
#) Left click for building / removing / replacing.
#) Switch to building: /cs -b|build <id|name>
#) Switch to replacing: /cs -r|replace <id|name>
#) Removing from a distance: make sure CreativeStick is enabled and just right click.
#) Undoing: /cs -u|undo [amount]
#) Toggle whether items are added to the inventory when removing: /cs -td
#) /cs [-h|help] shows help information
#) /cs -c|config shows configuration information
#) /cs -c|config <parameter> <value> allows config parameters to be changed; currently supported:
	#) number of default undos
	#) distance from which the tool acts
	#) whether or not the bottom layer of bedrock can be removed
	#) whether or not right-clicking switches the item to build with
	#) debug mode
#) /cs -v|version shows version information
#) Aliases: /is or /istick or /cstick
#) Support for protection plugins
#) Support for both it's own permissions or via Permissions or GroupManager
#) assign 'creativestick.use' to any users who are allowed to use the plugin
#) assign 'creativestick.config' to any user who are allowed to change configuration parameters for themselves.

INSTALLATION:
-------------

#) download zip file and move jar file to your plugins folder
#) remove iStick.jar if it is present (but leave the iStick folder in place, where ever it is)
#) reload your server
#) read and adjust configuration file in plugins/CreativeStick/config.yml
#) reload your server if you made any changes

UPGRADING FROM PREVIOUS VERSIONS OF CreativeStick:
--------------------------------------------------

#) follow INSTALLATION steps 1 - 3.
#) if you see a warning message about an outdated config.yml file, simply follow the instructions in plugins/CreativeStick/config-new.yml (settings.user.debug is the only new parameter in 0.4.14 and can be ignored. Just set settings.version to 0.4.14)
#) if there is no warning message, you are good to go

MIGRATION FROM iStick:
----------------------

same as INSTALLATION!

KNOWN ISSUES
------------
* CS can kill you and torches are a nuisance
* inventory doesn't update until stacks are clicked on
* sometimes, there is a very long lag when removing blocks
* non-stackable items can be selected to build with and will generate errors

Licensing
_________

This software is copyright by sunkid <sunkid@iminurnetz.com> and is distributed under a dual license:

Non-Commercial Use:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
Commercial Use:
    Please contact sunkid@iminurnetz.com

