-- NORD POS is a fork of Openbravo POS.
-- Copyright (C) 2009-2014 Nord Trading Ltd. 
-- <http://sourceforge.net/p/nordpos/>
-- This file is part of NORD POS.
-- NORD POS is free software: you can redistribute it and/or modify it under the
-- terms of the GNU General Public License as published by the Free Software
-- Foundation, either version 3 of the License, or (at your option) any later
-- version.
-- NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
-- WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
-- A PARTICULAR PURPOSE. See the GNU General Public License for more details.
-- You should have received a copy of the GNU General Public License along with
-- NORD POS. If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for MYSQL
-- NORD POS v3.0.1CE -> NORD POS v3.0.4CE

UPDATE ROLES SET PERMISSIONS = $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} WHERE ID = '0';
UPDATE ROLES SET PERMISSIONS = $FILE{/com/openbravo/pos/templates/Role.Manager.xml} WHERE ID = '1';

UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Menu.Root.bsh} WHERE NAME = 'Menu.Root';
UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Window.Description.txt} WHERE NAME = 'Window.Description';

ALTER TABLE CUSTOMERS ADD COLUMN PROPERTIES MEDIUMBLOB;
ALTER TABLE STOCKCURRENT ADD COLUMN ID VARCHAR(255);
ALTER TABLE PEOPLE ADD COLUMN PROPERTIES MEDIUMBLOB;

-- final script

DELETE FROM SHAREDTICKETS;

UPDATE APPLICATIONS SET ID = $APP_ID{}, NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = 'nordpos';