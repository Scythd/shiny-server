/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  MSI
 * Created: 25 янв. 2022 г.
 */

create or replace function crutchQueueResultsCleaner(uid BIGINT) returns integer
as $$
declare
begin
    delete from queueresults where playerfirst = uid or playersecond = uid;
    return 0;
end $$ language plpgsql;

