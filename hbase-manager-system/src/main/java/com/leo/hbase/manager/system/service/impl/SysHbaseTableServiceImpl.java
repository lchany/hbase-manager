package com.leo.hbase.manager.system.service.impl;

import com.leo.hbase.manager.common.core.text.Convert;
import com.leo.hbase.manager.common.utils.DateUtils;
import com.leo.hbase.manager.common.utils.StringUtils;
import com.leo.hbase.manager.system.domain.SysHbaseTable;
import com.leo.hbase.manager.system.domain.SysHbaseTableTag;
import com.leo.hbase.manager.system.mapper.SysHbaseTableMapper;
import com.leo.hbase.manager.system.mapper.SysHbaseTableTagMapper;
import com.leo.hbase.manager.system.service.ISysHbaseTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * HBaseService业务层处理
 *
 * @author leojie
 * @date 2020-08-16
 */
@Service
public class SysHbaseTableServiceImpl implements ISysHbaseTableService {
    @Autowired
    private SysHbaseTableMapper sysHbaseTableMapper;

    @Autowired
    private SysHbaseTableTagMapper sysHbaseTableTagMapper;

    /**
     * 查询HBase
     *
     * @param tableId HBaseID
     * @return HBase
     */
    @Override
    public SysHbaseTable selectSysHbaseTableById(Long tableId) {
        return sysHbaseTableMapper.selectSysHbaseTableById(tableId);
    }

    @Override
    public SysHbaseTable selectSysHbaseTableByName(String tableName) {
        return sysHbaseTableMapper.selectSysHbaseTableByName(tableName);
    }


    /**
     * 查询HBase列表
     *
     * @param sysHbaseTable HBase
     * @return HBase
     */
    @Override
    public List<SysHbaseTable> selectSysHbaseTableList(SysHbaseTable sysHbaseTable) {
        String[] tagIds = Convert.toStrArray(sysHbaseTable.getQueryHBaseTagIdStr());
        if (tagIds.length == 0) {
            sysHbaseTable.setQueryHBaseTagIds(null);
        } else {
            sysHbaseTable.setQueryHBaseTagIds(tagIds);
        }
        return sysHbaseTableMapper.selectSysHbaseTableList(sysHbaseTable);
    }


    /**
     * 新增HBase表
     *
     * @param sysHbaseTable HBase
     * @return 结果
     */
    @Override
    public int insertSysHbaseTable(SysHbaseTable sysHbaseTable) {
        sysHbaseTable.setCreateTime(DateUtils.getNowDate());
        int rows = sysHbaseTableMapper.insertSysHbaseTable(sysHbaseTable);
        //新增HBase表和tag对应关系
        insertTableTag(sysHbaseTable);
        return rows;
    }

    /**
     * 新增表的标签信息
     *
     * @param sysHbaseTable HBase
     */
    public void insertTableTag(SysHbaseTable sysHbaseTable) {
        Long[] tagIds = sysHbaseTable.getTagIds();
        if (StringUtils.isNotNull(tagIds)) {
            List<SysHbaseTableTag> list = new ArrayList<>(tagIds.length);

            for (Long tagId : tagIds) {
                SysHbaseTableTag ht = new SysHbaseTableTag();
                ht.setTableId(sysHbaseTable.getTableId());
                ht.setTagId(tagId);
                list.add(ht);
            }
            if (list.size() > 0) {
                sysHbaseTableTagMapper.batchInsertSysHbaseTableTag(list);
            }
        }

    }

    /**
     * 修改HBase
     *
     * @param sysHbaseTable HBase
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysHbaseTable(SysHbaseTable sysHbaseTable) {
        Long updateTableId = sysHbaseTable.getTableId();

        sysHbaseTableTagMapper.deleteSysHbaseTableTagById(updateTableId);
        insertTableTag(sysHbaseTable);
        sysHbaseTable.setUpdateTime(DateUtils.getNowDate());
        return sysHbaseTableMapper.updateSysHbaseTable(sysHbaseTable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysHbaseTableStatus(SysHbaseTable sysHbaseTable) {
        sysHbaseTable.setUpdateTime(DateUtils.getNowDate());
        return sysHbaseTableMapper.updateSysHbaseTableStatus(sysHbaseTable);
    }

    @Override
    public int updateSysHbaseTableDisabledStatus(SysHbaseTable sysHbaseTable) {
        sysHbaseTable.setUpdateTime(DateUtils.getNowDate());
        return sysHbaseTableMapper.updateSysHbaseTableDisabledStatus(sysHbaseTable);
    }

    /**
     * 删除HBase对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSysHbaseTableByIds(String ids) {
        return sysHbaseTableMapper.deleteSysHbaseTableByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除HBase信息
     *
     * @param tableId HBaseID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysHbaseTableById(Long tableId) {
        //删除HBase表后删除tag
        int row = sysHbaseTableMapper.deleteSysHbaseTableById(tableId);
        if (row > 0) {
            sysHbaseTableTagMapper.deleteSysHbaseTableTagById(tableId);
            return 1;
        } else {
            return 0;
        }
    }
}
