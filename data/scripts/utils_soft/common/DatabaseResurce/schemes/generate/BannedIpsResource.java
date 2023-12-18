package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.BannedIpsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "banned_ips",
        primary_key = {IP},
        fields = {
                @Field(name = IP , type = VARCHAR , type_size = 15 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = ADMIN , type = VARCHAR , type_size = 45 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = EXPIRETIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = COMMENTS , type = VARCHAR , type_size = 255 , nullable = true , defValue = @DefValue(String = "")),
        }
)
public class BannedIpsResource extends DataBaseTable<BannedIpsResource> {

    public static final String IP = "ip";
    public static final String ADMIN = "admin";
    public static final String EXPIRETIME = "expiretime";
    public static final String COMMENTS = "comments";

    public BannedIpsResource() {
super(BannedIpsResource.class);
}


    public BannedIpsResource(String ip){
        super(BannedIpsResource.class);
        getSTAT_SET().set(IP, ip);
    }

    public String getIp() {
        return get(IP, String.class);
    }
    public String getAdmin() {
        return get(ADMIN, String.class);
    }
    public Integer getExpiretime() {
        return get(EXPIRETIME, Integer.class);
    }
    public String getComments() {
        return get(COMMENTS, String.class);
    }

    public void setIp(String value) {
        set(IP, value);
    }
    public void setAdmin(String value) {
        set(ADMIN, value);
    }
    public void setExpiretime(Integer value) {
        set(EXPIRETIME, value);
    }
    public void setComments(String value) {
        set(COMMENTS, value);
    }

}
