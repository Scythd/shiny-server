/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */



/* global CoockieManager, AuthRequests, ClickEvents, readyBtn, Cycle, QueueRequests, cellB, cellC, cellD, cellA */

class Pages {
    static mainEl = document.getElementById('mainElement');

    static enterTrigger = function (event) {
        if (event.keyCode === 13) {
            ClickEvents.loginBtn();
        }

    }

    static clearAllTriggers = function () {
        Pages.mainEl.removeEventListener("keyup", Pages.enterTrigger);
    }

    static clearPage() {
        Pages.clearAllTriggers();
        Cycle.actions = new Array();
    }

    static setPage = function (page) {
        switch (page) {
            case 'main':
            {
                Pages.setPageMain();
                break;
            }
            case 'login':
            {
                Pages.setPageLogin();
                break;
            }
            case 'game':
            {
                Pages.setPageGame();
                break;
            }
            default :
            {
                Pages.setPageMain();
            }
        }
    }

    static async setPageLogin() {
        Pages.clearPage();
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
        Pages.clearPage();

        Pages.mainEl.innerHTML = `
            <div>
                <select id="gameType">
                    <option selected="" disabled="" value="Null">Выберите тип игры</option>
                    <option value="Chess">Шахматы</option>
                    <option value="Checkers">Шашки</option>
                    <option value="BullCow">Быки и коровы</option>
                </select>
                <button onclick="ClickEvents.enterQueueBtn()">Войти в очередь</button>
            </div>
            <div>
                <button onclick="ClickEvents.lastGameBtn()">Последняя игра</button>
            </div>`;
        window.localStorage.setItem('lastPage', 'main');
    }

    static setPageQueue() {
        Pages.clearPage();

        Pages.mainEl.innerHTML = `
            <table>
                <tbody>
                    <tr>
                        <td id="cellA">&nbsp;</td>
                        <td id="cellB">&nbsp;</td>
                        <td id="cellC">&nbsp;</td>
                        <td id="cellD">&nbsp;</td>
                     </tr>
                </tbody>
            </table>
            
            <button id="leaveBtn" onclick="ClickEvents.leaveQueueBtn()">Покинуть очередь</button>
            <button id="readyBtn" onclick="ClickEvents.becomeReadyBtn()">Готов</button>
            `;
        readyBtn.style.display = "hidden";

        Cycle.actions.push(async function () {
            let res = await QueueRequests.queueResult();

            try {

                cellB.innerHTML = res.gameType;
                readyBtn.style.display = "hidden";

                switch (res.queueState) {
                    case 'waitingReady':
                    {
                        cellC.innerHTML = res.readyFirst;
                        cellD.innerHTML = res.readySecond;
                        cellA.innerHTML = 'Ожидание готовности';
                        readyBtn.style.display = "block";
                        break;
                    }
                    case 'queueing' :
                    {
                        cellC.innerHTML = "&nbsp;";
                        cellD.innerHTML = "&nbsp;";
                        cellA.innerHTML = "Позиция: " + res.position;
                        break;
                    }
                    case 'resolved':
                    {
                        Pages.setPageGame();
                        break;
                    }
                    case 'noPos':
                    {
                        Pages.setPageMain();
                        break;
                    }
                    default:
                    {

                    }
                }
            } catch (ex){
                if (ex.name !== 'ReferenceError'){
                    throw ex;
                }
            }
        });
        window.localStorage.setItem('lastPage', 'queue');
    }

    static setPageGame() {
        Pages.clearPage();


        Pages.mainEl.innerHTML = `game`;
        window.localStorage.setItem('lastPage', 'game');
    }
}

{
    if (CoockieManager.getCookie('Authorization') !== null
            && AuthRequests.validate() === false) {
        let pg = window.localStorage.getItem('lastPage');
        if (pg === "" || pg === undefined) {
            pg = "main";
        }
        window.localStorage.setItem('pageBeforeLogin', pg);
        Pages.setPage(window.localStorage.getItem('lastPage'));
    } else {
        Pages.setPageLogin();
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
