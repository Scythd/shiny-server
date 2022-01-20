/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  MSI
 * Created: 19 янв. 2022 г.
 */

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
    select into queueres_eq count(*) from queueresults where playerFirst = NEW.user_id or playerSecond = NEW.user_id; 
    select into games_eq count(*) from games where (not state = 'ended') and (playerfirst = NEW.user_id or playersecond = NEW.user_id); 
    if (queue_eq = 0 and queueres_eq = 0 and games_eq = 0) then 
        return NEW; 
    else 
        return null; 
    end if; 
end $$ language plpgsql;

create trigger uniquequeue_trigger_before_insert before insert on queue
for each row
execute procedure unique_queue_check ();