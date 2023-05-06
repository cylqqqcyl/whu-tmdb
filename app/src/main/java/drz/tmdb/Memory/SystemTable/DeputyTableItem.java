package drz.tmdb.memory.SystemTable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class DeputyTableItem implements Serializable {
    public DeputyTableItem(int originid, int deputyid, String deputyname,String[] deputyrule) {
        this.originid = originid;
        this.deputyid = deputyid;
        this.deputyrule = deputyrule;
        this.deputyname = deputyname;
    }

    public DeputyTableItem() {
    }

    public int originid = 0;            //类id
    public int deputyid = 0;           //代理类id
    public String deputyname = "";
    public String[] deputyrule = new String[0];    //代理guizedui


    @Override
    public boolean equals(Object object){
        if(this==object) return true;
        if (!(object instanceof DeputyTableItem)) {
            return false;
        }
        DeputyTableItem oi=(DeputyTableItem) object;
        if(this.originid!=oi.originid){
            return false;
        }
        if(this.deputyid!=oi.deputyid){
            return false;
        }
        if(this.deputyrule!=oi.deputyrule){
            return false;
        }

        return Objects.equals(this.deputyname, oi.deputyname);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hash(this.originid)+Objects.hash(Arrays.stream(this.deputyrule).toArray())+Objects.hash(this.deputyid)+Objects.hash(this.deputyname);
        return result;
    }
}
