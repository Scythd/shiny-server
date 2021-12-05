
drop trigger refresh_queue_results_trigger on queue;
drop function refresh_queue_results;
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
                -- also added here clearing cery old queueresults
                delete from queueresults where age(current_timestamp, timestamp_created) > (time '00:01:00');
            end if;
        end if;
    end if; 
    return new;
--     exception 
--     when sqlstate '42703' then
--     return new;
--     when others then
--     raise notice '% %', SQLERRM, SQLSTATE;
end $$ language plpgsql;

create trigger refresh_queue_results_trigger after insert
on queue
for each row
execute procedure refresh_queue_results ();



insert into queue values(6, 'Checkers');
select * from queue;
select * from queueResults;


drop trigger uniquequeue_trigger_before_insert on queue;
drop function unique_queue_check;
create or replace function unique_queue_check () returns trigger
as $$
declare
    queue_eq integer;
    queueres_eq integer;
    games_eq integer;
begin
    select into queue_eq count(*) from queue where user_id = NEW.user_id;
    select into queueres_eq count(*) from queueresults where player1 = NEW.user_id or player2 = NEW.user_id;
    select into games_eq count(*) from games where player1 = NEW.user_id or player2 = NEW.user_id;
    if ((queue_eq + queueres_eq + games_eq) = 0) then
        return NEW;
    else
        return null;
    end if;
    
end $$ language plpgsql;

create trigger uniquequeue_trigger_before_insert before insert on queue
for each row
execute procedure unique_queue_check ();


insert into queue values(11, 'Checkers');
select * from queue;
select * from queueResults;
select * from users;

alter table queue add foreign key (user_id) references users (id) ;

SELECT con.*
    FROM pg_catalog.pg_constraint con
        INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid
        INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace
        WHERE nsp.nspname = 'public'
             AND rel.relname = 'queue';
alter table games alter column startdatetime set default (current_timestamp);
alter table queueresults add column timestamp_created timestamp not null default current_timestamp;


create or replace function resolvePendedQueueResults(user_id BIGINT) returns integer
as $$
declare
    res record;
begin
    select into res * from queueresults where player1 = user_id or player2 = user_id;
    if (res.ready1 = true and res.ready2 = true) then
        insert into games (state, turn, win_player, player1, player2, game_type)
        values('starting', 0, 0, res.player1, res.player2, res.game_type);
    end if;

    if (age(current_timestamp, res.timestamp_created) > (time '00:00:30')) then
        -- ready player back to queue // unredy remove from queue at all
        -- first delete cause of custom uniwue trigger
        delete from queueresults 
        where player1 = res.player1 and player2 = res.player2;
        if (res.ready1 and not res.ready2) then

            insert into queue values (res.player1, res.game_type, res.timestamp_created);

        end if;
        if (res.ready2 and not res.ready1) then

            insert into queue values (res.player2, res.game_type, res.timestamp_created);

        end if;
        
    end if;
    return 0;
end $$ language plpgsql;



insert into games (state, turn, win_player, player1, player2, game_type)
values('starting', 0, 0, 1, 11, 'Chess');

select resolvePendedQueueResults(1);

insert into queue values(1, 'Checkers');
insert into queue values(11, 'Checkers');
select * from queue;
select * from queueResults;
select * from games;

insert into queueresults
values(1, 11, 'Checkers', true, false, current_timestamp - time '00:00:30');
select * from queueresults;
select resolvePendedQueueResults(1);
select * from queue;


create or replace function leaveQueue(uid BIGINT) returns integer
as $$
declare 
    rec record;
begin
    delete from queue where user_id = uid;
    select into rec * from queueResults where player1 = uid or player2 = uid;
    if (rec.player1 = uid) then 
        insert into queue values (rec.player2, rec.game_type, rec.timestamp_created);
    end if;
    if (rec.player2 = uid) then 
        insert into queue values (rec.player1, rec.game_type, rec.timestamp_created);
    end if;
    delete from queueREsults where player1 = rec.player1 and player2 = rec.player2;
    return 0;
end $$ language plpgsql;