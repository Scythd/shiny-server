/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


class Cycle{
    
    static cycleId;
    
    static actions = new Array();
    
    
    static run () {
        Cycle.actions.forEach((x)=>{x();});
        
    }
    
    static init() {
        if (Cycle.cycleId !== undefined){
            window.clearInterval(Cycle.cycleId);
        }
        Cycle.cycleId = window.setInterval(Cycle.run, 2000);
    }
    
    static stop(){
        if (Cycle.cycleId !== undefined){
            window.clearInterval(Cycle.cycleId);
        }
    }
    
}

Cycle.init();