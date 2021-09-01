package com.lenovo.lefacecamerademo.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

public class MyDbHelper extends SQLiteOpenHelper
{
    private Context mycontext;
    //数据库版本号

    //会加入本地数据库存储最大人脸数、识别卡数量 cpb


    private static Integer Version = 1;
    private static MyDbHelper dbHelper = null;
    private static String TAG = "MyDbHelper";
    public static boolean InitDb(Context context, String name)
    {
        if(dbHelper == null) {
            try
            {
                dbHelper = new MyDbHelper(context, name);
                dbHelper.createDb();
                //Log.i("InitDb","dbPath = "+dbHelper.getDatabaseName());
                return true;

            } catch (Exception ex)
            {
                Log.e("InitDb","Exception: "+ex.toString());
                ex.printStackTrace();
            }
        }
        return false;
    }
    public static MyDbHelper Instance()
    {
        return dbHelper;
    }
    public static void delDatabaseByName(Context context, String dbname)
    {
        context.deleteDatabase(dbname);
    }
    //在SQLiteOpenHelper的子类当中，必须有该构造函数
    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
        this.mycontext = context;
    }
    //参数说明
    //context:上下文对象
    //name:数据库名称
    //param:factory
    //version:当前数据库的版本，值必须是整数并且是递增的状态

    public MyDbHelper(Context context,String name,int version)
    {
        this(context,name,null,version);
    }


    public MyDbHelper(Context context,String name)
    {
        this(context, name, Version);
    }

    public void createDb()
    {
        SQLiteDatabase db = super.getWritableDatabase();
        Log.i("dbhelper","dbPath = "+db.getPath());
        db.close();
    }
    //当数据库创建的时候被调用
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i("dbhelper","创建数据库和表: "+db.toString());
        //创建了数据库并创建一个叫records的表
        //SQLite数据创建支持的数据类型： 整型数据，字符串类型，日期类型，二进制的数据类型
        String[] sqls = new String[]{
                "create table IF NOT EXISTS user_faces(id integer primary key AUTOINCREMENT,user_id varchar(32), user_name varchar(32),face_feature blob, ctime TIMESTAMP  DEFAULT CURRENT_TIMESTAMP, utime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP); ",    // ON UPDATE CURRENT_TIMESTAMP);";
                "create table IF NOT EXISTS user_cardnums(id integer primary key AUTOINCREMENT,user_id varchar(32),user_name varchar(32),card_num varchar(16), ctime TIMESTAMP  DEFAULT CURRENT_TIMESTAMP, utime TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ); ",
                "create table IF NOT EXISTS pass_logs(id integer primary key AUTOINCREMENT,user_id varchar(32),auth_mode char(1),auth_ok char(1), device_id varchar(32),card_num varchar(16),img_data blob,face_feature blob, face_score float, ctime TIMESTAMP DEFAULT CURRENT_TIMESTAMP); "
        };
        //execSQL用于执行SQL语句
        //完成数据库的创建
        try {
            for (String sql : sqls) {
                db.execSQL(sql);  //不能一次执行多个sql语句（分号分割），要逐句执行才可以
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("dbhelper","onCreate Exception: "+ex.toString());
        }
        //数据库实际上是没有被创建或者打开的，直到getWritableDatabase() 或者 getReadableDatabase() 方法中的一个被调用时才会进行创建或者打开


    }

    //数据库升级时调用
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade（）方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //根据版本号，更新脚本
        //db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        //onCreate(db);

        Log.i("dbhelper","更新数据库版本为:"+newVersion);
    }

    public String[] getUserInfoByCardnum(String cardnum)
    {
		SQLiteDatabase db = null;
		String[] strVals = null;
		try
		{
			//MyDbHelper dbHelper4 = new MyDbHelper(this,"test_carson",2);
			// 调用getWritableDatabase()方法创建或打开一个可以读的数据库
			db = this.getReadableDatabase();
			// 调用SQLiteDatabase对象的query方法进行查询
			// 返回一个Cursor对象：由数据库查询返回的结果集对象
			Cursor cursor = db.query("user_cardnums", new String[] { "user_id",
					"user_name" }, "card_num=?", new String[] { cardnum }, null, null, null);

			//将光标移动到下一行，从而判断该结果集是否还有下一条数据
			//如果有则返回true，没有则返回false
			if (cursor.moveToNext()) {
				strVals = new String[2];
				strVals[0] = cursor.getString(cursor.getColumnIndex("user_id"));
				strVals[1] = cursor.getString(cursor.getColumnIndex("user_name"));
				//输出查询结果
                Log.i("dbhelper","查询到的数据是:"+"user_id: "+strVals[0]+"  "+"user_name: "+strVals[1]);

			}
			return strVals;
		}		
        catch (Exception ex)
        {
            Log.e("dberror", ex.toString());
        } 
		finally {
            if(db != null) {
                //关闭数据库
                db.close();
            }

        }
        //
        return strVals;
    }

    public String[] getUserInfoByUserId(String uid, String flag)
    {
		SQLiteDatabase db = null;
		String[] strVals = null;
        String tablename = "";
        if(flag == "card") {
            tablename = "user_cardnums";
        } else if (flag == "face") {
            tablename = "user_faces";
        } else {
            return null;
        }
		try
		{
			//MyDbHelper dbHelper4 = new MyDbHelper(this,"test_carson",2);
			// 调用getWritableDatabase()方法创建或打开一个可以读的数据库
			db = this.getReadableDatabase();
			// 调用SQLiteDatabase对象的query方法进行查询
			// 返回一个Cursor对象：由数据库查询返回的结果集对象
			Cursor cursor = db.query(tablename, new String[] {
					"user_name" }, "user_id=?", new String[] { uid }, null, null, null);


			//将光标移动到下一行，从而判断该结果集是否还有下一条数据
			//如果有则返回true，没有则返回false
			if (cursor.moveToNext()) {
				strVals = new String[2];
				strVals[0] = uid; //cursor.getString(cursor.getColumnIndex("user_id"));
				strVals[1] = cursor.getString(cursor.getColumnIndex("user_name"));
				//输出查询结果
                Log.i("dbhelper","查询到的数据是:" + "user_id: " + strVals[0] + "  " + "user_name: " + strVals[1]);
			}
			return strVals;
		}
		catch (Exception ex)
        {
            Log.e("dberror", ex.toString());
        } 
		finally {
            if(db != null) {
                //关闭数据库
                db.close();
            }

        }
        //
        return strVals;
    }

    public java.util.LinkedHashMap<java.lang.String,float[]> loadAllFaces()
    {
        SQLiteDatabase db = null;
		java.util.LinkedHashMap<java.lang.String,float[]> features = new java.util.LinkedHashMap<java.lang.String,float[]>();
		try
		{
			db = this.getReadableDatabase();
			String uid = null;
			byte[] bArrayFeat = {};
			int i = 0, each_cnt = 1000;
			//分批读取； 一次全读出,会出现内存警告: CursorWindow: Window is full: requested allocation 1024 bytes, free space 56 bytes, window size 2097152 bytes
			while(true) {
                Cursor cursor = db.query("user_faces", new String[] {"user_id",
                        "face_feature" }, null, null, null, null, null,String.format("%d,%d",i, each_cnt));
                if(cursor.getCount() < 1)
                {
                    break;
                }
                while (cursor.moveToNext()) {
                    uid = cursor.getString(cursor.getColumnIndex("user_id"));
                    bArrayFeat = cursor.getBlob(cursor.getColumnIndex("face_feature"));
                    features.put(uid, MyUtils.ByteArrayToFloatArray(bArrayFeat));
                    //输出查询结果
                    //System.out.println("查询到的数据是:"+"user_id: "+uid+"  "+"feature bytes: "+String.format("%d",bArrayFeat.length));
                }
                cursor.close();
                i+=each_cnt;
            }
            Log.i("dbhelper","查询到人脸数据:"+features.size());
			float[] feats = new float[] {-0.06392403692007065f, 0.0026356345042586327f, -0.0462053082883358f, -0.052314598113298416f, 0.03260710462927818f, 0.13700877130031586f, 0.07615628838539124f, -0.02238902449607849f, -0.023147789761424065f, -0.045561373233795166f, 0.05750279128551483f, -0.07523790001869202f, 0.024277862161397934f, 0.033988285809755325f, -0.05057735741138458f, 0.05584760755300522f, -0.04191442206501961f, 0.04734804853796959f, -0.1398877650499344f, 0.11046319454908371f, -0.08271420747041702f, 0.006454231217503548f, 0.05601922795176506f, 0.008744451217353344f, 0.0647006556391716f, 0.01580791547894478f, 0.04096861556172371f, 0.11064749956130981f, -0.0314924456179142f, -0.07780387997627258f, 0.018387189134955406f, -0.037185680121183395f, 0.04874426871538162f, 0.02073485776782036f, -0.0683186873793602f, -0.15601439774036407f, -0.04542176052927971f, -0.04169683903455734f, -0.07904325425624847f, 0.03641315922141075f, 0.056134629994630814f, 0.018944934010505676f, 0.01780254952609539f, -0.10139236599206924f, 0.009718898683786392f, -0.09798939526081085f, -0.007595644798129797f, 0.026092659682035446f, -0.05114060267806053f, -0.06511043757200241f, 0.00456719845533371f, 0.01314536016434431f, -0.016303202137351036f, 0.04225693643093109f, 0.05864300578832626f, 0.000470582366688177f, -0.023658903315663338f, -0.048472512513399124f, -0.06305278837680817f, -0.11954214423894882f, -0.032705213874578476f, -0.03929077461361885f, 0.04173462465405464f, -0.014713932760059834f, -0.10736124217510223f, 0.11394745111465454f, -0.07350117713212967f, -0.045833468437194824f, -0.10811121761798859f, -0.05948527157306671f, 0.012523910030722618f, 0.04858824983239174f, 0.05635354295372963f, 0.06485028564929962f, -0.04925094544887543f, 0.02233658730983734f, 0.07356277108192444f, -0.0058122677728533745f, -0.05407314375042915f, 0.06470920890569687f, -0.012072204612195492f, 0.028099460527300835f, -0.04960022494196892f, -0.0023343341890722513f, 0.007518554572016001f, 0.04103117808699608f, 0.04385696351528168f, -0.12658807635307312f, 0.028246978297829628f, 0.008552715182304382f, 0.050423625856637955f, 0.057637590914964676f, 0.08729276806116104f, -0.09315933287143707f, -0.019992223009467125f, 0.013745121657848358f, -0.003373531624674797f, -0.10014014691114426f, -0.04867211729288101f, -0.04650585353374481f, 0.011192616075277328f, -0.011731838807463646f, 0.06632727384567261f, 0.08681610226631165f, -0.0017242803005501628f, -0.012556701898574829f, 0.02304754965007305f, -0.0311751589179039f, 0.015191585756838322f, -0.09438048303127289f, 0.16373835504055023f, 0.058199796825647354f, -0.02770838513970375f, 0.11787143349647522f, -0.07595355063676834f, -0.04969343543052673f, 0.11420302093029022f, 0.011756429448723793f, -0.018671413883566856f, 0.04421339929103851f, 0.02174961008131504f, -0.03869437798857689f, -0.05973019450902939f, 0.11739756166934967f, 0.03295445442199707f, 0.0528111457824707f, -0.03290759027004242f, 0.01756076142191887f, -0.09924060106277466f, 0.07400964200496674f, -0.12949185073375702f, -0.028378242626786232f, -0.13328434526920319f, -0.034970253705978394f, 0.06901507824659348f, 0.03608456626534462f, 0.02317310869693756f, -0.022963805124163628f, -0.015904007479548454f, -0.08513297885656357f, 0.002543617272749543f, 0.016002310439944267f, 0.05162949487566948f, -0.165076345205307f, -0.020856402814388275f, -0.07514093071222305f, -0.06578481197357178f, -0.04714711010456085f, 0.018967928364872932f, 0.0624823234975338f, -0.03004746325314045f, -0.03786792233586311f, 0.06552642583847046f, 0.03438882157206535f, -0.030423877760767937f, -0.0450739823281765f, 0.14728961884975433f, 0.05841216444969177f, 0.08890581876039505f, -0.05454837530851364f, 0.05921214073896408f, -0.058990925550460815f, 0.011330119334161282f, 0.04954640939831734f, 0.025895556434988976f, 0.013718970119953156f, 0.03479338064789772f, 0.021350186318159103f, -0.06125061959028244f, 0.14798420667648315f, -0.11825154721736908f, -0.08830592781305313f, -0.038170889019966125f, -0.06530264765024185f, 0.054292187094688416f, 0.016054462641477585f, 0.03484402224421501f, -0.05547548085451126f, -0.005612784996628761f, 0.036303818225860596f, 0.01529370341449976f, 0.029085494577884674f, -0.013062220998108387f, 0.0018116674618795514f, -0.030784059315919876f, -0.025359036400914192f, 0.05400324612855911f, -0.09796430170536041f, 0.03072374500334263f, 0.04908331483602524f, -0.1278967261314392f, 0.012551075778901577f, -0.044606875628232956f, -0.0910748615860939f, 0.12459388375282288f, -0.00440739281475544f, 0.04878142476081848f, 0.0429043285548687f, -0.08989149332046509f, 0.06065850704908371f, -0.056555576622486115f, -0.08289215713739395f, 0.08701344579458237f, -0.08740507811307907f, 0.0037499023601412773f, -0.06292643398046494f, -0.030173152685165405f, -0.08658406138420105f, 0.02434738352894783f, -0.05413604900240898f, -0.008492930792272091f, -0.010467569343745708f, 0.12251162528991699f, 0.05124497786164284f, 0.025312040001153946f, 0.07822336256504059f, -0.07406603544950485f, -0.01159422006458044f, 0.010311544872820377f, 0.004290297161787748f, 0.016573302447795868f, 0.05440543591976166f, -0.1311521679162979f, 0.10654449462890625f, -0.02818569727241993f, 0.038120515644550323f, -0.053502652794122696f, 0.017609257251024246f, 0.02788621559739113f, 0.025634020566940308f, 0.08865784108638763f, -0.036733388900756836f, 0.015004055574536324f, -0.05311731994152069f, 0.07822025567293167f, -0.06863432377576828f, -0.019473176449537277f, -0.09125864505767822f, 0.09026877582073212f, -0.0014171487418934703f, 0.05716903880238533f, -0.02895774319767952f, 0.11680974811315536f, -0.0832236260175705f, -0.04135511443018913f, 0.04806467145681381f, 0.062471259385347366f, -0.07751842588186264f, -0.020750824362039566f, 0.05632660537958145f, -0.011092418804764748f, 0.03388145565986633f, 0.0449308380484581f, 0.08910782635211945f, -0.0424121618270874f, 0.0028749213088303804f};
            features.put("test001", feats);
			return features;
		}
		catch (Exception ex)
        {
            Log.e("dberror", ex.toString());
        } 
		finally {
            if(db != null) {
                //关闭数据库
                db.close();
            }

        }
        return features;
    }
    public int addPassLog(Object[] params)
    {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
        /*
        // 创建ContentValues对象
        ContentValues values1 = new ContentValues();

        // 向该对象中插入键值对
        values1.put("user_id", params[0].toString());
        values1.put("auth_mode", params[1].toString());
        values1.put("auth_ok", params[2].toString());
        values1.put("device_id", params[3].toString());
        values1.put("card_num", params[4].toString());
        values1.put("img_data", (byte[])params[5]);
        values1.put("face_feature", (byte[])params[6]);
        values1.put("face_score", (float)params[7]);

        // 调用insert()方法将数据插入到数据库当中
        db.insert("pass_logs", null, values1);
*/
            String sql = "insert into pass_logs(user_id,auth_mode,auth_ok,device_id,card_num,img_data,face_feature,face_score) values(?,?,?,?,?,?,?,?)";
            db.execSQL(sql, params);
            // sqliteDatabase.execSQL("insert into user (id,name) values (1,'carson')");
            return 1;
        }
        catch (Exception ex)
        {
            Log.e("dberror", ex.toString());
        }
        finally {
            if(db != null) {
                //关闭数据库
                db.close();
            }

        }

        return 0;
    }

    public int addOrUpdUserFace(String user_id, String user_name,  byte[] feature)
    {
        String[] uinfo = getUserInfoByUserId(user_id, "face");
        String uid = (uinfo==null?"":uinfo[0]);
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String sql = "";
            if(uid.isEmpty()) {
                sql = "insert into user_faces(user_id,user_name,face_feature) values(?,?,?)";
                db.execSQL(sql, new Object[]{user_id, user_name, feature});
            } else {
                sql = "update user_faces set user_name=?,face_feature=? where user_id=?";
                db.execSQL(sql, new Object[]{user_name, feature, user_id});
            }
            // sqliteDatabase.execSQL("insert into user (id,name) values (1,'carson')");
            Log.i(TAG, "addOrUpdUserFace： " + sql);
            return 1;
        }
        catch (Exception ex)
        {
            Log.e("dberror", ex.toString());
        } 
		finally {
            if(db != null) {
                //关闭数据库
                db.close();
            }

        }

        return 0;
    }

    public int addOrUpdUserCard(String user_id, String user_name,  String card_num)
    {
        String[] uinfo = getUserInfoByUserId(user_id, "card");
        String uid = (uinfo==null?"":uinfo[0]);
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String sql = "";
            if(uid.isEmpty()) {
                sql = "insert into user_cardnums(user_id,user_name,card_num) values(?,?,?)";
                db.execSQL(sql, new Object[]{user_id, user_name, card_num});
            } else {
                sql = "update user_cardnums set user_name=?, card_num=? where user_id=?";
                db.execSQL(sql, new Object[]{ user_name, card_num, user_id});
            }
            Log.i(TAG, "addOrUpdUserCard： " + sql);
            // sqliteDatabase.execSQL("insert into user (id,name) values (1,'carson')");
            return 1;
        }
        catch (Exception ex)
        {
            Log.e("dberror", ex.toString());
        }
        finally {
            if(db != null) {
                //关闭数据库
                db.close();
            }

        }

        return 0;
    }
}
