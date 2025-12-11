package icu.nyat.dreamjournalsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.nyat.dreamjournalsystem.dto.request.DreamRequest;
import icu.nyat.dreamjournalsystem.entity.Dream;
import icu.nyat.dreamjournalsystem.exception.BusinessException;
import icu.nyat.dreamjournalsystem.exception.ErrorCode;
import icu.nyat.dreamjournalsystem.mapper.DreamMapper;
import icu.nyat.dreamjournalsystem.service.AISummaryService;
import icu.nyat.dreamjournalsystem.service.DreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 梦境服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DreamServiceImpl implements DreamService {

    private final DreamMapper dreamMapper;
    private final AISummaryService aiSummaryService;

    @Override
    @Transactional
    public Dream createDream(Long userId, DreamRequest request) {
        Dream dream = new Dream();
        dream.setUserId(userId);
        dream.setTitle(request.getTitle());
        dream.setContent(request.getContent());
        dream.setDreamDate(request.getDreamDate());
        dream.setSleepStartTime(request.getSleepStartTime());
        dream.setSleepEndTime(request.getSleepEndTime());
        dream.setSleepQuality(request.getSleepQuality());
        dream.setMoodBeforeSleep(request.getMoodBeforeSleep());
        dream.setMoodAfterWake(request.getMoodAfterWake());
        dream.setDreamType(request.getDreamType() != null ? request.getDreamType() : Dream.DreamType.NORMAL);
        dream.setVividness(request.getVividness());
        dream.setIsFavorite(request.getIsFavorite() != null ? request.getIsFavorite() : false);
        dream.setIsPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : true);
        dream.setTags(request.getTags());
        dream.setCreatedAt(LocalDateTime.now());
        dream.setUpdatedAt(LocalDateTime.now());

        dreamMapper.insert(dream);

        log.info("用户 {} 创建了新的梦境记录: {}", userId, dream.getId());

        // 异步生成AI总结
        if (Boolean.TRUE.equals(request.getGenerateAISummary())) {
            aiSummaryService.generateSummaryAsync(dream);
        }

        return dream;
    }

    @Override
    public Dream getDreamById(Long dreamId, Long userId) {
        Dream dream = dreamMapper.selectById(dreamId);
        
        if (dream == null) {
            throw new BusinessException(ErrorCode.DREAM_NOT_FOUND, "梦境记录不存在");
        }

        if (!dream.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权访问此梦境记录");
        }

        return dream;
    }

    @Override
    public IPage<Dream> getDreamsByUserId(Long userId, int page, int size,
                                          String keyword, LocalDate startDate, LocalDate endDate,
                                          Dream.DreamType dreamType, Boolean isFavorite) {
        Page<Dream> pageRequest = new Page<>(page, size);
        
        LambdaQueryWrapper<Dream> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dream::getUserId, userId);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Dream::getTitle, keyword)
                    .or()
                    .like(Dream::getContent, keyword));
        }
        
        if (startDate != null) {
            queryWrapper.ge(Dream::getDreamDate, startDate);
        }
        
        if (endDate != null) {
            queryWrapper.le(Dream::getDreamDate, endDate);
        }
        
        if (dreamType != null) {
            queryWrapper.eq(Dream::getDreamType, dreamType);
        }
        
        if (Boolean.TRUE.equals(isFavorite)) {
            queryWrapper.eq(Dream::getIsFavorite, true);
        }
        
        queryWrapper.orderByDesc(Dream::getDreamDate, Dream::getCreatedAt);
        
        return dreamMapper.selectPage(pageRequest, queryWrapper);
    }

    @Override
    @Transactional
    public Dream updateDream(Long dreamId, Long userId, DreamRequest request) {
        Dream dream = getDreamById(dreamId, userId);

        if (request.getTitle() != null) {
            dream.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            dream.setContent(request.getContent());
        }
        if (request.getDreamDate() != null) {
            dream.setDreamDate(request.getDreamDate());
        }
        if (request.getSleepStartTime() != null) {
            dream.setSleepStartTime(request.getSleepStartTime());
        }
        if (request.getSleepEndTime() != null) {
            dream.setSleepEndTime(request.getSleepEndTime());
        }
        if (request.getSleepQuality() != null) {
            dream.setSleepQuality(request.getSleepQuality());
        }
        if (request.getMoodBeforeSleep() != null) {
            dream.setMoodBeforeSleep(request.getMoodBeforeSleep());
        }
        if (request.getMoodAfterWake() != null) {
            dream.setMoodAfterWake(request.getMoodAfterWake());
        }
        if (request.getDreamType() != null) {
            dream.setDreamType(request.getDreamType());
        }
        if (request.getVividness() != null) {
            dream.setVividness(request.getVividness());
        }
        if (request.getIsFavorite() != null) {
            dream.setIsFavorite(request.getIsFavorite());
        }
        if (request.getIsPrivate() != null) {
            dream.setIsPrivate(request.getIsPrivate());
        }
        if (request.getTags() != null) {
            dream.setTags(request.getTags());
        }

        dream.setUpdatedAt(LocalDateTime.now());
        dreamMapper.updateById(dream);

        log.info("用户 {} 更新了梦境记录: {}", userId, dreamId);

        return dream;
    }

    @Override
    @Transactional
    public void deleteDream(Long dreamId, Long userId) {
        Dream dream = getDreamById(dreamId, userId);
        dreamMapper.deleteById(dream.getId());
        log.info("用户 {} 删除了梦境记录: {}", userId, dreamId);
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long dreamId, Long userId) {
        Dream dream = getDreamById(dreamId, userId);
        dream.setIsFavorite(!Boolean.TRUE.equals(dream.getIsFavorite()));
        dream.setUpdatedAt(LocalDateTime.now());
        dreamMapper.updateById(dream);
        return dream.getIsFavorite();
    }

    @Override
    public List<Dream> getFavoritesByUserId(Long userId) {
        LambdaQueryWrapper<Dream> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dream::getUserId, userId)
                .eq(Dream::getIsFavorite, true)
                .orderByDesc(Dream::getDreamDate, Dream::getCreatedAt);
        return dreamMapper.selectList(queryWrapper);
    }

    @Override
    public void regenerateAISummary(Long dreamId, Long userId) {
        Dream dream = getDreamById(dreamId, userId);
        aiSummaryService.generateSummaryAsync(dream);
    }
}
