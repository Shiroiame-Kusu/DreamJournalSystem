package icu.nyat.dreamjournalsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 梦境实体类
 */
@Data
@TableName(value = "dreams", autoResultMap = true)
public class Dream {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private LocalDate dreamDate;

    private LocalTime sleepStartTime;

    private LocalTime sleepEndTime;

    private SleepQuality sleepQuality;

    private Mood moodBeforeSleep;

    private Mood moodAfterWake;

    private DreamType dreamType;

    private Integer vividness;

    private Boolean isFavorite;

    private Boolean isPrivate;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 睡眠质量枚举
     */
    public enum SleepQuality {
        EXCELLENT, GOOD, FAIR, POOR, TERRIBLE
    }

    /**
     * 情绪枚举
     */
    public enum Mood {
        HAPPY, CALM, ANXIOUS, SAD, STRESSED, EXCITED, TIRED, CONFUSED, REFRESHED
    }

    /**
     * 梦境类型枚举
     */
    public enum DreamType {
        NORMAL, LUCID, NIGHTMARE, RECURRING, PROPHETIC
    }
}
