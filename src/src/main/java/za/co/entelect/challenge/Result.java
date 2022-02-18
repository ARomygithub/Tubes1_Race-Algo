package za.co.entelect.challenge;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import static java.lang.Math.*;

public class Result {
    public int time;
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
//        return new Result(this.speed+res.speed,this.damage+res.damage,this.ctBoost+res.ctBoost,this.ctLizard+res.ctLizard,this.ctEmp+res.ctEmp,this.ctTweet+res.ctTweet,this.ctOil+res.ctOil,this.boostcounter+res.boostcounter);
//        time +=res.time;
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
//        return new Result(this.speed-res.speed,this.damage-res.damage,this.ctBoost-res.ctBoost,this.ctLizard-res.ctLizard,this.ctEmp-res.ctEmp,this.ctTweet-res.ctTweet,this.ctOil-res.ctOil,this.boostcounter-res.boostcounter);
//        time -=res.time;
        speed -=res.speed;
        damage -=res.damage;
        ctBoost -=res.ctBoost;
        ctLizard -=res.ctLizard;
        ctEmp -=res.ctEmp;
        ctTweet -=res.ctTweet;
        ctOil -=res.ctOil;
        boostcounter -=res.boostcounter;
    }

    public Boolean greaterThan(Result res) {
        boolean ctBoostImportant = !(this.ctBoost>2 && res.ctBoost>2);
        if(this.time<res.time) {
            return true;
        } else if(this.time==res.time) {
            if(this.speed>res.speed) {
                return true;
            } else if(this.speed==res.speed) {
                // kombinasi damage, boostcounter, powerups
                // kalo orientasi nya kecepatan boost sm lizard penting.
                if(this.damage<res.damage) {
                    return true;
                } else if(this.damage==res.damage) {
                    if(ctBoostImportant) {
                        if(this.ctBoost>res.ctBoost) {
                            return true;
                        } else if(this.ctBoost<res.ctBoost) {
                            return false;
                        }
                    }
                    if(this.xbonus>res.xbonus) {
                        return true;
                    } else if(this.xbonus<res.xbonus) {
                        return false;
                    }
                    if(this.ctBoost>res.ctBoost) {
                        return true;
                    } else if(this.ctBoost<res.ctBoost) {
                        return false;
                    }
                    if((this.boostcounter>res.boostcounter)||(boostcounter==res.boostcounter && ctLizard>res.ctLizard)) {
                        return true;
                    } else if(ctLizard==res.ctLizard) {
                        if(ctEmp>res.ctEmp) {
                            return true;
                        } else if(ctEmp==res.ctEmp) {
                            if(ctTweet>res.ctTweet) {
                                return true;
                            } else if(ctTweet==res.ctTweet) {
                                if(ctOil>res.ctOil) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
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
        if(res.ctLizard!=this.ctLizard) {
            return res.ctLizard-this.ctLizard;
        }
        return (4*res.ctEmp+3*res.ctTweet+2*res.ctOil)-(4*this.ctEmp+3*this.ctTweet+2*this.ctOil);
    }
}
