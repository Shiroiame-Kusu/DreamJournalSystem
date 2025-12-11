package icu.nyat.dreamjournalsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI总结实体类
 */
@Data
@TableName(value = "ai_summaries", autoResultMap = true)
public class AISummary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dreamId;

    private String summary;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Keywords keywords;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private EmotionAnalysis emotionAnalysis;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private SymbolAnalysis symbolAnalysis;

    private String psychologicalInsight;

    private String advice;

    private String aiModel;

    private String promptVersion;

    private BigDecimal confidenceScore;

    private Integer tokensUsed;

    private Integer generationTimeMs;

    private SummaryStatus status;

    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * AI总结状态枚举
     */
    public enum SummaryStatus {
        PENDING, COMPLETED, FAILED
    }

    /**
     * 关键词数据结构
     */
    @Data
    public static class Keywords {
        private java.util.List<String> primary;
        private java.util.List<String> secondary;
        private java.util.List<String> emotions;
    }

    /**
     * 情绪分析数据结构
     */
    @Data
    public static class EmotionAnalysis {
        private String dominantEmotion;
        private EmotionSpectrum emotionSpectrum;
        private Double intensity;
        private java.util.List<EmotionScore> emotionsDetected;

        @Data
        public static class EmotionSpectrum {
            private Double positive;
            private Double negative;
            private Double neutral;
        }

        @Data
        public static class EmotionScore {
            private String emotion;
            private Double score;
        }
    }

    /**
     * 象征分析数据结构
     */
    @Data
    public static class SymbolAnalysis {
        private java.util.List<Symbol> symbols;
        private String overallTheme;
        private String lifeConnection;

        @Data
        public static class Symbol {
            private String symbol;
            private String meaning;
            private String psychologicalInterpretation;
        }
    }
}
