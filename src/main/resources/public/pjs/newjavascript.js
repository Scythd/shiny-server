/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


class AuthRequests {

    static async login(username, password) {
        let data = {};
        data.username = username;
        data.password = password;
        let response = new FetchWrapper("api/auth/login", "POST", data);
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
        let auth = CoockieManager.getCookie("Authorization");
        if (auth !== null) {
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
        this.result = await this.response.json();
        if (this.result !== undefined && this.result !== null
                && this.result.token !== undefined && this.result.token !== null) {
            CoockieManager.setCookie("Authorization", "Bearer_" + this.result.token, 1 / 48);

        } else {
            CoockieManager.setCookie("Authorization", this.response.headers.get("Authorization"), 1 / 48);

        }

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
            while (c.charAt(0) == ' ')
                c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0)
                return c.substring(nameEQ.length, c.length);
        }
        return null;
    }
    static eraseCookie(name) {
        document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }
}

