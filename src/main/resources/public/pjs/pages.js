/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */



/* global CoockieManager, AuthRequests, ClickEvents */

class Pages {
    static mainEl = document.getElementById('mainElement');

    static enterTrigger = function (event) {
        if (event.keyCode === 13) {
            ClickEvents.loginBtn();
        }

    }

    static clearAllTriggers = function(){
        Pages.mainEl.removeEventListener("keyup", Pages.enterTrigger);
    }

    static setPage = function (page) {
        switch (page) {
            case 'main':
            {
                Pages.setPageMain();
            }
            case 'login':
            {
                Pages.setPageLogin();
            }
            default :
            {
                Pages.setPageMain();
            }
        }
    }

    static setPageLogin() {
        Pages.clearAllTriggers();
        Pages.mainEl.addEventListener("keyup", Pages.enterTrigger);
        Pages.mainEl.innerHTML = `
            <label for="uname"><b>Username</b></label>
            <input type="text" placeholder="Enter Username" id="uname" name="uname" required>

            <label for="psw"><b>Password</b></label>
            <input type="password" placeholder="Enter Password" id="pwd" name="pwd" required>

            <button onclick="ClickEvents.loginBtn();" type="submit">Login</button>`;
        window.localStorage.setItem('lastPage', 'login');
    }
    
    static setPageMain() {
        Pages.clearAllTriggers();

        Pages.mainEl.innerHTML = `main`;
        window.localStorage.setItem('lastPage', 'main');
    }
     static setPageQueue() {
        Pages.clearAllTriggers();

        Pages.mainEl.innerHTML = `queue`;
        window.localStorage.setItem('lastPage', 'queue');
    }
}

{
    if (CoockieManager.getCookie('Authorization') !== null 
            && AuthRequests.validate() === true){
        Pages.setPage(window.localStorage.getItem('lastPage'));
    } else {
        Pages.setPageLogin();
    }
}