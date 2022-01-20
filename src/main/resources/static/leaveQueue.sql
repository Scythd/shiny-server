/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  MSI
 * Created: 19 янв. 2022 г.
 */

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