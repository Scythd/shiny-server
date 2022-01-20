



insert into queue values(6, 'Checkers');
select * from queue;
select * from queueResults;





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






insert into games (state, turn, win_player, playerFirst, playerSecond, game_type)
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









SELECT proname, prosrc FROM pg_proc WHERE proname = 'resolvePendedQueueResults(bigint)';
