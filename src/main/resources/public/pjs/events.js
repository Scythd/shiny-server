/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


/* global uname, pwd, AuthRequests, Pages */

class ClickEvents{
    static loginBtn = async function (){
        let username = uname.value;
        let password = pwd.value;
        await AuthRequests.login(username, password);
        Pages.setPage( window.localStorage.getItem('pageBeforeLogin'));
    }
}