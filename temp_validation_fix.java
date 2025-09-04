private boolean validateResolution(ConflictBlock conflict, String resolution) {
    // 1. 충돌 마커 제거
    String cleanResolution = removeConflictMarkers(resolution);
    String expectedClean = removeConflictMarkers(conflict.getExpectedResolution());
    
    // 2. 공백/줄바꿈 정규화 (더 관대하게)
    String normalizedResolution = normalizeCode(cleanResolution);
    String normalizedExpected = normalizeCode(expectedClean);
    
    // 3. 여러 정답 허용 (cost, amount, price 모두 허용)
    return normalizedResolution.equals(normalizedExpected) ||
           isValidAlternative(normalizedResolution, normalizedExpected);
}

private String removeConflictMarkers(String code) {
    return code.replaceAll("<<<<<<< HEAD.*?=======.*?>>>>>>> incoming", "")
               .replaceAll("<<<<<<< HEAD.*?\n", "")
               .replaceAll("=======.*?\n", "")
               .replaceAll(">>>>>>> incoming.*?\n", "")
               .trim();
}

private String normalizeCode(String code) {
    return code.replaceAll("\\s+", " ")     // 모든 공백을 하나로
               .replaceAll("\\s*\\{\\s*", "{")  // { 주변 공백 제거
               .replaceAll("\\s*\\}\\s*", "}")  // } 주변 공백 제거
               .replaceAll("\\s*;\\s*", ";")    // ; 주변 공백 제거
               .replaceAll("\\s*,\\s*", ",")    // , 주변 공백 제거
               .trim();
}

private boolean isValidAlternative(String resolution, String expected) {
    // cost, amount, price 버전 모두 허용
    String[] alternatives = {"cost", "amount", "price"};
    
    for (String alt1 : alternatives) {
        for (String alt2 : alternatives) {
            String modifiedExpected = expected.replace("item." + alt1, "item." + alt2);
            if (resolution.equals(modifiedExpected)) {
                return true;
            }
        }
    }
    return false;
}
