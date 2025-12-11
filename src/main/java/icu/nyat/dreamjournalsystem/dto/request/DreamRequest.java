package icu.nyat.dreamjournalsystem.dto.request;

import icu.nyat.dreamjournalsystem.entity.Dream;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 创建/更新梦境请求DTO
 */
@Data
public class DreamRequest {

    @NotBlank(message = "梦境标题不能为空")
    @Size(max = 200, message = "梦境标题不能超过200个字符")
    private String title;

    @NotBlank(message = "梦境内容不能为空")
    @Size(min = 10, message = "梦境内容至少需要10个字符")
    private String content;

    @NotNull(message = "做梦日期不能为空")
    private LocalDate dreamDate;

    private LocalTime sleepStartTime;

    private LocalTime sleepEndTime;

    private Dream.SleepQuality sleepQuality;

    private Dream.Mood moodBeforeSleep;

    private Dream.Mood moodAfterWake;

    private Dream.DreamType dreamType = Dream.DreamType.NORMAL;

    @Min(value = 1, message = "清晰度最小为1")
    @Max(value = 10, message = "清晰度最大为10")
    private Integer vividness;

    private Boolean isFavorite = false;

    private Boolean isPrivate = true;

    private List<String> tags;

    private Boolean generateAISummary = true;
}
