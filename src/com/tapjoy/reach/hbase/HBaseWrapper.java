package com.tapjoy.reach.hbase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;


//revised by LJ 10-29
public class HBaseWrapper {

    private static Configuration conf = null;

    private static HTablePool htpool = null;

    protected static HConnection conn = null;

    private static Logger logger = Logger.getLogger(HBaseWrapper.class);

    /**
     * Initialization
     */
    public static void init() {
        conf = HBaseConfiguration.create();

        try {
            conn = HConnectionManager.createConnection(conf);
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        }

        htpool = new HTablePool(conf, 20);

        if (conf == null || conn == null || htpool == null) {
            logger.error("HBase init -- hbase init failed! ");
            System.out.println("Error in hbase init");
        }
    }

    public static void shutdown() {
        if (htpool != null) {
            try {
                htpool.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            if (conn != null)
                conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conf = null;
    }

    public static HTablePool getHPool() {
        return htpool;
    }

    /**
     * Create a table
     */
    public static boolean createTable(String tableName, String[] familys) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(conf);

            if (admin.tableExists(tableName)) {
                logger.error("HBase creatTable - table already exists!  -- tableName: "
                        + tableName);
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(tableName);
                for (int i = 0; i < familys.length; i++) {
                    tableDesc.addFamily(new HColumnDescriptor(familys[i]));
                }
                admin.createTable(tableDesc);
                logger.debug("HBase create table - " + tableName + " - ok.");
                return true;
            }

        } catch (Exception e) {
            logger.error("HBase creatTable - Exception happend. TableName: "
                    + tableName);
            e.printStackTrace();
        } finally {
            if (admin != null)
                try {
                    admin.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return false;
    }

    /**
     * Delete a table
     */
    public static boolean deleteTable(String tableName) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(conf);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            logger.debug("HBase delete table - " + tableName + " - ok.");
            return true;
        } catch (Exception e) {
            logger.error("HBase deleteTable - Exception happend. TableName: "
                    + tableName);
            e.printStackTrace();
        } finally {
            if (admin != null)
                try {
                    admin.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return false;
    }

    /*
     * List all the table names by LJ
     */
    public static String[] listTableNames() {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(conf);
            HTableDescriptor[] tables = admin.listTables();
            logger.debug("HBase table listed - " + " - ok.");
            String[] tablenames = new String[tables.length];
            for (int i = 0; i < tables.length; i++)
                tablenames[i] = tables[i].getNameAsString();
            return tablenames;
        } catch (Exception e) {
            logger.error("HBase listTables - Exception happend.");
            e.printStackTrace();
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return new String[1];
    }

    /**
     * Put (or insert) a row
     */
    public static boolean addRecord(String tableName, String rowKey,
            String family, String qualifier, String value) {
        HTableInterface table = htpool.getTable(tableName);
        try {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
                    Bytes.toBytes(value));
            table.put(put);
            logger.debug("HBase insert record " + rowKey + " to table "
                    + tableName + " ok.");
            return true;
        } catch (IOException e) {
            logger.error("HBase addRecord - Exception happend. TableName: "
                    + tableName + "; RowKey: " + rowKey);
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    // added by LJ
    public static boolean checkandaddRecord(String tableName, String rowKey,
            String family, String qualifier, String value) {
        HTableInterface table = htpool.getTable(tableName);
        try {
            table.checkAndPut(Bytes.toBytes(rowKey), Bytes.toBytes(family),
                    Bytes.toBytes(qualifier), Bytes.toBytes(value), null);
            logger.debug("HBase checked and insert record " + rowKey
                    + " to table " + tableName + " ok.");
            return true;
        } catch (IOException e) {
            logger.error("HBase checkandaddRecord - Exception happend. TableName: "
                    + tableName + "; RowKey: " + rowKey);
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Delete a row
     */
    public static boolean delRecord(String tableName, String rowKey) {
        HTableInterface table = htpool.getTable(tableName);
        try {
            List<Delete> list = new ArrayList<Delete>();
            Delete del = new Delete(rowKey.getBytes());
            list.add(del);
            table.delete(list);
            logger.debug("HBase del record ok. TableName: " + tableName
                    + "; Rowkey: " + rowKey);
            return true;
        } catch (IOException e) {
            logger.error("HBase delRecord - Exception happend. TableName: "
                    + tableName + "; RowKey: " + rowKey);
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Get a row
     */
    public static Result getOneRecord(String tableName, String rowKey) {

        HTableInterface table = htpool.getTable(tableName);
        rowKey.replace(':', ','); // added 10-30
        try {
            Get get = new Get(rowKey.getBytes());
            Result rs = table.get(get);
            return rs;
        } catch (Exception e) {
        } finally {
            if (table != null) {
                try {
                    table.close();
                    table = null;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static Result getOneRecordInTable(String udid, HTable table,
            int token, boolean versionFlag) throws ClassNotFoundException,
            SQLException, InterruptedException { // added by LJ
        byte[] rowKey = null;

        try {
            udid.replace(':', ','); // added 10-30
            rowKey = HBaseUtil.constructKey(token, udid);
            Get get = new Get(rowKey);
            if (versionFlag) {
                get.setMaxVersions();
            }
            Result rs = table.get(get);
            return rs;
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error(e);
        }
        return null;
    }
    
    public static Result getOneRecordInTable(String key, String tableName,
            int token) throws ClassNotFoundException,
            SQLException, InterruptedException { 
        byte[] rowKey = null;

        try {
        	HTableInterface table = htpool.getTable(tableName);
            rowKey = HBaseUtil.constructKey(token, key);
            Get get = new Get(rowKey);
            Result rs = table.get(get);
            return rs;
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error(e);
        }
        return null;
    }

    /**
     * Scan (or list) a table
     */
    public static ResultScanner getAllRecord(String tableName, int cacheSize) {
        HTableInterface table = htpool.getTable(tableName);
        try {
            Scan s = new Scan();
            s.setCaching(cacheSize);

            ResultScanner ss = table.getScanner(s);
            return ss;
        } catch (IOException e) {
            logger.error("Hbase getAllRecord -- exception happened");
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                    table = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Scan (or list) a table
     */
    public static ResultScanner scanRecord(String tableName, String startRow,
            String endRow) {
        HTableInterface table = htpool.getTable(tableName);
        try {
            Scan s = null;
            if (startRow == null) {
                return null;
            } else if (endRow != null) {
                s = new Scan(startRow.getBytes(), endRow.getBytes());
            }
            return table.getScanner(s);
        } catch (IOException e) {
            logger.error("Hbase scanRecord -- exceptino happened");
            e.printStackTrace();
        } finally {
            if (table != null) {
                try {
                    table.close();
                    table = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

 /*   public static Result getOneRecordInTableWithTimeout(String udid,
            int tokenorder, String[] tablenames, long timeoutparam,
            boolean versionFlag) // timeout
    // parameter
    // in
    // milliseconds
    {
        if (tablenames == null || tablenames.length < 1)
            return null;
        int tsize = tablenames.length;
        int token = TokenCache.getToken(tokenorder);
        CountDownLatch doneSignal = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<Thread>();

        HBaseConcurrentGet getobj = new HBaseConcurrentGet();

        for (int i = 0; i < tsize; i++) {
            try {
                HBaseConcurrentGet.GetHBaseThread gett = getobj.new GetHBaseThread(
                        udid, token, tablenames[i], doneSignal, versionFlag);
                Thread p = new Thread(gett);
                threads.add(p);
                p.start();
            } catch (Exception e) {
            }
        }

        long tmpstarttime = System.nanoTime();
        try {
            doneSignal.await(timeoutparam, TimeUnit.MILLISECONDS);
            return getobj.getResult();
        } catch (Exception e) {
            return null;
        } finally {
            long restime = System.nanoTime() - tmpstarttime;
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");
            if (restime >= timeoutparam * 1000000) {

                logger.debug("-1" + (char) 1 + new Integer(token).toString()
                        + (char) 1 + udid + (char) 1 + sdf.format(new Date())
                        + (char) 1 + restime + (char) 1 + "1" + (char) 1
                        + TokenCache.getTableName(tokenorder));
            } else {
                if (!getobj.getResult().isEmpty())
                    logger.debug("100" + (char) 1
                            + new Integer(token).toString() + (char) 1 + udid
                            + (char) 1 + sdf.format(new Date()) + (char) 1
                            + restime + (char) 1 + "0" + (char) 1
                            + TokenCache.getTableName(tokenorder));
                else
                    logger.debug("100" + (char) 1
                            + new Integer(token).toString() + (char) 1 + udid
                            + (char) 1 + sdf.format(new Date()) + (char) 1
                            + restime + (char) 1 + "1" + (char) 1
                            + TokenCache.getTableName(tokenorder));
            }
            getobj.setResult(null);
            getobj.destroy();
        }
    }

    public static Result getOneRecordInTableSerial(String udid, int tokenorder,
            String tablename, boolean versionFlag) // timeout parameter in
                                                   // milliseconds
    {
        if (tablename == null || tablename.isEmpty())
            return null;
        int token = TokenCache.getToken(tokenorder);
        HTable htable = null;
        try {
            long starttime = System.nanoTime();
            ExecutorService tableExecutor = Executors.newSingleThreadExecutor();
            htable = new HTable(Bytes.toBytes(tablename), HBaseConn.conn,
                    tableExecutor);
            Result res = HBaseConn.getOneRecordInTable(udid, htable, token,
                    versionFlag);
            long restime = System.nanoTime() - starttime;
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");
            if (restime <= 1000000 * 100 && res != null) {

                if (res.isEmpty())
                    logger.debug("0" + (char) 1 + new Integer(token).toString()
                            + (char) 1 + udid + (char) 1
                            + sdf.format(new Date()) + (char) 1 + restime
                            + (char) 1 + "1" + (char) 1 + "serialtest");
                else
                    logger.debug("0" + (char) 1 + new Integer(token).toString()
                            + (char) 1 + udid + (char) 1
                            + sdf.format(new Date()) + (char) 1 + restime
                            + (char) 1 + "0" + (char) 1 + "serialtest");
            } else {
                logger.debug("-1" + (char) 1 + new Integer(token).toString()
                        + (char) 1 + udid + (char) 1 + sdf.format(new Date())
                        + (char) 1 + restime + (char) 1 + "2" + (char) 1
                        + "serialtest");
            }
            return res;
        } catch (Exception e) {
            return null;
        } finally {
            if (htable != null) {
                try {
                    htable.close();
                } catch (Exception e) {
                }
                htable = null;
            }
        }
    }

    public static Result getOneRecordInTableWithTimeoutTest(String udid,
            int token, String[] tablenames, long timeoutparam,
            boolean versionFlag) // timeout
    // parameter in
    // milliseconds
    {
        if (tablenames == null || tablenames.length < 1)
            return null;
        int tsize = tablenames.length;
        long starttime = System.nanoTime();
        CountDownLatch doneSignal = new CountDownLatch(1);

        List<Thread> threads = new ArrayList<Thread>();
        HBaseConcurrentGet getobj = new HBaseConcurrentGet();

        for (int i = 0; i < tsize; i++) {
            try {
                HBaseConcurrentGet.GetHBaseThread gett = getobj.new GetHBaseThread(
                        udid, token, tablenames[i], doneSignal, versionFlag);
                Thread p = new Thread(gett);
                threads.add(p);
                p.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            doneSignal.await(timeoutparam, TimeUnit.MILLISECONDS);
            long restime = System.nanoTime() - starttime;
            if (restime >= timeoutparam * 1000000) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");
                System.out.println("-1" + (char) 1
                        + new Integer(token).toString() + (char) 1 + udid
                        + (char) 1 + sdf.format(new Date()) + (char) 1
                        + restime + (char) 1 + "1" + (char) 1 + "test");
            }
            return getobj.getResult(starttime);
        } catch (Exception e) {
            return null;
        } finally {
            getobj.setResult(null);
            getobj.destroy();
        }

    }*/

    public static List<String> getHBaseStringToArray(String val, int hist_thres) {
        ArrayList<String> histids = new ArrayList<String>();
        String[] hists = val.split(",");
        for (int i = 0; i < hists.length; i++) {
            String[] details = hists[i].split("Z\\^");
            if (details.length == 2) {
                histids.add(details[1]);
            } else if (details.length == 1)
                histids.add(details[0]);
        }
        return histids.subList(0, Math.min(hist_thres, histids.size()));
    }

    public static String getHBaseResultToString(Result res, String colfamily, String colqualifier) {
        if (res == null || res.isEmpty())
            return "";
        List<String> histids = new ArrayList<String>();
        String val = null;
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        
        final byte[] colf = colfamily.getBytes();
        final byte[] colq = colqualifier.getBytes();
        
        KeyValue kv = res.getColumnLatest(colf, colq); 
        b = new ByteArrayInputStream(kv.getValue());
        try {
            o = new ObjectInputStream(b);
            val = o.readObject().toString();
        } catch (Exception e) {
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (val == null || val.equals("null")) {
            return "";
        }
        String[] hists = val.split(",");
        for (int i = 0; i < hists.length; i++) {
            String[] details = hists[i].split("Z\\^");
            if (details.length == 2) {
                histids.add(details[1]);
            } else if (details.length == 1)
                histids.add(details[0]);
        }
        
        val = "";
        for (int i = 0; i < histids.size() - 1; i++) {
            val += histids.get(i) + ",";
        }
        return val + histids.get(histids.size() - 1);
    }

    public static List<String> getHBaseResultToArray(Result res,  String colfamily, String colqualifier, int hist_thres) {
    	if (res == null || res.isEmpty())
            return null;
        List<String> histids = new ArrayList<String>();
        String val = null;
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        
        final byte[] colf = colfamily.getBytes();
        final byte[] colq = colqualifier.getBytes();
        
        KeyValue kv = res.getColumnLatest(colf, colq);
        b = new ByteArrayInputStream(kv.getValue());
        try {
            o = new ObjectInputStream(b);
            val = o.readObject().toString();
        } catch (Exception e) {
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (val == null || val.equals("null")) {
            return null;
        }
        String[] hists = val.split(",");
        for (int i = 0; i < hists.length; i++) {
            String[] details = hists[i].split("Z\\^");
            if (details.length == 2) {
                histids.add(details[1]);
            } else if (details.length == 1)
                histids.add(details[0]);
        }
        
        return histids.subList(0, Math.min(hist_thres, histids.size()));
    }
    
    public static ArrayList<List<String>> getHBasePairResultToArray(Result res, String colfamily, String colqualifier, int minreq) {
       
    	if(res == null || res.isEmpty())
    	{
    		return null;
    	}
    	
    	List<String> histids = new ArrayList<String>();
        List<String> histfreq = new ArrayList<String>();
        String val = null;
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        
        final byte[] colf = colfamily.getBytes();
        final byte[] colq = colqualifier.getBytes();
        
        KeyValue kv = res.getColumnLatest(colf, colq);
        
        b = new ByteArrayInputStream(kv.getValue());
        try {
            o = new ObjectInputStream(b);
            val = o.readObject().toString();
        } catch (Exception e) {
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (val == null || val.equals("null")) {
            return null;
        }
        //System.out.println("original unf record = "+val);
        String[] hists = val.split("#");
        for (int i = 1; i < hists.length; i++) {
            String[] details = hists[i].split(",");
            if (details.length == 2) {
            	if(Integer.parseInt(details[1]) >= minreq)
            	{
            		histids.add(details[0]);
            		histfreq.add(details[1]);
            	}
            } else 
            	continue;
        }
        
        //if(!histids.isEmpty())
        //	System.out.println("unf HBase result is size "+ new Integer(histids.size()).toString());
        
        ArrayList<List<String>> reslist = new ArrayList<List<String>>();
        
        reslist.add(histids);
        reslist.add(histfreq);
        
        return reslist;
    }

    public static List<String> getShortTermResultToArray(Result res,
            boolean versionFlag) {
        List<String> feedbacks = new ArrayList<String>();
        String val = null;
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        final byte[] CF = "u".getBytes();
        final byte[] QL = "v".getBytes();

        for (KeyValue kv : res.getColumn(CF, QL)) {
            b = new ByteArrayInputStream(kv.getValue());
            try {
                o = new ObjectInputStream(b);
                val = o.readObject().toString();
            } catch (Exception e) {
            } finally {
                if (o != null) {
                    try {
                        o.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (b != null) {
                    try {
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (val == null || val.equals("null")) {
                continue;
            }
            String[] token = val.split("#");
            for (int i = 0; i < token.length; i++) {
                feedbacks.add(token[i]);
            }
        }

       return feedbacks;
   }

    public static List<String> getLongTermResultToArray(Result res,
           boolean versionFlag) {
        List<String> feedbacks = new ArrayList<String>();
        String val = null;
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        final byte[] CF = "v".getBytes();
        final byte[] QL = "l".getBytes();

        for (KeyValue kv : res.getColumn(CF, QL)) {
            b = new ByteArrayInputStream(kv.getValue());
            try {
                o = new ObjectInputStream(b);
                val = o.readObject().toString();
            } catch (Exception e) {
            } finally {
                if (o != null) {
                    try {
                        o.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (b != null) {
                    try {
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (val == null || val.equals("null")) {
                continue;
            }
            String[] token = val.split("#");
            for (int i = 0; i < token.length; i++) {
                feedbacks.add(token[i]);
            }
        }

        return feedbacks;
    }

  
    /*public static void main(String[] args) {
        try {
            init();

            // no unittest available for getOneRecordInTableWithTimeout now
            // because of the dependency

            /*
             * HTableInterface table1 =
             * HBaseConn.getHPool().getTable("conversion_history_1month");
             * //("conversion_history"); // HTableInterface table2 =
             * HBaseConn.getHPool().getTable("conversion_history_1week_I");
             * HTableInterface table3 =
             * HBaseConn.getHPool().getTable("conversion_history_1week_II");
             */

            // String[] tablenames = { "conversion_history_1month_I",
            // "conversion_history_1month_II" };
        /*    String[] tablenames = { "unf_I", "unf_II" };

            int token = Integer.parseInt(args[2]);

            int testnum = Integer.parseInt(args[0]);
            String udid = args[1]; // "001436292878"; //"000d00004d28";

            double t1 = 0.0;

            long empty_count = 0, excesscount = 0, tocount = 0;
     

            for (int i = 0; i < testnum; i++) {
                // localconn = HConnectionManager.createConnection(conf);
                // HTable table1 = new
                // HTable(Bytes.toBytes("conversion_history_1month"), localconn,
                // exec);
                long starttime = System.nanoTime();
                Result res1 = getOneRecordInTableWithTimeoutTest(udid, token,
                        tablenames, Long.parseLong(args[3]), true); // HBaseConn.getOneRecordInTable2(udid,
                // table1, token);
                long endtime = System.nanoTime();
                t1 += (double) (endtime - starttime);

                int tmpcount = 0;
                if (res1 == null) {
                    // System.out.println("return is null! something is wrong here!!!");
                    tocount++;
                    continue;
                }
            
                for (KeyValue kv : res1.raw()) {
                    ByteArrayInputStream b = new ByteArrayInputStream(
                            kv.getValue());
                    ObjectInputStream o = new ObjectInputStream(b);
                    System.out.println("result: " + o.readObject().toString());
                    tmpcount++;
                }

                if (tmpcount > 1)
                    excesscount++;
                else if (tmpcount == 0) {
                    // System.out.println("Empty result returned!");
                    empty_count++;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {

                }
                // table1.close();
                // localconn.close();
            }
            // table2.close();
            // table3.close();
            System.out.println("time stats: avgtime=" + t1 / testnum
                    + " empty result count=" + empty_count + " timeout count="
                    + tocount + " excessive result count=" + excesscount);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
        return;
    }*/
}
