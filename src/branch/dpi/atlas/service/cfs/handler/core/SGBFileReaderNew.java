package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.calendar.Calendar;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.SGBBatch;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SGBFileReaderNew extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SGBFileReaderNew.class);
    private String filePath;
    //    private int sgbFileRecordLenght = 81;
    private int sgbFileRecordLenght = 99;
    private SGBBatch activeSGBBatch;
    private long log_id;
    private long commit_size = 1000;
    private String fileHeader = "CFSAPPLIER";
//    private CFSFacade cfsFacade;

    private void putRecordNewWithPS(String sgbRecord, String fileDate, PreparedStatement sgb_log_ps) {
        try {
            String cardno = sgbRecord.substring(82, 98);
            if (cardno.startsWith(Constants.BANKE_TEJARAT_BIN))
                cardno = cardno + "000";

            CFSFacadeNew.putSGBLogWithPS(sgbRecord.substring(0, 3),
                    sgbRecord.substring(3, 6),
                    sgbRecord.substring(6, 17),
                    sgbRecord.substring(17, 18),
                    sgbRecord.substring(18, 33),
                    sgbRecord.substring(33, 41),
                    sgbRecord.substring(41, 44),
                    sgbRecord.substring(44, 51),
                    sgbRecord.substring(51, 56),
                    sgbRecord.substring(56, 61),
                    sgbRecord.substring(61, 66),
                    sgbRecord.substring(66, 72),
                    sgbRecord.substring(72, 82),
                    cardno,
                    DateUtil.getSystemDate(),
                    DateUtil.getSystemTime(),
                    ++log_id,
                    fileDate,
                    CFSConstants.SGB_LOG_PROCESS_STATUS_NOT_PROCESSED, // Process Status: 0:Not Proccessed - 1:Proccessed
                    activeSGBBatch.getBatchPk().longValue(), sgb_log_ps);
        } catch (SQLException e) {
            log.error("Could not insert line: " + sgbRecord + " :: ERROR >> " + e.getMessage());
        }
    }

    public synchronized void doProcess(CMMessage msg, Map holder) throws CFSFault {
        System.out.println(" *********** SGBFileReaderNew :: doProcess ***********");

//        String commitSizeStr = msg.getAttributeAsString(CFSConstants.APPLIER_COMMIT_SIZE);
//        if (commitSizeStr != null && !commitSizeStr.equals(""))
//            commit_size = Long.parseLong(commitSizeStr);

        File sgbFile = new File(filePath);
        BufferedInputStream reader = null;
        Connection connection = null;
        PreparedStatement sgb_log_ps = null;
        try {
            connection = CFSFacadeNew.getDbConnectionPool().getConnection();
            connection.setAutoCommit(false);
            String sql = "insert into TB_SGB_Log (ACCOUNT_GROUP,PARTNO,BANK_CODE,ACCOUNT_NO,DEBIT_CREDIT,AMOUNT,EFFECTIVE_DATE," +
                    "OPERATION_CODE,DOCUMENT_NO,BRANCH_CODE,JOURNAL_NO,ACCOUNT_BRANCH_COD,TIME,TERMINAL_ACCOUNT,CREATION_DATE," +
                    "CREATION_TIME,LOG_ID,SGB_FILEDATE,PROCCESS_STATUS,SGB_BATCH_PK,CARD_NO) " +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            sgb_log_ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        activeSGBBatch = null;
        if (log.isDebugEnabled())
            log.debug("Starting reading SGB file " + filePath);
        long rowInserted_count = 0;
        long countBes=0;
        long countBed=0;
        long amountBes=0;
        long amountBed=0;
        try {
            if (sgbFile != null && sgbFile.isFile()) {
//                reader = new DataInputStream(new FileInputStream(sgbFile));

//                String line = reader.readLine();
                reader = new BufferedInputStream(new FileInputStream(sgbFile));
                StringBuffer sb = new StringBuffer();
                int chi = reader.read();
                char ch = (char) chi;
                String fileDate = null;
                while (chi != -1) {
                    if (log.isInfoEnabled())
                        log.info("Start to read new line");
                    while (ch != '\n') {
                        sb.append(ch);
                        chi = reader.read();
                        ch = (char) chi;
                    }
                    if (log.isInfoEnabled())
                        log.info("End of read new line");
                    String line = sb.toString();

                    if (log.isInfoEnabled())
                        log.info("SGB file record read successfully: " + line);
                    char endCharacter = sb.charAt(sb.length() - 1);
                    if (log.isInfoEnabled())
                        log.info("SGB file processing char is : '" + endCharacter + "'");
                    if (endCharacter == '0') {// This is the first record, extract date from it
                        if (line.startsWith("9999999999"))//This is a end row, so just break
                            break;
                        if (!line.substring(0, 10).equals(fileHeader))
                            throw new Exception("SGB file is wrong , Please check the format of input file!!!!!");

                        fileDate = line.substring(10, 20);
                        if (CFSFacadeNew.findSGBBatchForDate(fileDate))
                            throw new Exception("SGB file for date " + fileDate + " already processed and not reversed.");
                        if (log.isDebugEnabled())
                            log.debug("fileDate = " + fileDate);

//                        CFSFacadeNew.clearSGBLogForDate(connection, fileDate);

                        try {
                            activeSGBBatch = CFSFacadeNew.getSGBBatch(String.valueOf(CFSConstants.ACTIVE_BATCH));
                        } catch (NotFoundException e) {  // No active SGB batch exists, so create a new one
                            long max_batch_pk = CFSFacadeNew.getSGBMaxBatch_PK();
                            Calendar cal = Calendar.getInstance();
                            activeSGBBatch = new SGBBatch(cal.getTime(), String.valueOf(CFSConstants.ACTIVE_BATCH), null, null, null);
                            activeSGBBatch.setBatchPk(new Long(++max_batch_pk));
                            CFSFacadeNew.insertSGBBatch(activeSGBBatch);
                        }
                        if (log.isDebugEnabled())
                            log.debug("ACTIVE SGB BATCH : " + activeSGBBatch);
                        log_id = CFSFacadeNew.getSGBLogMaxLogId();
                    } else {
                        if (log.isInfoEnabled())
                            log.info("Inside putting phase...");
                        if (endCharacter == '1') {
                            if (line.length() == sgbFileRecordLenght) {
                                if (log.isDebugEnabled())
                                    log.debug("Start to put record to tb_sgb_log");
                                putRecordNewWithPS(line, fileDate, sgb_log_ps);
                                if (log.isDebugEnabled())
                                    log.debug("End to put record to tb_sgb_log");
                                if(line.substring(17, 18).equals("1")){
                                    countBes++;
                                    amountBes=amountBes+Long.valueOf(line.substring(18, 33));
                                }else{
                                    countBed++;
                                    amountBed=amountBed+Long.valueOf(line.substring(18, 33));
                                }
                                rowInserted_count++;
                                if (rowInserted_count % commit_size == 0)
                                    connection.commit();
                            } else {
                                throw new Exception("SGB file " + filePath + " record corrupt: " + line);
                            }
                        }else if (endCharacter == '9') {
                            if(amountBes!= Long.valueOf(line.substring(14,29))){
                                System.out.println("Total amount of BES is not correct");
                                log.error("Total amount of BES is not correct");
                            }
                            if(amountBed!= Long.valueOf(line.substring(30,45))){
                                System.out.println("Total amount of BED is not correct");
                                log.error("Total amount of BED is not correct");
                            }
                            if(countBes!= Long.valueOf(line.substring(46,52))){
                                System.out.println(" Transaction count of BES is not correct");
                                log.error("Transaction count of BES is not correct");
                            }
                            if(countBed!= Long.valueOf(line.substring(53,59))){
                                System.out.println(" Transaction count of BED is not correct");
                                log.error("Transaction count of BED is not correct");
                            }
                            if(rowInserted_count!= Long.valueOf(line.substring(60,66))){
                                System.out.println(" Total transaction count is not correct");
                                log.error("Total transaction count is not correct");
                            }
                        }
                        if (log.isInfoEnabled())
                            log.info("End of insertion if and start to go to start of while...");
                    }
                    chi = reader.read();
                    ch = (char) chi;
                    sb = new StringBuffer();
                }
                connection.commit();
                if (activeSGBBatch != null) {
                    activeSGBBatch.setSgbFileDate(fileDate);
                    CFSFacadeNew.updateSGBBatch(activeSGBBatch);
                    if (log.isDebugEnabled()) log.debug("SGB file " + filePath + " reading completed successfully.");
                }
                if (fileDate == null) {
                    log.error("SGB File Corrupt");
                    throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception("SGB File Corrupt"));
                }
            } else
                throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            log.error("SGB File " + filePath + " does not exist");
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        } catch (IOException e) {
            log.error("Can not read from SGB file: " + filePath);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        } finally {
            try {
                if (connection != null) connection.commit();
                if (sgb_log_ps != null) sgb_log_ps.close();
                CFSFacadeNew.getDbConnectionPool().returnConnection(connection);
            } catch (SQLException e1) {
                log.error(e1);
            }
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                log.error("Error in closing reader for SGB file: " + filePath);
                throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
            }
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        filePath = cfg.get(CFSConstants.FILE_PATH);
    }


    public static void main(String[] args) {
        SGBFileReaderNew sgbFileReaderNew = new SGBFileReaderNew();
        try {
            sgbFileReaderNew.doProcess(new CMMessage(), new HashMap());
        } catch (CFSFault cfsFault) {
            cfsFault.toString();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
