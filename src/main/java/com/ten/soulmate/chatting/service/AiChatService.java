package com.ten.soulmate.chatting.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ten.soulmate.chatting.dto.AiRequestDto;
import com.ten.soulmate.chatting.dto.ReportAiResponse;
import com.ten.soulmate.chatting.dto.SummaryAiResponse;
import com.ten.soulmate.global.prompt.PromptService;
import com.ten.soulmate.global.type.SoulMateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PromptService promptService = new PromptService();
    private final WebClient webClient;
    
    @Value("${clova.api.url}")
    private String apiUrl;
    
    @Value("${clova.api.endpoint.dash}")
    private String dash;
    
    @Value("${clova.api.endpoint.hcx007}")
    private String hcx007;
    
    @Value("${clova.api.endpoint.hcx005}")
    private String hcx005;
    
    @Value("${clova.api.endpoint.summary}")
    private String summary;

    @Value("${clova.api.key}")
    private String apiKey;
    
    @Value("${soulmate.answer-type.T}")
    private String Ttype;
    
    @Value("${soulmate.answer-type.F}")
    private String Ftype;
        
    
    //HCX-002-DASH
    //채팅용 모델 - 고도화 때 진행      
//    public Flux<String> ResponseChatMessage(AiRequestDto aiRequestDto, Sinks.Many<String> sink) {
//        String systemPrompt;
//        try {
//            systemPrompt = promptService.getSystemPrompt("ChattingPrompt");
//        } catch (IOException e) {
//            sink.tryEmitNext("System 프롬프트 로딩 오류");
//            return Flux.empty();
//        }
//
//        String answerType = "";
//        if (aiRequestDto.getSoulMateType().equals(SoulMateType.T))
//            answerType = Ttype;
//        if (aiRequestDto.getSoulMateType().equals(SoulMateType.F))
//            answerType = Ftype;
//
//        Map<String, String> replacements = Map.of(
//            "soulmate", aiRequestDto.getSoulmateName(),
//            "member", aiRequestDto.getMemberName(),
//            "answerType", answerType
//        );
//
//        String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("messages", new Object[]{
//            Map.of("role", "system", "content", fullPrompt),
//            Map.of("role", "user", "content", aiRequestDto.getMessage()),
//        });
//        body.put("topP", 0.8);
//        body.put("topK", 0);
//        body.put("maxTokens", 256);
//        body.put("repetitionPenalty", 1.1);
//        body.put("includeAiFilters", true);
//
//        Flux<String> responseFlux = webClient.post()
//            .uri(dash)
//            .bodyValue(body)
//            .retrieve()
//            .bodyToFlux(DataBuffer.class)
//            .map(buffer -> {
//                byte[] bytes = new byte[buffer.readableByteCount()];
//                buffer.read(bytes);
//                DataBufferUtils.release(buffer);
//                return new String(bytes, StandardCharsets.UTF_8);
//            })
//            .share();
//
//        // 1) 토큰 단위 원본 스트림 클라이언트 전달
//        responseFlux.subscribe(sink::tryEmitNext);
//
//        return responseFlux;
//    }
    
    
    public String ResponseChatMessage(AiRequestDto aiRequestDto)
    {
    	try {
			String systemPrompt = promptService.getSystemPrompt("ChattingPrompt");
	        String answerType = "";
	        
	        if(aiRequestDto.getSoulMateType().equals(SoulMateType.T))
	        	answerType = Ttype;
	        if(aiRequestDto.getSoulMateType().equals(SoulMateType.F))
	        	answerType = Ftype;
	        
	        Map<String, String> replacements = Map.of(
    		        "soulmate", aiRequestDto.getSoulmateName(),
    		        "member", aiRequestDto.getMemberName(),
    		        "answerType", answerType
    		    );
			
	    	String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);
	        
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Bearer "+apiKey);	         	
	        
	        Map<String, Object> body = new HashMap<>();
	         body.put("messages", new Object[] {
	         		Map.of("role", "system", "content", fullPrompt),
	                Map.of("role", "user", "content", aiRequestDto.getMessage())
	         });
	        
	         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
             ResponseEntity<String> response = restTemplate.postForEntity(apiUrl+dash, request, String.class);
             JsonNode root = objectMapper.readTree(response.getBody());
             String answer = root.path("result").path("message").path("content").asText();
			
             return answer;
             
		} catch (IOException e) {			
			log.error("HCX-002-DASH Error : "+e.getMessage());
			e.printStackTrace();
			
			return e.getMessage();
		}
    }
      
    
    //HCX-005
    //정보량 판단용 모델
    public boolean ResponseCheckMessage(AiRequestDto aiRequestDto)
    {   	
    	try {
			String systemPrompt = promptService.getSystemPrompt("CheckPrompt");	     
	        
	        Map<String, String> replacements = Map.of(
    		        "member", aiRequestDto.getMemberName()
    		    );
			
	    	String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);
	        
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Bearer "+apiKey);	         	
	        
	        Map<String, Object> body = new HashMap<>();
	         body.put("messages", new Object[] {
	         		Map.of("role", "system", "content", fullPrompt),
	                Map.of("role", "user", "content", aiRequestDto.getMessage())
	         });
	        
	         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
             ResponseEntity<String> response = restTemplate.postForEntity(apiUrl+hcx005, request, String.class);
             JsonNode root = objectMapper.readTree(response.getBody());
             String answer = root.path("result").path("message").path("content").asText();
			
             log.info("AI Check Result : "+answer);
                         
             if(answer.equals("Yes"))
            	 return true;
                          
             return false;
             
		} catch (IOException e) {			
			log.error("HCX-005 Error : "+e.getMessage());
			e.printStackTrace();
			
			return false;
		}
    	
    }

    //HCX-007
    //Report용 모델
    public ReportAiResponse ResponseReportMessage(AiRequestDto aiRequestDto)
    { 	
    	ReportAiResponse responseAi = null;
    	try {
    		
    		String systemPrompt = promptService.getSystemPrompt("ReportPrompt");
        	String soulmate = aiRequestDto.getSoulmateName();
        	String member = aiRequestDto.getMemberName();
        	String valueAttribute = aiRequestDto.getValueAttribute();
        	String decision = aiRequestDto.getDecision();
        	String regret = aiRequestDto.getRegret();
        	String decisionTrust = aiRequestDto.getDecisionTrust();    		    		
    		
	       	Map<String, String> replacements = Map.of(
	       		        "soulmate", soulmate,
	       		        "member", member,
	       		        "valueAttribute", valueAttribute,
	       		        "decision", decision,
	       		        "regret", regret,
	       		        "decisionTrust",decisionTrust
	       		    );
	    	String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);

	    	HttpHeaders headers = new HttpHeaders();
	         headers.setContentType(MediaType.APPLICATION_JSON);
	         headers.set("Authorization", "Bearer "+apiKey);
	         	         
	         Map<String, Object> body = new HashMap<>();
	         body.put("messages", new Object[] {
	         		Map.of("role", "system", "content", fullPrompt),
	                Map.of("role", "user", "content", aiRequestDto.getMessage())
	         });
	         body.put("thinking.effort", "high");
	         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
	         ResponseEntity<String> response = restTemplate.postForEntity(apiUrl+hcx007, request, String.class);
             JsonNode root = objectMapper.readTree(response.getBody());
             String content = root.path("result").path("message").path("content").asText();
             String thinkingContent = root.path("result").path("message").path("thinkingContent").asText();                                             
                         
             content = cleanJson(content);
             
             ObjectMapper objectMapper = new ObjectMapper();
             JsonNode jsonData = objectMapper.readTree(content);

             String titleA = jsonData.get("titleA").asText();
             String answerA = jsonData.get("answerA").asText();  
             
             String titleB = jsonData.get("titleB").asText();    
             String answerB = jsonData.get("answerB").asText();  
             
             String conclusion = jsonData.get("conclusion").asText();    
             String recommend = jsonData.get("recommend").asText();
                 
             responseAi = ReportAiResponse.builder()
            		 						.thinkingContent(thinkingContent)
            		 						.titleA(titleA)
            		 						.titleB(titleB)
            		 						.answerA(answerA)
            		 						.answerB(answerB)
            		 						.recommend(recommend)
            		 						.conclusion(conclusion).build();

          
             log.info("HCX-007 Content : "+content);
             log.info("======================================================================================");
             log.info("HCX-007 Thinking Content : "+thinkingContent);	       	    		
    	}    	
    	catch(Exception e)
    	{            
            log.error("HCX-007 Error : "+e.getMessage());
    	}
    	return responseAi;
    }   

    
    //요약
    //내용 요약 모델
    public SummaryAiResponse ResponseSummaryMessage(AiRequestDto aiRequestDto)
    {
    	SummaryAiResponse responseAi = null;    	
    	
    	try {
			String systemPrompt = promptService.getSystemPrompt("SummaryPrompt");

	        Map<String, String> replacements = Map.of(
    		        "soulmate", aiRequestDto.getSoulmateName(),
    		        "member", aiRequestDto.getMemberName()
    		    );
			
	    	String fullPrompt = promptService.buildFinalPrompt(systemPrompt, replacements);
	        
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Bearer "+apiKey);	         	
	        
	        Map<String, Object> body = new HashMap<>();
	         body.put("messages", new Object[] {
	         		Map.of("role", "system", "content", fullPrompt),
	                Map.of("role", "user", "content", aiRequestDto.getMessage())
	         });
	        
	         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
             ResponseEntity<String> response = restTemplate.postForEntity(apiUrl+hcx005, request, String.class);
             JsonNode root = objectMapper.readTree(response.getBody());
             String answer = root.path("result").path("message").path("content").asText();
             	
             log.info("Summary Content : "+answer);
            
             answer = cleanJson(answer);
             
             ObjectMapper objectMapper = new ObjectMapper();
             JsonNode jsonData = objectMapper.readTree(answer);

             String title = jsonData.get("title").asText();
             String content = jsonData.get("content").asText();           
             
             responseAi = SummaryAiResponse.builder()
            		 		.summaryTitle(title)
            		 		.summaryContent(content)
            		 		.build();                       
             
		} catch (IOException e) {			
			log.error("HCX-005-Summary Error : "+e.getMessage());
			e.printStackTrace();			
		}
    	    	
    	return responseAi;
    }
    
    
    public static String cleanJson(String input) {
        // 백틱 및 마크다운 태그 제거
        return input
                .replaceAll("(?m)^\\s*```[a-zA-Z]*\\s*$", "") // ```json, ``` 등 제거
                .replaceAll("(?m)^\\s*```\\s*$", "")          // ``` 단독 제거
                .replaceAll("`", "")                          // inline 백틱 제거
                .trim();
    }
    
//    private String extractMessageContent(String rawEvent) {
//        try {
//            int jsonStart = rawEvent.indexOf('{');
//            if (jsonStart == -1) return null;
//
//            String jsonPart = rawEvent.substring(jsonStart);
//            JsonNode root = new ObjectMapper().readTree(jsonPart);
//            JsonNode contentNode = root.path("message").path("content");
//
//            if (!contentNode.isMissingNode() && !contentNode.asText().isBlank()) {
//                return contentNode.asText();
//            }
//        } catch (Exception e) {
//            log.error("JSON 파싱 오류", e);
//        }
//        return null;
//    }
//
//    private boolean hasUsage(String rawEvent) {
//        return rawEvent.contains("\"usage\"") && !rawEvent.contains("\"usage\":null");
//    }


}
