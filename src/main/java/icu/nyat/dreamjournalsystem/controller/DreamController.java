package icu.nyat.dreamjournalsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import icu.nyat.dreamjournalsystem.dto.request.DreamRequest;
import icu.nyat.dreamjournalsystem.dto.response.ApiResponse;
import icu.nyat.dreamjournalsystem.entity.AISummary;
import icu.nyat.dreamjournalsystem.entity.Dream;
import icu.nyat.dreamjournalsystem.entity.User;
import icu.nyat.dreamjournalsystem.service.AISummaryService;
import icu.nyat.dreamjournalsystem.service.DreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 梦境控制器
 */
@RestController
@RequestMapping("/dreams")
@RequiredArgsConstructor
public class DreamController {

    private final DreamService dreamService;
    private final AISummaryService aiSummaryService;
    private final icu.nyat.dreamjournalsystem.mapper.UserMapper userMapper;

    /**
     * 创建梦境记录
     */
    @PostMapping
    public ApiResponse<Dream> createDream(@Valid @RequestBody DreamRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Dream dream = dreamService.createDream(userId, request);
        return ApiResponse.created(dream);
    }

    /**
     * 获取梦境列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getDreams(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "dreamType", required = false) Dream.DreamType dreamType,
            @RequestParam(value = "isFavorite", required = false) Boolean isFavorite,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserId(userDetails);
        IPage<Dream> dreamPage = dreamService.getDreamsByUserId(userId, page, size, 
                keyword, startDate, endDate, dreamType, isFavorite);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", dreamPage.getRecords());
        response.put("totalElements", dreamPage.getTotal());
        response.put("totalPages", dreamPage.getPages());
        response.put("currentPage", dreamPage.getCurrent());
        response.put("pageSize", dreamPage.getSize());
        response.put("hasNext", dreamPage.getCurrent() < dreamPage.getPages());
        response.put("hasPrevious", dreamPage.getCurrent() > 1);
        
        return ApiResponse.success(response);
    }

    /**
     * 获取收藏的梦境列表
     */
    @GetMapping("/favorites")
    public ApiResponse<List<Dream>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<Dream> favorites = dreamService.getFavoritesByUserId(userId);
        return ApiResponse.success(favorites);
    }

    /**
     * 获取梦境详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getDream(@PathVariable("id") Long id,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Dream dream = dreamService.getDreamById(id, userId);
        AISummary aiSummary = aiSummaryService.getSummaryByDreamId(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", dream.getId());
        response.put("userId", dream.getUserId());
        response.put("title", dream.getTitle());
        response.put("content", dream.getContent());
        response.put("dreamDate", dream.getDreamDate());
        response.put("sleepStartTime", dream.getSleepStartTime());
        response.put("sleepEndTime", dream.getSleepEndTime());
        response.put("sleepQuality", dream.getSleepQuality());
        response.put("moodBeforeSleep", dream.getMoodBeforeSleep());
        response.put("moodAfterWake", dream.getMoodAfterWake());
        response.put("dreamType", dream.getDreamType());
        response.put("vividness", dream.getVividness());
        response.put("isFavorite", dream.getIsFavorite());
        response.put("isPrivate", dream.getIsPrivate());
        response.put("tags", dream.getTags());
        response.put("aiSummary", aiSummary);
        response.put("createdAt", dream.getCreatedAt());
        response.put("updatedAt", dream.getUpdatedAt());
        
        return ApiResponse.success(response);
    }

    /**
     * 更新梦境记录
     */
    @PutMapping("/{id}")
    public ApiResponse<Dream> updateDream(@PathVariable Long id,
                                          @Valid @RequestBody DreamRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Dream dream = dreamService.updateDream(id, userId, request);
        return ApiResponse.success("更新成功", dream);
    }

    /**
     * 删除梦境记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDream(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        dreamService.deleteDream(id, userId);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 切换收藏状态
     */
    @PutMapping("/{id}/favorite")
    public ApiResponse<Map<String, Boolean>> toggleFavorite(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        boolean isFavorite = dreamService.toggleFavorite(id, userId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        
        return ApiResponse.success(response);
    }

    /**
     * 重新生成AI总结
     */
    @PostMapping("/{id}/ai-summary/regenerate")
    public ApiResponse<Map<String, Object>> regenerateAISummary(@PathVariable Long id,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        dreamService.regenerateAISummary(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "PENDING");
        response.put("estimatedTime", 10);
        
        return ApiResponse.success("AI总结生成中", response);
    }

    /**
     * 获取用户ID
     */
    private Long getUserId(UserDetails userDetails) {
        User user = userMapper.findByUsername(userDetails.getUsername());
        return user.getId();
    }
}
