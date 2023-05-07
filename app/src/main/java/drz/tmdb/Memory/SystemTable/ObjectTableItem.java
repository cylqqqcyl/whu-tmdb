package drz.tmdb.memory.SystemTable;

//import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class ObjectTableItem implements Serializable{


    public int classid = 0;    //类id
    public int tupleid = 0;    //元组id
    public String classname="";
    public int sstSuffix = -1;  // SSTable文件的后缀

    public ObjectTableItem() {
    }

    public ObjectTableItem( int classid, int tupleid,String classname) {
        this.classid = classid;
        this.tupleid = tupleid;
        this.classname = classname;
    }

    @Override
    public boolean equals(Object object){
        if(this==object) return true;
        if (!(object instanceof ObjectTableItem)) {
            return false;
        }
        ObjectTableItem oi=(ObjectTableItem) object;
        if(this.classid!=oi.classid){
            return false;
        }
        if(this.tupleid!=oi.tupleid){
            return false;
        }
        return Objects.equals(this.classname, oi.classname);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hash(this.classid)+Objects.hash(this.tupleid)+Objects.hash(this.classname);
        return result;
    }

}