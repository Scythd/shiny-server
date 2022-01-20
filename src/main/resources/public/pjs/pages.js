/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */



/* global CoockieManager, AuthRequests, ClickEvents, readyBtn, Cycle, QueueRequests, cellB, cellC, cellD, cellA, GameBullCowRequests, turn, askedNumber, ansHist, ansBtn, guessBtn */

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
                        window.localStorage.setItem('gameType', res.gameType);
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
            } catch (ex) {
                if (ex.name !== 'ReferenceError') {
                    throw ex;
                }
            }
        });
        window.localStorage.setItem('lastPage', 'queue');
    }

    static setPageGame() {
        Pages.clearPage();


        Pages.mainEl.innerHTML = `
            <input id="gameInput" pattern="[0-9]{4}">
            <button id="ansBtn" onclick="ClickEvents.postAnsGameBtn()">Задать значение</button>
            <button id="guessBtn" onclick="ClickEvents.postGuessGameBtn()">Ответить</button>
            <button onclick="ClickEvents.toMainBtn()">На главную</button>
            <br>
            <label id="turn">&nbsp;</label>
            <br/>
            <label id="askedNumber">Ваше заданное значение: </label>
            <table>
                <tbody id="ansHist">
                    <tr id="row0">
                        <td>
                            Ваш ответ
                        </td>
                        <td>
                            Ваши быки
                        </td>
                        <td>
                            Ваши коровы
                        </td>
                        <td>
                            Ответ оппонента
                        </td>
                        <td>
                            Быки оппонента
                        </td>
                        <td>
                            Коровы оппонента
                        </td>
                    </tr>
                </tbody>
            </table>`;

        ansBtn.style.display = "hidden";
        guessBtn.style.display = "hidden";

        Cycle.actions.push(async function () {
            let gameType = window.localStorage.getItem('gameType');
            let gameObj;


            switch (gameType) {
                case "BullCow":
                {
                    gameObj = await GameBullCowRequests.gameInfo();
                    if (gameObj.yourAskedNumber !== -1) {
                        askedNumber.innerHTML = 'Ваше загаданное значение: ' + (gameObj.yourAskedNumber < 1000 ? '0' + gameObj.yourAskedNumber : gameObj.yourAskedNumber);
                    } else {
                        askedNumber.innerHTML = 'Задайте ответ';
                    }

                    let fIO = ansHist.innerHTML.indexOf('</tr>');
                    let indexToUpdate = -1;
                    while (fIO !== -1) {
                        fIO = ansHist.innerHTML.indexOf('</tr>', fIO + 1);
                        indexToUpdate++;
                    }
                    let i;
                    for (i = indexToUpdate; i < Math.max(gameObj.yourAnswerHistory.length, gameObj.opponentAnswerHistory.length); i++) {
                        ansHist.innerHTML += `
                        <tr id="row` + (i + 1) + `">
                            <td ></td>
                            <td ></td>
                            <td ></td>
                            <td ></td>
                            <td ></td>
                            <td ></td>
                        </tr>`;
                    }
                    for (i = 0; i < gameObj.yourAnswerHistory.length; i++) {
                        let el = document.getElementById("row" + (i + 1));
                        el.children[0].innerHTML = gameObj.yourAnswerHistory[i] < 1000 ? '0' + gameObj.yourAnswerHistory[i] : gameObj.yourAnswerHistory[i];
                        el.children[1].innerHTML = gameObj.yourBullsHistory[i];
                        el.children[2].innerHTML = gameObj.yourCowsHistory[i];

                    }
                    for (i = 0; i < gameObj.opponentAnswerHistory.length; i++) {
                        let el = document.getElementById("row" + (i + 1));
                        el.children[3].innerHTML = gameObj.opponentAnswerHistory[i] < 1000 ? '0' + gameObj.opponentAnswerHistory[i] : gameObj.opponentAnswerHistory[i];
                        el.children[4].innerHTML = gameObj.opponentBullsHistory[i];
                        el.children[5].innerHTML = gameObj.opponentCowsHistory[i];
                    }
                    break;
                }
                case "Chess":
                {
                    gameObj = null;
                    break;
                }
                case "Checkers":
                {
                    gameObj = null;
                    break;
                }
                default:
                {
                    // must not happen
                }
            }
            // first part even = second, otherwise = first
            // second part player first - 1 = 0, second player - 1 = 1
            // when even and player second (0 != 1)
            // when not even and player first (1 != 0)
            // so player s turn
            if (gameObj.winPlayer === 'NA') {
                if (gameObj.turn !== undefined && gameObj.turn > 0) {
                    if ((gameObj.turn % 2) !== (gameObj.playerNum - 1)) {
                        turn.innerHTML = "Ваш ход";
                        guessBtn.style.display = "block";
                        ansBtn.style.display = "hidden";
                    } else {
                        turn.innerHTML = "Ход оппонента";
                        guessBtn.style.display = "hidden";
                        ansBtn.style.display = "hidden";
                    }
                } else {
                    ansBtn.style.display = "block";

                    turn.innerHTML = "Ожидание задания ответа игроками";
                }
            } else {
                switch (gameObj.winPlayer) {
                    case 'DRAW':
                    {
                        turn.innerHTML = "Результат: Ничья";
                        break;
                    }
                    case 'FIRST_PLAYER':
                    {
                        if (gameObj.playerNum === 1) {
                            turn.innerHTML = "Результат: Вы победили";
                        } else {
                            turn.innerHTML = "Результат: Вы проиграли";
                        }
                        break;
                    }
                    case 'SECOND_PLAYER':
                    {
                        if (gameObj.playerNum === 2) {
                            turn.innerHTML = "Результат: Вы победили";
                        } else {
                            turn.innerHTML = "Результат: Вы проиграли";
                        }
                        break;
                        break;
                    }
                }
                guessBtn.style.display = "hidden";
                ansBtn.style.display = "hidden";
            }

        });
        window.localStorage.setItem('lastPage', 'game');
    }
}

{
    if (CoockieManager.getCookie('Authorization') !== null
            && AuthRequests.validate() === true) {
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
