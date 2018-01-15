package com.gofirst.scenecollection.evidence.sync;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.model.UploadFile;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author maxiran
 *         产生每个文件分块
 */
public class CreateFileBlocks {

    /**
     * 开始分割文件
     */
    public void startCreateBlocks(UploadFile recordFileInfo, int singleSize,boolean spec,String caseId) {
        if (!checkFileHasBlocks(recordFileInfo)) {
            createBlockBySize(recordFileInfo, singleSize,spec,caseId);
        }
    }


    /**
     * 第一步对文件分割
     *
     * @param recFile    需要分割的文件
     * @param singleSize 分割后单个文件大小
     */
    private void createBlockBySize(UploadFile recFile, int singleSize,boolean spec,String caseId) {
        File file = new File(recFile.getFilePath());
        int count = (int) Math.ceil(file.length() / (double) singleSize);
        int coreSize = count/4;
        UpLoadService.writerInfo(recFile.getFilePath() + "threadPool CoreSize is " + (coreSize != 0 ? coreSize : 1));
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(coreSize != 0 ? coreSize : 1,
                count != 0 ? count  : 4, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(count != 0 ? count : 2));
        for (int i = 0; i < count; i++) {
            String partPath = recFile.getFilePath() + ".part" + (i + 1);
            new CreateSingleBlockTask(partPath, singleSize, i).
                    executeOnExecutor(threadPool, recFile);
            saveUnUpLoadBlock(recFile.getFilePath(), partPath, i, count,spec,caseId);
        }
        recFile.setUpload(true);
        EvidenceApplication.db.save(recFile);
        UpLoadService.writerInfo(recFile.getFilePath() + " create block success");
        RecordFileInfo parentRec = EvidenceApplication.db.findById(recFile.getParentId(),RecordFileInfo.class);
        if (parentRec != null){
            parentRec.setHasBlock(true);
            EvidenceApplication.db.update(parentRec);
        }
    }

    private boolean checkFileHasBlocks(UploadFile recordFileInfo) {
        List<UnUpLoadBlock> list = EvidenceApplication.db.findAllByWhere(UnUpLoadBlock.class,
                "parentPath = '" + recordFileInfo.getFilePath() + "'");
        return list != null && list.size() != 0;
    }

    /**
     * 保存未上传的分块到表里
     *
     * @param blockPath  分块路径
     * @param parentPath 母文件路径
     * @param index      分块编号
     * @param total      分块总数
     */
    private void saveUnUpLoadBlock(String parentPath, String blockPath, int index, int total,boolean spec,String caseId) {
        UnUpLoadBlock unUpLoadBlock = new UnUpLoadBlock();
        unUpLoadBlock.setId(ViewUtil.getUUid());
        unUpLoadBlock.setParentPath(parentPath);
        unUpLoadBlock.setPath(blockPath);
        unUpLoadBlock.setBlockIndex(index);
        unUpLoadBlock.setBlockTotal(total);
        unUpLoadBlock.setSpec(spec);
        unUpLoadBlock.setCaseId(caseId);
        EvidenceApplication.db.save(unUpLoadBlock);
    }
}
