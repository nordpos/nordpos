--    Openbravo POS is a point of sales application designed for touch screens.
--    Copyright (C) 2010 Openbravo, S.L.
--    http://sourceforge.net/projects/openbravopos
--
--    This file is part of Openbravo POS.
--
--    Openbravo POS is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    Openbravo POS is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for MYSQL

-- Openbravo POS v2.30.2 -> NORD POS v2.30.2

UPDATE ROLES SET PERMISSIONS = $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} WHERE ID = '0';
UPDATE ROLES SET PERMISSIONS = $FILE{/com/openbravo/pos/templates/Role.Manager.xml} WHERE ID = '1';

UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Window.Logo.png} WHERE ID = '9';
UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Window.Title.txt} WHERE ID = '10';
UPDATE RESOURCES SET CONTENT = $FILE{/com/openbravo/pos/templates/Menu.Root.txt} WHERE ID = '14';

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('90005', 'Window.Description', 0, $FILE{/com/openbravo/pos/templates/Window.Description.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('90006', 'Window.DescLogo', 1, $FILE{/com/openbravo/pos/templates/Window.DescLogo.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('90007', 'Window.SupportBy', 1, $FILE{/com/openbravo/pos/templates/Window.SupportBy.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('92001', 'Printer.ProductLabel', 0, $FILE{/com/openbravo/pos/templates/Printer.ProductLabel.xml});

ALTER TABLE TICKETLINES ADD COLUMN ID VARCHAR(255);
ALTER TABLE CATEGORIES ADD COLUMN CODE VARCHAR(255);

-- final script

DELETE FROM SHAREDTICKETS;

UPDATE APPLICATIONS SET ID = $APP_ID{}, NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = 'openbravopos';