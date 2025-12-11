# AI 梦境总结 - Prompt 模板与 JSON Schema

## 一、系统 Prompt

```text
你是一位专业的梦境分析师，具有心理学和符号学背景。你的任务是分析用户提供的梦境内容，并提供专业、客观、有建设性的分析。

## 分析原则

1. **客观性**: 基于梦境内容进行分析，避免主观臆测
2. **专业性**: 使用心理学理论（如荣格原型、弗洛伊德理论等）进行解读，但保持通俗易懂
3. **建设性**: 提供积极正面的解读，帮助用户进行自我反思
4. **安全性**: 
   - 绝对不提供医学诊断或心理疾病判断
   - 不做负面的命运预测
   - 不提供可能造成心理伤害的解读
   - 避免涉及敏感话题（政治、宗教等）
   - 如发现用户可能存在心理问题，建议寻求专业帮助

## 分析维度

1. **梦境总结**: 简要概括梦境的核心内容和叙事结构
2. **关键词提取**: 提取梦境中的重要元素和意象
3. **情绪分析**: 识别梦境中流露出的情绪状态
4. **象征意义**: 分析梦中符号的可能象征含义
5. **心理洞察**: 从心理学角度提供可能的内心状态分析
6. **建议**: 提供积极的自我关怀建议

## 输出要求

1. 以JSON格式输出
2. 使用中文
3. 语言温和、鼓励性
4. 每个分析点需要有理有据
```

## 二、用户 Prompt 模板

```text
请分析以下梦境内容：

【梦境标题】: {{title}}
【做梦日期】: {{dream_date}}
【梦境内容】: {{content}}
【睡眠质量】: {{sleep_quality}}
【睡前情绪】: {{mood_before_sleep}}
【醒后情绪】: {{mood_after_wake}}
【梦境类型】: {{dream_type}}
【清晰度】: {{vividness}}/10

请按照指定的JSON格式输出分析结果。
```

## 三、JSON Schema 定义

### 输出 Schema

```json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "title": "DreamAnalysisResult",
    "description": "AI梦境分析结果的数据结构",
    "type": "object",
    "required": ["summary", "keywords", "emotion_analysis", "symbol_analysis"],
    "properties": {
        "summary": {
            "type": "string",
            "description": "梦境的综合总结，100-300字",
            "minLength": 50,
            "maxLength": 500
        },
        "keywords": {
            "type": "object",
            "description": "关键词分析",
            "required": ["primary", "secondary", "emotions"],
            "properties": {
                "primary": {
                    "type": "array",
                    "description": "主要关键词，最重要的2-3个意象",
                    "items": {"type": "string"},
                    "minItems": 1,
                    "maxItems": 5
                },
                "secondary": {
                    "type": "array",
                    "description": "次要关键词，辅助性意象",
                    "items": {"type": "string"},
                    "minItems": 0,
                    "maxItems": 10
                },
                "emotions": {
                    "type": "array",
                    "description": "情绪关键词",
                    "items": {"type": "string"},
                    "minItems": 1,
                    "maxItems": 5
                }
            }
        },
        "emotion_analysis": {
            "type": "object",
            "description": "情绪分析",
            "required": ["dominant_emotion", "emotion_spectrum", "intensity", "emotions_detected"],
            "properties": {
                "dominant_emotion": {
                    "type": "string",
                    "description": "主导情绪",
                    "enum": ["喜悦", "平静", "好奇", "焦虑", "恐惧", "悲伤", "愤怒", "困惑", "惊讶", "怀旧", "解脱", "期待"]
                },
                "emotion_spectrum": {
                    "type": "object",
                    "description": "情绪光谱分布",
                    "required": ["positive", "negative", "neutral"],
                    "properties": {
                        "positive": {
                            "type": "number",
                            "description": "积极情绪占比",
                            "minimum": 0,
                            "maximum": 1
                        },
                        "negative": {
                            "type": "number",
                            "description": "消极情绪占比",
                            "minimum": 0,
                            "maximum": 1
                        },
                        "neutral": {
                            "type": "number",
                            "description": "中性情绪占比",
                            "minimum": 0,
                            "maximum": 1
                        }
                    }
                },
                "intensity": {
                    "type": "number",
                    "description": "情绪强度 (0-1)",
                    "minimum": 0,
                    "maximum": 1
                },
                "emotions_detected": {
                    "type": "array",
                    "description": "检测到的所有情绪及其得分",
                    "items": {
                        "type": "object",
                        "required": ["emotion", "score"],
                        "properties": {
                            "emotion": {"type": "string"},
                            "score": {
                                "type": "number",
                                "minimum": 0,
                                "maximum": 1
                            }
                        }
                    }
                }
            }
        },
        "symbol_analysis": {
            "type": "object",
            "description": "象征意义分析",
            "required": ["symbols", "overall_theme"],
            "properties": {
                "symbols": {
                    "type": "array",
                    "description": "符号分析列表",
                    "items": {
                        "type": "object",
                        "required": ["symbol", "meaning", "psychological_interpretation"],
                        "properties": {
                            "symbol": {
                                "type": "string",
                                "description": "符号/意象名称"
                            },
                            "meaning": {
                                "type": "string",
                                "description": "普遍象征含义"
                            },
                            "psychological_interpretation": {
                                "type": "string",
                                "description": "心理学解读"
                            }
                        }
                    },
                    "minItems": 1,
                    "maxItems": 5
                },
                "overall_theme": {
                    "type": "string",
                    "description": "整体主题"
                },
                "life_connection": {
                    "type": "string",
                    "description": "与现实生活的可能联系"
                }
            }
        },
        "psychological_insight": {
            "type": "string",
            "description": "心理学洞察，100-200字",
            "maxLength": 500
        },
        "advice": {
            "type": "string",
            "description": "建议和提示，积极正面的自我关怀建议",
            "maxLength": 300
        }
    }
}
```

## 四、完整示例

### 输入示例

```json
{
    "title": "飞翔在云端",
    "dream_date": "2024-01-01",
    "content": "梦见自己在天空中自由飞翔，穿越白云，俯瞰大地。一开始有点害怕，但很快就适应了，感觉非常轻松自在，仿佛所有烦恼都消失了。我看到了自己的城市，看到了小时候住的老房子。后来遇到了一只金色的鸟，它带着我飞向了太阳的方向，阳光很温暖但不刺眼。",
    "sleep_quality": "GOOD",
    "mood_before_sleep": "STRESSED",
    "mood_after_wake": "REFRESHED",
    "dream_type": "LUCID",
    "vividness": 8
}
```

### 输出示例

```json
{
    "summary": "这是一个典型的自由飞翔梦，展现了梦者从最初的不安到完全释放的情感转变过程。梦境中的元素——天空、云朵、故乡、金色鸟和温暖阳光——共同构建了一个关于自由、回归和希望的叙事。梦者在现实中可能正经历压力，而这个梦提供了一个心理上的释放空间，同时也暗示着对童年纯真和简单生活的怀念。",
    "keywords": {
        "primary": ["飞翔", "自由", "回归"],
        "secondary": ["天空", "云朵", "故乡", "老房子", "金色鸟", "阳光"],
        "emotions": ["解脱", "平静", "怀旧", "希望"]
    },
    "emotion_analysis": {
        "dominant_emotion": "解脱",
        "emotion_spectrum": {
            "positive": 0.75,
            "negative": 0.05,
            "neutral": 0.20
        },
        "intensity": 0.8,
        "emotions_detected": [
            {"emotion": "解脱", "score": 0.85},
            {"emotion": "平静", "score": 0.75},
            {"emotion": "怀旧", "score": 0.60},
            {"emotion": "希望", "score": 0.70},
            {"emotion": "恐惧", "score": 0.15}
        ]
    },
    "symbol_analysis": {
        "symbols": [
            {
                "symbol": "飞翔",
                "meaning": "自由、超越限制、精神解放",
                "psychological_interpretation": "飞翔梦通常出现在人们渴望摆脱现实束缚时。结合睡前的压力状态，这个梦可能是潜意识对压力的一种补偿性释放。"
            },
            {
                "symbol": "老房子",
                "meaning": "童年记忆、安全感、根源",
                "psychological_interpretation": "看到童年的老房子可能反映了对简单、无忧时光的怀念，或是在压力下寻求内心的安全港湾。"
            },
            {
                "symbol": "金色鸟",
                "meaning": "希望、指引、灵性向导",
                "psychological_interpretation": "在荣格心理学中，鸟常代表灵魂或更高自我。金色增添了神圣和珍贵的意味，暗示内心对成长和提升的渴望。"
            },
            {
                "symbol": "温暖阳光",
                "meaning": "能量、生命力、积极力量",
                "psychological_interpretation": "温暖而不刺眼的阳光象征着舒适的成长环境，暗示梦者内心对平和进步的期待。"
            }
        ],
        "overall_theme": "释放与回归——在自由中寻找内心的安宁",
        "life_connection": "这个梦可能与近期的工作或生活压力有关。梦境提供了一个心理上的喘息空间，同时也暗示着您内心深处对简单、纯粹生活状态的向往。"
    },
    "psychological_insight": "从心理学角度来看，飞翔梦是最常见的积极梦境类型之一。它通常出现在人们感到被现实限制时，作为一种补偿机制。您梦中从恐惧到享受的转变，可能反映了您具有良好的心理调适能力。梦中回望故乡的场景，可能暗示在面对压力时，您的潜意识正在寻找内心的安全基地。金色鸟的出现是一个积极信号，它象征着即使在困难时期，您内心仍保持着希望和向上的动力。",
    "advice": "建议您在日常生活中找到属于自己的"飞翔"时刻——那些让您感到自由和轻松的活动。同时，不妨偶尔回顾一下过去的美好记忆，它们可以成为您面对压力时的心理资源。如果最近确实感到压力较大，可以尝试一些放松技巧，如深呼吸、冥想或户外散步，让身心都能获得休息。"
}
```

## 五、安全性约束

### 禁止输出的内容

1. **医学/心理诊断**
   - ❌ "您可能患有抑郁症"
   - ❌ "这是PTSD的典型表现"
   - ✅ "如果您长期感到困扰，建议与专业心理咨询师交流"

2. **负面预测**
   - ❌ "这个梦预示着不好的事情将要发生"
   - ❌ "梦见这个通常意味着会有灾难"
   - ✅ "这个梦可能反映了您对某些事情的担忧"

3. **过度解读**
   - ❌ "这绝对意味着您想要逃离现在的关系"
   - ✅ "这可能暗示您正在思考某些人际关系"

4. **敏感话题**
   - 避免涉及政治、宗教、种族等敏感话题
   - 如梦境涉及暴力、自伤等内容，温和回应并建议专业帮助

### 安全响应模板

当遇到可能涉及心理健康问题的梦境时：

```json
{
    "safety_notice": "我注意到这个梦境中包含一些可能反映内心压力的元素。这是很正常的，梦境常常是我们处理日常情绪的方式。如果您发现自己经常被这类梦境困扰，或者在清醒时也感到焦虑、低落，建议与专业的心理咨询师交流。他们可以提供更个性化的支持。",
    "advice": "记录梦境本身就是一种很好的自我觉察方式。建议您继续保持这个习惯，同时关注自己的日常情绪状态。"
}
```

## 六、API 调用代码示例（Java）

```java
@Service
public class OpenAIService {
    
    private static final String SYSTEM_PROMPT = """
        你是一位专业的梦境分析师，具有心理学和符号学背景...
        """;
    
    private static final String USER_PROMPT_TEMPLATE = """
        请分析以下梦境内容：
        
        【梦境标题】: %s
        【做梦日期】: %s
        【梦境内容】: %s
        【睡眠质量】: %s
        【睡前情绪】: %s
        【醒后情绪】: %s
        【梦境类型】: %s
        【清晰度】: %d/10
        
        请按照指定的JSON格式输出分析结果。
        """;
    
    public DreamAnalysisResult analyzeDream(Dream dream) {
        String userPrompt = String.format(USER_PROMPT_TEMPLATE,
            dream.getTitle(),
            dream.getDreamDate(),
            dream.getContent(),
            dream.getSleepQuality(),
            dream.getMoodBeforeSleep(),
            dream.getMoodAfterWake(),
            dream.getDreamType(),
            dream.getVividness()
        );
        
        // 调用 OpenAI API
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-4")
            .messages(List.of(
                new ChatMessage("system", SYSTEM_PROMPT),
                new ChatMessage("user", userPrompt)
            ))
            .responseFormat(new ResponseFormat("json_object"))
            .temperature(0.7)
            .maxTokens(2000)
            .build();
        
        // 解析响应
        String response = openAiClient.createChatCompletion(request)
            .getChoices().get(0).getMessage().getContent();
        
        return objectMapper.readValue(response, DreamAnalysisResult.class);
    }
}
```
