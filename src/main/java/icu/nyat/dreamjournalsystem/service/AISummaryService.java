package icu.nyat.dreamjournalsystem.service;

import icu.nyat.dreamjournalsystem.entity.AISummary;
import icu.nyat.dreamjournalsystem.entity.Dream;

/**
 * AI总结服务接口
 */
public interface AISummaryService {

    /**
     * 异步生成AI总结
     */
    void generateSummaryAsync(Dream dream);

    /**
     * 仅预先将AI总结重置为PENDING并清空旧结果，供重新生成时前端立即看到最新状态。
     */

    /**
     * 同步生成AI总结
     */
    AISummary generateSummary(Dream dream);

    /**
     * 获取梦境的AI总结
     */
    AISummary getSummaryByDreamId(Long dreamId);
}
