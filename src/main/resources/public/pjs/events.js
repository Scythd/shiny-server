/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


/* global uname, pwd, AuthRequests, Pages, gameType, QueueRequests, Cycle, gameInput, GameBullCowRequests */

class ClickEvents {
    static loginBtn = async function () {
        let username = uname.value;
        let password = pwd.value;
        await AuthRequests.login(username, password);
        let pageBefore = window.localStorage.getItem('pageBeforeLogin');
        if (pageBefore === 'login'){
            pageBefore = 'main';
        }
        Pages.setPage(pageBefore);
    }

    static enterQueueBtn = async function () {
        let alreadyQueue = await QueueRequests.queueResult();

        let type = gameType.value;
        if (type === 'Null') {
            alert('Выберите тип игры.');
            return;
        }
        if (alreadyQueue.queueState === "noPos") {
            alreadyQueue = await QueueRequests.enterQueue(type);
        }
        Pages.setPageQueue();
    }

    static becomeReadyBtn = async function () {
        await QueueRequests.becomeReady();
    }

    static leaveQueueBtn = async function () {
        Cycle.actions = new Array();
        Cycle.stop();
        QueueRequests.leaveQueue();
        Cycle.init();
        Pages.setPageMain();
    }

    static lastGameBtn = async function () {
        Pages.setPageGame();
    }
    
    static postAnsGameBtn = async function () {
        let input = gameInput.value;
        await GameBullCowRequests.postPlayerAns(input);
    }
    
    static postGuessGameBtn = async function () {
        let input = gameInput.value;
        await GameBullCowRequests.postPlayerGuess(input);
    }
    
    static toMainBtn = async function () {
        Pages.setPageMain();
    }
}