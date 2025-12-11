package icu.nyat.dreamjournalsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import icu.nyat.dreamjournalsystem.entity.AISummary;
import icu.nyat.dreamjournalsystem.entity.Dream;
import icu.nyat.dreamjournalsystem.mapper.AISummaryMapper;
import icu.nyat.dreamjournalsystem.service.AISummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.theokanning.openai.service.OpenAiService.*;

/**
 * AI总结服务实现
 */
@Slf4j
@Service
public class AISummaryServiceImpl implements AISummaryService {

    private final AISummaryMapper aiSummaryMapper;
    private final ObjectMapper objectMapper;

    /**
     * Self-reference to enable transactional proxy for internal calls.
     */
    private AISummaryServiceImpl self;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSelf(@org.springframework.context.annotation.Lazy AISummaryServiceImpl self) {
        this.self = self;
    }

    public AISummaryServiceImpl(AISummaryMapper aiSummaryMapper, ObjectMapper objectMapper) {
        this.aiSummaryMapper = aiSummaryMapper;
        this.objectMapper = objectMapper;
    }

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.base-url}")
    private String baseUrl;

    @Value("${openai.timeout}")
    private Long timeout;

    @Value("${openai.max-tokens}")
    private Integer maxTokens;

    @Value("${openai.temperature}")
    private Double temperature;

    private static final String PROMPT_VERSION = "1.0.1";

    /**
     * 将AI总结重置为PENDING并清空旧字段，供生成前复用。
     */
    private void resetSummaryFieldsToPending(AISummary summary) {
        summary.setStatus(AISummary.SummaryStatus.PENDING);
        summary.setAiModel(model);
        summary.setPromptVersion(PROMPT_VERSION);
        summary.setUpdatedAt(LocalDateTime.now());
        summary.setErrorMessage(null);
        summary.setSummary(null);
        summary.setKeywords(null);
        summary.setEmotionAnalysis(null);
        summary.setSymbolAnalysis(null);
        summary.setPsychologicalInsight(null);
        summary.setAdvice(null);
        summary.setTokensUsed(null);
        summary.setGenerationTimeMs(null);
        summary.setConfidenceScore(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AISummary markPending(Dream dream) {
        AISummary summary = aiSummaryMapper.findByDreamIdForUpdate(dream.getId());
        boolean isNew = (summary == null);

        if (isNew) {
            summary = new AISummary();
            summary.setDreamId(dream.getId());
            summary.setCreatedAt(LocalDateTime.now());
        } else {
            if (summary.getStatus() == AISummary.SummaryStatus.PENDING) {
                log.info("AI总结正在生成中, dreamId: {}, 跳过重复请求", dream.getId());
                return null;
            }
        }

        resetSummaryFieldsToPending(summary);

        if (isNew) {
            try {
                aiSummaryMapper.insert(summary);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                log.warn("并发创建AI总结记录, dreamId: {}, 重新获取现有记录", dream.getId());
                summary = aiSummaryMapper.findByDreamIdForUpdate(dream.getId());
                if (summary == null) {
                    throw new RuntimeException("无法获取AI总结记录", e);
                }
                if (summary.getStatus() == AISummary.SummaryStatus.PENDING) {
                    log.info("AI总结正在由其他线程生成, dreamId: {}", dream.getId());
                    return null;
                }
            }
        } else {
            aiSummaryMapper.updateAllFields(summary);
        }

        return summary;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSummary(AISummary summary) {
        aiSummaryMapper.updateAllFields(summary);
    }

        private static final String SYSTEM_PROMPT =
                        "你是一位专业的梦境分析师，具有心理学和符号学背景。你的任务是分析用户提供的梦境内容，并提供专业、客观、有建设性的分析。\n" +
                        "\n" +
                        "## 分析原则\n" +
                        "1. 客观性: 基于梦境内容进行分析，避免主观臆测\n" +
                        "2. 专业性: 使用心理学理论进行解读，但保持通俗易懂\n" +
                        "3. 建设性: 提供积极正面的解读，帮助用户进行自我反思\n" +
                        "4. 安全性: \n" +
                        "   - 绝对不提供医学诊断或心理疾病判断\n" +
                        "   - 不做负面的命运预测\n" +
                        "   - 不提供可能造成心理伤害的解读\n" +
                        "   - 如发现用户可能存在心理问题，建议寻求专业帮助\n" +
                        "\n" +
                        "## 输出要求\n" +
                        "直接输出纯JSON对象，不要使用markdown代码块，不要添加```json标记，不要有任何其他文字说明。\n" +
                        "\n" +
                        "JSON结构如下：\n" +
                        "{\n" +
                        "  \"summary\": \"梦境总结(100-300字)\",\n" +
                        "  \"keywords\": {\"primary\": [], \"secondary\": [], \"emotions\": []},\n" +
                        "  \"emotion_analysis\": {\"dominant_emotion\": \"\", \"emotion_spectrum\": {\"positive\": 0.0, \"negative\": 0.0, \"neutral\": 0.0}, \"intensity\": 0.0, \"emotions_detected\": [{\"emotion\": \"\", \"score\": 0.0}]},\n" +
                        "  \"symbol_analysis\": {\"symbols\": [{\"symbol\": \"\", \"meaning\": \"\", \"psychological_interpretation\": \"\"}], \"overall_theme\": \"\", \"life_connection\": \"\"},\n" +
                        "  \"psychological_insight\": \"心理学洞察(100-200字)\",\n" +
                        "  \"advice\": \"积极的建议(50-100字)\"\n" +
                        "}\n" +
                        "\n" +
                        "重要：直接以 { 开头输出JSON，不要有任何前缀或后缀！\n";

        private static final String USER_PROMPT_TEMPLATE =
                        "请分析以下梦境内容：\n\n" +
                        "【梦境标题】: %s\n" +
                        "【做梦日期】: %s\n" +
                        "【梦境内容】: %s\n" +
                        "【睡眠质量】: %s\n" +
                        "【睡前情绪】: %s\n" +
                        "【醒后情绪】: %s\n" +
                        "【梦境类型】: %s\n" +
                        "【清晰度】: %s/10\n\n" +
                        "请按照指定的JSON格式输出分析结果。\n";

    @Override
    @Async
    public void generateSummaryAsync(Dream dream) {
        try {
            generateSummary(dream);
        } catch (Exception e) {
            log.error("异步生成AI总结失败, dreamId: {}", dream.getId(), e);
            // generateSummary 内部已经处理了失败状态的更新，这里不需要再次处理
        }
    }

    @Override
    public AISummary generateSummary(Dream dream) {
        long startTime = System.currentTimeMillis();

        // 先用短事务将状态重置为PENDING并持久化，避免长事务持锁
        AISummary summary = self.markPending(dream);
        if (summary == null) {
            return null;
        }

        try {
            // 构建Prompt
            String userPrompt = String.format(USER_PROMPT_TEMPLATE,
                    dream.getTitle(),
                    dream.getDreamDate(),
                    dream.getContent(),
                    dream.getSleepQuality() != null ? dream.getSleepQuality().name() : "未知",
                    dream.getMoodBeforeSleep() != null ? dream.getMoodBeforeSleep().name() : "未知",
                    dream.getMoodAfterWake() != null ? dream.getMoodAfterWake().name() : "未知",
                    dream.getDreamType() != null ? dream.getDreamType().name() : "NORMAL",
                    dream.getVividness() != null ? dream.getVividness() : "未知"
            );

            // 调用OpenAI API (支持自定义base URL，如DeepSeek)
            ObjectMapper mapper = defaultObjectMapper();
            OkHttpClient client = defaultClient(apiKey, Duration.ofMillis(timeout))
                    .newBuilder()
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            OpenAiApi api = retrofit.create(OpenAiApi.class);
            OpenAiService openAiService = new OpenAiService(api);
            
                ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(
                        new ChatMessage("system", SYSTEM_PROMPT),
                        new ChatMessage("user", userPrompt)
                    ))
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

                com.theokanning.openai.completion.chat.ChatCompletionResult response = openAiService.createChatCompletion(request);
            String content = response.getChoices().get(0).getMessage().getContent();
            
            // 清理可能存在的 markdown 代码块标记
            content = cleanJsonContent(content);
            
            log.debug("AI返回内容(清理后): {}", content);
            
            // 使用配置了 snake_case 的 ObjectMapper 解析响应
            ObjectMapper snakeCaseMapper = new ObjectMapper();
            snakeCaseMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            snakeCaseMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AISummaryResponse aiResponse = snakeCaseMapper.readValue(content, AISummaryResponse.class);
            
            log.info("解析AI响应: summary={}, keywords={}, emotionAnalysis={}, symbolAnalysis={}",
                    aiResponse.getSummary() != null ? "有" : "无",
                    aiResponse.getKeywords() != null ? "有" : "无", 
                    aiResponse.getEmotionAnalysis() != null ? "有" : "无",
                    aiResponse.getSymbolAnalysis() != null ? "有" : "无");
            
            // 更新总结记录
            summary.setSummary(aiResponse.getSummary());
            summary.setKeywords(convertKeywords(aiResponse.getKeywords()));
            summary.setEmotionAnalysis(convertEmotionAnalysis(aiResponse.getEmotionAnalysis()));
            summary.setSymbolAnalysis(convertSymbolAnalysis(aiResponse.getSymbolAnalysis()));
            summary.setPsychologicalInsight(aiResponse.getPsychologicalInsight());
            summary.setAdvice(aiResponse.getAdvice());
            summary.setStatus(AISummary.SummaryStatus.COMPLETED);
            summary.setTokensUsed(response.getUsage() != null ? (int) response.getUsage().getTotalTokens() : null);
            summary.setGenerationTimeMs((int) (System.currentTimeMillis() - startTime));
            summary.setConfidenceScore(new BigDecimal("0.85")); // 默认置信度
            summary.setErrorMessage(null); // 清除之前的错误信息
            summary.setUpdatedAt(LocalDateTime.now());
            
            log.info("准备更新AI总结, id={}, status={}", summary.getId(), summary.getStatus());
            
            self.saveSummary(summary);
            
            log.info("AI总结生成成功, dreamId: {}, 耗时: {}ms", dream.getId(), summary.getGenerationTimeMs());
            
            return summary;

        } catch (Exception e) {
            log.error("AI总结生成失败, dreamId: {}", dream.getId(), e);
            
            summary.setStatus(AISummary.SummaryStatus.FAILED);
            summary.setErrorMessage(e.getMessage());
            summary.setGenerationTimeMs((int) (System.currentTimeMillis() - startTime));
            summary.setUpdatedAt(LocalDateTime.now());
            self.saveSummary(summary);
            
            throw new RuntimeException("AI总结生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清理 AI 返回内容中的 markdown 代码块标记
     */
    private String cleanJsonContent(String content) {
        if (content == null) {
            return null;
        }
        
        String cleaned = content.trim();
        
        // 使用正则表达式移除开头的 ```json 或 ``` (支持各种变体)
        // 匹配: ```json, ``` json, ```JSON, 等
        cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
        
        // 移除结尾的 ```
        cleaned = cleaned.replaceFirst("\\s*```$", "");
        
        cleaned = cleaned.trim();
        
        // 确保结果以 { 开头（找到第一个 { 的位置）
        int jsonStart = cleaned.indexOf('{');
        if (jsonStart > 0) {
            cleaned = cleaned.substring(jsonStart);
        }
        
        // 确保结果以 } 结尾（找到最后一个 } 的位置）
        int jsonEnd = cleaned.lastIndexOf('}');
        if (jsonEnd > 0 && jsonEnd < cleaned.length() - 1) {
            cleaned = cleaned.substring(0, jsonEnd + 1);
        }
        
        return cleaned;
    }

    @Override
    public AISummary getSummaryByDreamId(Long dreamId) {
        return aiSummaryMapper.findByDreamId(dreamId);
    }

    // AI响应的数据类
    @lombok.Data
    private static class AISummaryResponse {
        private String summary;
        private KeywordsResponse keywords;
        private EmotionAnalysisResponse emotionAnalysis;
        private SymbolAnalysisResponse symbolAnalysis;
        private String psychologicalInsight;
        private String advice;

        @lombok.Data
        static class KeywordsResponse {
            private List<String> primary;
            private List<String> secondary;
            private List<String> emotions;
        }

        @lombok.Data
        static class EmotionAnalysisResponse {
            private String dominantEmotion;
            private EmotionSpectrum emotionSpectrum;
            private Double intensity;
            private List<EmotionScore> emotionsDetected;

            @lombok.Data
            static class EmotionSpectrum {
                private Double positive;
                private Double negative;
                private Double neutral;
            }

            @lombok.Data
            static class EmotionScore {
                private String emotion;
                private Double score;
            }
        }

        @lombok.Data
        static class SymbolAnalysisResponse {
            private List<Symbol> symbols;
            private String overallTheme;
            private String lifeConnection;

            @lombok.Data
            static class Symbol {
                private String symbol;
                private String meaning;
                private String psychologicalInterpretation;
            }
        }
    }

    // 转换方法
    private AISummary.Keywords convertKeywords(AISummaryResponse.KeywordsResponse response) {
        AISummary.Keywords keywords = new AISummary.Keywords();
        if (response != null) {
            keywords.setPrimary(response.getPrimary());
            keywords.setSecondary(response.getSecondary());
            keywords.setEmotions(response.getEmotions());
        }
        return keywords;
    }

    private AISummary.EmotionAnalysis convertEmotionAnalysis(AISummaryResponse.EmotionAnalysisResponse response) {
        AISummary.EmotionAnalysis analysis = new AISummary.EmotionAnalysis();
        if (response == null) {
            return analysis;
        }
        
        analysis.setDominantEmotion(response.getDominantEmotion());
        analysis.setIntensity(response.getIntensity());
        
        if (response.getEmotionSpectrum() != null) {
            AISummary.EmotionAnalysis.EmotionSpectrum spectrum = new AISummary.EmotionAnalysis.EmotionSpectrum();
            spectrum.setPositive(response.getEmotionSpectrum().getPositive());
            spectrum.setNegative(response.getEmotionSpectrum().getNegative());
            spectrum.setNeutral(response.getEmotionSpectrum().getNeutral());
            analysis.setEmotionSpectrum(spectrum);
        }
        
        if (response.getEmotionsDetected() != null) {
            analysis.setEmotionsDetected(response.getEmotionsDetected().stream()
                    .map(e -> {
                        AISummary.EmotionAnalysis.EmotionScore score = new AISummary.EmotionAnalysis.EmotionScore();
                        score.setEmotion(e.getEmotion());
                        score.setScore(e.getScore());
                        return score;
                    })
                    .collect(Collectors.toList()));
        }
        
        return analysis;
    }

    private AISummary.SymbolAnalysis convertSymbolAnalysis(AISummaryResponse.SymbolAnalysisResponse response) {
        AISummary.SymbolAnalysis analysis = new AISummary.SymbolAnalysis();
        if (response == null) {
            return analysis;
        }
        
        analysis.setOverallTheme(response.getOverallTheme());
        analysis.setLifeConnection(response.getLifeConnection());
        
        if (response.getSymbols() != null) {
            analysis.setSymbols(response.getSymbols().stream()
                    .map(s -> {
                        AISummary.SymbolAnalysis.Symbol symbol = new AISummary.SymbolAnalysis.Symbol();
                        symbol.setSymbol(s.getSymbol());
                        symbol.setMeaning(s.getMeaning());
                        symbol.setPsychologicalInterpretation(s.getPsychologicalInterpretation());
                        return symbol;
                    })
                    .collect(Collectors.toList()));
        }
        
        return analysis;
    }
}
