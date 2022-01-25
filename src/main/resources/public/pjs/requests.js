/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Pages */

class AuthRequests {

    static async login(username, password) {
        let data = {};
        data.username = username;
        data.password = password;
        let response = new FetchWrapper("api/auth/login", "POST", data);
        await response.fetch();
        await response.result;
        window.localStorage.setItem('UserNick', response.result.username);

        return response.result;
    }

    static async register(username, password, email, nickname) {
        let data = {};
        data.username = username;
        data.password = password;
        data.email = email;
        data.nickname = nickname;
        let response = new FetchWrapper("api/auth/register", "POST", data);

        await response.fetch();
        await response.result;
        window.localStorage.setItem('UserNick', response.result.username);
        return response.result;
    }
    static async validate() {
        let response = new FetchWrapper("api/auth/validate", "GET");
        await response.fetch();
        return await response.result;
    }
}

class GameBullCowRequests {
    static async gameInfo() {
        let response = new FetchWrapper("api/game/bullcow/getInfo", "GET");
        await response.fetch();
        return await response.result;
    }
    static async postPlayerAns(ans) {
        let response = new FetchWrapper("api/game/bullcow/setAnswer", "POST", ans);
        await response.fetch();
        return await response.result;
    }
    static async postPlayerGuess(guess) {
        let response = new FetchWrapper("api/game/bullcow/playerGuess", "POST", guess);
        await response.fetch();
        return await response.result;
    }
}

class QueueRequests {

    static async enterQueue(gameType) {
        let dto = {};
        dto.gameType = gameType;
        let response = new FetchWrapper("api/game/queue/enter", "POST", dto);
        await response.fetch();
        return await response.result;
    }

    static async queueResult() {
        let response = new FetchWrapper("api/game/queue/result", "GET");
        await response.fetch();
        return await response.result;
    }

    static async becomeReady() {
        let response = new FetchWrapper("api/game/queue/becomeready", "GET");
        await response.fetch();
        return await response.result;
    }

    static async leaveQueue() {
        let response = new FetchWrapper("api/game/queue/leavequeue", "GET");
        await response.fetch();
        return await response.result;
    }
}

class FetchWrapper {

    static mutex = 0;

    constructor(url, method, body, headers = {
    'Content-Type': 'application/json'
    }) {
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.method = method;
    }

    async fetch() {
        let tempheaders = this.headers;
        while (FetchWrapper.mutex !== 0){
            await sleep(100);
        }
        FetchWrapper.mutex = 1;
        
        let auth = CoockieManager.getCookie("Authorization");
        if (auth !== null) {
            let i = 0;
            while (i < 10 && !auth.startsWith('Bearer_')){
                 auth = CoockieManager.getCookie("Authorization");
                 i++;
            }
            tempheaders.Authorization = CoockieManager.getCookie("Authorization") + "";
        }
        tempheaders['Content-Type'] = 'application/json';
//        tempheaders['Content-Length'] = JSON.stringify(this.body).length.toString();
//        tempheaders['Host'] = window.location.host;

        if (this.method === "GET") {
            this.response = await fetch(this.url, {
                headers: tempheaders
            });

        } else {
            this.response = await fetch(this.url, {
                method: this.method,
                body: JSON.stringify(this.body),
                headers: tempheaders
            });
        }
        if (this.response.status === 403) {
            let valid = await fetch("api/auth/validate", {
                method: 'GET',
                headers: tempheaders
            });
            if (valid.status === 403) {
                window.localStorage.setItem('pageBeforeLogin', window.localStorage.getItem('lastPage'));
                Pages.setPageLogin();
                
                // add error that authn timed out
            }
        }
        this.result = await this.response.json();

        if (this.result !== undefined && this.result !== null
                && this.result.token !== undefined && this.result.token !== null) {
            CoockieManager.setCookie("Authorization", "Bearer_" + this.result.token);

        } else {
            CoockieManager.setCookie("Authorization", this.response.headers.get("Authorization"));

        }
        FetchWrapper.mutex = 0;
        //console.log(CoockieManager.getCookie("Authorization"));
    }

}

class CoockieManager {
    static setCookie(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    }
    static getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) === ' ')
                c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0)
                return c.substring(nameEQ.length, c.length);
        }
        return null;
    }
    static eraseCookie(name) {
        document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }
}

