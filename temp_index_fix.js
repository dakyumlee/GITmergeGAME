       async function startSoloGame() {
           try {
               const response = await fetch('/api/game/start', {
                   method: 'POST',
                   headers: { 'Content-Type': 'application/json' },
                   body: JSON.stringify({
                       userId: 1,
                       difficulty: selectedDifficulty
                   })
               });
               
               if (response.ok) {
                   currentSession = await response.json();
                   gameStartTime = Date.now();
                   displayConflict();
                   document.getElementById('gameContainer').classList.add('active');
                   startTimer();
               } else {
                   alert('게임 시작 실패');
               }
           } catch (error) {
               alert('서버 연결 오류: ' + error.message);
           }
       }
       
       function displayConflict() {
           if (!currentSession || !currentSession.conflicts || currentSession.conflicts.length === 0) return;
           
           const conflict = currentSession.conflicts[0];
           document.getElementById('fileHeader').textContent = conflict.fileName;
           document.getElementById('codeEditor').value = conflict.conflictMarkers;
           document.getElementById('questionStatus').textContent = `1/${currentSession.totalConflicts}`;
       }
       
       async function submitAnswer() {
           if (!currentSession || !currentSession.conflicts || currentSession.conflicts.length === 0) return;
           
           const answer = document.getElementById('codeEditor').value.trim();
           const conflict = currentSession.conflicts[0];
           
           try {
               const response = await fetch('/api/game/resolve', {
                   method: 'POST',
                   headers: { 'Content-Type': 'application/json' },
                   body: JSON.stringify({
                       sessionId: currentSession.sessionId,
                       fileName: conflict.fileName,
                       resolution: answer
                   })
               });
               
               if (response.ok) {
                   const result = await response.json();
                   console.log('API Response:', result);
                   showResult(result);
                   
                   if (result.correct) {
                       updateCorrectCount();
                   } else {
                       updateWrongCount();
                   }
                   
                   if (result.gameCompleted) {
                       setTimeout(showFinalResult, 2000);
                   }
               } else {
                   console.error('Submit failed:', await response.text());
               }
           } catch (error) {
               console.error('Submit error:', error);
               alert('제출 오류: ' + error.message);
           }
       }
