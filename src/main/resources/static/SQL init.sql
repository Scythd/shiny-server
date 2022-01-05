create table users (
    id integer generated always as identity(start with 1 increment by 1) primary key not null,
    username varchar not null,
    email varchar not null,
    nickname varchar not null,
    password varchar not null,
    status varchar not null

)
;
create table roles (
    id integer generated always as identity(start with 1 increment by 1) primary key not null,
    name varchar not null
)
;
alter table roles add unique(name);
insert into roles (name) values ('ROLE_USER'), ('ROLE_ADMIN');

create table user_roles(
    user_id integer not null,
    role_id integer not null
);

alter table user_roles add foreign key (user_id) references users(id);
alter table user_roles add foreign key (role_id) references roles(id);




create table queue (
    date_created timestamp default current_timestamp,
    user_id integer not null,
    game_type varchar not null
);
alter table queue add check( game_type IN ('Chess','Checkers','BullCow'));
alter table queue add foreign key (user_id) references users(id);

create table queueresults (
    playerFirst integer not null,s
    playerSecond integer not null,
    game_type varchar not null,
    readyFirst boolean default false,
    readySecond boolean default false,
    timestamp_created timestamp default current_timestamp
);
alter table queueresults add foreign key (playerFirst) references users(id);
alter table queueresults add foreign key (playerSecond) references users(id);

create table games (
    id integer generated always as identity(start with 1 increment by 1) primary key not null,
    playerFirst integer not null,
    playerSecond integer not null,
    game_type varchar not null,
    win_player integer not null,
    turn integer not null,
    endDateTime timestamp,
    startDateTime timestamp default current_timestamp not null,
    game_info varchar,
    state varchar not null
);

--alter table games alter column endDateTime type timestamp;
--alter table games alter column startDateTime type timestamp;
alter table games add foreign key (playerFirst) references users(id);
alter table games add foreign key (playerSecond) references users(id);
