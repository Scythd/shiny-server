/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  MSI
 * Created: 19 янв. 2022 г.
 */

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
        -- ready player back to queue // unredy remove from queue at all
        -- first delete cause of custom uniwue trigger
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