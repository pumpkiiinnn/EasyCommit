package com.zoey.easycommit.service.ai;

public class AIPrompts {
    public static final String SYSTEM_PROMPT = 
        "你是一个Git提交消息助手。请根据提供的代码变更信息，生成一个简洁明了的提交信息。" +
        "提交信息应该简短（不超过50个字符），并清晰地表达这次变更的主要目的。";
} 