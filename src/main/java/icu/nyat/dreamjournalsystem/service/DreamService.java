package icu.nyat.dreamjournalsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import icu.nyat.dreamjournalsystem.dto.request.DreamRequest;
import icu.nyat.dreamjournalsystem.entity.Dream;

import java.time.LocalDate;

/**
 * 梦境服务接口
 */
public interface DreamService {

    /**
     * 创建梦境记录
     */
    Dream createDream(Long userId, DreamRequest request);

    /**
     * 获取梦境详情
     */
    Dream getDreamById(Long dreamId, Long userId);

    /**
     * 获取梦境列表（分页）
     */
    IPage<Dream> getDreamsByUserId(Long userId, int page, int size, 
                                   String keyword, LocalDate startDate, LocalDate endDate,
                                   Dream.DreamType dreamType, Boolean isFavorite);

    /**
     * 更新梦境记录
     */
    Dream updateDream(Long dreamId, Long userId, DreamRequest request);

    /**
     * 删除梦境记录
     */
    void deleteDream(Long dreamId, Long userId);

    /**
     * 切换收藏状态
     */
    boolean toggleFavorite(Long dreamId, Long userId);

    /**
     * 获取用户收藏的梦境列表
     */
    java.util.List<Dream> getFavoritesByUserId(Long userId);

    /**
     * 重新生成AI总结
     */
    void regenerateAISummary(Long dreamId, Long userId);
}
