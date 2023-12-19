package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.BannedIpsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "banned_ips",
        primary_key = {IP},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 15 , name = IP , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 45 , name = ADMIN , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EXPIRETIME , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 255 , name = COMMENTS , defValue = "NULL"),
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

    public String getIp() {
        return get(IP);
    }
    public String getAdmin() {
        return get(ADMIN);
    }
    public Integer getExpiretime() {
        return get(EXPIRETIME);
    }
    public String getComments() {
        return get(COMMENTS);
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
