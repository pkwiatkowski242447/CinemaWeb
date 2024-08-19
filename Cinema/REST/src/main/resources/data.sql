-----------------------------------------------------------------------------------------------------------------------------------------------------------
--- Example account no.1: AccountNo1, P@ssw0rd! with Client, Staff and Admin access levels
-----------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO public.accounts (active, blocked, language, version, avatar_id, id, password, login)
VALUES (true, false, 'PL', 0, NULL, '07c2507d-378f-4632-af14-65864b834bd8', '$2a$12$zIPcT6VT6rC4xzZYC6vZK.SP5XYBmOUvOi.oyScTBFuea7/GzTShS', 'AccountNo1');

INSERT INTO public.personal_data (account_id, email, first_name, last_name, phone_number)
VALUES ('07c2507d-378f-4632-af14-65864b834bd8', 'wduda@onet.pl', 'Włodzimierz', 'Duda', '785503525');

INSERT INTO public.address_data (account_id, zip_code, country, city_name, province, street_name, street_number)
VALUES ('07c2507d-378f-4632-af14-65864b834bd8', '30-851', 'Poland', 'Kraków', 'małopolskie', 'Stacyjna', 17);

--- Client access level for AccountNo1

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, '07c2507d-378f-4632-af14-65864b834bd8', '6a80adc3-7894-42da-8047-1ce490ca9151', 'CLIENT');

INSERT INTO public.clients (id)
VALUES ('6a80adc3-7894-42da-8047-1ce490ca9151');

--- Staff access level for AccountNo1

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, '07c2507d-378f-4632-af14-65864b834bd8', 'da4bc44c-4680-4b0f-9a93-18b0baa8e770', 'STAFF');

INSERT INTO public.staff (id)
VALUES ('da4bc44c-4680-4b0f-9a93-18b0baa8e770');

--- Admin access level for AccountNo1

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, '07c2507d-378f-4632-af14-65864b834bd8', '84c428d0-37ab-4e98-8467-9f599698f98d', 'ADMIN');

INSERT INTO public.admins (id)
VALUES ('84c428d0-37ab-4e98-8467-9f599698f98d');

-----------------------------------------------------------------------------------------------------------------------------------------------------------
--- Example account no.2: AccountNo2, P@ssw0rd! with Client access level
-----------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO public.accounts (active, blocked, language, version, avatar_id, id, password, login)
VALUES (true, false, 'EN', 0, NULL, '63ea398a-1af3-4fc2-82ab-422fa3d2a9ee', '$2a$12$zIPcT6VT6rC4xzZYC6vZK.SP5XYBmOUvOi.oyScTBFuea7/GzTShS', 'AccountNo2');

INSERT INTO public.personal_data (account_id, email, first_name, last_name, phone_number)
VALUES ('63ea398a-1af3-4fc2-82ab-422fa3d2a9ee', 'fsawicki@interia.pl', 'Frydrych', 'Sawicki', '799971887');

INSERT INTO public.address_data (account_id, zip_code, country, city_name, province, street_name, street_number)
VALUES ('63ea398a-1af3-4fc2-82ab-422fa3d2a9ee', '80-299', 'Poland', 'Gdańsk', 'pomorskie', 'Cefeusza', 8);

--- Client access level for AccountNo2

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, '63ea398a-1af3-4fc2-82ab-422fa3d2a9ee', 'f6999d95-d3dc-4663-9f53-fea730431e75', 'CLIENT');

INSERT INTO public.clients (id)
VALUES ('f6999d95-d3dc-4663-9f53-fea730431e75');

-----------------------------------------------------------------------------------------------------------------------------------------------------------
--- Example account no.3: AccountNo3, P@ssw0rd! with Staff access level
-----------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO public.accounts (active, blocked, language, version, avatar_id, id, password, login)
VALUES (true, false, 'PL', 0, NULL, '5e80aa96-4ceb-4901-9f6e-90dcaf942c25', '$2a$12$zIPcT6VT6rC4xzZYC6vZK.SP5XYBmOUvOi.oyScTBFuea7/GzTShS', 'AccountNo3');

INSERT INTO public.personal_data (account_id, email, first_name, last_name, phone_number)
VALUES ('5e80aa96-4ceb-4901-9f6e-90dcaf942c25', 'sdąbrowski@wp.pl', 'Stanisław', 'Dąbrowski', '515552932');

INSERT INTO public.address_data (account_id, zip_code, country, city_name, province, street_name, street_number)
VALUES ('5e80aa96-4ceb-4901-9f6e-90dcaf942c25', '80-299', 'Poland', 'Gdańsk', 'pomorskie', 'Herosa', 70);

--- Staff access level for AccountNo3

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, '5e80aa96-4ceb-4901-9f6e-90dcaf942c25', '0bb73d7f-fb6c-49eb-a2f3-87f81f3a69f9', 'STAFF');

INSERT INTO public.staff (id)
VALUES ('0bb73d7f-fb6c-49eb-a2f3-87f81f3a69f9');

-----------------------------------------------------------------------------------------------------------------------------------------------------------
--- Example account no.4: AccountNo4, P@ssw0rd! with Admin access level
-----------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO public.accounts (active, blocked, language, version, avatar_id, id, password, login)
VALUES (true, false, 'PL', 0, NULL, '971b3beb-1708-486f-89be-a98f2dfc99b2', '$2a$12$zIPcT6VT6rC4xzZYC6vZK.SP5XYBmOUvOi.oyScTBFuea7/GzTShS', 'AccountNo4');

INSERT INTO public.personal_data (account_id, email, first_name, last_name, phone_number)
VALUES ('971b3beb-1708-486f-89be-a98f2dfc99b2', 'anowicki@wp.pl', 'Amadej', 'Nowicki', '606631238');

INSERT INTO public.address_data (account_id, zip_code, country, city_name, province, street_name, street_number)
VALUES ('971b3beb-1708-486f-89be-a98f2dfc99b2', '61-353', 'Poland', 'Poznań', 'wielkopolskie', 'Szydłowiecka', 61);

--- Admin access level for AccountNo4

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, '971b3beb-1708-486f-89be-a98f2dfc99b2', 'f93fc6c7-91f7-4f48-b112-18cf2324efe2', 'ADMIN');

INSERT INTO public.admins (id)
VALUES ('f93fc6c7-91f7-4f48-b112-18cf2324efe2');

-----------------------------------------------------------------------------------------------------------------------------------------------------------
--- Example account no.5: AccountNo5, P@ssw0rd! with Client access level - This account is not active
-----------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO public.accounts (active, blocked, language, version, avatar_id, id, password, login)
VALUES (false, false, 'PL', 0, NULL, 'dd622e5f-448c-4ce7-9cbd-90dba3d9dfa6', '$2a$12$zIPcT6VT6rC4xzZYC6vZK.SP5XYBmOUvOi.oyScTBFuea7/GzTShS', 'AccountNo5');

INSERT INTO public.personal_data (account_id, email, first_name, last_name, phone_number)
VALUES ('dd622e5f-448c-4ce7-9cbd-90dba3d9dfa6', 'czostrowski@gmail.pl', 'Czesław', 'Ostrowski', '607013130');

INSERT INTO public.address_data (account_id, zip_code, country, city_name, province, street_name, street_number)
VALUES ('dd622e5f-448c-4ce7-9cbd-90dba3d9dfa6', '03-385', 'Poland', 'Warszawa', 'mazowieckie', 'Łabiszyńska', 105);

--- Admin access level for AccountNo4

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, 'dd622e5f-448c-4ce7-9cbd-90dba3d9dfa6', 'e4fef744-4223-4123-8f82-f52050c8fbcc', 'CLIENT');

INSERT INTO public.clients (id)
VALUES ('e4fef744-4223-4123-8f82-f52050c8fbcc');

-----------------------------------------------------------------------------------------------------------------------------------------------------------
--- Example account no.6: AccountNo6, P@ssw0rd! with Client access level - This account is blocked
-----------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO public.accounts (active, blocked, language, version, avatar_id, id, password, login)
VALUES (true, true, 'PL', 0, NULL, 'e567f0a1-b6b5-4946-887c-e022c269d6a1', '$2a$12$zIPcT6VT6rC4xzZYC6vZK.SP5XYBmOUvOi.oyScTBFuea7/GzTShS', 'AccountNo6');

INSERT INTO public.personal_data (account_id, email, first_name, last_name, phone_number)
VALUES ('e567f0a1-b6b5-4946-887c-e022c269d6a1', 'jkamiński@spoko.pl', 'Janusz', 'Kamiński', '675164014');

INSERT INTO public.address_data (account_id, zip_code, country, city_name, province, street_name, street_number)
VALUES ('e567f0a1-b6b5-4946-887c-e022c269d6a1', '20-469', 'Poland', 'Lublin', 'lubelskie', 'Wrotkowska', 31);

--- Admin access level for AccountNo4

INSERT INTO public.access_levels (version, account_id, id, type)
VALUES (0, 'e567f0a1-b6b5-4946-887c-e022c269d6a1', '4878102f-c8fb-4ae5-88f0-abba3773ef14', 'CLIENT');

INSERT INTO public.clients (id)
VALUES ('4878102f-c8fb-4ae5-88f0-abba3773ef14');