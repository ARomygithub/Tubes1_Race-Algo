package za.co.entelect.challenge;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import static java.lang.Math.*;

public class Result {
    public int time; //time, xbonus akhirnya tidak digunakan
    public int speed;
    public int damage;
    public int ctBoost;
    public int ctLizard;
    public int ctEmp;
    public int ctTweet;
    public int ctOil;
    public boolean boosting;
    public int boostcounter;
    public int xbonus=0;
    public int xr=0; //xr,yr hanya diset terakhir untuk command
    public int yr=0;

    public Result() {
        time=600;
        speed=0;
        damage=0;
        ctBoost=0;
        ctLizard=0;
        ctEmp=0;
        ctTweet=0;
        ctOil=0;
        boosting=false;
        boostcounter=0;
    }

    public Result(int speed, int damage, int ctBoost, int ctLizard, int ctEmp, int ctTweet, int ctOil, int boostcounter) {
        time = 600;
        this.speed = speed;
        this.damage = damage;
        this.ctBoost = ctBoost;
        this.ctLizard = ctLizard;
        this.ctEmp = ctEmp;
        this.ctTweet = ctTweet;
        this.ctOil = ctOil;
        this.boostcounter = boostcounter;
    }

    public Result(Terrain terrain) {
        time=600;
        speed=0;
        damage=0;
        ctBoost=0;
        ctLizard=0;
        ctEmp=0;
        ctTweet=0;
        ctOil=0;
        boosting=false;
        boostcounter=0;
        if(terrain==Terrain.EMPTY) {
            ;
        } else if(terrain==Terrain.MUD) {
            ;
        } else if(terrain==Terrain.OIL_SPILL) {
            ;
        } else if(terrain==Terrain.OIL_POWER) {
            ctOil +=1;
        } else if(terrain==Terrain.FINISH) {
            ;
        } else if(terrain==Terrain.BOOST) {
            ctBoost +=1;
        } else if(terrain==Terrain.WALL) {
            ;
        } else if(terrain==Terrain.LIZARD) {
            ctLizard +=1;
        } else if(terrain==Terrain.TWEET) {
            ctTweet +=1;
        } else if(terrain==Terrain.EMP) {
            ctEmp +=1;
        }
    }

    public Result(GameState gS) {
        time = 600;
        this.speed = gS.player.speed;
        this.damage = gS.player.damage;
        this.ctBoost = 0;
        this.ctLizard = 0;
        this.ctEmp = 0;
        this.ctTweet = 0;
        this.ctOil = 0;
        this.boosting = gS.player.boosting;
        this.boostcounter = gS.player.boostCounter;
        PowerUps[] pU = gS.player.powerups;
        for(int i=0;i<pU.length;i++) {
            if(pU[i]==PowerUps.BOOST) {
                ++this.ctBoost;
            } else if(pU[i]==PowerUps.LIZARD) {
                ++this.ctLizard;
            } else if(pU[i]==PowerUps.EMP) {
                ++this.ctEmp;
            } else if(pU[i]==PowerUps.TWEET) {
                ++this.ctTweet;
            } else if(pU[i]==PowerUps.OIL) {
                ++this.ctOil;
            }
        }
    }

    public Result(Result res) {
        time=res.time;
        speed=res.speed;
        damage=res.damage;
        ctBoost=res.ctBoost;
        ctLizard=res.ctLizard;
        ctEmp=res.ctEmp;
        ctTweet=res.ctTweet;
        ctOil=res.ctOil;
        boosting= res.boosting;
        boostcounter=res.boostcounter;
    }

    public void add(Result res) {
        speed +=res.speed;
        damage +=res.damage;
        ctBoost +=res.ctBoost;
        ctLizard +=res.ctLizard;
        ctEmp +=res.ctEmp;
        ctTweet +=res.ctTweet;
        ctOil +=res.ctOil;
        boostcounter +=res.boostcounter;
    }

    public void minus(Result res) {
        speed -=res.speed;
        damage -=res.damage;
        ctBoost -=res.ctBoost;
        ctLizard -=res.ctLizard;
        ctEmp -=res.ctEmp;
        ctTweet -=res.ctTweet;
        ctOil -=res.ctOil;
        boostcounter -=res.boostcounter;
    }

    public int greaterThanV1(Result res) {
        return this.damage-res.damage;
    }

    public int greaterThanV2(Result res) {
        return res.speed-this.speed;
    }

    public int greaterThanV3(Result res) {
        if(this.damage!=res.damage) {
            return this.damage-res.damage;
        }
        if(this.speed!=res.speed) {
            return res.speed-this.speed;
        }
        return res.xr-this.xr;
    }

    public int greaterThanV4(Result res) {
        if(this.damage!=res.damage) {
            return this.damage-res.damage;
        }
        if(this.speed!=res.speed) {
            return res.speed-this.speed;
        }
        if(abs(res.xr-this.xr)>1) {
            return res.xr-this.xr;
        }
        return res.ctBoost-this.ctBoost;
    }

    public int greaterThanV5(Result res) {
        if(this.damage!=res.damage) {
            return this.damage-res.damage;
        }
        if(this.speed!=res.speed) {
            return res.speed-this.speed;
        }
        if(abs(res.xr-this.xr)>1) {
            return res.xr-this.xr;
        }
        if(res.ctBoost!=this.ctBoost) {
            return res.ctBoost-this.ctBoost;
        }
        return res.ctLizard-this.ctLizard;
    }

    public int greaterThanV6(Result res) {
        // menentukan prioritas dari dua Result, yaitu this dan res.
        // bila this diprioritaskan, kembalikan bilangan negatif.
        // bila res diprioritaskan, kembalikan bilangan positif.
        // bila sama, kembalikan 0.

        // bila damage tidak sama, prioritaskan damage yang lebih kecil
        if(this.damage!=res.damage) {
            return this.damage-res.damage;
        }
        // bila speed tidak sama, prioritaskan speed yang lebih besar
        if(this.speed!=res.speed) {
            return res.speed-this.speed;
        }
        // bila jarak yang ditempuh berbeda setidaknya 2 blok, prioritaskan yang lebih jauh
        if(abs(res.xr-this.xr)>1) {
            return res.xr-this.xr;
        }
        // bila powerups boost yang dimiliki tidak sama, prioritaskan yang lebih banyak
        if(res.ctBoost!=this.ctBoost) {
            return res.ctBoost-this.ctBoost;
        }
        // bila powerups lizard yang dimiliki tidak sama, prioritaskan yang lebih banyak
        if(res.ctLizard!=this.ctLizard) {
            return res.ctLizard-this.ctLizard;
        }
        // prioritaskan yang memiliki powerups attack lebih banyak
        // Emp dibobot 4, Tweet dibobot 3, Oil dibobot 2.
        return (4*res.ctEmp+3*res.ctTweet+2*res.ctOil)-(4*this.ctEmp+3*this.ctTweet+2*this.ctOil);
    }
}
