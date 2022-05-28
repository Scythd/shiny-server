
create table if not exists users ();
alter table users add column if not exists id integer generated always as identity(start with 1 increment by 1) primary key not null;
alter table users add column if not exists username varchar not null;
alter table users add column if not exists email varchar not null;
alter table users add column if not exists nickname varchar not null;
alter table users add column if not exists password varchar not null;
alter table users add column if not exists status varchar not null;
alter table users drop column if exists testing;

create table if not exists roles ();
alter table roles add column if not exists id integer generated always as identity(start with 1 increment by 1) primary key not null;
alter table roles add column if not exists name varchar not null;
alter table roles drop constraint if exists r_name_unique;
alter table roles add constraint r_name_unique unique(name);

insert into roles (name) values ('ROLE_USER') on conflict do nothing;
insert into roles (name) values ('ROLE_ADMIN') on conflict do nothing;

create table if not exists user_roles();
alter table user_roles add column if not exists user_id integer not null;
alter table user_roles add column if not exists role_id integer not null;

alter table user_roles drop constraint if exists ur_to_u_f;
alter table user_roles drop constraint if exists ur_to_r_f;
alter table user_roles add constraint ur_to_u_f foreign key (user_id) references users(id);
alter table user_roles add constraint ur_to_r_f foreign key (role_id) references roles(id);


create table if not exists game_types ();
alter table game_types add column if not exists id integer generated always as identity(start with 1 increment by 1) primary key not null;
alter table game_types add column if not exists name varchar not null;
alter table game_types drop constraint if exists gt_name_unique;
alter table game_types add constraint gt_name_unique unique(name);
insert into game_types(name) values ('BullCow'), ('Chess'), ('Checkers') on conflict do nothing;

create table if not exists queue ();
alter table queue add column if not exists date_created timestamp default current_timestamp;
alter table queue add column if not exists user_id integer not null;
alter table queue add column if not exists game_type integer not null;

alter table queue drop constraint if exists q_to_u_f;
alter table queue drop constraint if exists q_to_gt_f;
alter table queue add constraint q_to_u_f foreign key (user_id) references users(id);
alter table queue add constraint q_to_gt_f foreign key (game_type) references game_types(id);

create table if not exists queueresults ();
alter table queueresults add column if not exists playerFirst integer not null;
alter table queueresults add column if not exists playerSecond integer not null;
alter table queueresults add column if not exists game_type integer not null;
alter table queueresults add column if not exists readyFirst boolean default false;
alter table queueresults add column if not exists readySecond boolean default false;
alter table queueresults add column if not exists timestamp_created timestamp default current_timestamp;



alter table queueresults drop constraint if exists qr_to_u_first_f;
alter table queueresults drop constraint if exists qr_to_u_second_f;
alter table queueresults drop constraint if exists qr_to_gt_f;
alter table queueresults add constraint qr_to_u_first_f foreign key (playerFirst) references users(id);
alter table queueresults add constraint qr_to_u_second_f foreign key (playerSecond) references users(id);
alter table queueresults add constraint qr_to_gt_f foreign key (game_type) references game_types(id);

create table if not exists games ();
alter table games add column if not exists id integer generated always as identity(start with 1 increment by 1) primary key not null;
alter table games add column if not exists playerFirst integer not null;
alter table games add column if not exists playerSecond integer not null;
alter table games add column if not exists game_type integer not null;
alter table games add column if not exists win_player integer not null;
alter table games add column if not exists turn integer not null;
alter table games add column if not exists endDateTime timestamp;
alter table games add column if not exists startDateTime timestamp default current_timestamp not null;
alter table games add column if not exists game_info varchar;
alter table games add column if not exists state varchar not null;

alter table games drop constraint if exists g_to_u_first_f;
alter table games drop constraint if exists g_to_u_second_f;
alter table games drop constraint if exists g_to_gt_f;
alter table games add constraint g_to_u_first_f foreign key (playerFirst) references users(id);
alter table games add constraint g_to_u_second_f foreign key (playerSecond) references users(id);
alter table games add constraint g_to_gt_f foreign key (game_type) references game_types(id);


drop trigger if exists uniquequeue_trigger_before_insert on queue;
drop function if exists unique_queue_check;
create or replace function unique_queue_check () returns trigger
as $body$
declare
    queue_eq integer;
    queueres_eq integer;
    games_eq integer;
begin
    select into queue_eq count(*) from queue where user_id = NEW.user_id;
    select into queueres_eq count(*) from queueresults where playerFirst = NEW.user_id or playerSecond = NEW.user_id;
    select into games_eq count(*) from games where (not state = 'ended') and (playerfirst = NEW.user_id or playersecond = NEW.user_id);
    if (queue_eq = 0 and queueres_eq = 0 and games_eq = 0) then
        return NEW;
    else
        return null;
    end if;
end $body$ language plpgsql;
create trigger uniquequeue_trigger_before_insert before insert on queue
for each row
execute procedure unique_queue_check ();


create or replace function resolvePendedQueueResults(user_id BIGINT) returns integer
as $$
declare
    res record;
    gameCheck record;
begin
    select into res * from queueresults where playerFirst = user_id or playerSecond = user_id;
    if (res.readyFirst = true and res.readySecond = true) then
        select into gameCheck * from games where (not state = 'ended') and (playerFirst = user_id or playerSecond = user_id);
        if (gameCheck is null) then
            insert into games (state, turn, win_player, playerFirst, playerSecond, game_type)
            values('starting', 0, -1, res.playerFirst, res.playerSecond, res.game_type);
        end if;
    end if;

    if (age(current_timestamp, res.timestamp_created) > (time '00:00:30')) then
        delete from queueresults
        where playerFirst = res.playerFirst and playerSecond = res.playerSecond;
        if (res.readyFirst and not res.readySecond) then

            insert into queue(user_id, game_type, date_created) values (res.playerFirst, res.game_type, res.timestamp_created);

        end if;
        if (res.readySecond and not res.readyFirst) then

            insert into queue(user_id, game_type, date_created) values (res.playerSecond, res.game_type, res.timestamp_created);

        end if;

    end if;
    return 0;
end $$ language plpgsql;

drop trigger if exists refresh_queue_results_trigger on queue;
drop function if exists refresh_queue_results;
create or replace function public.refresh_queue_results () returns trigger
as $$
declare
    temp_user record;
    qlen integer;
begin
    select into qlen count(*) from queue;
    if (qlen > 2) then
        select into temp_user * from queue where game_type = NEW.game_type order by date_created asc FETCH FIRST 1 ROW ONLY;
        if (not (temp_user is null)) then
            if ( temp_user.user_id != NEW.user_id) then
                insert into queueresults values (temp_user.user_id, NEW.user_id, NEW.game_type);
                delete from queue where user_id = NEW.user_id or user_id = temp_user.user_id;
            end if;
        end if;
    elseif (qlen = 2) then
        select into temp_user * from queue where game_type = NEW.game_type order by date_created asc FETCH FIRST 1 ROW ONLY;
        if (not (temp_user is null)) then
            if ( temp_user.user_id != NEW.user_id) then
                insert into queueresults values (temp_user.user_id, NEW.user_id, NEW.game_type);
                delete from queue where user_id = NEW.user_id or user_id = temp_user.user_id;
                -- also added here clearing very old queueresults
                delete from queueresults where age(current_timestamp, timestamp_created) > (time '00:00:35');
            end if;
        end if;
    end if;
    return new;
end $$ language plpgsql;

create trigger refresh_queue_results_trigger after insert
on queue
for each row
execute procedure refresh_queue_results ();

create or replace function leaveQueue(uid BIGINT) returns integer
as $$
declare
    rec record;
begin
    delete from queue where user_id = uid;
    select into rec * from queueResults where playerFirst = uid or playerSecond = uid;
    if (rec.playerFirst = uid) then
        insert into queue(user_id, game_type, date_created) values (rec.playerSecond, rec.game_type, rec.timestamp_created);
    end if;
    if (rec.playerSecond = uid) then
        insert into queue(user_id, game_type, date_created) values (rec.playerFirst, rec.game_type, rec.timestamp_created);
    end if;
    delete from queueREsults where playerFirst = rec.playerFirst and playerSecond = rec.playerSecond;
    return 0;
end $$ language plpgsql;


create or replace function crutchQueueResultsCleaner(uid BIGINT) returns integer
as $$
declare
begin
    delete from queueresults where playerfirst = uid or playersecond = uid;
    return 0;
end $$ language plpgsql;




