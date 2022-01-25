/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  MSI
 * Created: 19 янв. 2022 г.
 */

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
                -- also added here clearing very old queueresults
                delete from queueresults where age(current_timestamp, timestamp_created) > (time '00:00:35');
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

