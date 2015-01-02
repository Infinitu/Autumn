package autumn.database;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Created by infinitu on 14. 12. 9..
 */
public class TableQuery<T extends Table> extends AbstractQuery<T> {
    public TableQuery(Class<T> cls){
        super(cls);
    }
    public static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    @Override
    protected void initInertSQLStr() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(table.getTableName());
        stringBuilder.append('(');

        String prefix = "";

        for(Column col : columnList){
            stringBuilder.append(prefix);
            prefix = ", ";
            stringBuilder.append(col.getColumnName());
        }

        stringBuilder.append(')');

        String insertTableSQL = stringBuilder.toString();
        insertSQLFormat = String.format(INSERT_QUERY_FORMAT, insertTableSQL,"%s");
    }

    @Override
    protected String genInsertSQL(Object[] data) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";

        for (Object obj : data){

            sb.append(prefix);
            prefix=", ";


            sb.append('(');
            String prefix_ = "";
            for(Field f : mappingFields){
                try {
                    Object o = f.get(obj);

                    sb.append(prefix_);
                    prefix_=", ";

                    if(o==null)
                        sb.append("NULL");
                    else if(o.getClass().equals(String.class)){
                        sb.append('\'');
                        sb.append(o.toString());
                        sb.append('\'');
                    }
                    else if(o.getClass().equals(Timestamp.class)
                            | o.getClass().equals(Date.class)
                            | o.getClass().equals(java.util.Date.class)){
                        sb.append('\'');
                        sb.append(dateFormatter.format(o));
                        sb.append('\'');
                    }
                    else
                        sb.append(o.toString());

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            sb.append(')');
        }

        return String.format(insertSQLFormat, sb.toString());
    }


    @Override
    protected void initDeleteSQLStr() {
        deleteSQLFormat = String.format(DELETE_QUERY_FORMAT,table.getTag() ,table.toSQL(),"%s");
    }


    @Override
    protected String genDeleteSQL() {
        return String.format(deleteSQLFormat, genWhereCondition());
    }



}
