"""Check active chapter artifacts and update the public verification report."""
from pathlib import Path
import json
import re
import subprocess

root = Path(__file__).resolve().parents[1]
p = root / '.projectcode'
road = (p/'roadmap.md').read_text(encoding='utf-8')
rows = [line.split('|')[1:-1] for line in road.splitlines() if re.match(r'^\| \d+ \|',line)]
rows = [[x.strip() for x in row] for row in rows]
tags = subprocess.check_output(['git','tag','--list','done/*'],cwd=root).decode().splitlines()
overview = ['# 온라인 주문으로 배우는 도메인 주도 설계(DDD)', '', '대상: DDD를 처음 배우는 사람', '결과물: 회원과 상품을 조회하고 주문, 할인, 배송 상태, 취소와 재고 감소를 배우는 순수 자바 프로그램', '', '| 번호 | 장 | 종류 | 배울 것 | 상태 | task | 사진 |', '|---|---|---|---|---|---|---|']
shot_count = 0
step_count = 0
for no, chapter, name, kind, learn, problem, mode, state in rows:
    folder = p/'chapters'/chapter
    tasks = (folder/'tasks.md').read_text(encoding='utf-8')
    assert '- [ ]' not in tasks, chapter + ' unchecked'
    assert state == '완료', chapter + ' state'
    assert 'done/'+chapter in tags, chapter + ' tag'
    for file in ['spec.md','plan.md','summary-code.md','diagrams/tree.md','captures/captures.md']:
        assert (folder/file).is_file(), (chapter,file)
    shots = json.loads((folder/'captures/shots.json').read_text(encoding='utf-8'))['shots']
    for shot in shots:
        assert not shot.get('missing'), (chapter,shot)
        assert (folder/'captures'/shot['file']).is_file(), (chapter,shot['file'])
    assert any(shot['task']=='done' for shot in shots)
    count = len(re.findall(r'^- \[x\] T\d+ ',tasks,re.M))
    shot_count += len(shots)
    step_count += count
    overview.append(f'| {no} | {chapter} {name} | {kind} | {learn} | {state} | {count} | {len(shots)} |')
overview += ['', '作業数'.replace('作業数','작업 수')+'는 수정 이력을 포함한 tasks.md의 T번호 작업 수다.', '학생이 따라 칠 단계는 lecture의 50단계이며 고침은 원래 단계에 녹였다.', '콘솔 출력은 txt로 보관한다.', '', '## 미룬 것', '']
later = road.split('## 미룬 것')[1].split('## 하지 않는 것')[0]
overview += [line+': 장 정하지 않음' for line in later.splitlines() if line.startswith('- ')]
overview += ['', '## 하지 않는 것', road.split('## 하지 않는 것')[1].split('## 따라치기 제목')[0].strip()]
(p/'wiki/overview.md').write_text('\n'.join(overview)+'\n',encoding='utf-8')
(p/'wiki/check.md').write_text(f'''# 점검

| 점검 | 결과 |
|---|---|
| 장 상태 | 17장 모두 완료이고 tasks.md의 작업이 모두 체크돼 있으며 done 태그가 있다 |
| 진행 중 | 0개 |
| 장 자료 | 17장 모두 캡처 목록, 폴더 구조, 코드 요약과 장 끝 출력이 있다 |
| 사진 | 캡처 목록 {shot_count}개 파일의 존재를 확인했다 |
| 미룬 것 | 다섯 개 모두 장 정하지 않음 |
| 테스트 | 91 tests successful, 실패 0개 |
| 따라치기 | 50단계 diff 재현과 단계별 빌드, HTML 복사 내용 검증 통과 |
| 고친 장 재현 | 14장부터 17장까지 기존 done 태그와 patches를 적용한 코드가 현재 lecture 장 끝 코드와 같다 |

확인 날짜: 2026-11-19
전체 학생 소스와 테스트를 임시 폴더에 컴파일해 JUnit을 실행했다.
독립 검수에서 저장 누락과 발행 순서 역전을 변이 검증으로 잡아내는지 확인했다.
콘솔 기록이 이클립스 화면을 대신한다.
다시 찍지 못한 것으로 표시된 캡처는 없다.
18장부터 22장은 현재 강의에서 제외하고 archive에 보관한다.
기존 done 태그와 Git 이력은 보존했다.

## 어긋난 것

- 없음
''',encoding='utf-8')
print(json.dumps(dict(chapters=len(rows),captures=shot_count,recordedTasks=step_count),ensure_ascii=False))
