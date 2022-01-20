/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


class Cycle {

    static cycleId;

    static actions = new Array();

    static async run() {

        for (let x of Cycle.actions) {
            try {
                await x();
            } catch (ex){
                console.log(ex);
            }
        }

        Cycle.cycleId = window.setTimeout(Cycle.run, 1000);
    }

    static init() {
        if (Cycle.cycleId !== undefined) {
            window.clearTimeout(Cycle.cycleId);
        }
        Cycle.cycleId = window.setTimeout(Cycle.run, 1000);
    }

    static stop() {
        if (Cycle.cycleId !== undefined) {
            window.clearTimeout(Cycle.cycleId);
        }
    }

}

Cycle.init();