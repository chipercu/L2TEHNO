package utils_soft.NpcEditor.models;

import utils_soft.common.Model;

import java.sql.ResultSet;

/**
 * Created by a.kiperku
 * Date: 15.12.2023
 */

public class NpcElementModel extends Model {

    public NpcElementModel(int mobId){
        super("npc_element", String.valueOf(mobId));
    }

    public int getId() {
        return getStat("id", 0);
    }

    public int getAtkElement() {
        return getStat("AtkElement", -1);
    }

    public int getElemAtkPower() {
        return getStat("elemAtkPower", 0);
    }

    public int getFireRes() {
        return getStat("FireRes", 0);
    }

    public int getWaterRes() {
        return getStat("WaterRes", 0);
    }

    public int getWindRes() {
        return getStat("WindRes", 0);
    }

    public int getEarthRes() {
        return getStat("EarthRes", 0);
    }

    public int getHolyRes() {
        return getStat("HolyRes", 0);
    }

    public int getDarkRes() {
        return getStat("DarkRes", 0);
    }


    public void setAtkElement(int value) {
        setStat("AtkElement", value);
    }

    public void setElemAtkPower(int value) {
        setStat("elemAtkPower", value);
    }

    public void setFireRes(int value) {
        setStat("FireRes", value);
    }

    public void setWaterRes(int value) {
        setStat("WaterRes", value);
    }

    public void setWindRes(int value) {
        setStat("WindRes", value);
    }

    public void setEarthRes(int value) {
        setStat("EarthRes", value);
    }

    public void setHolyRes(int value) {
        setStat("HolyRes", value);
    }

    public void setDarkRes(int value) {
        setStat("DarkRes", value);
    }

}
