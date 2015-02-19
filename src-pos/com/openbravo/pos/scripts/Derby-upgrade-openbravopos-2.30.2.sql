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

-- Database upgrade script for DERBY
-- Openbravo POS v2.30.2 -> NORD POS v3.0.4CE

UPDATE ROLES SET PERMISSIONS = $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} WHERE ID = '0';
UPDATE ROLES SET PERMISSIONS = $FILE{/com/openbravo/pos/templates/Role.Manager.xml} WHERE ID = '1';

UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Window.Logo.png} WHERE NAME = 'Window.Logo';
UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Window.Title.txt} WHERE NAME = 'Window.Title';
UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Menu.Root.bsh} WHERE NAME = 'Menu.Root';
UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Ticket.Buttons.xml} WHERE NAME = 'Ticket.Buttons';

DELETE FROM RESOURCES WHERE ID='90005';
DELETE FROM RESOURCES WHERE ID='90006';
DELETE FROM RESOURCES WHERE ID='90007';

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('90005', 'Window.Description', 0, $FILE{/com/openbravo/pos/templates/Window.Description.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('90006', 'Window.DescLogo', 1, $FILE{/com/openbravo/pos/templates/Window.DescLogo.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('90007', 'Window.SupportBy', 1, $FILE{/com/openbravo/pos/templates/Window.SupportBy.png});

ALTER TABLE TICKETLINES ADD COLUMN ID VARCHAR(256);
ALTER TABLE CATEGORIES ADD COLUMN CODE VARCHAR(256);
ALTER TABLE LOCATIONS ADD COLUMN ISCLOSE SMALLINT DEFAULT 0 NOT NULL;
ALTER TABLE CUSTOMERS ADD COLUMN PROPERTIES BLOB;
ALTER TABLE STOCKCURRENT ADD COLUMN ID VARCHAR(256);
ALTER TABLE PEOPLE ADD COLUMN PROPERTIES BLOB;

-- final script

DELETE FROM SHAREDTICKETS;

UPDATE APPLICATIONS SET ID = $APP_ID{}, NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = 'openbravopos';