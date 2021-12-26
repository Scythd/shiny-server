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
create table user_roles(
    user_id integer not null,
    role_id integer not null
);

alter table user_roles add foreign key (user_id) references users(id);
alter table user_roles add foreign key (role_id) references roles(id);

create table queue (
    date_created date default current_timestamp,
    uid integer not null,
    game_type varchar not null
);

alter table queue add foreign key (uid) references users(id);


create table queueresults (
    playerFirst integer not null,
    playerSecond integer not null,
    game_type varchar not null,
    readyFirst boolean default false,
    readySecond boolean default false
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
    endDateTime date,
    startDateTime date default current_timestamp not null,
    game_info varchar
);

alter table games add foreign key (playerFirst) references users(id);
alter table games add foreign key (playerSecond) references users(id);