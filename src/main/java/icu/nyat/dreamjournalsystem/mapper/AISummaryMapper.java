package icu.nyat.dreamjournalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.nyat.dreamjournalsystem.entity.AISummary;
import org.apache.ibatis.annotations.*;

/**
 * AI总结 Mapper
 */
@Mapper
public interface AISummaryMapper extends BaseMapper<AISummary> {

    /**
     * 根据梦境ID查找AI总结（使用ResultMap以正确处理JSON字段）
     */
    @Select("SELECT * FROM ai_summaries WHERE dream_id = #{dreamId}")
    @Results(id = "aiSummaryResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "dream_id", property = "dreamId"),
            @Result(column = "summary", property = "summary"),
            @Result(column = "keywords", property = "keywords", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class),
            @Result(column = "emotion_analysis", property = "emotionAnalysis", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class),
            @Result(column = "symbol_analysis", property = "symbolAnalysis", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class),
            @Result(column = "psychological_insight", property = "psychologicalInsight"),
            @Result(column = "advice", property = "advice"),
            @Result(column = "ai_model", property = "aiModel"),
            @Result(column = "prompt_version", property = "promptVersion"),
            @Result(column = "confidence_score", property = "confidenceScore"),
            @Result(column = "tokens_used", property = "tokensUsed"),
            @Result(column = "generation_time_ms", property = "generationTimeMs"),
            @Result(column = "status", property = "status"),
            @Result(column = "error_message", property = "errorMessage"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    AISummary findByDreamId(@Param("dreamId") Long dreamId);

    /**
     * 检查梦境是否已有AI总结
     */
    @Select("SELECT COUNT(*) FROM ai_summaries WHERE dream_id = #{dreamId}")
    int existsByDreamId(@Param("dreamId") Long dreamId);

    /**
     * 完整更新AI总结（包含所有字段，包括null值）
     */
    @Update("UPDATE ai_summaries SET " +
            "summary = #{summary}, " +
            "keywords = #{keywords, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, " +
            "emotion_analysis = #{emotionAnalysis, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, " +
            "symbol_analysis = #{symbolAnalysis, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, " +
            "psychological_insight = #{psychologicalInsight}, " +
            "advice = #{advice}, " +
            "ai_model = #{aiModel}, " +
            "prompt_version = #{promptVersion}, " +
            "confidence_score = #{confidenceScore}, " +
            "tokens_used = #{tokensUsed}, " +
            "generation_time_ms = #{generationTimeMs}, " +
            "status = #{status}, " +
            "error_message = #{errorMessage}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateAllFields(AISummary summary);

    /**
     * 使用悲观锁查询AI总结记录（用于并发控制）
     */
    @Select("SELECT * FROM ai_summaries WHERE dream_id = #{dreamId} FOR UPDATE")
    @ResultMap("aiSummaryResultMap")
    AISummary findByDreamIdForUpdate(@Param("dreamId") Long dreamId);
}
