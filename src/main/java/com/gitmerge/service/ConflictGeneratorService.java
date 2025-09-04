package com.gitmerge.service;

import com.gitmerge.enums.Difficulty;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ConflictGeneratorService {
    
    private static final String[] CODE_TEMPLATES = {
        "function calculateTotal(items) {\n    return items.reduce((sum, item) => sum + item.price, 0);\n}",
        "class UserService {\n    async getUserById(id) {\n        return await this.repository.findById(id);\n    }\n}",
        "const validateEmail = (email) => {\n    const regex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;\n    return regex.test(email);\n};"
    };

    public ConflictData generateConflictByDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> generateEasyConflict();
            case NORMAL -> generateNormalConflict(); 
            case HARD -> generateHardConflict();
            case HELL -> generateHellConflict();
        };
    }
    
    private ConflictData generateEasyConflict() {
        List<ConflictBlock> conflicts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            conflicts.add(createSimpleConflict(i));
        }
        return new ConflictData("easy_" + System.currentTimeMillis(), conflicts);
    }
    
    private ConflictData generateNormalConflict() {
        List<ConflictBlock> conflicts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            conflicts.add(createSimpleConflict(i));
        }
        return new ConflictData("normal_" + System.currentTimeMillis(), conflicts);
    }
    
    private ConflictData generateHardConflict() {
        List<ConflictBlock> conflicts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            conflicts.add(createSimpleConflict(i));
        }
        return new ConflictData("hard_" + System.currentTimeMillis(), conflicts);
    }
    
    private ConflictData generateHellConflict() {
        List<ConflictBlock> conflicts = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            conflicts.add(createSimpleConflict(i));
        }
        return new ConflictData("hell_" + System.currentTimeMillis(), conflicts);
    }
    
    private ConflictBlock createSimpleConflict(int index) {
        String baseCode = CODE_TEMPLATES[index % CODE_TEMPLATES.length];
        String correctAnswer = baseCode.replace("price", "amount");
        
        return ConflictBlock.builder()
            .fileName("file" + index + ".js")
            .lineStart(1)
            .lineEnd(3)
            .currentBranch(baseCode.replace("price", "cost"))
            .incomingBranch(baseCode.replace("price", "amount"))
            .conflictMarkers(generateConflictMarkers(baseCode))
            .expectedResolution(correctAnswer)
            .build();
    }
    
    private String generateConflictMarkers(String code) {
        return "<<<<<<< HEAD\n" + code.replace("price", "cost") + "\n=======\n" + code.replace("price", "amount") + "\n>>>>>>> incoming\n";
    }
    
    public static class ConflictData {
        private final String sessionId;
        private final List<ConflictBlock> conflicts;
        private final long timestamp;
        
        public ConflictData(String sessionId, List<ConflictBlock> conflicts) {
            this.sessionId = sessionId;
            this.conflicts = conflicts;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getSessionId() { return sessionId; }
        public List<ConflictBlock> getConflicts() { return conflicts; }
        public long getTimestamp() { return timestamp; }
        public int getTotalConflicts() { return conflicts.size(); }
    }
    
    public static class ConflictBlock {
        private String fileName;
        private int lineStart;
        private int lineEnd;
        private String currentBranch;
        private String incomingBranch;
        private String conflictMarkers;
        private String expectedResolution;
        private boolean isResolved = false;
        
        public static ConflictBlockBuilder builder() {
            return new ConflictBlockBuilder();
        }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public int getLineStart() { return lineStart; }
        public void setLineStart(int lineStart) { this.lineStart = lineStart; }
        public int getLineEnd() { return lineEnd; }
        public void setLineEnd(int lineEnd) { this.lineEnd = lineEnd; }
        public String getCurrentBranch() { return currentBranch; }
        public void setCurrentBranch(String currentBranch) { this.currentBranch = currentBranch; }
        public String getIncomingBranch() { return incomingBranch; }
        public void setIncomingBranch(String incomingBranch) { this.incomingBranch = incomingBranch; }
        public String getConflictMarkers() { return conflictMarkers; }
        public void setConflictMarkers(String conflictMarkers) { this.conflictMarkers = conflictMarkers; }
        public String getExpectedResolution() { return expectedResolution; }
        public void setExpectedResolution(String expectedResolution) { this.expectedResolution = expectedResolution; }
        public boolean isResolved() { return isResolved; }
        public void setResolved(boolean resolved) { isResolved = resolved; }
        
        public static class ConflictBlockBuilder {
            private ConflictBlock block = new ConflictBlock();
            
            public ConflictBlockBuilder fileName(String fileName) { block.fileName = fileName; return this; }
            public ConflictBlockBuilder lineStart(int lineStart) { block.lineStart = lineStart; return this; }
            public ConflictBlockBuilder lineEnd(int lineEnd) { block.lineEnd = lineEnd; return this; }
            public ConflictBlockBuilder currentBranch(String current) { block.currentBranch = current; return this; }
            public ConflictBlockBuilder incomingBranch(String incoming) { block.incomingBranch = incoming; return this; }
            public ConflictBlockBuilder conflictMarkers(String markers) { block.conflictMarkers = markers; return this; }
            public ConflictBlockBuilder expectedResolution(String resolution) { block.expectedResolution = resolution; return this; }
            
            public ConflictBlock build() { return block; }
        }
    }
}
