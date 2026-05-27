INSERT INTO menus (id, name, label, display_order, active)
VALUES (1, 'PROFILE', 'Profile', 1, true);

INSERT INTO menus (id, name, label, display_order, active)
VALUES (2, 'PORTFOLIO', 'Portfolio Creation / Updation', 2, true);

INSERT INTO menus (id, name, label, display_order, active)
VALUES (3, 'ALERT_SETTING', 'Alert Setting', 3, true);

INSERT INTO menus (id, name, label, display_order, active)
VALUES (4, 'MONITOR_PORTFOLIO', 'Monitor Portfolio', 4, true);

INSERT INTO menus (id, name, label, display_order, active)
VALUES (5, 'DASHBOARD', 'Dashboard', 5, true);


INSERT INTO permissions (id, name, label)
VALUES (1, 'READ', 'Read');

INSERT INTO permissions (id, name, label)
VALUES (2, 'CREATE', 'Create');

INSERT INTO permissions (id, name, label)
VALUES (3, 'UPDATE', 'Update');

INSERT INTO permissions (id, name, label)
VALUES (4, 'DELETE', 'Delete');


INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (1, 1, 1); -- PROFILE READ

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (2, 1, 3); -- PROFILE UPDATE


INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (3, 2, 1); -- PORTFOLIO READ

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (4, 2, 2); -- PORTFOLIO CREATE

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (5, 2, 3); -- PORTFOLIO UPDATE

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (6, 2, 4); -- PORTFOLIO DELETE

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (7, 3, 1); -- ALERT_SETTING READ

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (8, 3, 2); -- ALERT_SETTING CREATE

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (9, 3, 3); -- ALERT_SETTING UPDATE

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (10, 3, 4); -- ALERT_SETTING DELETE

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (11, 4, 1); -- MONITOR_PORTFOLIO READ

INSERT INTO menu_permissions (id, menu_id, permission_id)
VALUES (12, 5, 1); -- DASHBOARD READ




INSERT INTO user_menu_permissions (id, user_id, menu_permission_id)
VALUES (1, 1, 1); -- User 1 PROFILE READ

INSERT INTO user_menu_permissions (id, user_id, menu_permission_id)
VALUES (2, 1, 2); -- User 1 PROFILE UPDATE

INSERT INTO user_menu_permissions (id, user_id, menu_permission_id)
VALUES (5, 1, 7); -- User 1 ALERT_SETTING READ

INSERT INTO user_menu_permissions (id, user_id, menu_permission_id)
VALUES (6, 1, 11); -- User 1 MONITOR_PORTFOLIO READ

INSERT INTO user_menu_permissions (id, user_id, menu_permission_id)
VALUES (7, 1, 12); -- User 1 DASHBOARD READ

